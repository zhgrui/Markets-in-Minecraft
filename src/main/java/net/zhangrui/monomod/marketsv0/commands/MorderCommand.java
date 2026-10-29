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

public class MorderCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        String side = StringArgumentType.getString(context, "side");
        int quantity = IntegerArgumentType.getInteger(context, "quantity");

        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        switch (side) {
            case SideArgument.BID_SIDE:
                return placeBid(player, item, quantity);
            case SideArgument.ASK_SIDE:
                return placeAsk(player, item, quantity);
            default:
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), () -> "Invalid Side Dumbahh");
        }

    }

    private int placeAsk(ServerPlayerEntity player, Item item, int quantity) {

        player.sendMessage(Text.literal(player.getEntityName() + " places a market ask on item " + item.toString() + " of volume " + quantity));
        Market market = MatchingEngine.getMarket(item);
        market.sendMarketAsk(player, quantity);

        return 1;

    }

    private int placeBid(ServerPlayerEntity player, Item item, int quantity) {

        player.sendMessage(Text.literal(player.getEntityName() + " places a market bid on item " + item.toString() + " of volume " + quantity));
        Market market = MatchingEngine.getMarket(item);
        market.sendMarketBid(player, quantity);

        return 1;
    }

}
