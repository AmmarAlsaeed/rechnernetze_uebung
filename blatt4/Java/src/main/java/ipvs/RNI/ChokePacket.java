package ipvs.RNI;

public class ChokePacket extends Packet {
    public ChokePacket(int senderID, int receiverID) {
        super(senderID, receiverID, 64);
    }
}
