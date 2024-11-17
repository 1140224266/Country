package Razier.country.block;

import Razier.country.Country;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockRegister {
    public static void registerBlock(String name, Block block, Item item){
        Registry.register(Registries.BLOCK, new Identifier(Country.MOD_ID,name), block);
        Registry.register(Registries.ITEM,new Identifier(Country.MOD_ID,name),item);

    }
    /*public static void registerBlockItem(String name , BlockItem item){
        Registry.register(Registries.ITEM)
    }*/
    public static void register(){
        registerBlock("one_level_compression_iron_block",ModBlocksAndItems.IRON_BLOCK.ONE_LEVEL_COMPRESSION_IRON_BLOCK,ModBlocksAndItems.IRON_BLOCK.ONE_LEVEL_COMPRESSION_IRON_BLOCK_ITEM);
        registerBlock("two_level_compression_iron_block",ModBlocksAndItems.IRON_BLOCK.TWO_LEVEL_COMPRESSION_IRON_BLOCK,ModBlocksAndItems.IRON_BLOCK.TWO_LEVEL_COMPRESSION_IRON_BLOCK_ITEM);

    }
}
