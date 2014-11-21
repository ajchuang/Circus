public class Circus {

    public String m_configFile;

    public static void log (String s) {
        System.out.println (s);
    }

    public Circus (String cfile) {
        m_configFile = cfile;
    }
    
    public void config () {
    }
    
    public static void main (String args[]) {
    
        Circus.log ("Program starts");
    }

}