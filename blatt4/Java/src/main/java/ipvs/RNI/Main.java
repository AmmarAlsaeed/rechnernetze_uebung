package ipvs.RNI;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // Connect entities
        //
        //                                                                       ------------
        //                                                                       |          |
        //                                                                       |  Host 1  |
        //                                                                       |          |
        //                                                                       ------------
        //                                                                             |
        //                                                                             |
        //                                                                             |
        //                                                                             |
        // ------------          --------------          --------------          --------------          ------------
        // |          |          |            |          |            |          |            |          |          |
        // |  Host 0  | -------- |  Router 0  | -------- |  Router 1  | -------- |  Router 2  | -------- |  Host 2  |
        // |          |          |            |          |            |          |            |          |          |
        // ------------          --------------          --------------          --------------          ------------

        //
        // Create entities
        //
        Host host0 = new Host(true, new Random(0), 16);
        Host host1 = new Host(true, new Random(0), 8);
        Host host2 = new Host(false, new Random(0), 0);

        Router router0 = new Router();
        Router router1 = new Router();
        Router router2 = new Router();

        //
        // Connect entities
        //
        router0.connectNetworkEntity(host0);
        router2.connectNetworkEntity(host1);
        router2.connectNetworkEntity(host2);

        router0.connectNetworkEntity(router1);
        router1.connectNetworkEntity(router2);

        //
        // Initialize static routes
        //
        router0.addStaticRoute(host0.getAddress(), host0.getAddress());
        router0.addStaticRoute(host1.getAddress(), router1.getAddress());
        router0.addStaticRoute(host2.getAddress(), router1.getAddress());

        router1.addStaticRoute(host0.getAddress(), router0.getAddress());
        router1.addStaticRoute(host1.getAddress(), router2.getAddress());
        router1.addStaticRoute(host2.getAddress(), router2.getAddress());

        router2.addStaticRoute(host0.getAddress(), router1.getAddress());
        router2.addStaticRoute(host1.getAddress(), host1.getAddress());
        router2.addStaticRoute(host2.getAddress(), host2.getAddress());


        //
        // Initialize entityList
        //
        LinkedList<NetworkEntity> entityList =  new LinkedList<>();
        entityList.add(host0);
        entityList.add(host1);
        entityList.add(host2);
        entityList.add(router0);
        entityList.add(router1);
        entityList.add(router2);

        //
        // Main Loop
        //
        while(true)
        {
            Iterator<NetworkEntity> it = entityList.iterator();

            while (it.hasNext())
            {
                it.next().processEvents();
            }

            Clock.elapseTime();
        }
    }
}
