import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;

/*This is used to store entries of flows in CPswitches*/
public class CPTable {
    
    /* a concurrent safe data structure */
    CopyOnWriteArrayList<CPTblEntry> m_table;
    
    public CPTable () {
        m_table = new CopyOnWriteArrayList<CPTblEntry> ();
    }
    
    public Properties matchPS (PPacket pp) {
    	String version = "" + pp.getVersion();
    	String ipid = "" + pp.getId();
    	String protocol = "" + pp.getProtocol();
    	String srcIP = pp.getSrcIp().toString();
    	String dstIP = pp.getDstIp().toString();
        for(CPTblEntry entry : m_table){
        	
        	if (entry.SrcisP == true){// match entry
        		Properties prop = entry.Srcinfo;
        		if(version.equals(prop.getProperty("version"))||true){
        			if(ipid.equals(prop.getProperty("ipid"))||true){
        				if(protocol.equals(prop.getProperty("protocol"))||true){
        					if(srcIP.equals(prop.getProperty("srcIP"))){
        						if(dstIP.equals(prop.getProperty("dstIP"))){
        							return entry.Dstinfo;
        						}
        					}
        				}
        			}
        		}
        	}
        }
        Circus.log ("CPTable Error: Entry not found while matching PP!" );
        return null;
    }
    
    public Properties matchCS (CPacket cp) {
    	String swFrom = "" + cp.getFromSw();
    	String swTo = "" + cp.getToSw();
    	String lambda = "" + cp.getLambda();
    	String tdmId = "" + cp.getTdmId();
    	
        for(CPTblEntry entry : m_table){
        	if (entry.SrcisP == false){// match entry
        		Properties prop = entry.Srcinfo;
        		if(swFrom.equals(prop.getProperty("swFrom"))){
        			if(swTo.equals(prop.getProperty("swTo"))||true){
        				if(tdmId.equals(prop.getProperty("tdmId"))){
        					if(lambda.equals(prop.getProperty("lambda"))){
                                return entry.Dstinfo;
                                
        					}
        				}
        			}
        		}
        	}
        }
        Circus.log ("CPTable Error: Entry not found while matching CP!" );
        return null;
    }
    
    public boolean insertEntry(Properties Srcinfo, Properties Dstinfo){
    	boolean SrcisP = (Srcinfo.getProperty("swFrom") == null);// src is PPacket
    	CPTblEntry entry = new CPTblEntry(Srcinfo, Dstinfo, SrcisP);
    	m_table.add(entry);
        Circus.log ("CPTable info: Entry inserted" );
    	return true;
    }
    
    public boolean removeEntry(Properties Srcinfo){
    	ListIterator<CPTblEntry> Iterator = m_table.listIterator();
    	while(Iterator.hasNext()){
    		CPTblEntry entry = Iterator.next();
    		if(entry.Srcinfo.equals(Srcinfo)){
    			Iterator.remove();
    	        Circus.log ("CPTable info: Entry removed" );
    			return true;
    		}
    	}
        Circus.log ("CPTable Error: Entry not found while removing" );
    	return false;
    }
    
    /* inner class for table entry */
    public class CPTblEntry {
        /* CS domain */
        //        int swFrom;
        //        int swTo;
        //        int lambda;
        //        int tdmId;
        
        //        /* PS domain */
        //        int version;
        //        int ipid;
        //        int protocol;
        //        InetAddress srcIp;
        //        InetAddress dstIp;
        boolean SrcisP; //1: Src is Pkt ; 0: Src is Cir, this is used to shorten inquiry time by 2
        Properties Srcinfo;
        Properties Dstinfo;
        
        public CPTblEntry (Properties Srcinfo, Properties Dstinfo, boolean SrcisP) {
        	this.Dstinfo = Dstinfo;
        	this.SrcisP = SrcisP;
        	this.Srcinfo = Srcinfo;
        }
        
        public Properties matchPS (PPacket pp) {
            return null;
        }
        
        public Properties matchCS (CPacket cp) {
            return null;
        }
    }
    
    
}