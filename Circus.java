import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;

public class Circus {

    public String m_configFile;

    public static void log (String s) {
        System.out.println ("[Circus] " + s);
    }
    
    public static void printUsage () {
        System.out.println ("[Usage] java Circus [ConfigFile]");
    }

    public Circus (String cfile) {
        m_configFile = cfile;
    }
    
    public void parseConfig () {
        log ("parsing config file");
        
        try {
            File file = new File (m_configFile);
            Scanner input = new Scanner (file);
            
            //CONTROLLER IP TCP_PORT
            String ln = input.nextLine ();
            String tks[] = ln.split ("\\s+");
            log ("cntl addr:    " + tks[0]);
            log ("cntl port:    " + tks[1]);
            log ("num switch:   " + tks[2]);
            

            // assume that the file is correct all the time
            while (input.hasNext ()) {
                String line = input.nextLine ();
                String toks[] = line.split ("\\s+");
            
                log ("mac:      " + toks[0]);
                log ("udp port: " + toks[1]);
                log ("port cnt: " + toks[2]);
                
                int mac = Integer.parseInt (toks[0]);
                int udp_port = Integer.parseInt (toks[1]);
                int port_cnt = Integer.parseInt (toks[2]);
                
                for (int i = 0; i < port_cnt; ++i) {
                    log ("Port " + i + " connect to "); 
                }
            }

            input.close();
        } catch (Exception e) {
            log ("Ooops: " + e);
            System.exit (0);
        }
    }
    
    public void startSystem () {
        log ("starting the system");
    }
    
    public static void main (String args[]) {
    
        Circus.log ("Program starts");
        
        if (args.length != 1) {
            Circus.log ("Incorrect parameters");
            Circus.printUsage ();
            return;
        }
        
        Circus c = new Circus(args[0]);
        c.parseConfig ();
        c.startSystem ();
    }

}