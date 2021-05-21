import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayDeque;
import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.layers.*;
import de.ipvs.rni.exercise2.packets.*;

import static org.junit.Assert.*;

public class DataLinkLayerTest
{
     @Rule
    public Timeout globalTimeout = Timeout.seconds(5);
	
	byte[] payload = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    ArrayDeque<Byte> bytesToSent = new ArrayDeque<>();

    @Before
    public void beforeTest()
    {
        bytesToSent.clear();

        for(int i=0; i<payload.length; i++)
            bytesToSent.push(payload[i]);
    }

    @Test
    public void createFrameSeqNo()
    {
        DataLinkLayer dl = new DataLinkLayer();
        int seqNo = dl.getSeqNo();

        Frame f = dl.createNextFrame(bytesToSent);
        assertEquals(seqNo, f.getSeqNo());
        assertEquals(seqNo + 1, dl.getSeqNo());
    }

    @Test
    public void createFrameLength()
    {
        DataLinkLayer dl = new DataLinkLayer();
        int MTU = 1500;

        Frame f = dl.createNextFrame(bytesToSent);
        assertTrue(payload.length <= MTU-16);
    }

    @Test
    public void processSentFrame()
    {
        DataLinkLayer dl = new DataLinkLayer();
        int availWindow = dl.getAvailWindow();

        dl.setFromUpper(bytesToSent);
        dl.setToLower(new ArrayDeque<Frame>());
        dl.setFromLower(new ArrayDeque<Frame>());
        dl.setToUpper(new ArrayDeque<Byte>());
        dl.process();

        assertEquals(availWindow-1, dl.getAvailWindow());

        beforeTest();
        dl.setSeqNo(0);
        Frame f = dl.createNextFrame(bytesToSent);
        Frame inFlightFrame = dl.getInFlight().peek();
        Frame toLowerFrame = dl.getToLower().peek();

        assertEquals(f.getSeqNo(), inFlightFrame.getSeqNo());
        assertArrayEquals(f.getPayload(), inFlightFrame.getPayload());
        assertEquals(f.getCRC(), inFlightFrame.getCRC());

        assertEquals(f.getSeqNo(), toLowerFrame.getSeqNo());
        assertArrayEquals(f.getPayload(), toLowerFrame.getPayload());
        assertEquals(f.getCRC(), toLowerFrame.getCRC());

        assertEquals(inFlightFrame, toLowerFrame);
    }
    
    //Create next frame and with payload size of MTU - size(ack) - size(seq) - size(crc)
    @Test
    public void test11(){
        //payload size 
        int payloadSize = 1500 - 4 - 4 - 8;
        DataLinkLayer dl = new DataLinkLayer();
        
        ArrayDeque<Byte> byteTestMessage = createLargeByteArray(1500);
        
        Frame f = dl.createNextFrame(byteTestMessage);
        assertEquals(f.getPayload().length, payloadSize);
    }

    public ArrayDeque<Byte> createLargeByteArray(int size){
        ArrayDeque<Byte> byteTestMessage = new ArrayDeque<>();
        for(int i = 0; i < size; i++){
            byteTestMessage.add(Byte.MIN_VALUE);
        }        
        return byteTestMessage;
    }
}