import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

public class CircusTestController {
    
    int m_dbgPort;
    int m_ctlPort;
    ConcurrentHashMap<Integer, ObjectOutputStream> m_output;
    
    
    public static void log (String s) {
        System.out.println ("[TestC] " + s);
    }
    
    public CircusTestController (int cport, int dport) {
        
        m_dbgPort = dport;
        m_ctlPort = cport;
        m_output = new ConcurrentHashMap<Integer, ObjectOutputStream> ();
        
        log ("CircusTestController: " + m_ctlPort + ":" + m_dbgPort);
    }
    
    public void startService () {
        new Thread (new CPlaneServ (m_ctlPort, this)).start ();
        new Thread (new DebugServer (m_dbgPort)).start ();
    }
    
    public static void main (String args[]) {
        
        if (args.length != 2) {
            log ("2 params are necessary");
            return;
        }
        
        int cPort = Integer.parseInt (args[0]);
        int dPort = Integer.parseInt (args[1]);
        
        CircusTestController ctc = new CircusTestController (cPort, dPort);
        ctc.startService ();
    }
    
    void startCPlaneServ (Socket s) {
        new Thread (new CPlaneSwitchServ (s)).start ();
    }
    
    private class CPlaneSwitchServ implements Runnable {
        
        Socket m_sk;
        ObjectInputStream   m_ois;
        ObjectOutputStream  m_oos;
        
        public CPlaneSwitchServ (Socket skt) {
            m_sk = skt;
        } 
        
        public void run () {
            
            try {
                m_oos = new ObjectOutputStream (m_sk.getOutputStream ());
                m_ois = new ObjectInputStream (m_sk.getInputStream ());
            } catch (Exception e) {
                log ("WTF: " + e);
                return;
            }
            
            while (true) {
                try {
                    log ("Starting to wait object");
                    Object obj = m_ois.readObject ();
                        
                    if (!(obj instanceof CircusCommObj)) {
                        log ("bad type");
                        continue;
                    }
                        
                    CircusCommObj cco = (CircusCommObj) obj; 
                    
                    /* process the cco */
                    int msg = cco.getMsgType (); 
                    log ("Receiving " + msg + " from " + cco.getSender ());
                    
                    /* handle syson message */
                    if (msg == CircusCommConst.mtype_sysup) {
                        
                        log ("Processing sysup from " + cco.getSender ());
                        Integer ikey = Integer.valueOf (cco.getSender ());
                        m_output.put (ikey, m_oos);
                        continue;
                        
                    } else if (msg == CircusCommConst.mtype_sysdown) {
                        
                        log ("Processing sysdown from " + cco.getSender ());
                        Integer ikey = Integer.valueOf (cco.getSender ());
                        m_output.remove (ikey, m_oos);
                        
                        /* terminate this thread */
                        return;
                    }
                    
                } catch (Exception e) {
                    log ("Ooops: " + e);
                    e.printStackTrace ();
                }                
            }
        }
    }
    
    private class CPlaneServ implements Runnable {
        
        int m_cpPort;
        CircusTestController m_me;
        
        public CPlaneServ (int port, CircusTestController me) {
            m_cpPort = port;
            m_me = me;
        }
        
        /* it's just a server thread used to accept the incoming connection */
        public void run () {
            
            try {
                /* welcome sockets to listen to the incoming sockets */
                ServerSocket ss = new ServerSocket (m_cpPort);
                
                while (true) {
                    Socket sc = ss.accept ();
                    log ("Incoming connection");
                    
                    m_me.startCPlaneServ (sc);
                }
                
        	} catch (Exception e) {
        		log ("Ooops: " + e);
                e.printStackTrace ();
        	}
        }
    }
    
    private class DebugServer implements Runnable {
        
        int m_dbgPort;
        
        public DebugServer (int dport) {
            m_dbgPort = dport;
        }
        
        void procCsIns (String cmd, PrintStream out) {
            
            out.println ("Processing CS insert...");
            
            String toks[] = cmd.split ("\\s+");
            
            if (toks.length != 5) {
                out.println ("format error");
                return;
            }
            
            int swId = Integer.parseInt (toks[1]);
            int toId = Integer.parseInt (toks[2]);
            int lambda = Integer.parseInt (toks[3]);
            int tdmId = Integer.parseInt (toks[4]);
            
            Integer iid = Integer.valueOf (swId);
            ObjectOutputStream oos = m_output.get (iid);
            
            if (oos == null) {
                out.println ("ERROR: sw id " + swId + " is not found");
                return;
            }
            
            /* send setup message */
            if (CircusComm.txSetup_cs (toId, lambda, tdmId, oos))
                out.println ("OK");
            else
                out.println ("ERROR: Cplane failed");
        }
        
        void procPsIns (String cmd, PrintStream out) {
            
            out.println ("Processing PS insert...");
            
            String toks[] = cmd.split ("\\s+");
            
            if (toks.length != 7) {
                out.println ("format error");
                return;
            }
            
            int swId    = Integer.parseInt (toks[1]);
            int toId    = Integer.parseInt (toks[2]);
            
            int lambda  = Integer.parseInt (toks[3]);
            int tdmId   = Integer.parseInt (toks[4]);
            String sip  = toks[5];
            String dip  = toks[6];
            
            Integer iid = Integer.valueOf (swId);
            ObjectOutputStream oos = m_output.get (iid);
            
            if (oos == null) {
                out.println ("ERROR: sw id " + swId + " is not found");
                return;
            }
            
            
            if (CircusComm.txAddEntry_ps (sip, dip, lambda, tdmId, oos))
                out.println ("OK");
            else
                out.println ("ERROR: fail to write");
                
            return;
        }
        
        /* handle the command */
        void procCmd (String cmd, PrintStream out) {
            
            if (cmd.startsWith ("cs_insert")) {
                procCsIns (cmd, out);
            } else if (cmd.startsWith ("ps_insert")) {
                procPsIns (cmd, out);
            }
            
        }
        
        public void run () {
            
            log ("Debug Server @ SW " + m_dbgPort + " is on");
            
            try {
                /* create the debug server */
                ServerSocket ss = new ServerSocket (m_dbgPort);
                
                while (true) {
                    
                    /* waiting for client */
                    Socket sc = ss.accept ();
                    
                    /* lots of cliches */
                    InputStream is = sc.getInputStream ();
                    OutputStream os = sc.getOutputStream ();
                    BufferedReader br = new BufferedReader (new InputStreamReader (is));
                    PrintStream out = new PrintStream (os);
                    
                    /* the string ref for command input */
                    String cmd;
                    
                    while ((cmd = br.readLine ()) != null) {
                        
                        cmd.toLowerCase ();
                        
                        /* check if leaving debug */
                        if (cmd.equals ("cmd quit"))
                            break;
                            
                        /* parsing commands, and do something here */
                        log (cmd);
                        
                        /* TODO */
                        procCmd (cmd, out);
                    }
                    
                    /* clean up */
                    out.close ();
                    br.close ();
                    os.close ();
                    is.close ();
                    sc.close ();
            	}
                
        	} catch (Exception e) {
        		log ("Ooops: " + e);
                e.printStackTrace ();
        	}
        }
    }  
    
}