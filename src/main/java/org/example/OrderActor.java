package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.List;

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
                    } else if (message instanceof OrderPriceResponse) {
                        handleOrderPriceResponse((OrderPriceResponse) message);
                    }
                })
                .build();
    }

    private void startPlanning() {
        for (Order order : orders) {
            for (ActorRef courierActor : courierActors) {
                courierActor.tell(new RequestOrderPrice(order), getSelf());
            }
        }
    }

    private static final Object lock = new Object();

    private void handleCourierResponse(CourierResponse response) {
        synchronized (lock) {
//            System.out.println("Courier " + response.getCourierActor().path().name() +
//                    " planned route: " + response.getPlannedRoute() +
//                    " with total profit " + response.getTotalProfit());

           // System.out.println("Assigning orders for Courier " + response.getCourierActor().path().name() + ":");
            for (Order order : orders) {
                if (response.getPlannedRoute().contains(order)) {
                    order.setAssignedCourier(response.getCourierActor());
                    //System.out.println("Order " + order.getOrderId() + " assigned to Courier " + response.getCourierActor().path().name());
                } else {
                   // System.out.println("Order " + order.getOrderId() + " not selected");
                }
            }
            System.out.println();
        }
        if (response.getCourierActor() == courierActors.get(courierActors.size() - 1)) {
            printFinalResults();
        }
    }



    private void handleOrderPriceResponse(OrderPriceResponse response) {
        synchronized (lock) {
            System.out.println("Received price " + response.getPrice() +
                    " from Courier " + response.getCourierActor().path().name() +
                    " for Order " + response.getOrder().getOrderId());

            // Forward the order to the chosen courier
            response.getCourierActor().tell(new RequestOrders(List.of(response.getOrder())), getSelf());
        }
    }
    private void printFinalResults() {
        synchronized (lock) {
            System.out.println("Final Results:");
            for (ActorRef courierActor : courierActors) {
                System.out.println("Courier " + courierActor.path().name() + " - Assigned Orders and Route:");
                List<Order> assignedOrders = new ArrayList<>();
                for (Order order : orders) {
                    if (order.getAssignedCourier() == courierActor) {
                        assignedOrders.add(order);
                    }
                }

                System.out.println("Assigned Orders: " + assignedOrders);
                if (!assignedOrders.isEmpty()) {
                    List<Order> plannedRoute = TSPSolver.solveTSP(assignedOrders, Integer.MAX_VALUE);
                    System.out.println("Planned Route: ");
                    for (Order order: plannedRoute) {
                        System.out.println(order);
                    }
                    System.out.println();
                } else {
                    System.out.println("No orders assigned.");
                }
            }
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
