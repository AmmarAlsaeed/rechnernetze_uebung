package ipvs.RNI;

public abstract class Packet {
    public final int senderID;
    public final int receiverID;
    public final int payloadSize;

    protected Packet(int senderID, int receiverID, int payloadSize) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.payloadSize = payloadSize;
    }
}
