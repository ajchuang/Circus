import java.io.*;
import java.util.Properties;

public class CircusCommObj implements Serializable {
    
    int m_msgId;
    int m_sender;
    int m_msgType;
    Properties m_param;
    
    public CircusCommObj () {
        m_msgId = (int)(Math.random () * 65535);
        m_param = new Properties ();
    }
    
    public String toString () {
        String x = new String ("CommObj: " + m_msgId);
        return x;
    }
    
    public Object getParam () {
        return m_param;
    }
    
    /* setters */
    public void setSender (int id) {
        m_sender = id;
    }
    
    /* setMsgId should only be called for ack/nack */
    public void setMsgId (int id) {
        m_msgId = id;
    }
    
    public void setMsgType (int type) {
        m_msgType = type;
    }
    
    public void setLambda (int lambda) {
        setParam (CircusCommConst.mkey_lambda, Integer.toString (lambda));
    }
    
    public void setTdmId (int tdm) {
        setParam (CircusCommConst.mkey_tdmId, Integer.toString (tdm));
    }
    
    public void setSrcSw (int id) {
        setParam (CircusCommConst.mkey_srcsw, Integer.toString (id));
    }
    
    public void setDstSw (int id) {
        setParam (CircusCommConst.mkey_dstsw, Integer.toString (id));
    }
    
    public void setSrcIp (String ip) {
        setParam (CircusCommConst.mkey_srcip, ip);
    }
    
    public void setDstIp (String ip) {
        setParam (CircusCommConst.mkey_dstip, ip);
    }
    
    public void setSwType (int type) {
        setParam (CircusCommConst.mkey_swType, Integer.toString (type));
    }
    
    /* getters */
    public int getSender () {
        return m_sender;
    }
    
    public int getMsgId () {
        return m_msgId;
    }
    
    public int getMsgType () {
        return m_msgType;
    }
    
    public int setLambda () {
        String s = getParam (CircusCommConst.mkey_lambda);
        return Integer.parseInt (s);
    }
    
    public int getTdmId () {
        String s = getParam (CircusCommConst.mkey_tdmId);
        return Integer.parseInt (s);
    }
    
    public int getSrcSw () {
        String s = getParam (CircusCommConst.mkey_srcsw);
        return Integer.parseInt (s);
    }
    
    public int getDstSw () {
        String s = getParam (CircusCommConst.mkey_dstsw);
        return Integer.parseInt (s);
    }
    
    public String getSrcIp () {
        return getParam (CircusCommConst.mkey_srcip);
    }
    
    public String getDstIp () {
        return getParam (CircusCommConst.mkey_dstip);
    }
    
    public int getSwType () {
        String s = getParam (CircusCommConst.mkey_swType);
        return Integer.parseInt (s);
    }
    
    /* generic method for getting/setting params */
    void setParam (String key, String val) {
        m_param.setProperty (key, val);
    }
    
    String getParam (String key) {
        return m_param.getProperty (key);
    }
} 
