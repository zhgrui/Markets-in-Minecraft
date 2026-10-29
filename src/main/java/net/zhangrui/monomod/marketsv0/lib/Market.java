package net.zhangrui.monomod.marketsv0.lib;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.zhangrui.monomod.MonoMod;
import net.zhangrui.monomod.markets.MarketsMod;

public class Market {

    Item item;
    Orders bids = new Orders(OrderType.BID);
    Orders asks = new Orders(OrderType.ASK);

    public Market(Item item) {
        this.item = item;
    }

    public void debugBids() {
        var current = bids.tail;

        while (current != null) {
            MonoMod.LOGGER.info("price: " + current.price + ", volume: " + current.totalVolume);
            current = current.next;
        }

    }

    public void debugAsks() {
        var current = asks.tail;

        while (current != null) {
            MonoMod.LOGGER.info("price: " + current.price + ", volume: " + current.totalVolume);
            current = current.next;
        }
    }

    public void sendSimpleBid(ServerPlayerEntity player, int price, int volume) {

        var count = player.getInventory().count(MarketsMod.currency);

        if (count < price * volume) {
            player.sendMessage(Text.literal("Bro you don't have enough diamonds."));
            return;
        }

        if (asks.matchOrder(price, volume)) {

            player.getInventory().remove(t -> t.getItem().equals(MarketsMod.currency), price * volume, player.playerScreenHandler.getCraftingInput());
            player.giveItemStack(new ItemStack(item, volume));

        } else {
            bids.placeOrder(player, price, volume);
        }

    }

    public void sendSimpleAsk(ServerPlayerEntity player, int price, int volume) {

        var count = player.getInventory().count(this.item);

        if (count < volume) {
            player.sendMessage(Text.literal("Bro you do not have this much of this item."));
            return;
        }

        if (bids.matchOrder(price, volume)) {

            player.getInventory().remove(t -> t.getItem().equals(this.item), volume, player.playerScreenHandler.getCraftingInput());
            player.giveItemStack(new ItemStack(MarketsMod.currency, price * volume));

        } else {
            asks.placeOrder(player, price, volume);
        }

    }


    public void sendMarketBid(ServerPlayerEntity player, int volume) {

        var count = player.getInventory().count(MarketsMod.currency);

        Pair<Integer, Integer> p = asks.marketOrder(volume);
        int boughtVolume = p.A;
        int price = p.B;

        player.getInventory().remove(t -> t.getItem().equals(MarketsMod.currency), price, player.playerScreenHandler.getCraftingInput());
        player.giveItemStack(new ItemStack(item, boughtVolume));

    }

    public void sendMarketAsk(ServerPlayerEntity player, int volume) {

        var count = player.getInventory().count(this.item);

        if (count < volume) {
            player.sendMessage(Text.literal("Bro you don't have enough of this item."));
            return;
        }

        Pair<Integer, Integer> v = bids.marketOrder(volume);
        int profit = v.B;
        int soldVolume = v.A;

        player.getInventory().remove(t -> t.getItem().equals(this.item), soldVolume, player.playerScreenHandler.getCraftingInput());
        player.giveItemStack(new ItemStack(MarketsMod.currency, profit));

    }

    public void sendLimitBid() {

    }

    public void sendLimitAsk() {

    }


}