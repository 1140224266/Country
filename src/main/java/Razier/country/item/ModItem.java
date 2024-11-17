package Razier.country.item;

import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;

import static Razier.country.entity.ModEntities.FARMER;
import static Razier.country.entity.ModEntities.KILLER;

public class ModItem {

    public static final Item KILLER_ENTITY_SPAWN_EGG =  new SpawnEggItem(KILLER,0xc4c4c4,0xadadad,new Item.Settings());
    public static final Item FARMER_SPAWN_EGG =  new SpawnEggItem(FARMER,0xc4c4c4,0xadadad,new Item.Settings());
}
