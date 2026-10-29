package net.zhangrui.monomod.marketsv0.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.zhangrui.monomod.marketsv0.lib.MatchingEngine;

public class CreateCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ItemStackArgument itemArgument = ItemStackArgumentType.getItemStackArgument(context, "item");
        context.getSource().sendFeedback(Text.literal(itemArgument.asString()), false);

//        DataResult<JsonElement> result = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, itemArgument.getItem().getDefaultStack());
//
//        if (result.result().isEmpty()) return 0;
//
//        JsonElement json = result.result().get();
//        String stringified = json.toString();

        Item item = itemArgument.getItem();

        MatchingEngine.addMarket(item);

        return 1;

    }

}
