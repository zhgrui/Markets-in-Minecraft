package net.zhangrui.monomod.marketsv0.lib;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhangrui.monomod.MonoMod;

import java.util.*;

public class MatchingEngine {

    private static final HashMap<String, Market> markets = new HashMap<>();

    public static void addMarket(Item item) {
        MatchingEngine.markets.putIfAbsent(item.toString(), new Market(item));
    }

    public static Market getMarket(Item item) {
        return MatchingEngine.markets.get(item.toString());
    }

    public static Set<String> listMarkets() {
        return markets.keySet();
    }


}


enum OrderType { BID, ASK }
// doubly linked list
class Orders {

    OrderType type;
    Order head = null;
    Order tail = null;
    TreeMap<Double, Order> orderMap = new TreeMap<>(new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {

            switch (type) {
                case BID:
                    // head must be highest
                    return Double.compare(o1, o2);
                case ASK:
                    // head must be lowest
                    return Double.compare(o2, o1);
                default:
                    return 0;
            }
        }
    });
    int totalVolume = 0;

    public Orders(OrderType ot) {
        this.type = ot;
    }

    public Order getOrder(double price) {
        return orderMap.get(price);
    }

    public void placeOrder(ServerPlayerEntity player, double price, int volume) {

        var newOrder = new Order(price);

        if (!orderMap.containsKey(price)) {

            var floorEntry = orderMap.floorEntry(price);
            var ceilEntry = orderMap.ceilingEntry(price);

            if (floorEntry == null) {
                tail = newOrder;
            }
            if (ceilEntry == null) {
                head = newOrder;
            }

            if (floorEntry != null) {
                newOrder.prev = floorEntry.getValue();
                floorEntry.getValue().next = newOrder;
            }
            if (ceilEntry != null) {
                newOrder.next = ceilEntry.getValue();
                ceilEntry.getValue().prev = newOrder;
            }

            newOrder.addNewPlayer(player, volume);
            orderMap.put(price, newOrder);

        } else {
            orderMap.get(price).addNewPlayer(player, volume);
        }

        totalVolume += volume;


    }

    public void cancelOrder(ServerPlayerEntity player, double price, int volume) {

        Order o = orderMap.get(price);

        var it = o.players.iterator();
        Pair<ServerPlayerEntity, Integer> current;

        int totalCancelledVolume = 0;

        while (it.hasNext()) {
            current = it.next();

            if (current.A.equals(player)) {
                if (current.B > volume) {
                    current.B -= volume;
                    totalCancelledVolume += volume;
                    break;
                } else {
                    it.remove();
                    volume -= current.B;
                    totalCancelledVolume += current.B;
                }
            }

        }

        if (o.players.isEmpty()) {
            removeNodeFromLinkedList(price, o);
        }
        totalVolume -= totalCancelledVolume;

    }


    // true means we have found a match and the orders have been removed.
    // false means that we have not found a match.
    public boolean matchOrder(double price, int volume) {

        var order = orderMap.get(price);
        if (order == null || order.totalVolume < volume) return false;

        do {

            Pair<ServerPlayerEntity, Integer> mostRecentOrder = order.players.peek();

            if (mostRecentOrder == null) throw new RuntimeException("totalVolume not updated correctly.");

            int mostRecentVolume = mostRecentOrder.B;
            if (mostRecentVolume <= volume) {
                var headPlayer = order.players.poll();
                order.totalVolume -= headPlayer.B;
                this.totalVolume -= headPlayer.B;
                // todo: give player money or items, depending on side
            } else {
                order.players.peek().B = mostRecentVolume - volume;
                order.totalVolume -= volume;
                this.totalVolume -= volume;
                // todo: give player money or items, depending on side
            }
            volume -= mostRecentVolume;

        } while (volume > 0);

        if (order.players.isEmpty()) {

            removeNodeFromLinkedList(price, order);

        }

        return true;

    }

    private void removeNodeFromLinkedList(double price, Order order) {
        orderMap.remove(price);

        var next = order.next;
        var prev = order.prev;

        if (next != null) {
            next.prev = prev;
        }
        if (prev != null) {
            prev.next = next;
        }

        if (order == head) {
            head = order.prev;
        }
        if (order == tail) {
            tail = order.next;
        }
    }

    public Pair<Integer, Integer> marketOrder(int volume) {
        // returns the total money earned

        Order current;
        current = this.head;
        int initialVolume = volume;
        int profit = 0;


        MonoMod.LOGGER.info("market order lets go");
        if (current != null) {
            MonoMod.LOGGER.info(current.price + "");
        } else {
            MonoMod.LOGGER.info("no head lol");
        }

        while (volume > 0 && totalVolume > 0 && current != null) {


            MonoMod.LOGGER.info("" + current.price);

            Iterator<Pair<ServerPlayerEntity, Integer>> playerIterator = current.players.iterator();

            while (volume > 0 && playerIterator.hasNext()) {


                Pair<ServerPlayerEntity, Integer> player = playerIterator.next();


                MonoMod.LOGGER.info(player.A + ", " + player.B);

                int playerVolume = player.B;

                int subtractedVolume = Math.min(playerVolume, volume);
                player.B -= subtractedVolume;
                current.totalVolume -= subtractedVolume;
                totalVolume -= subtractedVolume;
                volume -= subtractedVolume;

                profit += subtractedVolume * (int) current.price;

                if (player.B == 0) {
                    playerIterator.remove();
                }
            }

            if (current.players.isEmpty()) {
                removeNodeFromLinkedList(current.price, current);
            }

            current = current.prev;

        }

        return new Pair<>(initialVolume - volume, profit);

    }

    public boolean limitOrder(int volume, int price) {
        return true;
    }

}

class Order {

    double price;
    // the people who placed the order and the volume, ordered by when the orders were placed
    LinkedList<Pair<ServerPlayerEntity, Integer>> players = new LinkedList<>();
    int totalVolume = 0;
    Order next = null;
    Order prev = null;

    public Order(double price) {
        this.price = price;
    }

    public void addNewPlayer(ServerPlayerEntity player, Integer volume) {
        this.players.add(new Pair<>(player, volume));
        this.totalVolume += volume;
    }

}