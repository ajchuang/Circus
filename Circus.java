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
    
    public boolean parseConfig () {
        
        /* local vars */
        CircusConfig cfg = CircusConfig.getConfig ();
        log ("parsing config file");
        
        try {
            File file = new File (m_configFile);
            Scanner input = new Scanner (file);
            
            // read the controller settings
            // controll_ip, port
            while (true) {
                
                if (!input.hasNextLine ()) {
                    log ("No valid header is found");
                    return false;
                }
                    
                String ln = input.nextLine ();
                
                /* comment line */
                if (ln.startsWith ("#") || ln.trim ().isEmpty ())
                    continue;
            
                String tks[] = ln.split ("\\s+");
                
                /* setup configuration */
                cfg.setCntlAddr (tks[0], tks[1]);
                cfg.setSwCnt (tks[2]);
                break;
            }

            /* read sw table */
            while (input.hasNext ()) {
                
                String line = input.nextLine ();
                
                // comment line
                if (line.startsWith ("#") || line.trim ().isEmpty ())
                    continue;
                
                /* read switch map */
                String toks[] = line.split ("\\s+");
                cfg.addSwList (toks[0], toks[1], toks[2]);
            }
            
            /* read sw connection map */

            /* close file */
            input.close ();
            
        } catch (Exception e) {
            log ("Ooops: " + e);
            System.exit (0);
        }
        
        return true;
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
        if (c.parseConfig ())
            c.startSystem ();
        else {
            log ("Config file format error");
            log ("Please refer to the README.md for details of the document");
        }
    }

}