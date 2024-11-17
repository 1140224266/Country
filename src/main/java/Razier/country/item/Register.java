package Razier.country.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static Razier.country.Country.*;
import static Razier.country.item.ModItem.FARMER_SPAWN_EGG;
import static Razier.country.item.ModItem.KILLER_ENTITY_SPAWN_EGG;

public class Register {
    public static void Register(){

        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"killer_spawn_egg"),KILLER_ENTITY_SPAWN_EGG);
        Registry.register(Registries.ITEM,new Identifier(MOD_ID,"farmer_spawn_egg"),FARMER_SPAWN_EGG);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(KILLER_ENTITY_SPAWN_EGG);
            content.add(FARMER_SPAWN_EGG);
        });
    }
}
