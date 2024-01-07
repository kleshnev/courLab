package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
public class CourierSystem {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("CourierSystem");

        List<Order> orders = Arrays.asList(
                new Order(1, 100, 10, 500),
                new Order(2, 200, 10, 1000),
                new Order(3, 50, 10, 600),
                new Order(4, -100, -5, 300)
        );

        List<ActorRef> courierActors = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            courierActors.add(system.actorOf(Props.create(CourierActor.class, 300, orders), "courierActor" + i));
        }

        ActorRef orderActor = system.actorOf(Props.create(OrderActor.class, courierActors, orders), "orderActor");

        orderActor.tell(new OrderActor.StartPlanning(), ActorRef.noSender());

        system.terminate();
    }
}
