import java.net.Socket;
import java.io.ObjectOutputStream;

public class CircusComm {
    
    static void log (String s) {
        System.out.println ("[CircusComm] " + s);
    }
    
    /* helper sending func */
    static boolean send (CircusCommObj cco, ObjectOutputStream oos) {
        try {
            oos.writeObject (cco);
            oos.flush ();
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
            return false;
        }
        
        return true;
    }
    
    /* general functions */
    public static boolean txSysUp (int swId, int swType, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_sysup);
        cco.setSender (swId);
        cco.setSwType (swType);
        return send (cco, oos);
    }
    
    public static boolean txSysDown (int swId, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_sysdown);
        cco.setSender (swId);
        return send (cco, oos);
    }
    
    public static boolean txAck (int swId, int msgId, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_ack);
        cco.setSender (swId);
        cco.setMsgId (msgId);
        return send (cco, oos);
    }
    
    public static boolean txNack (int swId, int msgId, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_nack);
        cco.setSender (swId);
        cco.setMsgId (msgId);
        return send (cco, oos);
    }
    
    /* CS functions */
    public static boolean txSetup_cs (int dstSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_setup_cs);
        cco.setDstSw (dstSw);
        cco.setLambda (lambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
        
    }
    
    public static boolean txReconfig_cs (int dstSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_reconfig);
        cco.setDstSw (dstSw);
        cco.setLambda (lambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    public static boolean txTeardown_cs (int dstSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_teardown);
        cco.setDstSw (dstSw);
        cco.setLambda (lambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    /* PS functions */
    /* this entry: PS - CS entry */
    public static boolean txAddEntry_ps (
        String srcIp, String dstIp, int lambda, int tdm_id, ObjectOutputStream oos) {
            
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_setup_ps);
        
        /* setup CS part */
        cco.setLambda (lambda);
        cco.setTdmId (tdm_id);
        
        /* setup PS part */
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);
        
        return send (cco, oos);
    }
    
    public static boolean txModifyEntry_ps (
        String srcIp, String dstIp, int lambda, int tdm_id, ObjectOutputStream oos) {
            
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_modify_ps);
        
        /* setup CS part */
        cco.setLambda (lambda);
        cco.setTdmId (tdm_id);
        
        /* setup PS part */
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);
        
        return send (cco, oos);
    }
    
    public static boolean txRmEntry_ps (String srcIp, String dstIp, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_remove_ps);
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);
        
        return send (cco, oos);
    }
}