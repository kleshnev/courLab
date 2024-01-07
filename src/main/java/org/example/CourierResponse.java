package org.example;
import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
class CourierResponse {
    private final ActorRef courierActor;
    private final List<Order> plannedRoute;
    private final int totalProfit;

    public CourierResponse(ActorRef courierActor, List<Order> plannedRoute, int totalProfit) {
        this.courierActor = courierActor;
        this.plannedRoute = plannedRoute;
        this.totalProfit = totalProfit;
    }

    public ActorRef getCourierActor() {
        return courierActor;
    }

    public List<Order> getPlannedRoute() {
        return plannedRoute;
    }

    public int getTotalProfit() {
        return totalProfit;
    }
}