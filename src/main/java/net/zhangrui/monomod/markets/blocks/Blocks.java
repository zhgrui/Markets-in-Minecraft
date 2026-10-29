package net.zhangrui.monomod.markets.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Blocks {

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registry.BLOCK, new Identifier("monomod", path), block);
        Registry.register(Registry.ITEM, new Identifier("monomod", path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {

        register("exchange_station", new ExchangeStation());

    }
}