package de.ipvs.rni.exercise2.demo;

import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.layers.ApplicationLayer;
import de.ipvs.rni.exercise2.layers.DataLinkLayer;
import de.ipvs.rni.exercise2.layers.PhysicalLayer;

public class Main
{
    public static void main(String [] args)
    {
        SenderApp sendApp = new SenderApp();
        ReceiverApp recvApp = new ReceiverApp();

        ApplicationLayer appLayerA = new ApplicationLayer();
        DataLinkLayer dataLinkLayerA = new DataLinkLayer();
        
        ApplicationLayer appLayerB = new ApplicationLayer();
        DataLinkLayer dataLinkLayerB = new DataLinkLayer();
        
        PhysicalLayer physicalLayer = new PhysicalLayer();
        
        appLayerA.bind(sendApp);
        dataLinkLayerA.bind(appLayerA);

        appLayerB.bind(recvApp);
        dataLinkLayerB.bind(appLayerB);
        
        physicalLayer.connectA(dataLinkLayerA);
        physicalLayer.connectB(dataLinkLayerB);
        
        GlobalClock clock = new GlobalClock();
        clock.registerCallback(sendApp);
        clock.registerCallback(recvApp);
        clock.registerCallback(appLayerA);
        clock.registerCallback(appLayerB);
        clock.registerCallback(dataLinkLayerA);
        clock.registerCallback(dataLinkLayerB);
        clock.registerCallback(physicalLayer);
        
        try
        {
            while(true)
                clock.tempusFugit();
        }catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
}
