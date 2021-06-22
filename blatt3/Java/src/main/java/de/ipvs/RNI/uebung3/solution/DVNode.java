package de.ipvs.RNI.uebung3.solution;

import java.util.*;
import java.util.stream.Stream;

import de.ipvs.RNI.uebung3.simulator.Link;
import de.ipvs.RNI.uebung3.simulator.Node;
import de.ipvs.RNI.uebung3.simulator.NodeBase;
import de.ipvs.RNI.uebung3.simulator.Route;
import de.ipvs.RNI.uebung3.simulator.RoutingTable;

/** A class representing a routing-capable node in the network. */
public class DVNode extends NodeBase implements Node {
    private static final int MAX_PATH_LENGTH = 7;
    private static final int ALL_NEIGHBOURS = -1;
    ArrayList<Route> distancevector;

    /** Create a new node with the given address. */
    public DVNode(int address) {
        super(address);
    }

    /**
     * Called when the node "boots". This function should initialize the node's routing table and
     * send out appropriate boot-time packets.
     */
    public void init() {
        System.out.println("node " + getAddress() + " - initializing!");
        routingTable = new RoutingTable(); //create new routing table at the start

        //add direct routes for all directly linked neighbours of this node
        int addr = getAddress();
        for (Link l : interfaces) {
            if (!l.isUp()) continue;
            int dest = l.getDest(addr);
            routingTable.add(new Route(dest, dest, l.getCost()));
        }

        sendDVMessages(ALL_NEIGHBOURS); //send initial DV messages
    }

