package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
class CourierActor extends AbstractActor {
    private final int maxDistance;
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
        List<Order> availableOrders = filterOrdersByDistance(request.getOrders(), 0, 0);
        Collections.sort(availableOrders, Comparator.comparingDouble(order -> order.getDistanceTo(0, 0) / (double) order.getPrice()));

        List<Order> plannedRoute = new ArrayList<>();
        int remainingDistance = maxDistance;
        int courierX = 0;
        int courierY = 0;

        System.out.println("Courier " + getSelf().path().name() + " is planning the route...");

        for (Order order : availableOrders) {
            double distanceToOrder = order.getDistanceTo(courierX, courierY);
            System.out.println("Order " + order.getOrderId() + ": Distance - " + distanceToOrder + ", Profit - " + order.getPrice());
            if (distanceToOrder <= remainingDistance) {
                plannedRoute.add(order);
                remainingDistance -= distanceToOrder;
                courierX = order.getX();
                courierY = order.getY();
            }
        }

        int totalProfit = plannedRoute.stream().mapToInt(Order::getPrice).sum();

        // Выводим маршрут курьера
        System.out.println("Courier " + getSelf().path().name() + " planned route with total profit " + totalProfit);
        System.out.println("Visualizing route for Courier " + getSelf().path().name() + ":");
        for (Order order : plannedRoute) {
            System.out.println("Order " + order.getOrderId() + " -> X: " + order.getX() + ", Y: " + order.getY());
        }
        System.out.println();

        getSender().tell(new CourierResponse(getSelf(), plannedRoute, totalProfit), getSelf());
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
