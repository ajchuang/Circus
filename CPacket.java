import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class CPacket {
    
    int m_swFrom;
    int m_swTo;
    int m_lambda;
    int m_tdmId;
    byte[] m_data;
    
    public static void log (String s) {
        System.out.println ("[CPacket] " + s);
    }
    
    /* convert from CS --> PS */
    /* from     : switch id */
    /* to       : switch id */
    /* lambda   : param     */
    /* tdm_id   : param     */
    /* data     : data      */   
    public static void transmit (int from, int to, int lambda, int tdm_id, byte[] data) {
        
        CircusConfig cfg = CircusConfig.getConfig ();
        
        /* lookup PS info */
        String ip = cfg.getSwAddr (to);
        int port = cfg.getSwPort (to);
        
        if (ip == null || port == -1) {
            log ("unknown destination switch id: " + to + ":" + port);
            return;
        }
        
        CPacket p = new CPacket ();
        p.setFromSw (from);
        p.setToSw (to);
        p.setLambda (lambda);
        p.setTdmId (tdm_id);
        p.setData (data);
        byte[] raw = p.pack ();

        /* do sending */
        try {
            InetAddress adr = InetAddress.getByName (ip);
            DatagramPacket packet = new DatagramPacket (raw, raw.length, adr, port);
            DatagramSocket socket = new DatagramSocket ();
            socket.send (packet);                         
            socket.close();
        } catch (Exception e) {
            log ("Ooops: " + e);
        }   
    }
    
    /* called when datagram is received --> convert PS --> CS */ 
    public static CPacket receive (DatagramPacket rawPacket) {
        CPacket p = new CPacket ();
        p.unpack (rawPacket);
        return p;
    } 
    
    public CPacket () {
    }
    
    public void setFromSw (int sw) {
        m_swFrom = sw;
    }
    
    public void setToSw (int sw) {
        m_swTo = sw;
    }
    
    public void setLambda (int l) {
        m_lambda = l;
    }
    
    public void setTdmId (int t) {
        m_tdmId = t;
    }
    
    public void setData (byte[] data) {
        m_data = data;
    }
    
    public int getFromSw () {
        return m_swFrom;
    }
    
    public int getToSw () {
        return m_swTo;
    }
    
    public int getLambda () {
        return m_lambda;
    }
    
    public int getTdmId () {
        return m_tdmId;
    }
    
    public byte[] getData () {
        return m_data;
    }
    
    public byte[] pack () {
        
        byte[] buf = new byte[16 + m_data.length];
        byte[] from = ByteBuffer.allocate (4).putInt (m_swFrom).array ();
        byte[] to   = ByteBuffer.allocate (4).putInt (m_swTo).array ();
        byte[] lmbd = ByteBuffer.allocate (4).putInt (m_lambda).array ();
        byte[] tid  = ByteBuffer.allocate (4).putInt (m_tdmId).array ();
        
        System.arraycopy (from,     0, buf,  0, from.length);
        System.arraycopy (to,       0, buf,  4, to.length);
        System.arraycopy (lmbd,     0, buf,  8, lmbd.length);
        System.arraycopy (tid,      0, buf, 12, tid.length);
        System.arraycopy (m_data,   0, buf, 16, m_data.length);
        
        return buf;
    }
    
    public void unpack (DatagramPacket rawPacket) {
        
        ByteBuffer wrapped;
        byte[] temp = new byte[4];
        byte[] raw = rawPacket.getData ();
        
        System.arraycopy (raw, 0, temp, 0, 4);
        wrapped = ByteBuffer.wrap (temp);
        m_swFrom = wrapped.getInt ();
        
        System.arraycopy (raw, 4, temp, 0, 4);
        wrapped = ByteBuffer.wrap (temp);
        m_swTo = wrapped.getInt ();
        
        System.arraycopy (raw, 8, temp, 0, 4);
        wrapped = ByteBuffer.wrap (temp);
        m_lambda = wrapped.getInt ();
        
        System.arraycopy (raw, 12, temp, 0, 4);
        wrapped = ByteBuffer.wrap (temp);
        m_tdmId = wrapped.getInt ();
        
        m_data = new byte[raw.length - 16];
        System.arraycopy (raw, 16, m_data, 0, (raw.length - 16));
    }
}