package net.zhangrui.monomod.markets.items;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;

public final class Items {

    private Items() {}

    public static <T extends Item> T register(String path, T item) {
        // For versions below 1.21, please replace ''Identifier.of'' with ''new Identifier''
        return Registry.register(Registry.ITEM, new Identifier("tutorial", path), item);
    }

    public static void initialize() {



    }
}