package ipvs.RNI;

import java.util.Random;

public class Host extends NetworkEntity{
    private DelayDataRateChannel toRouter;
    private DelayDataRateChannel fromRouter;

    private final boolean sender;
    private final int RTT;
    private Random rand;
    private int lastCongestionWindowUpdate;
    private int curCongestionWindow;
    private boolean chokePacketReceived;
    private int lastChokePacketReceived;

    public Host(boolean sender, Random rand, int RTT) {
        this.sender = sender;
        this.lastCongestionWindowUpdate = 0;
        this.lastChokePacketReceived = 0;
        this.curCongestionWindow = 1500;
        this.RTT = RTT;
        this.chokePacketReceived = false;

        if(sender)
        {
            this.rand = rand;
        }
    }

    public DelayDataRateChannel getToRouter() {
        return toRouter;
    }

    public DelayDataRateChannel getFromRouter() {
        return fromRouter;
    }

    private DataPacket getApplicationData(int maxPacketSize)
    {
        return new DataPacket(this.getAddress(), 2, Math.min(maxPacketSize, 1500));
    }

    private int calcCongestionWindow(int dataChannelRate)
    {
        int time = Clock.getTime();

        if (chokePacketReceived) { //handle received choke packet (half congestion window)
            chokePacketReceived = false;
            curCongestionWindow /= 2;
            lastCongestionWindowUpdate = time;
        } else if (time - lastCongestionWindowUpdate > RTT && time - lastChokePacketReceived > RTT) { //if last update long enough ago, double congestion window
            curCongestionWindow = Math.min(curCongestionWindow * 2, dataChannelRate);
            lastCongestionWindowUpdate = time;
        }

        return curCongestionWindow;
    }

    private void processChokePacket()
    {
        int time = Clock.getTime();
        if (time - lastChokePacketReceived > RTT) { //mark choke packet as received unless host is in ignore phase
            chokePacketReceived = true;
            lastChokePacketReceived = time;
        }
    }

    @Override
    public void registerInput(DelayDataRateChannel input) {
        fromRouter = input;
    }

    @Override
    public void registerOutput(DelayDataRateChannel output) {
        toRouter = output;
    }

    @Override
    public void processEvents() {
        Packet curPacket;
        int receivedData = 0;
        while((curPacket = fromRouter.receive()) != null)
        {
            if(ChokePacket.class.isInstance(curPacket))
            {
                processChokePacket();
                System.out.println("[" + getAddress() + "]" + " Choke packet received: " + Clock.getTime());
            }
            else
            {
                receivedData += curPacket.payloadSize;
            }
        }
        System.out.println("[" + getAddress() + "] Received data: " + receivedData);

        if(sender && rand.nextBoolean()) {
            int curSendRate = calcCongestionWindow(fromRouter.getDataRate());

            while(curSendRate > 0) {
                DataPacket packet = getApplicationData(curSendRate);
                if (!toRouter.send(packet)) {
                    System.out.println("[" + getAddress() + "]" + " Channel data rate exceeded");
                    break;
                }

                curSendRate -= packet.payloadSize;

                System.out.println("[" + getAddress() + "] Sent packet: " + packet.payloadSize);
            }

            System.out.println("[" + getAddress() + "] Current data rate: " + calcCongestionWindow(fromRouter.getDataRate()));
        }
        else
        {
            System.out.println("[" + getAddress() + "] Current data rate: " + 0);
        }
    }
}
