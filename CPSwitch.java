import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Properties;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import CircusPPacket.*;
import CircusCommunication.*;
import CircusCfg.*;

public class CPSwitch extends CSwitch implements DebugInterface, DataPlaneHandler {
    
    /* data members */
    CPTable m_cpTable;
    int switchId;
    
    public static void log (String s) {
        System.out.println ("[CPSwitch] " + s);
    }
    
    public CPSwitch (int switchId) {
        super (switchId);
        this.switchId = switchId;
        
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
    	Properties forward = m_cpTable.matchCS(cp);
    	if(forward != null && forward.getProperty("swTo") != null){
    		cp.setFromSw(switchId);
    		cp.setLambda(Integer.parseInt(forward.getProperty("lambda")));
    		cp.setTdmId(Integer.parseInt(forward.getProperty("tdmId")));
    		cp.setToSw(Integer.parseInt(forward.getProperty("swTo")));
    		CPacket.transmit (cp);
            Circus.log ("CPSwitch " + switchId + " delivered a CPacket");
    	}
    	else if(forward != null && forward.getProperty("srcIp") != null){
    		PPacket pkt = PPacket.unpack(cp.getData());
            Circus.log ("CPSwitch " + switchId + " End point pkt received: srcIP "+ pkt.getSrcIp() +" dstIP "+pkt.getDstIp() + " IPID " + pkt.getId());
    	}
        /* TODO */
        /* check if we need to transform to PS first */
        /* check if we need to further pass data to next switch */
        return;
    }
    
    /* implement DataPlaneHandler */
    public void handlePsData (PPacket pp) {
        
        log ("handle PS input");
        
    	Properties forward = m_cpTable.matchPS (pp);
        
    	if (forward != null && forward.getProperty("swTo") != null) {
            log ("CPSwitch " + switchId + " delivered a CPacket");
            
    		CPacket pkt = new CPacket ();
    		pkt.setData (PPacket.pack (pp));
    		pkt.setFromSw (switchId);
    		pkt.setLambda (Integer.parseInt (forward.getProperty ("lambda")));
    		pkt.setTdmId (Integer.parseInt (forward.getProperty ("tdmId")));
    		pkt.setToSw (Integer.parseInt (forward.getProperty ("swTo")));
    		CPacket.transmit (pkt);
            
    	} else {
    		log ("SW " + switchId + " ERROR: entry not found while forwarding!");
            CircusComm.send_unknown_packet (switchId, PPacket.pack (pp), m_cntChannel);
    	}

        return;
    }
    
    /*  this method is used to send data to myself -
        just to simplify the process of resend data.
     */
    boolean send_data_to_myself (InetAddress adr, int port, byte[] data) {
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket (data, data.length, adr, port);
            socket.send (packet);
            socket.close ();
        } catch (Exception e) {
            log ("oops: " + e);
            return false;
        }
        
