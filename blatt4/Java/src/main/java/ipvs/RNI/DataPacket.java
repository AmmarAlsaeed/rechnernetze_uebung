package ipvs.RNI;

import java.util.Random;

public class DataPacket extends Packet {
    private static Random rand = new Random(0);

    public DataPacket(int senderID, int receiverID) {
        this(senderID, receiverID, rand.nextInt(1436) + 64);
    }

    public DataPacket(int senderID, int receiverID, int payloadSize) {
        super(senderID, receiverID, payloadSize);
    }
}
