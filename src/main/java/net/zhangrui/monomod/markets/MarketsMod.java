package net.zhangrui.monomod.markets;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.zhangrui.monomod.markets.blocks.Blocks;
import net.zhangrui.monomod.markets.items.Items;
import net.zhangrui.monomod.marketsv0.commands.*;

public class MarketsMod {

    public static String MARKET_API_URL = "http://127.0.0.1:8000";
    public static Item currency = Item.byRawId(722);

    public void initializeCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(
                    CommandManager.literal("markets")
                        .then(CommandManager.literal("list")
                            .executes(new ListCommand()))
                        .then(CommandManager.literal("create")
                            .then(CommandManager.argument("item", new ItemStackArgumentType(registryAccess))
                            .executes(new CreateCommand())))
                        .then(CommandManager.literal("sorder")
                            .then(CommandManager.argument("item", new ItemStackArgumentType(registryAccess))
                            .then(CommandManager.argument("side", StringArgumentType.string())
                            .then(CommandManager.argument("price", IntegerArgumentType.integer(0)) // in diamonds
                            .then(CommandManager.argument("quantity", IntegerArgumentType.integer(1))
                            .executes(new SorderCommand()))))))
                        .then(CommandManager.literal("morder")
                            .then(CommandManager.argument("item", new ItemStackArgumentType(registryAccess))
                            .then(CommandManager.argument("side", StringArgumentType.string())
                            .then(CommandManager.argument("quantity", IntegerArgumentType.integer(1))
                            .executes(new MorderCommand())))))
                        .then(CommandManager.literal("debug")
                            .then(CommandManager.argument("side", StringArgumentType.string())
                            .then(CommandManager.argument("item", new ItemStackArgumentType(registryAccess))
                            .executes(new DebugCommand()))))

            );
        });

    }

    public void initialize() {
        initializeCommands();
        Items.initialize();
        Blocks.initialize();

    }

}
