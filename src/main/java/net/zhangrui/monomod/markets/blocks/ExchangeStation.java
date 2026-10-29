package net.zhangrui.monomod.markets.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExchangeStation extends Block {

    public ExchangeStation() {
        super(AbstractBlock.Settings.of((Material.METAL)));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Hello, world!"), false);
        }

        return ActionResult.SUCCESS;
    }

}
