import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.PrintStream;

public class CPSwitch extends CSwitch implements DebugInterface {
    
    public static void log (String s) {
        System.out.println ("[CPSwitch] " + s);
    }

    public CPSwitch (int switchId) {
        super (switchId);
        
        log ("CPSwitch id: " + switchId + " is on");
        CircusConfig cc = CircusConfig.getConfig ();
        
        /* starting PS thread */
        int pport = cc.getPsPort (switchId);
        log ("CPSwitch PS_PORT: " + pport);
        new Thread (new PacketSwitchServer (pport)).start ();
    }
    
    /* implement DebugInterface */
    public void processCmd (String cmd, PrintStream os) {
        log ("dbg command: " + cmd);
            
        if (cmd.equals ("id")) {
            os.println ("I am PS-CS switch"); 
            os.println ("switch id: " + selfID);
        } else if (cmd.equals ("dump ctable")) {
            //os.println (circuit_table.toString ());
        } else {
            os.println ("unknown command: " + cmd);
        }
    }
    
    public class PacketSwitchServer implements Runnable {
        
        final int MAX_PACKET_SIZE = 2^12;
        int m_psPort;
        
        public PacketSwitchServer (int port) {
            m_psPort = port;
        }
        
        public void run () {
                
            byte buffer[] = new byte[MAX_PACKET_SIZE];
            
        	try {
                /* create data plane listener */
                DatagramSocket socket = new DatagramSocket (m_psPort);
                
                while (true) {
                    /* do packet-switching receiving */
                    DatagramPacket packet = new DatagramPacket (buffer, buffer.length);
                    socket.receive (packet);                                   
                    
                    /* TODO: PS to CS switching */
                }
        	} catch (Exception e) {
        		log ("Ooops: " + e);
                e.printStackTrace ();
        	}
    	}
    }
}