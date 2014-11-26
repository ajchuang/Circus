public class CircusCommConst {

    /* Public Interface */
    /**************************************************************************/
    /* MSG TYPE Constants */
    public final static int mtype_sysup     = 0;
    public final static int mtype_sysdown   = 1;
    public final static int mtype_setup_cs  = 2;
    public final static int mtype_setup_ps  = 3;
    public final static int mtype_teardown  = 4;
    public final static int mtype_reconfig  = 5;
    public final static int mtype_modify_ps = 6;
    public final static int mtype_remove_ps = 7;
    
    public final static int mtype_ack       = 0x88;
    public final static int mtype_nack      = 0xff;
    
    /* switch type */
    public final static int msw_csSwitch    = 0xf0;
    public final static int msw_pcSwitch    = 0x0f;
    
    /* Local Interface */
    /**************************************************************************/
    /* PARAM key Constants */
    final static String mkey_swId   = "SWID";
    final static String mkey_swType = "SW_TYPE"; //0:C 1:CP
    
    final static String mkey_inlambda = "IN_LAMBDA";
    final static String mkey_outlambda = "OUT_LAMBDA";
    final static String mkey_tdmId  = "TDM_ID";
    final static String mkey_srcsw  = "SRC_SW";
    final static String mkey_dstsw  = "DST_SW";
    
    final static String mkey_CPdir  = "CP_dir";// this is to store the direction of a CPswitch entry, CP means from c to p, PC means... ,CC means....
    final static String mkey_prot   = "PROT";
    final static String mkey_srcip  = "SRC_IP";
    final static String mkey_dstip  = "DST_IP";

}