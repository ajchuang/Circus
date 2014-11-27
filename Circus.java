import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;
import CircusCfg.*;

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
        int sw_counter = 0;
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
                sw_counter = cfg.getSwCnt ();
                break;
            }
            
            /* read sw table */
            for (int i = 0; i < sw_counter; ++i) {
                
                if (input.hasNextLine () == false) {
                    log ("config file format error");
                    System.exit (0);
                }
                
                String line = input.nextLine ();
                
                // comment line
                if (line.startsWith ("#") || line.trim ().isEmpty ()) {
                    i--;
                    continue;
                }
                
                /* read switch map */
                String toks[] = line.split ("\\s+");
                
                if (toks.length == 6)
                    cfg.addSwList (toks[0], toks[1], toks[2], toks[3], toks[4], toks[5]);
                else {
                    log ("config file format error");
                    System.exit (0);
                }
            }

            /* read sw connection map */
            for (int i = 0; i < sw_counter; ++i) {
                
                if (input.hasNextLine () == false) {
                    log ("config file format error");
                    System.exit (0);
                }
                
                String line = input.nextLine ();
                
                if (line.startsWith ("#") || line.trim ().isEmpty ()) {
                    i--;
                    continue;
                }
                
                String toks[] = line.split ("\\s+");
                
                if (toks.length < 3) {
                    log ("config file format error");
                    System.exit (0);
                }
                
                String id = toks[0];
                int pcnt = Integer.parseInt (toks[1]);
                cfg.addSwCfg (id, toks[1]);
                
                if (toks.length != pcnt + 2) {
                    log ("config file format error");
                    System.exit (0);
                }
                
                for (int j = 0; j < pcnt; ++j)
                    cfg.addSwPortCfg (id, j, toks[2 + j]);
            }

            /* close file */
            input.close ();
            
        } catch (Exception e) {
            log ("Ooops: " + e);
            e.printStackTrace ();
            System.exit (0);
        }
        
        return true;
    }
    
    public void startSystem () {
        
        log ("starting the system");
        
        CircusConfig cfg = CircusConfig.getConfig ();
        
        /* start switches one after another */
        /* assuming that switch id starting from 1 */
        for (int i = 1; i < cfg.getSwCnt () + 1; ++i) {
            
            /* depending on the PS-CS / CS setting */
            if (cfg.getDualState (i))
                new CPSwitch (i);
            else
                new CSwitch (i);
        }
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