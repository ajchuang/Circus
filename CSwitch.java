import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

import CircusPPacket.*;
import CircusCfg.*;
import CircusCommunication.*;

public class CSwitch implements DebugInterface, DataPlaneHandler {
	final static String dest = "destID";
    final static String length = "length";
    
    /* data structure for each switch */
    int selfID;
    int selfIP;
    int selfudpport;
    int dbg_port;
    InetAddress controladd;
    int controlport;
    ObjectOutputStream m_cntChannel;
    
    
    HashMap<Integer, HashMap<Integer, HashMap<Integer, Properties>>> circuit_table;// keep all circuit information of this switch
    //(srcID, (length_in, (TDID, Properties(destID, length_out))))
    
    static void log (String s) {
        System.out.println ("[CSwitch] " + s);
    }
    
    public CSwitch (int switchID) {
        
        log ("Switch " + switchID + " is up!" );
        
    	CircusConfig cc = CircusConfig.getConfig ();
    	selfID      = switchID;
    	controladd  = cc.getCntlAddr ();
    	controlport = cc.getCntlPort ();
    	selfudpport = cc.getSwPort (switchID);
        dbg_port    = cc.getSwDport (switchID);
        circuit_table = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Properties>>>();
        
        /* starting servers */
    	new Thread (new DataPlaneServer (this)).start();
    	new Thread (new CntlPlaneServer ()).start();
        new Thread (new DebugServer (dbg_port, this)).start();
    }
    
    public boolean insertcircuit(int srcID, int inlength, int TDID, int destID, int outlength){
        log ("Switch " + selfID + " inserting circuit from " +srcID +" to "+destID);
        
        HashMap<Integer, HashMap<Integer, Properties>> src_table = circuit_table.get(srcID);
        if (src_table == null) {
        	src_table = new HashMap<Integer, HashMap<Integer, Properties>>();
        	circuit_table.put(srcID, src_table);
        }
        HashMap<Integer, Properties> wlength_table = src_table.get(inlength);
        if (wlength_table == null) {
        	wlength_table = new HashMap<Integer, Properties>();
        	src_table.put(inlength, wlength_table);
        }
        Properties destinfo = wlength_table.get(TDID);
        if (destinfo == null){
        	destinfo = new Properties ();
        	String temp = ""+destID;
        	destinfo.setProperty(dest, temp);
        	temp = ""+outlength;
        	destinfo.setProperty(length, temp);
        	wlength_table.put(TDID, destinfo);//add entry
    		return true;
        }
        else {
        	String temp = ""+destID;
        	String temp1 = ""+outlength;
        	if (destinfo.get(dest).equals(temp) && destinfo.get(length).equals(temp1)){
        		return true;//entry already exist
        	}
            Circus.log ("Switch " + selfID + " Error: multiple destination detected while inserting circuit from" +srcID +" to "+destID );
            return false;//entry conflict
            
        }
    }
    
    public boolean removecircuit(int srcID, int inlength, int TDID){
        HashMap<Integer, HashMap<Integer, Properties>> src_table = circuit_table.get(srcID);
        HashMap<Integer, Properties> wlength_table = src_table.get(inlength);
        Properties destinfo = wlength_table.get(TDID);
        if (destinfo ==  null){
            log ("Switch " + selfID + " removes circuit: src" +srcID +" length: "+inlength+" circuit not exsit! ");
    		return false;
        }
        
        else{
            log ("Switch " + selfID + " removes circuit: src" +srcID +" length: "+inlength+" succeed! ");
        	wlength_table.remove(TDID);//remove circuit info
        	return true;
        }
    }
    
    public boolean updatepkt(CPacket pkt){
    	int swtichfrom = pkt.getFromSw();
    	int swtichto = pkt.getToSw();
    	int length = pkt.getLambda();
    	int TDID = pkt.getTdmId();
    	if(swtichto != selfID){// if this pkt is not sent to self, return false
    		return false;
    	}
        HashMap<Integer, HashMap<Integer, Properties>> src_table = circuit_table.get(swtichfrom);
        if(src_table == null){
            Circus.log ("Switch " + selfID + " Error: src not found" );
            return false;
        }
        HashMap<Integer, Properties> wlength_table = src_table.get(length);
        if(wlength_table == null){
            Circus.log ("Switch " + selfID + " Error: lambda not found" );
            return false;
        }
        Properties destinfo = wlength_table.get(TDID);
        if (destinfo ==  null){// if flow can not be found
            Circus.log ("Switch " + selfID + "Error: flow not found" );
     		return false;
        }
        pkt.setFromSw(selfID);
        pkt.setToSw(Integer.parseInt(destinfo.getProperty(dest)));
        pkt.setLambda(Integer.parseInt(destinfo.getProperty("length")));
        Circus.log ("Switch " + selfID + " forwarding pkt to "+ dest);
        return true;
    }
    
    /* implement DebugInterface */
    public void processCmd (String cmd, PrintStream os) {
        CSwitch.log ("dbg command: " + cmd);
            
        if (cmd.equals ("id")) {
            os.println ("switch id: " + selfID);
        } else if (cmd.equals ("dump ctable")) {
            //os.println (circuit_table.toString ());
        } else {
            os.println ("unknown command: " + cmd);
        }
    }
    
