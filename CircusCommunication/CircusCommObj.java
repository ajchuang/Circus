package CircusCommunication;

import java.io.*;
import java.util.Properties;

public class CircusCommObj implements Serializable {
    
    int m_msgId;
    int m_sender;
    int m_msgType;
    Properties m_param;
    byte[] m_rawPkt;
    
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
    
    public void setinLambda (int lambda) {
        setParam (CircusCommConst.mkey_inlambda, Integer.toString (lambda));
    }
    
    public void setoutLambda (int lambda) {
        setParam (CircusCommConst.mkey_outlambda, Integer.toString (lambda));
    }
    
    public void setCPdir (String CP) {
    	setParam (CircusCommConst.mkey_CPdir, (CP));
       
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
    
    public void setRawPacket (byte[] data) {
        m_rawPkt = data;
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
    
    public int getinLambda () {
        String s = getParam (CircusCommConst.mkey_inlambda);
        return Integer.parseInt (s);
    }
    
    public int getoutLambda () {
        String s = getParam (CircusCommConst.mkey_outlambda);
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
    
    public String getCPdir () {
        String s = getParam (CircusCommConst.mkey_CPdir);
        return s;
    }
    
    public byte[] getRawPacket () {
        return m_rawPkt;
    }
    
    /* generic method for getting/setting params */
    void setParam (String key, String val) {
        m_param.setProperty (key, val);
    }
    
    String getParam (String key) {
        return m_param.getProperty (key);
    }
} 
