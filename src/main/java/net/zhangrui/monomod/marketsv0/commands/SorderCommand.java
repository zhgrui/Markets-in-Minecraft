package net.zhangrui.monomod.marketsv0.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.zhangrui.monomod.marketsv0.lib.Market;
import net.zhangrui.monomod.marketsv0.lib.MatchingEngine;
import net.zhangrui.monomod.marketsv0.lib.SideArgument;

public class SorderCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        String side = StringArgumentType.getString(context, "side");
        int price = IntegerArgumentType.getInteger(context, "price");
        int quantity = IntegerArgumentType.getInteger(context, "quantity");

        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        switch (side) {
            case SideArgument.BID_SIDE:
                return placeBid(player, item, price, quantity);
            case SideArgument.ASK_SIDE:
                return placeAsk(player, item, price, quantity);
            default:
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), () -> "Invalid Side Dumbahh");
        }

    }

    private int placeAsk(ServerPlayerEntity player, Item item, int price, int quantity) {

//        int count = player.getInventory().count(item);
//
//        if (count < quantity) {
//            player.sendMessage(Text.literal("Could not place ask, because you don't actually have that much stuff."));
//            return 0;
//        };
//
//        PlayerInventory playerInventory = player.getInventory();
//
//        playerInventory.remove(itemStack -> itemStack.getItem().equals(item), quantity, playerInventory);


        player.sendMessage(Text.literal(player.getEntityName() + " places an ask on item " + item.toString() + " of volume " + quantity + " and price " + price));
        Market market = MatchingEngine.getMarket(item);
        market.sendSimpleAsk(player, price, quantity);

        return 1;

    }

    private int placeBid(ServerPlayerEntity player, Item item, int price, int quantity) {

        player.sendMessage(Text.literal(player.getEntityName() + " places a bid on item " + item.toString() + " of volume " + quantity + " and price " + price));
        Market market = MatchingEngine.getMarket(item);
        market.sendSimpleBid(player, price, quantity);

        return 1;
    }

}
