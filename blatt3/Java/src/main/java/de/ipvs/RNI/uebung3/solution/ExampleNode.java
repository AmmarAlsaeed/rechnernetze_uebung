package de.ipvs.RNI.uebung3.solution;

import de.ipvs.RNI.uebung3.simulator.Link;
import de.ipvs.RNI.uebung3.simulator.Node;
import de.ipvs.RNI.uebung3.simulator.NodeBase;

/** A class representing a routing-capable node in the network. */
public class ExampleNode extends NodeBase implements Node {
    /** Create a new node with the given address. */
    public ExampleNode(int address) {
        super(address);
    }

    /**
     * Called when the node "boots". This function should initialize the node's routing table and
     * send out appropriate boot-time packets.
     */
    public void init() {
        System.out.println("node " + getAddress() + " - initialized");
    }

    /**
     * Called when one of the node's interfaces is brought up.
     * 
     * @param address
     *            The address of the node at the other end of the interface being brought up.
     */
    public void interfaceUp(Link lnk) {
        System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress())
                + " up");
    }

    /**
     * Called when one of the node's interfaces is brought down.
     * 
     * @param address
     *            The address of the node at the other end of the interface being brought down.
     */
    public void interfaceDown(Link lnk) {
        System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress())
                + " down");
    }

    /**
     * Called when the node receives a packet.
     * 
     * @param source
     *            The node that sent the packet.
     * @param pkt
     *            The packet itself.
     */
    public void receivePacket(int source, String pkt) {
        System.out.println("node " + getAddress() + " - received from " + source + ": \"" + pkt
                + "\"");
    }
}
