package org.example;

public class RequestOrderPrice {
    private final Order order;

    public RequestOrderPrice(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
