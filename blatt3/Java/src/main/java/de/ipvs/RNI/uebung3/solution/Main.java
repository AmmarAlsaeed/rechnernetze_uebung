package de.ipvs.RNI.uebung3.solution;

import de.ipvs.RNI.uebung3.simulator.Network;

public class Main {

    public static void main(String[] args) throws Exception {
        String topologyFile = "src/main/java/de/ipvs/RNI/uebung3/def/assignment.net";
        String alg = "DV";
        //alg = "LSR"; // Use LSR instead of DV
        Network.runNetwork(alg, topologyFile);
    }
}
