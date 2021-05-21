package apps;



import de.ipvs.rni.exercise2.demo.*;
import de.ipvs.rni.exercise2.common.Application;
import de.ipvs.rni.exercise2.packets.Message;

/**
 *
 * @author bibartoo
 */
public class SenderApp extends Application {
    
    String text;
    Message msg;

    public SenderApp(String text) {
        this.text = text;
        msg = new Message("Full Stack Test", text);
        send(msg);
    }

    public SenderApp() {
            msg = new Message("New Message",text);
            send(msg);
    }

    @Override
    public void receiveCallback(Object obj) {
            return;
    }

    public Message getMsg() {
        return msg;
    }
}
