package net.zhangrui.monomod.markets;

public enum OrderType {
    BID("bid"),
    ASK("ask");

    String value;
    OrderType(String value) {
        this.value = value;
    }
}
