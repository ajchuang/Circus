import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.PrintStream;

public class CPSwitch extends CSwitch implements DebugInterface, DataPlaneHandler {
    
    CPTable m_cpTable;
    
    public static void log (String s) {
        System.out.println ("[CPSwitch] " + s);
    }

    public CPSwitch (int switchId) {
        super (switchId);
        
        /* CS-PS switching table */
        m_cpTable = new CPTable ();
        
        log ("CPSwitch id: " + switchId + " is on");
        CircusConfig cc = CircusConfig.getConfig ();
        
        /* starting PS thread */
        int pport = cc.getPsPort (switchId);
        log ("CPSwitch PS_PORT: " + pport);
        
        /* starting the PS server */
        new Thread (new PacketSwitchServer (pport, this)).start ();
    }
    
    /* implement DebugInterface */
    public void processCmd (String cmd, PrintStream os) {
        log ("dbg command: " + cmd);
            
        if (cmd.equals ("id")) {
            os.println ("I am PS-CS switch"); 
            os.println ("switch id: " + selfID);
        } else if (cmd.startsWith ("gen_traffic")) {
            
            String[] toks = cmd.split ("\\s+");
            
            if (toks.length != 3) {
                os.println ("incorrect format");
                return;
            }
                
            PPacket pp = PPacket.createPacket (toks[1], toks[2]);
            byte[] raw = PPacket.pack (pp);
            
            /* send to the packet switch server */
            try {
                CircusConfig cfg = CircusConfig.getConfig ();
        
                /* lookup PS info */
                String ip = cfg.getSwAddr (selfID);
                int port = cfg.getPsPort (selfID);
                
                InetAddress adr = InetAddress.getByName (ip);
                
                DatagramPacket packet = new DatagramPacket (raw, raw.length, adr, port);
                DatagramSocket socket = new DatagramSocket ();
                
                log ("tx length: " + packet.getData().length);
                
                socket.send (packet);                         
                socket.close();
                
            } catch (Exception e) {
                log ("Ooops: " + e);
            }
            
            
        } else {
            os.println ("unknown command: " + cmd);
        }
    }
    
    /* implement DataPlaneHandler */
    public void handleCsData (CPacket cp) {
        
        /* TODO */
        /* check if we need to transform to PS first */
        /* check if we need to further pass data to next switch */
        return;
    }
    
    /* implement DataPlaneHandler */
    public void handlePsData (PPacket pp) {
        /* TODO: PS to CS switching */
        /* look up the table and determine where to go */
        return;
    }
    
    /* inner class for ps switch */
    public class PacketSwitchServer implements Runnable {
        
        final int MAX_PACKET_SIZE = 2048;
        int m_psPort;
        DataPlaneHandler m_hdlr;
        
        public PacketSwitchServer (int port, DataPlaneHandler hdlr) {
            m_psPort = port;
            m_hdlr = hdlr;
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
                    
                    /* unpack the received PS packet */
                    byte[] raw = new byte[packet.getLength ()];
                    System.arraycopy (packet.getData (), 0, raw, 0, packet.getLength());
                    
                    PPacket pp = PPacket.unpack (raw);
                    m_hdlr.handlePsData (pp);
                }
        	} catch (Exception e) {
        		log ("Ooops: " + e);
                e.printStackTrace ();
        	}
    	}
    }
}