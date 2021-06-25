package ipvs.RNI;

import java.util.*;

public class Router extends NetworkEntity {
    // List of all ingress channels
    private LinkedList<DelayDataRateChannel> ingressChannels = new LinkedList<>();
    // Maps receiverID to corresponding egress queue
    private HashMap<Integer, LinkedList<Packet>> egressQueueMap = new HashMap<>();
    // Maps receiverID to egress channel
    private HashMap<Integer, DelayDataRateChannel> egressChannelMap = new HashMap<>();
    // List of all egress queues (one per egress port)
    private LinkedList<LinkedList<Packet>> egressQueueList = new LinkedList<>();
    // Maps receiver address to next hop address
    private HashMap<Integer, Integer> routingTable = new HashMap<>();

    // Constants and variables for the EWMA calculation
    private HashMap<Integer, Double> dMap = new HashMap<>();
    private double dAlpha = 0.75;
    private double dThreshold = 100000;

    public void connectNetworkEntity(NetworkEntity networkEntity){
        int delay = 2;
        int dataRate = 30000; //(int)(Clock.discretizationStep * Math.pow(10, 6) * 1/8); // 1 Gbit/s

        connectNetworkEntity(networkEntity, delay, dataRate);
    }

    public void connectNetworkEntity(NetworkEntity networkEntity, int delay, int dataRate){
        registerInput(new DelayDataRateChannel(delay, dataRate,this, networkEntity));
        registerOutput(new DelayDataRateChannel(delay, dataRate, networkEntity, this));

        networkEntity.registerInput(egressChannelMap.get(networkEntity.getAddress()));
        networkEntity.registerOutput(ingressChannels.getLast());
    }

    public void addStaticRoute(int addr, int nextHop)
    {
        routingTable.put(addr, nextHop);
    }


    @Override
    public void registerInput(DelayDataRateChannel input) {
        ingressChannels.add(input);
    }

    @Override
    public void registerOutput(DelayDataRateChannel output) {
        LinkedList<Packet> egressQueue = new LinkedList<>();
        egressQueueList.add(egressQueue);
        egressQueueMap.put(output.getOtherEnd(this).getAddress(), egressQueue);
        dMap.put(output.getOtherEnd(this).getAddress(), new Double(0));
        egressChannelMap.put(output.getOtherEnd(this).getAddress(), output);
    }

    public HashMap<Integer, Double> getdMap() {
        return dMap;
    }

    public void setdAlpha(double dAlpha) {
        this.dAlpha = dAlpha;
    }

    public void setdThreshold(double dThreshold) {
        this.dThreshold = dThreshold;
    }

    @Override
    public void processEvents() {
        boolean packetsAvailable;
        int ingressDataRate = 0;

        // Read incoming packets from ingress ports and assign them to the according egress queue
        do
        {
            packetsAvailable = false;
            // Iterate all ingress ports as long as packets are available (round robin)
            Iterator<DelayDataRateChannel> it = ingressChannels.iterator();

            while(it.hasNext())
            {
                DelayDataRateChannel curChannel = it.next();
                Packet curPacket;
                if(curChannel.packetsAvailable()) {
                    // Get packet
                    curPacket = curChannel.receive();
                    // Get next hop from routing table and add the packet to the according queue
                    egressQueueMap.get(routingTable.get(curPacket.receiverID)).add(curPacket);
                    // Update ingress data rate
                    ingressDataRate += curPacket.payloadSize;

                    // Set flag if still packets are available
                    packetsAvailable = packetsAvailable || curChannel.packetsAvailable();
                }
            }
        } while(packetsAvailable);

        System.out.println("[" + this.getAddress() + "] Ingress data rate: " + ingressDataRate);


        // Process egress packets in egress queues
        int egressDataRate = 0;
        // Iterate all egress queues
        Iterator<LinkedList<Packet>> it = egressQueueList.iterator();
        while(it.hasNext())
        {
            // Iterate all packets in the current egress queue
            LinkedList<Packet> curEgressQueue = it.next();
            while (curEgressQueue.size() > 0)
            {
                // Check if current packet is not exceeding egress data channel capacity and send it
                if(egressChannelMap.get(routingTable.get(curEgressQueue.peek().receiverID)).send(curEgressQueue.peek()))
                {
                    // Packet has been sent
                    // Update egress data rate counter
                    egressDataRate += curEgressQueue.peek().payloadSize;
                    // Remove packet from egress queue
                    curEgressQueue.remove();
                }
                else
                {
                    // Packets exceeds egress data rate channel capacity
                    System.out.println("[" + this.getAddress() + "]: Egress channel data rate exceeded");
                    // Stop iterating packets because data channel is already exceeded
                    break;
                }
            }
        }

        System.out.println("[" + this.getAddress() + "] Egress data rate: " + egressDataRate);

        // Evaluate egress queue length
        Iterator<Map.Entry<Integer, LinkedList<Packet>>>mapIt = egressQueueMap.entrySet().iterator();
        while(mapIt.hasNext())
        {
            // Current egress queue
            Map.Entry<Integer, LinkedList<Packet>> entry = mapIt.next();

            // Queue length calculation
            int s = 0;
            Iterator<Packet> packetIt = entry.getValue().iterator();
            while (packetIt.hasNext())
            {
                s += packetIt.next().payloadSize;
            }

            //calculate EWMA
            int key = entry.getKey();
            double dOld = dMap.getOrDefault(key, 0d);
            double dNew = dAlpha * dOld + (1d - dAlpha) * s;
            dMap.put(key, dNew);

            System.out.println("[" + this.getAddress() + "][" + entry.getKey() + "] Egress queue size: " + s);
            System.out.println("[" + this.getAddress() + "][" + entry.getKey() + "] Egress d: " + dMap.get(entry.getKey()));

            if (dNew >= dThreshold) { //if EWMA exceeds threshold, send choke packets
                int addr = getAddress();
                for (Packet p : entry.getValue()) { //iterate through all waiting packets and send choke packets to sender
                    int packetSender = p.senderID;
                    egressChannelMap.get(routingTable.get(packetSender)).send(new ChokePacket(addr, packetSender));
                }
            }
        }
    }
}