    /**
     * Called when one of the node's interfaces is brought up.
     *
     * @param lnk
     *            The link/interface being brought up.
     */
    public void interfaceUp(Link lnk) {
        System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress()) + " up");

        int dest = lnk.getDest(getAddress());
        int linkCost = lnk.getCost();
        Route oldRoute = routingTable.findRoute(dest);

        //compare old route to activated link and update routing table if necessary
        if (oldRoute == null || oldRoute.getCost() > linkCost) {
            routingTable.remove(dest);
            routingTable.add(new Route(dest, dest, linkCost));
            sendDVMessages(ALL_NEIGHBOURS); //routing updated, send messages to neighbours
        }
    }

    /**
     * Called when one of the node's interfaces is brought down.
     *
     * @param lnk
     *            The link/interface being brought down.
     */
    public void interfaceDown(Link lnk) {
        System.out.println("node " + getAddress() + " - interface to " + lnk.getDest(getAddress()) + " down");

        int linkDest = lnk.getDest(getAddress());

        //determine routes that are now cut
        List<Integer> cutRoutes = new ArrayList<>();
        for (Enumeration<Route> routes = routingTable.enumerate(); routes.hasMoreElements();) {
            Route r = routes.nextElement();
            if (r.getNextHop() == linkDest) cutRoutes.add(r.getDest());
        }

        //downgrade all cut routes to "infinite" cost
        for (int dest : cutRoutes) {
            downgradeRoute(dest, MAX_PATH_LENGTH);
        }

        sendDVMessages(ALL_NEIGHBOURS); //routing updated, send messages to neighbours
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
        System.out.println("node " + getAddress() + " - received from " + source + ": \"" + pkt + "\"");

        //extract routes from message
        Route[] receivedRoutes = readDVMessage(source, pkt);
        if (receivedRoutes == null) return;

        /* Flag to track what DV messages should be sent after this message has been processed
            0x1: Message source (i.e. received routes are incomplete/suboptimal)
            0x2: All neighbours (i.e. new information is available) */
        int messageFlag = 0;

        //update routing table
        int addr = getAddress();
        for (Route newRoute : receivedRoutes) {
            int dest = newRoute.getDest();
            if (dest == addr) continue;
            Route oldRoute = routingTable.findRoute(dest);

            /* update route if:
                - no route to destination previously known
                - previously known route is more expensive than new route */
            if (oldRoute == null || oldRoute.getCost() > newRoute.getCost()) {
                routingTable.remove(dest);
                routingTable.add(newRoute);
                messageFlag |= 0x2; //routing updated, so messages should be sent
            } else if (oldRoute.getNextHop() == newRoute.getNextHop() && oldRoute.getCost() < newRoute.getCost()) {
                //if new and old route use same next hop but higher cost: downgrade route (used for updates)
                downgradeRoute(dest, newRoute.getCost());
                messageFlag |= 0x2; //routing updated, so messages should be sent
            }

            if (newRoute.getCost() >= MAX_PATH_LENGTH) {
                messageFlag |= 0x1; //sender has incomplete routing info, so message should be sent
            }
        }

        //check whether this node knows routes that are unknown to sender, if so, its routes are incomplete and message should be sent
        if (messageFlag == 0) {
            for (Enumeration<Route> routes = routingTable.enumerate(); routes.hasMoreElements(); ) {
                Route r = routes.nextElement();
                if (r.getDest() == source) continue;
                if (Arrays.stream(receivedRoutes).noneMatch(e -> e.getDest() == r.getDest())) {
                    messageFlag |= 0x1;
                    break;
                }
            }
        }

        if ((messageFlag & 0x2) > 0) sendDVMessages(ALL_NEIGHBOURS);
        else if ((messageFlag & 0x1) > 0) sendDVMessages(source);
    }

    /**
     * Sends this node's DV to given destination
     * @param dest Destination to send the message, sends to all directly linked neighbours if negative
     */
    private void sendDVMessages(int dest) {
        //build message string
        StringBuilder messageBuilder = new StringBuilder();
        for (Enumeration<Route> routes = routingTable.enumerate(); routes.hasMoreElements();) {
            Route r = routes.nextElement();
            messageBuilder.append(r.getDest());
            messageBuilder.append(';');
            messageBuilder.append(r.getCost());
            messageBuilder.append(';');
        }

        String message = messageBuilder.toString();

        //send message
        if (dest < 0) { //negative destination address: send to all directly linked neighbours
            int addr = getAddress();
            for (Link l : interfaces) {
                if (l.isUp()) sendPacket(l.getDest(addr), message);
            }
        } else sendPacket(dest, message); //otherwise send to given address
    }

    /**
     * Reads a message containing a DV from a directly linked neighbour
     * @param source Source address of message
     * @param message Message string
     * @return Array of potential routes derived from message, or null if there is no direct link to the source
     */
    private Route[] readDVMessage(int source, String message) {
        int addr = getAddress();

        //get route to message source
        Route routeToSource = routingTable.findRoute(source);
        if (routeToSource == null) return null;
        int sourceCost = routeToSource.getCost();
        int sourceHop = routeToSource.getNextHop();

        //dissect message and store route data, with the cost of the route to the source already taken into account
        String[] parts = message.split(";");
        Route[] routes = new Route[parts.length / 2];
        for (int i = 0; i < parts.length; i += 2) {
            int dest = Integer.parseInt(parts[i]);
            int cost = Integer.parseInt(parts[i + 1]);
            int routeCost = Math.min(cost + sourceCost, MAX_PATH_LENGTH - 1); //route cost is received cost + source route cost, up to MAX_PATH_LENGTH
            routes[i / 2] = new Route(dest, sourceHop, routeCost);
        }

        return routes;
    }

    /**
     * Downgrades the given known route to a new cost. If there is a direct link to the destination available that is
     * better than the newly downgraded route, that link is used instead.
     * @param dest Destination of the route to downgrade
     * @param newCost New cost of the route
     */
    private void downgradeRoute(int dest, int newCost) {
        Route route = routingTable.findRoute(dest); //find route to be downgraded, return if not found or the new cost wouldn't be a downgrade
        if (route == null || route.getCost() >= newCost) return;

        routingTable.remove(dest); //remove old route from table

        //look for direct link to route destination
        //if there is one with lower cost than newCost, create route through it and return
        int addr = getAddress();
        for (Link l : interfaces) {
            if (l.isUp() && l.getDest(addr) == dest && l.getCost() < newCost) {
                routingTable.add(new Route(dest, dest, l.getCost()));
                return;
            }
        }

        routingTable.add(new Route(dest, route.getNextHop(), newCost)); //create route with same next hop and new cost
    }

}
