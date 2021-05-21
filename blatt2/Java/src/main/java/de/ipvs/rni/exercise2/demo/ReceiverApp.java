package de.ipvs.rni.exercise2.demo;

import de.ipvs.rni.exercise2.common.Application;
import de.ipvs.rni.exercise2.packets.Message;

/**
 *
 * 
 */
public class ReceiverApp extends Application{

    @Override
    public void receiveCallback(Object obj) {
        Message msg = (Message) obj;
        System.out.println("[Rcv Msg] Title: "+msg.getTitle()+" Body:"+msg.getBody());
    }
    
}
