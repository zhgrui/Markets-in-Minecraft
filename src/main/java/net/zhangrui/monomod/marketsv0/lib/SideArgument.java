package net.zhangrui.monomod.marketsv0.lib;

import java.util.function.Predicate;

public class SideArgument
        implements Predicate<String> {

    public static final String BID_SIDE = "bid";
    public static final String ASK_SIDE = "ask";

    public String side;

    public SideArgument(String side) {
        this.side = side;
    }

    @Override
    public boolean test(String s) {
        return s.equals(BID_SIDE) || s.equals(ASK_SIDE);
    }
}
