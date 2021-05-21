package apps;



import de.ipvs.rni.exercise2.demo.*;
import de.ipvs.rni.exercise2.common.Application;
import de.ipvs.rni.exercise2.common.GlobalClock;
import de.ipvs.rni.exercise2.packets.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class ReceiverApp extends Application{

    Message msg;

    
    public Message getMsg() {
        return msg;
    }
    
    @Override
    public void receiveCallback(Object obj) {
        msg = (Message) obj;
        System.out.println("[Rcv Msg] Title: "+msg.getTitle()+" Body:"+msg.getBody());
    }
    
}
