package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
public class OrderActor extends AbstractActor {
    private final List<ActorRef> courierActors;
    private final List<Order> orders;

    public OrderActor(List<ActorRef> courierActors, List<Order> orders) {
        this.courierActors = courierActors;
        this.orders = orders;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .matchAny(message -> {
                    if (message instanceof StartPlanning) {
                        startPlanning();
                    } else if (message instanceof CourierResponse) {
                        handleCourierResponse((CourierResponse) message);
                    }
                })
                .build();
    }

    private void startPlanning() {
        for (ActorRef courierActor : courierActors) {
            courierActor.tell(new RequestOrders(orders), getSelf());
        }
    }

    private static final Object lock = new Object(); // Добавляем объект для синхронизации вывода

// ... (оставляем остальной код без изменений)

    private void handleCourierResponse(CourierResponse response) {
        synchronized (lock) {
            System.out.println("Courier " + response.getCourierActor().path().name() +
                    " planned route: " + response.getPlannedRoute() +
                    " with total profit " + response.getTotalProfit());

            // Визуализация маршрута курьера и выбранных заказов
            System.out.println("Visualizing route for Courier " + response.getCourierActor().path().name() + ":");
            for (Order order : orders) {
                if (response.getPlannedRoute().contains(order)) {
                    order.setAssignedCourier(response.getCourierActor()); // Помечаем заказы выбранным курьером
                    System.out.println("Order " + order.getOrderId() + " -> X: " + order.getX() + ", Y: " + order.getY());
                } else {
                    System.out.println("Order " + order.getOrderId() + " not selected");
                }
            }
            System.out.println();
        }
    }

    static class StartPlanning {
    }

    static class RequestOrders {
        private final List<Order> orders;

        public RequestOrders(List<Order> orders) {
            this.orders = orders;
        }

        public List<Order> getOrders() {
            return orders;
        }
    }
}