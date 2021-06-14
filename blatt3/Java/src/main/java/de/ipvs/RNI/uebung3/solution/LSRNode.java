package de.ipvs.RNI.uebung3.solution;

import java.util.*;
import de.ipvs.RNI.uebung3.simulator.Link;
import de.ipvs.RNI.uebung3.simulator.Node;
import de.ipvs.RNI.uebung3.simulator.NodeBase;
import de.ipvs.RNI.uebung3.simulator.Route;
import de.ipvs.RNI.uebung3.simulator.RoutingTable;

/** A class representing a routing-capable node in the network. */
public class LSRNode extends NodeBase implements Node {
	private static final int INITIAL_TTL = 5;
	private static final int NETWORK_SIZE = 6;
	private static final int INFINITE_DISTANCE = 1000;

	private int lspSeqnum = 0;
	private int pktSrc;

	private HashMap<Integer, LSRPacket> lsps;

	/** Create a new node with the given address. */
	public LSRNode(int address) {
		super(address);
	}

	/**
	 * Called when the node "boots". 
	 * This function should initialize the node's
	 * routing table and 
	 * send out appropriate boot-time packets.
	 */
	public void init() {
		System.out.println("node " + getAddress() + " - initializing!");
		routingTable = new RoutingTable(); //create new routing table at the start

		lsps = new HashMap<>();

		sendLSP(); //send initial LSP
	}

	/**
	 * Called when one of the node's interfaces (links to neighbor) is brought up.
	 * 
	 * @param lnk
	 *            The interface being
	 *            brought up.
	 */
	public void interfaceUp(Link lnk) {
		System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress()) + " up");
		sendLSP(); //create and send updated LSP
		if (lsps.size() == NETWORK_SIZE) buildRoutingTable(); //build routing table if all LSPs have been received
	}

	/**
	 * Called when one of the node's interfaces (link to neighbor) is brought down.
	 * 
	 * @param lnk
	 *            The interface being
	 *            brought down.
	 */
	public void interfaceDown(Link lnk) {
		System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress()) + " down");
		sendLSP(); //create and send updated LSP
		if (lsps.size() == NETWORK_SIZE) buildRoutingTable(); //build routing table if all LSPs have been received
	}

	/**
	 * Called when the node receives a LSR-packet.
	 * 
	 * @param source
	 *            The node that sent the packet.
	 * @param pkt
	 *            The packet itself.
	 */
	public void receivePacket(int source, String pkt) {
		System.out.println("node " + getAddress() + " - received from " + source + ": \"" + pkt + "\"");

		LSRPacket lsp = LSRPacket.convertFromString(pkt); //decode packet from message
		if (lsp.getTTL() <= 0) return;
		int lspSrc = lsp.getSrc();

		//if no LSP from source stored or stored LSP has lower sequence number: store and forward LSP
		if (!lsps.containsKey(lspSrc) || lsp.getSeqNum() > lsps.get(lspSrc).getSeqNum()) {
			lsps.put(lspSrc, new LSRPacket(lsp));
			pktSrc = source;
			forwardLSP(lsp);
		}

		if (lsps.size() == NETWORK_SIZE) buildRoutingTable(); //build routing table if all LSPs have been received
	}

	/**
	 * builds the nodes routingTable
	 */
	public void buildRoutingTable() {
		//distance and "previous" maps for dijkstra's algorithm
		int[] dist = new int[NETWORK_SIZE];
		int[] prev = new int[NETWORK_SIZE];

		//priority queue for dijkstra's algorithm
		PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(e -> dist[e]));

		//init dijkstra's algorithm
		int addr = getAddress();
		for (int i = 0; i < NETWORK_SIZE; i++) {
			dist[i] = i == addr ? 0 : INFINITE_DISTANCE; //distance is 0 for this node, "infinity" for all others
			prev[i] = i == addr ? addr : -1; //"previous" for this node is itself, unknown for others
			queue.add(i);
		}

		//main loop of dijkstra's algorithm
		while (!queue.isEmpty()) {
			int node = queue.remove(); //remove node with lowest distance from queue
			List<Route> links = lsps.get(node).getContent();

			//iterate over neighbours of node
			for (Route link : links) {
				int neighbour = link.getDest();
				int nDist = dist[node] + link.getCost(); //calculate neighbour distance if path through node is taken

				//if new path better than previously known: update and (re-)insert neighbour into priority queue
				if (nDist < dist[neighbour]) {
					dist[neighbour] = nDist;
					prev[neighbour] = node;
					queue.remove(neighbour);
					queue.add(neighbour);
				}
			}
		}

		routingTable.flush(); //clear routing table

		//create routing table from dijkstra results
		for (int dest = 0; dest < NETWORK_SIZE; dest++) {
			if (dest == addr) continue;
			int nextHop = dest;
			while (nextHop >= 0 && prev[nextHop] != addr) nextHop = prev[nextHop]; //find next hop
			if (nextHop >= 0) routingTable.add(new Route(dest, nextHop, dist[dest])); //add route to table (if it exists)
		}
	}

	/**
	 * forwarding a received LSP
	 *
	 * @param lsp LSP to forward
	 */
	public void forwardLSP(LSRPacket lsp) {
		lsp.decreaseTTL(); //decrease TTL, discard if TTL reaches 0
		if (lsp.getTTL() <= 0) return;

		//forward LSP to all neighbours except for packet source
		int addr = getAddress();
		String message = lsp.toMessageString();
		for (Link l : interfaces) {
			if (!l.isUp()) continue;
			int dest = l.getDest(addr);
			if (dest != pktSrc) sendPacket(dest, message);
		}
	}

	/**
	 * emit a new LSP 
	 */
	public void sendLSP() {
		int addr = getAddress();
		LSRPacket lsp = new LSRPacket(lspSeqnum++, INITIAL_TTL, addr); //create LSP with next sequence number

		//add all active links to LSP
		for (Link l : interfaces) {
			if (!l.isUp()) continue;
			int dest = l.getDest(addr);
			lsp.addContent(new Route(dest, dest, l.getCost()));
		}

		lsps.put(addr, new LSRPacket(lsp)); //copy LSP into this node's storage

		//send LSP to all neighbours
		String message = lsp.toMessageString();
		for (Link l : interfaces) {
			if (!l.isUp()) continue;
			sendPacket(l.getDest(addr), message);
		}
	}

	// for testing purposes only
	public HashMap<Integer, LSRPacket> getLsps() {
		return lsps;
	}

	public void setLsps(HashMap<Integer, LSRPacket> lsps) {
		this.lsps = lsps;
	}

}
