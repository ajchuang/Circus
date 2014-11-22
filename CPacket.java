
public class CPacket {
    
    int m_swFrom;
    int m_swTo;
    int m_lambda;
    int m_tdmId;
    byte[] m_data;
    
    /* from     : switch id */
    /* to       : switch id */
    /* lambda   : param     */
    /* tdm_id   : param     */
    /* data     : data      */   
    public static void transmit (int from, int to, int lambda, int tdm_id, byte[] data) {
    }
    
    public CPacket () {
    }
    
    public void setFromSW (int sw) {
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
    
}