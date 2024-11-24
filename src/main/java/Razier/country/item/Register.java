package Razier.country.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static Razier.country.Country.*;
import static Razier.country.item.ModItem.*;

public class Register {
    public static void Register(){

        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"killer_spawn_egg"),KILLER_ENTITY_SPAWN_EGG);
        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"farmer_spawn_egg"),FARMER_SPAWN_EGG);

        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"test_spawn_egg"),TEST_SPAWN_EGG);
        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"fragments_of_life"),FRAGMENTS_OF_LIFE);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(KILLER_ENTITY_SPAWN_EGG);
            content.add(FARMER_SPAWN_EGG);
            content.add(TEST_SPAWN_EGG);
        });
    }
}
