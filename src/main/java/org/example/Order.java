package org.example;

class Order {
    private final int orderId;
    private final int x;
    private final int y;
    private final int price;

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


