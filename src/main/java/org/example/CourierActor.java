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

    private static final Object lock = new Object(); // Добавляем объект для синхронизации вывода

// ... (оставляем остальной код без изменений)

    private void handleOrderRequest(OrderActor.RequestOrders request) {
        List<Order> filteredOrders = filterOrdersByDistance(request.getOrders());

        // Находим оптимальный маршрут
        List<Order> optimalRoute = TSPSolver.solveTSP(filteredOrders, maxDistance);
        int totalProfit = calculateTotalProfit(optimalRoute);

        // Помечаем заказы выбранным курьером
        synchronized (lock) {
            for (Order order : optimalRoute) {
                order.setAssignedCourier(getSelf());
            }

            // Выводим информацию о планируемом маршруте
            System.out.println("Courier " + getSelf().path().name() + " planned route...");
            int courierX = 0;
            int courierY = 0;

            System.out.println("Courier " + getSelf().path().name() + " planned route with total profit " + totalProfit);
            System.out.println("0) Start: " + "X:" + courierX + ", Y: " + courierY + " (" + maxDistance + " distance left)");
            int step = 1;
            for (Order order : optimalRoute) {
                double distanceToOrder = order.getDistanceTo(courierX, courierY);
                maxDistance -= distanceToOrder;
                System.out.println(step + ")" + " Order " + order.getOrderId() + ", X: " + order.getX() + ", Y: " + order.getY()
                        + ". Price= " + order.getPrice() + ", (" +
                        maxDistance + " distance left)");
                courierX = order.getX();
                courierY = order.getY();
                step++;
            }
            System.out.println();
        }

        // Отправляем ответ с выбранным маршрутом обратно заказу
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
            if (order.getAssignedCourier() == null) { // Исключаем заказы, которые уже были выбраны другими курьерами
                double distanceToOrder = order.getDistanceTo(0, 0);
                if (distanceToOrder <= maxDistance) {
                    filteredOrders.add(order);
                }
            }
        }
        return filteredOrders;
    }
}
