package net.zhangrui.monomod.marketsv0.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.zhangrui.monomod.MonoMod;
import net.zhangrui.monomod.marketsv0.lib.Market;
import net.zhangrui.monomod.marketsv0.lib.MatchingEngine;
import net.zhangrui.monomod.marketsv0.lib.SideArgument;

// todo: for later, I want to introduce API keys...
public class DebugCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        String side = StringArgumentType.getString(context, "side");

        Market m = MatchingEngine.getMarket(item);

        switch (side) {
            case SideArgument.BID_SIDE:
                m.debugBids();
                break;
            case SideArgument.ASK_SIDE:
                m.debugAsks();
                break;
            case "id":
                var player = context.getSource().getPlayer();
                if (player == null) break;
                MonoMod.LOGGER.info(String.valueOf(Item.getRawId(player.getMainHandStack().getItem())));
                break;
            default:
                throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), () -> "Invalid Side Dumbahh");
        }


        return 1;
    }

}
