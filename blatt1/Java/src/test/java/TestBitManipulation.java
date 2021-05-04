
import static org.junit.Assert.*;

import org.junit.Test;

import de.ipvs.RNI.uebung1.BitManipulation;
import org.junit.Assert;


public class TestBitManipulation {

	@Test
	public void testBitNor() {
	    assertEquals(-14,(new BitManipulation()).bitNor(5,9));
	}
        
    @Test
	public void testBitXor() {
            assertEquals(12,(new BitManipulation()).bitXor(5,9));
	}
        
        @Test
	public void testGetByte() {
            assertEquals(1,(new BitManipulation()).getByte(0x01FFaa,(short)2));
	}
      
        @Test
	public void testReverseBytes() {
            assertEquals(0xaaFF0100,(new BitManipulation()).reverseBytes(0x01FFaa));
	}
       
        @Test
	public void testGetBit() {
            assertEquals(1,(new BitManipulation()).getBit((byte)5,2));
	}
    
        @Test
	public void testAddOverflowCheck() {
            assertEquals(true,(new BitManipulation()).addOverflowCheck(Integer.MAX_VALUE,1));
	} 
        
        @Test
        public void testMLT3Encoder(){
            byte[] in = {
                    (byte)0b11110000,
                    (byte)0b10101010,
                    (byte)0b11111010,
            };
            char[] out = {
                    'H','0','L','0','0','0','0','0', // 0b11110000
                    'H','H','0','0','L','L','0','0', // 0b10101010
                    'H','0','L','0','H','H','0','0', // 0b11111010
            };
            Assert.assertArrayEquals(out, (new BitManipulation()).MLT3Encoder(in));
        }
        
        @Test
        public void testMLT3Decoder(){
            char[] in = {
                    'H','0','L','0','0','0','0','0',
                    'H','H','0','0','L','L','0','0',
            };
            byte[] out = {
                    (byte)0b11110000,
                    (byte)0b10101010
            };
            Assert.assertArrayEquals(out, (new BitManipulation()).MLT3Decoder(in));
        }
        
        @Test
        public void test4B5Bencode(){
            String test = "a"; //0x61
            boolean[] res = (new BitManipulation()).e4B5BEncoder(test.getBytes());
            
            boolean[] exp = {
                    false, true, true, true, false,  // 0, 1, 1 , 1, 0 -> bit 0-4 -> 5B-Symbol for higher-value semi-octet of 0x61
                    false, true, false, false, true, // 0, 1, 0 , 0, 1 -> bit 5-9 -> 5B-Symbol for lower-value semi-octet of 0x61
            };
            //    LSB (least-significant) bit has highest array index
            Assert.assertArrayEquals(exp, res); 
        }
        
        @Test
        public void test4B5Bdecode(){
           boolean[] test = {
                   false, true, true, true, false, false, true, false, false, true, //  0 1 1 1 0 ' 0 1 0 0 1 -> 0x61
                   false, true, true, true, false, true, false, true, false, false, //  0 1 1 1 0 ' 1 0 1 0 0 -> 0x62
           };
           byte[] res = (new BitManipulation()).d4B5BDecoder(test);
           byte[] exp = {(byte)0x61, (byte) 0x62};
           Assert.assertArrayEquals(exp, res); 
        }
}
