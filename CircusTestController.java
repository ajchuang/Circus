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
    }
    
    public void startService () {
        new Thread (new CPlaneServ (m_ctlPort)).start ();
        new Thread (new DebugServer (m_dbgPort)).start ();
    }
    
    public static void main (String args[]) {
        
        if (args.length != 2) {
            log ("2 params are necessary");
            return;
        }
        
    }
    
    private class CPlaneServ implements Runnable {
        
        int m_cpPort;
        
        public CPlaneServ (int port) {
            m_cpPort = port;
        }
        
        public void run () {
            try {
                /* initiate a socket to connect to the server */
                ServerSocket ss = new ServerSocket (m_cpPort);
                ObjectInputStream ois;
                
                while (true) {
                    Socket sc = ss.accept(); 
                    
                    /* using object stream to retrieve the commobj */
                    ois = new ObjectInputStream (sc.getInputStream ());
                    
                    Object obj = ois.readObject ();
                    
                    if (!(obj instanceof CircusCommObj)) {
                        log ("bad type");
                        continue;
                    }
                        
                    CircusCommObj cco = (CircusCommObj) obj; 
                    
                    /* process the cco */
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