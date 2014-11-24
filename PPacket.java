import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/*  
    the class is used to do
    1. fake IP format (in IP)
    2. simulate packet switching packets 
*/

public class PPacket {
    
    int m_version;
    int m_ipid;
    int m_protocol;
    InetAddress m_srcIp;
    InetAddress m_dstIp;
    byte[]  m_data;
    
    static int sm_ipid = 0;
    static int sm_headerLen = 6 * 4;
    
    public static void log (String s) {
        System.out.println ("[PPacket] " + s);
    }
    
    /* interface for switch: generate a random packet */
    /* as for now: we just create a packet with NO payload */
    public static PPacket createPacket (String src, String dst) {
        
        PPacket p = new PPacket ();
        p.setSrcIp (src);
        p.setDstIp (dst);
        return p;
    }
    
    /* interface for switch: pack to a single binary form */
    public static byte[] pack (PPacket psPkt) {
        
        byte[] raw;
        int len;
        byte[] rd = psPkt.getData (); 
        
        if (rd != null) 
            len = sm_headerLen + rd.length;
        else
            len = sm_headerLen;
            
        raw = new byte[len];
            
        /* version | ipid | protocol | src | dst | packet len | data */
        byte[] v = ByteBuffer.allocate (4).putInt (psPkt.getVersion ()).array ();
        byte[] i = ByteBuffer.allocate (4).putInt (psPkt.getId ()).array ();
        byte[] p = ByteBuffer.allocate (4).putInt (psPkt.getProtocol ()).array ();
        byte[] s = psPkt.getSrcIp ().getAddress ();
        byte[] d = psPkt.getDstIp ().getAddress ();
        byte[] l = ByteBuffer.allocate (4).putInt (len).array ();
        
        System.arraycopy (v, 0, raw,  0, v.length);
        System.arraycopy (i, 0, raw,  4, i.length);
        System.arraycopy (p, 0, raw,  8, p.length);
        System.arraycopy (s, 0, raw, 12, s.length);
        System.arraycopy (d, 0, raw, 16, d.length);
        System.arraycopy (l, 0, raw, 20, l.length);
        
        if (len > sm_headerLen)
            System.arraycopy (rd, 0, raw, 24, rd.length);
            
        return raw;
    }
    
    /* interface for switch: unpacket a cs frame to ps packet */
    public static PPacket unpack (byte[] raw) {
        
        ByteBuffer wrapped;
        byte[] temp = new byte[4];
        
        System.arraycopy (raw, 0, temp, 0, 4);
        wrapped = ByteBuffer.wrap (temp);
        int v = wrapped.getInt ();
        
        System.arraycopy (raw, 0, temp, 4, 4);
        wrapped = ByteBuffer.wrap (temp);
        int i = wrapped.getInt ();
        
        System.arraycopy (raw, 0, temp, 8, 4);
        wrapped = ByteBuffer.wrap (temp);
        int p = wrapped.getInt ();
        
        System.arraycopy (raw, 0, temp, 12, 4);
        InetAddress s = null;
        try {
            s = InetAddress.getByAddress (temp);
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
            System.exit (0);
        }
        
        System.arraycopy (raw, 0, temp, 16, 4);
        InetAddress d = null;
        try {
            d = InetAddress.getByAddress (temp); 
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
            System.exit (0);
        }
        
        System.arraycopy (raw, 0, temp, 20, 4);
        wrapped = ByteBuffer.wrap (temp);
        int l = wrapped.getInt ();
        
        byte[] data = null;
        
        if (l > sm_headerLen) {
            data = new byte[l - sm_headerLen];
            System.arraycopy (data, 0, temp, 24, (l-24));
        }
        
        PPacket pp = new PPacket ();
        pp.setVersion (v);
        pp.setId (i);
        pp.setProtocol (p);
        pp.setSrcIp (s);
        pp.setDstIp (d);
        pp.setData (data);
        
        return pp;
    } 
    
    /* init packet variables */
    public PPacket () {
        m_version = 4;
        m_protocol = 0;
        
        /* TODO: not thread-safe */
        if (sm_ipid < 0)
            sm_ipid = 0;
        else    
            sm_ipid++;
        
        m_ipid = sm_ipid;
        m_data = null;
    }
    
    /* setters */
    public void setVersion (int v) {
        m_version = v;
    }
    
    public void setProtocol (int p) {
        m_protocol = p;
    }
    
    public void setId (int id) {
        m_ipid = id;
    }
    
    public void setSrcIp (String ip) {
        try {
            m_srcIp = InetAddress.getByName (ip);
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
        }
    }
    
    public void setSrcIp (InetAddress ip) {
        m_srcIp = ip;
    }
    
    public void setDstIp (String ip) {
        try {
            m_dstIp = InetAddress.getByName (ip);
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
        }
    }
    
    public void setDstIp (InetAddress ip) {
        m_dstIp = ip;
    }
    
    public void setData (byte[] d) {
        m_data = d;
    }
    
    /* getters */
    public int getVersion () {
        return m_version;
    }
    
    public int getProtocol () {
        return m_protocol;
    }
    
    public int getId () {
        return m_ipid;
    }
    
    public InetAddress getSrcIp () {
        return m_srcIp;
    }
    
    public InetAddress getDstIp () {
        return m_dstIp;
    }
    
    public byte[] getData () {
        return m_data;
    }
}