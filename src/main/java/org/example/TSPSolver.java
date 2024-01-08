package org.example;

import java.util.ArrayList;
import java.util.List;

public class TSPSolver {

    public static List<Order> solveTSP(List<Order> orders, int maxDistance) {
        List<Order> optimalRoute = new ArrayList<>();
        List<Order> remainingOrders = new ArrayList<>(orders);

        int courierX = 0;
        int courierY = 0;

        Order currentOrder = findNearestOrder(remainingOrders, courierX, courierY);
        while (currentOrder != null && currentOrder.getDistanceTo(courierX, courierY) <= maxDistance) {
            optimalRoute.add(currentOrder);
            maxDistance -= currentOrder.getDistanceTo(courierX, courierY);
            remainingOrders.remove(currentOrder);
            courierX = currentOrder.getX();
            courierY = currentOrder.getY();
            currentOrder = findNearestOrder(remainingOrders, courierX, courierY);
        }

        return optimalRoute;
    }

    private static Order findNearestOrder(List<Order> orders, int courierX, int courierY) {
        Order nearestOrder = null;
        double minDistance = Double.MAX_VALUE;

        for (Order order : orders) {
            double distance = order.getDistanceTo(courierX, courierY);
            if (distance < minDistance) {
                nearestOrder = order;
                minDistance = distance;
            }
        }

        return nearestOrder;
    }
}

