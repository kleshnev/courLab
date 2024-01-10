package org.example;


import akka.actor.ActorRef;

public class OrderPriceResponse {
    private final ActorRef courierActor;
    private final Order order;
    private final double price;

    public OrderPriceResponse(ActorRef courierActor, Order order, double price) {
        this.courierActor = courierActor;
        this.order = order;
        this.price = price;
    }

    public ActorRef getCourierActor() {
        return courierActor;
    }

    public Order getOrder() {
        return order;
    }

    public double getPrice() {
        return price;
    }
}
