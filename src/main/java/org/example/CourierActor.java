package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CourierActor extends AbstractActor {
    private int maxDistance;
    private final List<Order> orders;

    public CourierActor(int maxDistance, List<Order> orders) {
        this.maxDistance = maxDistance;
        this.orders = orders;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(OrderActor.RequestOrders.class, this::handleOrderRequest)
                .build();
    }

    private void handleOrderRequest(OrderActor.RequestOrders request) {
        List<Order> optimalRoute = TSPSolver.solveTSP(request.getOrders(), maxDistance);
        int totalProfit = calculateTotalProfit(optimalRoute);

        System.out.println("Courier " + getSelf().path().name() + " planned route...");
        int courierX = 0;
        int courierY = 0;


        System.out.println("Courier " + getSelf().path().name() + " planned route with total profit " + totalProfit);
        System.out.println("0) Start: " + "X:"+ courierX + ", Y: " + courierY+ " ("+maxDistance+" distance left)");
        int step = 1;
        for (Order order : optimalRoute) {
            double distanceToOrder = order.getDistanceTo(courierX, courierY);
            maxDistance -= distanceToOrder;
            System.out.println(step+")" + " Order " + order.getOrderId() + ", X: " + order.getX() + ", Y: " + order.getY()
                    + ". Price= " +  order.getPrice() + ", (" +
                     maxDistance + " distance left)");
            courierX = order.getX();
            courierY = order.getY();
            step++;
        }
        System.out.println();

        getSender().tell(new CourierResponse(getSelf(), optimalRoute, totalProfit), getSelf());
    }








    private int calculateTotalProfit(List<Order> route) {
        int totalProfit = 0;
        for (Order order : route) {
            totalProfit += order.getPrice();
        }
        return totalProfit;
    }

    private List<Order> filterOrdersByDistance(List<Order> allOrders, int courierX, int courierY) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : allOrders) {
            double distanceToOrder = order.getDistanceTo(courierX, courierY);
            if (distanceToOrder <= maxDistance) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }
}

