import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

public class CircusConfig {

    InetAddress m_cntlAddr;
    int m_cntlPort;
    int m_swCnt;
    
    /* switch map: id: <ip, port> */
    HashMap<Integer, Properties> m_swList;

    /* switch config for each switch */
    HashMap<Integer, Properties> m_swCfg;
    
    /* Singleton trick */
    static CircusConfig sm_cfg = null;
    
    /* Property names */
    final static String m_propAddr = "IP";
    final static String m_propPort = "PORT";
    final static String m_propPortCnt = "PORT_CNT";
    final static String m_propPortNum = "PORT_";
    
    /* singleton implementation */
    private CircusConfig () {
        m_swList = new HashMap<Integer, Properties> ();
        m_swCfg = new HashMap<Integer, Properties> ();
    }
    
    public static CircusConfig getConfig () {
        if (sm_cfg == null)
            sm_cfg = new CircusConfig ();
            
        return sm_cfg;
    }
    
    public void setCntlAddr (String ip, String port) {
 
        try {
            m_cntlAddr = InetAddress.getByName (ip);
            m_cntlPort = Integer.parseInt (port);
        
            Circus.log ("Controller " + ip + ":" + port);
        
        } catch (Exception e) {
            Circus.log ("Ooops: " + e);
        }
    }
    
    public void setSwCnt (String swCnt) {
        m_swCnt = Integer.parseInt (swCnt);
    }
    
    public int getSwCnt () {
        return m_swCnt;
    }
    
    public void addSwList (String sid, String sip, String sport) {
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        Properties prop = new Properties ();
        
        if (m_swList.containsKey (iid)) {
            Circus.log ("duplicate id: " + sid);
            System.exit (0);
        }
        
        m_swList.put (iid, prop);
        prop.setProperty (m_propAddr, sip);
        prop.setProperty (m_propPort, sport);
        
        Circus.log ("Switch " + sid + " added ( " + sip + ":" + sport + " )");
    }
    
    /* add switch port count config */
    public void addSwCfg (String sid, String sPortCnt) {
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            Circus.log ("invalid key: " + sid);
            System.exit (0);
        }
        
        if (m_swCfg.containsKey (iid) == true) {
            Circus.log ("dup key: " + sid);
            System.exit (0);
        }
        
        Properties prop = new Properties ();
        prop.setProperty (m_propPortCnt, sPortCnt);
        
        m_swCfg.put (iid, prop);
        
        Circus.log ("switch " + sid + " is installed");
    }
    
    /* add switch connection config */
    public void addSwPortCfg (String sid, int port_id, String to_id) {
        
        Circus.log ("addSwPortCfg: " + sid + ":" + port_id + ":" + to_id);
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        if (m_swCfg.containsKey (iid) == false) {
            Circus.log ("invalid id: " + sid);
            System.exit (0);
        }
        
        String key = m_propPortNum + port_id;
        Properties p = m_swCfg.get (iid);
        p.setProperty (key, to_id);
    }
}