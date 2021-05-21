
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayDeque;

import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.layers.*;
import de.ipvs.rni.exercise2.packets.*;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author bibartoo
 */
public class ApplicationLayerTest
{
	 @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    // Aspect 1.1
    // Object is transformed into byte queue
    @Test
    public void testTransformMessageForSending()
    {
        Message testMessage = new Message("My Message", "My Text");
        ApplicationLayer al = new ApplicationLayer();
        ArrayDeque<Byte> bt = al.transformMessageForSending(testMessage);
        Message resultMessage = (Message) Utils.bytesToObj(bt);
        assertEquals(testMessage, resultMessage);
    }

    // Aspect 1.2
    // Object is transformed into byte queue with end byte
    @Test
    public void aspect12()
    {
        Message testMessage = new Message("My Message", "My Text");
        ApplicationLayer al = new ApplicationLayer();
        ArrayDeque<Byte> bt = al.transformMessageForSending(testMessage);
        assertEquals(al.END, (byte) bt.peekLast());
    }

    // Aspect 1.3
    // Handle empty fromUpper queue
    @Test
    public void aspect13()
    {
        ApplicationLayer al = new ApplicationLayer();
        al.setToLower(new ArrayDeque<>());
        al.setFromLower(new ArrayDeque<>());
        al.process();
        assertEquals(al.getFromUpper().isEmpty(), al.getToLower().isEmpty());
    }

    // Aspect 1.4
    // Process a non-empty fromUpper queue and transform it and sent it to lower
    @Test
    public void aspect14()
    {
        Message testMessage = new Message("My Message", "My Text");
        ApplicationLayer al = new ApplicationLayer();
        al.setToLower(new ArrayDeque<>());
        al.setFromLower(new ArrayDeque<>());
        al.getFromUpper().add(testMessage);
        al.process();
        Message resultMessage = (Message) Utils.bytesToObj(al.getToLower());
        assertEquals(testMessage, resultMessage);
    }

    // Aspect 1.5
    // When read from Upper then remove object
    @Test
    public void testReadFromUpper()
    {
        ApplicationLayer al = new ApplicationLayer();
        Message testMessage = new Message("hello", "world");
        al.setToLower(new ArrayDeque<>()); //as there is no binding.
        al.setFromLower(new ArrayDeque<>()); //as there is no binding.
        al.getFromUpper().add(testMessage); // write into the queue that comes from the application
        al.process();
        assertTrue(al.getFromUpper().isEmpty());
    }

    // Aspect 2.1
    // Handle empty fromLower queue
    @Test
    public void aspect21()
    {
        ApplicationLayer al = new ApplicationLayer();
        al.setToLower(new ArrayDeque<>());
        al.setFromLower(new ArrayDeque<>());
        al.process();
        assertEquals(al.getFromLower().isEmpty(), al.getToUpper().isEmpty());
    }

    // Aspect 2.2
    // transformMessageForReceiving: remove bytes from byteInput until END tag
    @Test
    public void aspect22()
    {
        Message testMessage = new Message("My Message", "My Text");
        ArrayDeque<Byte> byteTestMessage = Utils.objToBytes(testMessage);
        int oldLength = byteTestMessage.size();
        byteTestMessage.add(ApplicationLayer.END);
        ApplicationLayer al = new ApplicationLayer();
        al.transformMessageForReceiving(byteTestMessage);
        int newLength = byteTestMessage.size();
        assertTrue(oldLength > newLength);
    }

    /**
     * tests if the "end" flag is removed properly and the text message can be
     * reconstructed
     */
    @Test
    public void testTransformMessageForReceiving()
    {
        Message testMessage = new Message("My Message", "My Text");
        ArrayDeque<Byte> byteTestMessage = Utils.objToBytes(testMessage);
        byteTestMessage.add(ApplicationLayer.END);
        ApplicationLayer al = new ApplicationLayer();
        Message received = (Message) al.transformMessageForReceiving(byteTestMessage);
        assertEquals(testMessage, received);
    }
}
