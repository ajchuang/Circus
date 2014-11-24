import java.util.ArrayList;
import java.util.Properties;
import java.net.InetAddress;

public class CPTable {
    
    ArrayList<CPTblEntry> m_table;
    
    public CPTable () {
        m_table = new ArrayList<CPTblEntry> ();
    }
    
    /* inner class for table entry */
    public class CPTblEntry {
        /* CS domain */
        int m_swFrom;
        int m_swTo;
        int m_lambda;
        int m_tdmId;
        
        /* PS domain */
        int m_version;
        int m_ipid;
        int m_protocol;
        InetAddress m_srcIp;
        InetAddress m_dstIp;
        
        public CPTblEntry () {
        }
        
        public Properties matchPS (PPacket pp) {
            return null;
        }
        
        public Properties matchCS (CPacket cp) {
            return null;
        }
    }


}