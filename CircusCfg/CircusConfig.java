package CircusCfg;

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
    final static String m_propDual = "DUAL";
    final static String m_propPSport = "PSPORT";
    final static String m_propDport = "DPORT";
    final static String m_propPortCnt = "PORT_CNT";
    final static String m_propPortNum = "PORT_";
    
    void log (String s) {
        System.out.println ("[CircusConfig] " + s);
    }
    
    /* singleton implementation */
    private CircusConfig () {
        m_swList = new HashMap<Integer, Properties> ();
                            //switch_ID, (IP; Port)
        m_swCfg = new HashMap<Integer, Properties> ();
                            //switch_ID, (ID )
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
        
            log ("Controller " + ip + ":" + port);
        
        } catch (Exception e) {
            log ("Ooops: " + e);
        }
    }
    
    public InetAddress getCntlAddr () {
        return m_cntlAddr;
    }
    
    public int getCntlPort () {
        return m_cntlPort;
    }
    
    public void setSwCnt (String swCnt) {
        m_swCnt = Integer.parseInt (swCnt);
    }
    
    public int getSwCnt () {
        return m_swCnt;
    }
    
    public void addSwList (
        String sid, String sip, String sport, String dport,
        String dual, String psport) {
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        Properties prop = new Properties ();
        
        if (m_swList.containsKey (iid)) {
            log ("duplicate id: " + sid);
            System.exit (0);
        }
        
        m_swList.put (iid, prop);
        prop.setProperty (m_propAddr, sip);
        prop.setProperty (m_propPort, sport);
        prop.setProperty (m_propDport, dport);
        prop.setProperty (m_propDual, dual);
        prop.setProperty (m_propPSport, psport);
        
        log ("Switch " + sid + " added ( " + sip + ":" + sport + ":" + dual + ":" + psport + " )");
    }
    
    public String getSwAddr (int id) {
        
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("Use an invalid key: " + id);
            return null;
        } 
        
        Properties p = m_swList.get (iid);
        return p.getProperty (m_propAddr);
    }
    
    public int getSwPort (int id) {
        
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("Use an invalid key: " + id);
            return -1;
        } 
        
        Properties p = m_swList.get (iid);
        return Integer.parseInt (p.getProperty (m_propPort));
    }
    
    public int getSwDport (int id) {
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("Use an invalid key: " + id);
            return -1;
        } 
        
        Properties p = m_swList.get (iid);
        return Integer.parseInt (p.getProperty (m_propDport));
    }
    
    public boolean getDualState (int id) {
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("Use an invalid key: " + id);
            return false;
        } 
        
        Properties p = m_swList.get (iid);
        if (p.getProperty (m_propDual).equals ("P"))
            return true;
        else
            return false;
    }
    
    public int getPsPort (int id) {
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("Use an invalid key: " + id);
            return -1;
        } 
        
        Properties p = m_swList.get (iid);
        return Integer.parseInt (p.getProperty (m_propPSport));
    }
    
    /* add switch port count config */
    public void addSwCfg (String sid, String sPortCnt) {
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        if (m_swList.containsKey (iid) == false) {
            log ("invalid key: " + sid);
            System.exit (0);
        }
        
        if (m_swCfg.containsKey (iid) == true) {
            log ("dup key: " + sid);
            System.exit (0);
        }
        
        Properties prop = new Properties ();
        prop.setProperty (m_propPortCnt, sPortCnt);
        
        m_swCfg.put (iid, prop);
        
        log ("switch " + sid + " is installed");
    }
    
    /* add switch connection config */
    public void addSwPortCfg (String sid, int port_id, String to_id) {
        
        log ("addSwPortCfg: " + sid + ":" + port_id + ":" + to_id);
        
        int id = Integer.parseInt (sid);
        Integer iid = Integer.valueOf (id);
        
        if (m_swCfg.containsKey (iid) == false) {
            log ("invalid id: " + sid);
            System.exit (0);
        }
        
        String key = m_propPortNum + port_id;
        Properties p = m_swCfg.get (iid);
        p.setProperty (key, to_id);
    }
}