    /* implement DataPlaneHandler */
    public void handleCsData (CPacket cp) {
            Circus.log ("Switch " + selfID + " received a pkt.");

        /* do CS switching procedures */
        if (updatepkt (cp) == true) {
            //deliver pkt!!!
            CPacket.transmit (cp);
            Circus.log ("Switch " + selfID + " delivered a pkt.");
        } 
    }
    
    /* implement DataPlaneHandler */
    public void handlePsData (PPacket pp) {
        return;
    }
    
    public class DataPlaneServer implements Runnable {
        
        DataPlaneHandler m_hdlr;
        
        /* note: the packet should be smaller than 2048 bytes */
        final int MAX_PACKET_SIZE = 2048;
        
        public DataPlaneServer (DataPlaneHandler hdlr) {
            m_hdlr = hdlr;
        }
        
    	public void run () {
                
            byte buffer[] = new byte[MAX_PACKET_SIZE];
            
        	try {
                /* create data plane listener */
                DatagramSocket socket = new DatagramSocket (selfudpport);
                
                while (true) {
                    /* do packet-switching receiving */
                    DatagramPacket packet = new DatagramPacket (buffer, buffer.length);
                    socket.receive (packet);                                   
                    
                    /* convert PS to CS */
                    CPacket pkt = CPacket.receive (packet);
                    m_hdlr.handleCsData (pkt);
                }
        	} catch (Exception e) {
        		log ("Ooops: " + e);
                e.printStackTrace ();
        	}
    	}
    }
    
    public class CntlPlaneServer implements Runnable {
    	public void run () {
            while (true) {
                try {
                    /* initiate a socket to connect to the server */
                    log ("Connecting to " + controladd + ":" + controlport);
                    Socket TCPsocket = new Socket (controladd, controlport);
                    
                    /* using object stream to retrieve the commobj */
                    ObjectOutputStream oos = new ObjectOutputStream (TCPsocket.getOutputStream ());
                    
                    /* Yin-Gao */
                    m_cntChannel = oos;
                    
                    /* first thing to do: send power on */
                    int swType;
                    CircusConfig cc = CircusConfig.getConfig ();
                    swType = cc.getDualState (selfID) ? CircusCommConst.msw_pcSwitch:CircusCommConst.msw_csSwitch;
                    CircusComm.txSysUp (selfID, swType, oos);
                    
                    ObjectInputStream  ois = new ObjectInputStream (TCPsocket.getInputStream ());
                    
                    while (true) {
                        log ("start to wait server msg");
                        Object obj = ois.readObject ();
                        
                        if (!(obj instanceof CircusCommObj)) {
                            log ("bad type");
                            continue;
                        }
                            
                        CircusCommObj cco = (CircusCommObj) obj; 
                        if (parsecco (cco)) {
                            CircusComm.txAck (selfID, cco.getMsgId(), oos);
                        } else {
                            CircusComm.txNack (selfID, cco.getMsgId(), oos);
                        }
                        /* TODO: process the cco */
                    }
                } catch (Exception e) {
                    
                    CSwitch.log ("Ooops: " + e);
                    e.printStackTrace ();
                    break;
                }
            }
    	}
    }
    
    public boolean parsecco (CircusCommObj cco){
    	int msgtype = cco.getMsgType();
    	
    	int dstSw= cco.getDstSw();
		int srcSw= cco.getSrcSw();
		int inlambda= cco.getinLambda();
		int outlambda= cco.getoutLambda();
		int tdm_id= cco.getTdmId();
		
    	boolean result = true;
        
		if(msgtype == CircusCommConst.mtype_setup_cs)
    		 result = insertcircuit(srcSw, inlambda, tdm_id, dstSw, outlambda);
		
    	else if(msgtype == CircusCommConst.mtype_teardown)
    		 result = removecircuit(srcSw, inlambda, tdm_id);
		
    	else if(msgtype == CircusCommConst.mtype_reconfig)
    		if(removecircuit(srcSw, inlambda, tdm_id)){
    			insertcircuit(srcSw, inlambda, tdm_id, dstSw, outlambda);
    		}
            
        return result;
    }
    
    public class DebugServer implements Runnable {
        
        int m_dbgPort;
        DebugInterface m_dif;
        
        public DebugServer (int dport, DebugInterface dif) {
            m_dbgPort = dport;
            m_dif = dif;
        }
        
        public void run () {
            
            CSwitch.log ("Debug Server @ SW " + m_dbgPort + " is on");
            
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
                    out.print ("[Debug @sw " + selfID + "]");

                    /* the string ref for command input */
                    String cmd;
                    
                    while ((cmd = br.readLine ()) != null) {
                        
                        cmd.toLowerCase ();
                        
                        /* check if leaving debug */
                        if (cmd.equals ("cmd quit"))
                            break;
                            
                        /* parsing commands, and do something here */
                        CSwitch.log (cmd);
                        m_dif.processCmd (cmd, out);
                        
                        /* print the input prompt */
                        out.print ("[Debug @sw " + selfID + "]");
                    }
                    
                    /* clean up */
                    out.close ();
                    br.close ();
                    os.close ();
                    is.close ();
                    sc.close ();
            	}
                
        	} catch (Exception e) {
        		CSwitch.log ("Ooops: " + e);
                e.printStackTrace ();
        	}
        }
    }
}

