package de.ipvs.rni.exercise2.common;

import de.ipvs.rni.exercise2.layers.ApplicationLayer;
import java.io.Serializable;
import java.util.ArrayDeque;

/**
 * The abstract Application class is used to develop different 
 * apps to test the message communication.
 * 
 */
public abstract class Application implements ProcessEvents{
    
    
    private ArrayDeque<Serializable> objBuffer = new ArrayDeque<>();
    
    /**
     * An application uses the application layer like an API for message 
     * communication.
     */
    private ApplicationLayer applicationLayer;
    
    /**
     * Connect the app with the application layer.
     * @param applicationLayer 
     */
    public void setApplicationLayer(ApplicationLayer applicationLayer){
        this.applicationLayer = applicationLayer;
    }
    
    /**
     * Send any object which implements the Serializable interface  
     * @param obj The object which should be send
     */
    public void send(Serializable obj){
        objBuffer.add(obj);
    }
    
    /**
     * The application layer calls the receive callback method of the app
     * when a new message has arrived.
     * @param obj The object which is received.
     */
    public abstract void receiveCallback(Object obj);

    
    /**
     * This method is called by the global clock to perform the main receive 
     * and send process of the app.
     */
    @Override
    public void process() {
        if(applicationLayer == null){return;}
        
        if(applicationLayer.hasMessage()){
            receiveCallback(applicationLayer.pollMessage());
        }    
        
        if(!objBuffer.isEmpty()){
             applicationLayer.sendMessage(objBuffer.poll());
        }
    }
}
