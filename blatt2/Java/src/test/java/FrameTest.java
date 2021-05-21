import org.junit.Test;

import java.util.zip.CRC32;

import static org.junit.Assert.*;

import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.layers.*;
import de.ipvs.rni.exercise2.packets.*;

public class FrameTest
{
    int seqNo = 5;
    private byte[] payload = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    @Test
    public void testConstructor()
    {
        CRC32 crclib = new CRC32();
        crclib.update(payload);

        Frame f = new Frame(seqNo, payload, payload.length);

        assertEquals(seqNo, f.getSeqNo());
        assertArrayEquals(payload, f.getPayload());
        assertEquals(payload.length, f.getPayload().length);
        assertEquals(crclib.getValue(), f.getCRC());
    }
}