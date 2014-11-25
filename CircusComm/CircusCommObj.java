import java.util.Properties;

public class CircusCommObj {
    /* Public Interface */
    /**************************************************************************/
    /* MSG TYPE Constants */
    public final static int mtype_poweron   = 0;
    public final static int mtype_setup     = 1;
    public final static int mtype_teardown  = 2;
    public final static int mtype_reconfig  = 3;
    
    public final static int mtype_ack       = 0x88;
    public final static int mtype_nack      = 0xff;
    
    /* switch type */
    public final static int msw_csSwitch    = 0xf0;
    public final static int msw_pcSwitch    = 0x0f;
    
    /* Local Interface */
    /**************************************************************************/
    /* PARAM key Constants */
    final static String mkey_swId   = "SWID";
    final static String mkey_swType = "SW_TYPE";
    
    final static String mkey_lambda = "LAMBDA";
    final static String mkey_tdmId  = "TDM_ID";
    final static String mkey_srcsw  = "SRC_SW";
    final static String mkey_dstsw  = "DST_SW";
    
    final static String mkey_prot   = "PROT";
    final static String mkey_srcip  = "SRC_IP";
    final static String mkey_dstip  = "DST_IP";
    
    int m_msgType;
    Properties m_param;
    
    public CircusCommObj () {
        m_param = new Properties ();
    }
    
    /* setters */
    public void setMsgType (int type) {
        m_msgType = type;
    }
    
    public void setLambda (int lambda) {
        setParam (mkey_lambda, Integer.toString(lambda));
    }
    
    public void setTdmId (int tdm) {
        setParam (mkey_tdmId, Integer.toString (tdm));
    }
    
    public void setSrcSw (int id) {
        setParam (mkey_srcsw, Integer.toString (id));
    }
    
    public void setDstSw (int id) {
        setParam (mkey_dstsw, Integer.toString (id));
    }
    
    public void setSrcIp (String ip) {
        setParam (mkey_srcip, ip);
    }
    
    public void setDstIp (String ip) {
        setParam (mkey_dstip, ip);
    }
    
    /* getters */
    public int getMsgType () {
        return m_msgType;
    }
    
    public int setLambda () {
        String s = getParam (mkey_lambda);
        return Integer.parseInt (s);
    }
    
    public int getTdmId () {
        String s = getParam (mkey_tdmId);
        return Integer.parseInt (s);
    }
    
    public int getSrcSw () {
        String s = getParam (mkey_srcsw);
        return Integer.parseInt (s);
    }
    
    public int getDstSw () {
        String s = getParam (mkey_dstsw);
        return Integer.parseInt (s);
    }
    
    public String getSrcIp () {
        return getParam (mkey_srcip);
    }
    
    public String getDstIp () {
        return getParam (mkey_dstip);
    }
    
    /* generic method for getting/setting params */
    void setParam (String key, String val) {
        m_param.setProperty (key, val);
    }
    
    String getParam (String key) {
        return m_param.getProperty (key);
    }
} 
