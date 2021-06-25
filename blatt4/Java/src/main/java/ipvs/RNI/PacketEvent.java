package ipvs.RNI;

public class PacketEvent {
    public final int t;
    public final Packet packet;

    public PacketEvent(int t, Packet packet) {
        this.t = t;
        this.packet = packet;
    }
}
