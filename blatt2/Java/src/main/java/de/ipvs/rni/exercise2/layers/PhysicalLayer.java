package de.ipvs.rni.exercise2.layers;

import de.ipvs.rni.exercise2.common.ProcessEvents;
import de.ipvs.rni.exercise2.packets.Frame;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PhysicalLayer implements ProcessEvents
{
    private final double PACKET_DROP_RATE = 0.1;
    private final double PACKET_FAILURE_RATE = PACKET_DROP_RATE - PACKET_DROP_RATE / 2;
    private Random rand = new Random();

    /**
     * Communication buffers of A
     */
    private ArrayDeque<Frame> fromUpperA = new ArrayDeque<>();
    private ArrayDeque<Frame> toUpperA = new ArrayDeque<>();
    
    /**
     * Communication buffers of B
     */
    private ArrayDeque<Frame> fromUpperB = new ArrayDeque<>();
    private ArrayDeque<Frame> toUpperB = new ArrayDeque<>();


    public void connectA(DataLinkLayer dataLinkLayer){
        dataLinkLayer.setToLower(fromUpperA);
        dataLinkLayer.setFromLower(toUpperA);
        
    }
    
    public void connectB(DataLinkLayer dataLinkLayer){
        dataLinkLayer.setToLower(fromUpperB);
        dataLinkLayer.setFromLower(toUpperB);
    }
    
    private Frame manipulatePayload(Frame f)
    {
        float x = rand.nextFloat();
        Frame toSent =  new Frame(f);

        if(x < PACKET_DROP_RATE)
        {
            //Drop or modify? CRC-Switch?
            if (x > PACKET_FAILURE_RATE && toSent.getPayload() != null && DataLinkLayer.CRC_IMPLEMENTED)
            {
                System.out.println("[PHY]Corrupt frame: " + toSent.toString());
                toSent.getPayload()[toSent.getPayload().length/2] = 0; /// Reference prpblem cpoy thingfs .arrayCpoy

            }
            else
            {
                System.out.println("[PHY]Dropping: " + toSent.toString());
                toSent = null;
            }
        }
        else
        {
            System.out.println("[DDL]Forwarding: " + toSent.toString());
        }

        return toSent;
    }

    
    public void communicationFromSrcToDest(ArrayDeque<Frame> source, ArrayDeque<Frame> dest){
        while(!source.isEmpty())
        {
            Frame f = source.poll();
            f = manipulatePayload(f);
            if (f != null)
            {
                dest.offer(f);
            }
        }
    }
    
    
    
    @Override
    public void process()
    {
        communicationFromSrcToDest(fromUpperA, toUpperB);
        communicationFromSrcToDest(fromUpperB, toUpperA);
    }
}
