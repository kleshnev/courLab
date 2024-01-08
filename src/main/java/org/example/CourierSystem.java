package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
public class CourierSystem {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("CourierSystem");

        List<Order> orders = Arrays.asList(
                new Order(1, 100, 0, 500),
                new Order(2, 150, 0, 1000),
                new Order(3, 50, 0, 600),
                new Order(6, 20, 0, 600),
                new Order(4, -200, 0, 600),
                new Order(5, 100, 30, 300)
        );

        List<ActorRef> courierActors = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            courierActors.add(system.actorOf(Props.create(CourierActor.class, 300, orders), "courierActor" + i));
        }

        ActorRef orderActor = system.actorOf(Props.create(OrderActor.class, courierActors, orders), "orderActor");

        orderActor.tell(new OrderActor.StartPlanning(), ActorRef.noSender());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        system.terminate();
    }
}
