package ipvs.RNI;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DelayDataRateChannel {
    private int delay;
    private int dataRate;
    private ConcurrentLinkedQueue<PacketEvent> channel =  new ConcurrentLinkedQueue<>();
    private final NetworkEntity endpoint0;
    private final NetworkEntity endpoint1;

    public DelayDataRateChannel(int delay, int dataRate, NetworkEntity endpoint0, NetworkEntity endpoint1) {
        this.delay = delay;
        this.dataRate = dataRate;
        this.endpoint0 = endpoint0;
        this.endpoint1 = endpoint1;
    }

    public NetworkEntity getOtherEnd(NetworkEntity thisEnd) {
        if (endpoint0 == thisEnd) {
            return endpoint1;
        } else {
            if (endpoint1 == thisEnd)
                return endpoint0;
            else
                return null;
        }
    }

    public boolean send(Packet packet) {
        int availableDataRate = this.dataRate;

        Iterator<PacketEvent> it = channel.iterator();

        while(it.hasNext())
        {
            PacketEvent curEvent = it.next();

            if(curEvent.t == Clock.getTime() + delay)
            {
                availableDataRate -= curEvent.packet.payloadSize;
            }
        }

        if(availableDataRate >= packet.payloadSize)
        {
            channel.add(new PacketEvent(Clock.getTime() + delay, packet));
            return true;
        }

        return false;
    }

    public Packet receive() {
        PacketEvent curEvent = channel.peek();
        if(curEvent == null || curEvent.t != Clock.getTime())
        {
            return null;
        }
        else
        {
            return channel.remove().packet;
        }
    }

    public boolean packetsAvailable()
    {
        PacketEvent curEvent = channel.peek();

        return !(curEvent == null || curEvent.t != Clock.getTime());
    }

    public int getDataRate() {
        return dataRate;
    }
}
