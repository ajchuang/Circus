import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

public class CircusTestController {
    
    int m_dbgPort;
    int m_ctlPort;
    
    
    public static void log (String s) {
        System.out.println ("[TestC] " + s);
    }
    
    public CircusTestController (int cport, int dport) {
        
        m_dbgPort = dport;
        m_ctlPort = cport;
        
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
                    log (cco.toString ());
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
        
        public void run () {
            try {
                
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