        return true;
    } 
    
    
    @Override
    public boolean parsecco (CircusCommObj cco){
    	//        int swFrom;
        //        int lambda;
        //        int tdmId;
        
        //        /* PS domain */
        //        int version;
        //        int ipid;
        //        int protocol;
        //        InetAddress srcIp;
        //        InetAddress dstIp;
    	
    	int msgtype = cco.getMsgType();
    	String dir = cco.getCPdir().toUpperCase ();
    	Properties Srcinfo = new Properties();
    	Properties Dstinfo = new Properties();
    	
    	int lambda = cco.getinLambda (); 
        int inSw = cco.getSrcSw();
        int tdm_id = cco.getTdmId ();
         /* setup PS part */
        String srcIp = cco.getSrcIp ();
        String dstIp = cco.getDstIp ();
        
        if (cco.getSwType() == 0) {
        	return parsecco_CS (cco);
        } else if (msgtype == CircusCommConst.mtype_setup_ps) {
    		
    		if (dir.equals("CP")) {
    	        Srcinfo.setProperty("swFrom", inSw+"");
    	        Srcinfo.setProperty("lambda", lambda+"");
    	        Srcinfo.setProperty("tdmId", tdm_id+"");
    	        
    	        Dstinfo.setProperty("srcIp", srcIp);
    	        Dstinfo.setProperty("dstIp", dstIp);

    	        m_cpTable.insertEntry( Srcinfo,  Dstinfo);
    		}
    		
    		else if (dir.equals("PC")) {
    			Srcinfo.setProperty("srcIp", srcIp);
    	        Srcinfo.setProperty("dstIp", dstIp);
    	        log ("srcIp : " + srcIp);
    			Dstinfo.setProperty("swTo", inSw+"");
    			Dstinfo.setProperty("lambda", lambda+"");
    			Dstinfo.setProperty("tdmId", tdm_id+"");

    	        m_cpTable.insertEntry( Srcinfo,  Dstinfo);
    		} else {
                return false;
            }
    	}
        
        else if (msgtype == CircusCommConst.mtype_remove_ps) {
    		
    		if(dir.equals("CP")){
    	        Srcinfo.setProperty("swFrom", inSw+"");
    	        Srcinfo.setProperty("lambda", lambda+"");
    	        Srcinfo.setProperty("tdmId", tdm_id+"");
    	        
    	        Dstinfo.setProperty("srcIp", srcIp);
    	        Dstinfo.setProperty("dstIp", dstIp);

    	        m_cpTable.removeEntry( Srcinfo);
    		} else if (dir.equals("PC")){
    			Srcinfo.setProperty("srcIp", srcIp);
    	        Srcinfo.setProperty("dstIp", dstIp);
    	        
    			Dstinfo.setProperty("swTo", inSw+"");
    			Dstinfo.setProperty("lambda", lambda+"");
    			Dstinfo.setProperty("tdmId", tdm_id+"");

    	        m_cpTable.removeEntry( Srcinfo);
    		} else {
                return false;
            }
    	}
        
        else if (msgtype == CircusCommConst.mtype_modify_ps) {
    		
    		if(dir.equals("CP")){
    	        Srcinfo.setProperty("swFrom", inSw+"");
    	        Srcinfo.setProperty("lambda", lambda+"");
    	        Srcinfo.setProperty("tdmId", tdm_id+"");
    	        
    	        Dstinfo.setProperty("srcIp", srcIp);
    	        Dstinfo.setProperty("dstIp", dstIp);
    	        if(m_cpTable.removeEntry(Srcinfo)){
    	        	m_cpTable.insertEntry( Srcinfo,  Dstinfo);
    	        }
    	        else {
    	        	log ("Ooops: fail to reconfig");
                    return false;
    	        }
    		}
    		
    		else if(dir.equals("PC")){
    			Srcinfo.setProperty("srcIp", srcIp);
    	        Srcinfo.setProperty("dstIp", dstIp);
    	        
    			Dstinfo.setProperty("swTo", inSw+"");
    			Dstinfo.setProperty("lambda", lambda+"");
    			Dstinfo.setProperty("tdmId", tdm_id+"");

    			if(m_cpTable.removeEntry(Srcinfo)){
    	        	m_cpTable.insertEntry( Srcinfo,  Dstinfo);
    	        }
    	        else {
    	        	log ("Ooops: fail to reconfig");
                    return false;
    	        }    
            }
    	}
        else if (msgtype == CircusCommConst.mtype_dofwd_pkt) {
            
            byte[] rawPkt = cco.getRawPacket ();
            CircusConfig cfg = CircusConfig.getConfig ();
            
            /* lookup PS info */
            
            try {
                int port = cfg.getPsPort (selfID);
                InetAddress adr = InetAddress.getByName (cfg.getSwAddr (selfID));
                return send_data_to_myself (adr, port, rawPkt);
            } catch (Exception e) {
                log ("Oops: " + e);
                return false;
            }
        }
        
        return true;
    }
    
    public boolean parsecco_CS (CircusCommObj cco) {
        
    	int msgtype = cco.getMsgType();
    	int dstSw= cco.getDstSw();
		int srcSw= cco.getSrcSw();
		int inlambda= cco.getinLambda();
		int outlambda= cco.getoutLambda();
		int tdm_id= cco.getTdmId();
		
    	boolean result = true;
		
        if (msgtype == CircusCommConst.mtype_setup_cs) {
    		 result = insertcircuit (srcSw, inlambda, tdm_id, dstSw, outlambda);
    	} else if (msgtype == CircusCommConst.mtype_teardown) {
    		 result = removecircuit (srcSw, inlambda, tdm_id);
    	} else if (msgtype == CircusCommConst.mtype_reconfig) {
    		if (removecircuit (srcSw, inlambda, tdm_id)) {
    			insertcircuit (srcSw, inlambda, tdm_id, dstSw, outlambda);
    		}
        }
            
        return result;
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
