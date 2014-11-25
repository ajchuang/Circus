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
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
            return false;
        }
        
        return true;
    }
    
    /* general functions */
    public static void txSysUp (int swId, int swType, ObjectOutputStream oos) {
    }
    
    public static void txSysDown (int swId, ObjectOutputStream oos) {
    }
    
    public static boolean txAck (int swId, int msgId, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommObj.mtype_ack);
        cco.setMsgId (msgId);
        return send (cco, oos);
    }
    
    public static boolean txNack (int swId, int msgId, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommObj.mtype_nack);
        cco.setMsgId (msgId);
        return send (cco, oos);
    }
    
    /* CS functions */
    public static void txSetup_cs (int swId, int swType, ObjectOutputStream oos) {
    }
    
    public static void txReconfig_cs (int swId, int swType, ObjectOutputStream oos) {
    }
    
    public static void txTeardown_cs (int swId, int swType, ObjectOutputStream oos) {
    }
    
    /* PS functions */
    public static void txAddEntry_ps (int swId, int swType, ObjectOutputStream oos) {
    }
    
    public static void txModifyEntry_ps (int swId, int swType, ObjectOutputStream oos) {
    }
    
    public static void txRmEntry_ps (int swId, int swType, ObjectOutputStream oos) {
    }
    
    
    public static void transmit (CircusCommObj cco, ObjectOutputStream oos) {
        try {
            oos.writeObject (cco);
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
        }
    }
}