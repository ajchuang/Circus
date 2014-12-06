package CircusCommunication;

import java.net.Socket;
import java.io.ObjectOutputStream;

import CircusCfg.*;

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
        cco.setConnMap (CircusConfig.getConfig().getSwConnMap (swId));
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
    public static boolean txSetup_cs (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_setup_cs);
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
        
    }
    
    public static boolean txReconfig_cs (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_reconfig);
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    public static boolean txTeardown_cs (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        
        CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_teardown);
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    /* PS functions */
    /* this entry: PS - CS entry */
    public static boolean txAddEntry_ps_C2P (String srcIp, String dstIp, int inSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	 CircusCommObj cco = new CircusCommObj ();
         cco.setMsgType (CircusCommConst.mtype_setup_ps);
         cco.setCPdir("CP");
         cco.setSwType(1);
         /* setup CS part */

         cco.setinLambda (lambda); 
         cco.setSrcSw(inSw);
         cco.setTdmId (tdm_id);
         /* setup PS part */
         cco.setSrcIp (srcIp);
         cco.setDstIp (dstIp);    
         return send (cco, oos);
    }
    
    public static boolean txAddEntry_ps_P2C (String srcIp, String dstIp, int outSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_setup_ps);
        cco.setCPdir("PC");
        cco.setSwType(1);
        /* setup CS part */
        cco.setinLambda (lambda); 
        cco.setSrcSw(outSw);
        cco.setTdmId (tdm_id);
        /* setup PS part */
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);    
        return send (cco, oos);
    }
    
    public static boolean txAddEntry_ps_C2C (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_setup_ps);
        cco.setCPdir("CC");
        cco.setSwType(0);
        /* setup CS part */
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    
    public static boolean txModifyEntry_ps_C2P (String srcIp, String dstIp, int inSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	 CircusCommObj cco = new CircusCommObj ();
         cco.setMsgType (CircusCommConst.mtype_modify_ps);
         cco.setCPdir("CP");
         cco.setSwType(1);
         /* setup CS part */

         cco.setinLambda (lambda); 
         cco.setSrcSw(inSw);
         cco.setTdmId (tdm_id);
         /* setup PS part */
         cco.setSrcIp (srcIp);
         cco.setDstIp (dstIp);    
         return send (cco, oos);
    }
    
    public static boolean txModifyEntry_ps_P2C (String srcIp, String dstIp, int outSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_modify_ps);
        cco.setCPdir("PC");
        cco.setSwType(1);
        /* setup CS part */
        cco.setinLambda (lambda); 
        cco.setSrcSw(outSw);
        cco.setTdmId (tdm_id);
        /* setup PS part */
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);    
        return send (cco, oos);
    }
    
    public static boolean txModifyEntry_ps_C2C (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_modify_ps);
        cco.setCPdir("CC");
        cco.setSwType(0);
        /* setup CS part */
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    public static boolean txRmEntry_ps_C2P (String srcIp, String dstIp, int inSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	 CircusCommObj cco = new CircusCommObj ();
         cco.setMsgType (CircusCommConst.mtype_remove_ps);
         cco.setCPdir("CP");
         cco.setSwType(1);
         /* setup CS part */

         cco.setinLambda (lambda); 
         cco.setSrcSw(inSw);
         cco.setTdmId (tdm_id);
         /* setup PS part */
         cco.setSrcIp (srcIp);
         cco.setDstIp (dstIp);    
         return send (cco, oos);
    }
    
    public static boolean txRmEntry_ps_P2C (String srcIp, String dstIp, int outSw, int lambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_remove_ps);
        cco.setCPdir("PC");
        cco.setSwType(1);
        /* setup CS part */
        cco.setinLambda (lambda); 
        cco.setSrcSw(outSw);
        cco.setTdmId (tdm_id);
        /* setup PS part */
        cco.setSrcIp (srcIp);
        cco.setDstIp (dstIp);    
        return send (cco, oos);
    }
    
    public static boolean txRmEntry_ps_C2C (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {
        //entrydir: CP or PC or CC 
    	CircusCommObj cco = new CircusCommObj ();
        cco.setMsgType (CircusCommConst.mtype_remove_ps);
        cco.setCPdir("CC");
        cco.setSwType(0);
        /* setup CS part */
        cco.setSrcSw (srcSw);
        cco.setDstSw (dstSw);
        cco.setinLambda (inlambda);
        cco.setoutLambda (outlambda);
        cco.setTdmId (tdm_id);
        return send (cco, oos);
    }
    
    public static boolean send_unknown_packet (int sender, byte[] data, ObjectOutputStream oos) {
        
        if (oos != null) {
            CircusCommObj cco = new CircusCommObj ();
            cco.setMsgType (CircusCommConst.mtype_unknown_pkt);
            cco.setSender (sender);
            cco.setRawPacket (data);
            return send (cco, oos);
        }

        return false;
    }

    public static boolean do_forward_packet(byte[] data, ObjectOutputStream oos) {
        if (oos != null) {
            CircusCommObj cco = new CircusCommObj();
            cco.setMsgType (CircusCommConst.mtype_dofwd_pkt);
            cco.setRawPacket(data);
            return send (cco, oos);
        }

        return false;
    }
}
