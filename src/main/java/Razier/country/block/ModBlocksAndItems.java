package Razier.country.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class ModBlocksAndItems {
    static class IRON_BLOCK{
        public static final Block ONE_LEVEL_COMPRESSION_IRON_BLOCK = new Block(FabricBlockSettings.create().strength(4.0f));
        public static final Item ONE_LEVEL_COMPRESSION_IRON_BLOCK_ITEM = new BlockItem(ONE_LEVEL_COMPRESSION_IRON_BLOCK,new Item.Settings().rarity(Rarity.COMMON));
        public static final Block TWO_LEVEL_COMPRESSION_IRON_BLOCK = new Block(FabricBlockSettings.create().strength(4.0f));
        public static final Item TWO_LEVEL_COMPRESSION_IRON_BLOCK_ITEM = new BlockItem(TWO_LEVEL_COMPRESSION_IRON_BLOCK,new Item.Settings().rarity(Rarity.COMMON));

    }
}
