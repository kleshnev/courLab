package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
                .match(RequestOrderPrice.class, this::handleOrderPriceRequest)
                .match(OrderActor.RequestOrders.class, this::handleOrderRequest)
                .build();
    }

    private static final Object lock = new Object();

    private void handleOrderPriceRequest(RequestOrderPrice request) {
        Order order = request.getOrder();
        double distanceToOrder = order.getDistanceTo(0, 0);
        double price = calculatePrice(distanceToOrder);

        getSender().tell(new OrderPriceResponse(getSelf(), order, price), getSelf());
    }


    private double calculatePrice(double distance) {
        // Replace this with your actual logic for calculating the price based on distance
        // For example, you can use a fixed rate per kilometer or any other pricing strategy.
        // For now, let's assume a fixed rate of 1 unit per kilometer.
        return distance * 1.0;
    }

    private void handleOrderRequest(OrderActor.RequestOrders request) {
        List<Order> filteredOrders = filterOrdersByDistance(request.getOrders());

        // Find the optimal route for the current order
        List<Order> optimalRoute = TSPSolver.solveTSP(filteredOrders, maxDistance);
        int totalProfit = calculateTotalProfit(optimalRoute);

        synchronized (lock) {
            for (Order order : optimalRoute) {
                order.setAssignedCourier(getSelf());
            }

          //  System.out.println("Courier " + getSelf().path().name() + " planned route...");
            int courierX = 0;
            int courierY = 0;

            //System.out.println("Courier " + getSelf().path().name() + " planned route with total profit " + totalProfit);
           // System.out.println("0) Start: " + "X:" + courierX + ", Y: " + courierY + " (" + maxDistance + " distance left)");
            int step = 1;
            for (Order order : optimalRoute) {
                double distanceToOrder = order.getDistanceTo(courierX, courierY);
                maxDistance -= distanceToOrder;
//                System.out.println(step + ")" + " Order " + order.getOrderId() + ", X: " + order.getX() + ", Y: " + order.getY()
//                        + ". Price= " + order.getPrice() + ", (" +
//                        maxDistance + " distance left)");
                courierX = order.getX();
                courierY = order.getY();
                step++;
            }
           // System.out.println();
        }

        getSender().tell(new CourierResponse(getSelf(), optimalRoute, totalProfit), getSelf());
    }

    private int calculateTotalProfit(List<Order> route) {
        int totalProfit = 0;
        for (Order order : route) {
            totalProfit += order.getPrice();
        }
        return totalProfit;
    }

    private List<Order> filterOrdersByDistance(List<Order> allOrders) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getAssignedCourier() == null) {
                double distanceToOrder = order.getDistanceTo(0, 0);
                if (distanceToOrder <= maxDistance) {
                    filteredOrders.add(order);
                }
            }
        }
        return filteredOrders;
    }
}
