package net.zhangrui.monomod.markets;


import net.zhangrui.monomod.MonoMod;
import net.zhangrui.monomod.lib.Queue.QueueNode;
import net.zhangrui.monomod.lib.Queue.Queue;
import net.zhangrui.monomod.lib.Triple;

import java.util.*;


public class MatchingEngine {

    String item;
    Comparator<Double> bidComparator = Comparator.reverseOrder();
    Comparator<Double> askComparator = Comparator.naturalOrder();
    TreeMap<Double, Queue<Order>> bids = new TreeMap<>(bidComparator);
    TreeMap<Double, Queue<Order>> asks = new TreeMap<>(askComparator);
    HashMap<Long, Triple<Double, OrderType, QueueNode<Order>>> orders = new HashMap<>();
    long id = 0;


    public MatchingEngine(String item) {
        this.item = item;
    }


    public boolean cancelOrder(long orderId) {
        Triple<Double, OrderType, QueueNode<Order>> triplet = orders.get(orderId);
        QueueNode<Order> orderNode = triplet.C;
        OrderType ot = triplet.B;
        double price = triplet.A;

        if (orderNode == null) {
            return false;
        } else {

            TreeMap<Double, Queue<Order>> book = switch (ot) {
                case ASK -> asks;
                default -> bids;
            };

            Queue<Order> q = book.get(price);
            q.remove(orderNode);

            if (q.isEmpty()) {
                book.remove(price);
            }

            return true;

        }

    }

    // returns id > 0 if we placed an order;
    public long limitOrder(OrderType ot, String userId, double volume, double price) {

        TreeMap<Double, Queue<Order>> book;
        Comparator<Double> comparator;

        switch (ot) {
            case ASK:
                book = bids;
                comparator = bidComparator;
                break;
            case BID:
            default:
                book = asks;
                comparator = askComparator;
                break;
        }

        Iterator<Map.Entry<Double, Queue<Order>>> it = book.entrySet().iterator();
        while (volume > 0 && it.hasNext()) {

            Map.Entry<Double, Queue<Order>> entry = it.next();
            double orderPrice = entry.getKey();

            if (comparator.compare(orderPrice, price) > 0) { break; }

            Queue<Order> orders = entry.getValue();

            while (!orders.isEmpty()) {
                Order o = orders.peek().val;

                double removedVolume = Math.min(o.volume, volume);
                o.volume -= removedVolume;
                volume -= removedVolume;
                MonoMod.LOGGER.info("{}: {} matches with {} on price {} and volume {}", this.item, userId, o.userId, orderPrice, removedVolume);

                if (o.volume == 0) {
                    orders.remove();
                } else {
                    break;
                }

            }

            if (orders.isEmpty()) {
                it.remove();
            }

        }

        if (volume > 0) {

            book = switch (ot) {
                case ASK -> asks;
                default -> bids;
            };

            book.putIfAbsent(price, new Queue<>());

            Order newOrder = new Order(userId, volume);
            QueueNode<Order> node = new QueueNode<>(newOrder);

            book.get(price).add(node);

            MonoMod.LOGGER.info("{}: {} places order of price {} and volume {} on side {}", this.item, userId, price, volume, ot.value);
            id++;
            orders.put(id, new Triple<>(price, ot, node));
        }

        return id;

    }


    public void marketOrder(OrderType ot, String userId, double volume) {

        TreeMap<Double, Queue<Order>> book = switch (ot) {
            case ASK -> bids;
            default -> asks;
        };


        Iterator<Map.Entry<Double, Queue<Order>>> it = book.entrySet().iterator();
        while (volume > 0 && it.hasNext()) {

            Map.Entry<Double, Queue<Order>> entry = it.next();
            Queue<Order> orders = entry.getValue();

            while (!orders.isEmpty()) {
                Order o = orders.peek().val;

                double removedVolume = Math.min(o.volume, volume);
                o.volume -= removedVolume;
                volume -= removedVolume;
                MonoMod.LOGGER.info("{}: {} matches with {} on price {} and volume {}", this.item, userId, o.userId, entry.getKey(), removedVolume);


                if (o.volume == 0) {
                    orders.remove();
                } else {
                    break;
                }

            }

            if (orders.isEmpty()) {
                it.remove();
            }

        }

    }

    public void debug(OrderType ot) {

        TreeMap<Double, Queue<Order>> book = switch (ot) {
            case ASK -> asks;
            default -> bids;
        };

        Iterator<Map.Entry<Double, Queue<Order>>> it = book.entrySet().iterator();

        StringBuilder sb = new StringBuilder();
        sb.append(ot).append(": ");
        while (it.hasNext()) {
            Map.Entry<Double, Queue<Order>> entry = it.next();
            Queue<Order> q = entry.getValue();
            QueueNode<Order> curr = q.peek();

            sb.append("Price: ").append(entry.getKey());
            while (curr != null) {
                sb.append(" - ").append(curr.val.userId).append(": ").append(curr.val.volume);
                curr = curr.next;
            }
            MonoMod.LOGGER.info(sb.toString());

        }


    }


}
