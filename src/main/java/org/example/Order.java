package org.example;

import akka.actor.ActorRef;

class Order {
    private final int orderId;
    private final int x;
    private final int y;
    private final int price;
    private ActorRef assignedCourier; // Добавляем поле для отслеживания выбранного курьера

    public ActorRef getAssignedCourier() {
        return assignedCourier;
    }

    public void setAssignedCourier(ActorRef assignedCourier) {
        this.assignedCourier = assignedCourier;
    }
    public Order(int orderId, int x, int y, int price) {
        this.orderId = orderId;
        this.x = x;
        this.y = y;
        this.price = price;
    }
    public double getDistanceTo(int courierX, int courierY) {
        return Math.sqrt(Math.pow(x - courierX, 2) + Math.pow(y - courierY, 2));
    }
    public int getOrderId() {
        return orderId;
    }
    public String getOrderName() {
        return "Order"+ orderId;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPrice() {
        return price;
    }
}


