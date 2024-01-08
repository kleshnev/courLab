package org.example;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TSPSolver {

    public static List<Order> solveTSP(List<Order> orders, int maxDistance) {
        List<Order> optimalRoute = new ArrayList<>();
        List<Order> remainingOrders = new ArrayList<>(orders);

        int courierX = 0;
        int courierY = 0;

        while (!remainingOrders.isEmpty()) {
            Order bestOrder = findBestOrder(remainingOrders, courierX, courierY, maxDistance);
            if (bestOrder != null) {
                optimalRoute.add(bestOrder);
                maxDistance -= bestOrder.getDistanceTo(courierX, courierY);
                remainingOrders.remove(bestOrder);
                courierX = bestOrder.getX();
                courierY = bestOrder.getY();
            } else {
                break; // Break if no feasible order found
            }
        }

        return optimalRoute;
    }

    private static Order findBestOrder(List<Order> orders, int courierX, int courierY, int maxDistance) {
        Order bestOrder = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Order order : orders) {
            double distance = order.getDistanceTo(courierX, courierY);
            double score = order.getPrice() / distance;

            if (distance <= maxDistance && score > bestScore) {
                bestOrder = order;
                bestScore = score;
            }
        }

        return bestOrder;
    }

}
