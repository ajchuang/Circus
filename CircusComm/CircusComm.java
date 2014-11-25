import java.net.Socket;
import java.io.ObjectOutputStream;

public class CircusComm {
    
    static void log (String s) {
        System.out.println ("[CircusComm] " + s);
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