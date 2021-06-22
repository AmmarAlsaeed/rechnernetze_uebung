package de.ipvs.RNI.uebung3.simulator;

import java.util.*;

/** A class representing a routing-capable node in the network. */
public interface Node  {

    /**
     * Called when the node "boots". This function should initialize the node's routing table and
     * send out appropriate boot-time packets.
     */
    public void init();

    /**
     * Called when one of the node's interfaces is brought up.
     * 
     * @param address
     *            The address of the node at the other end of the interface being brought up.
     */
    public void interfaceUp(Link lnk);

    /**
     * Called when one of the node's interfaces is brought down.
     * 
     * @param address
     *            The address of the node at the other end of the interface being brought down.
     */
    public void interfaceDown(Link lnk);

    /**
     * Called when the node receives a packet.
     * 
     * @param source
     *            The node that sent the packet.
     * @param pkt
     *            The packet itself.
     */
    public void receivePacket(int source, String pkt);
    
    /** Return an enumeration of the routes in the routing tables. 
     *  (Implemented in NodeBase!)*/
    public Enumeration<Route> getRoutingTable();
    
    
    /** Get the node's address. 
     *  (Implemented in NodeBase!)
     */
    public int getAddress();
    
    /** Add a new interface. 
     * (Implemented in NodeBase!) */
    public void addInterface(Link l);
}
