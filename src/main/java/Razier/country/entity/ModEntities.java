package Razier.country.entity;

import Razier.country.Country;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public  static final EntityType<Farmer> FARMER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Country.MOD_ID,"farmer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,Farmer::new)
                    .dimensions(EntityDimensions.fixed(0.6f,1.8f)).build());
    public  static final EntityType<KillerEntity> KILLER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Country.MOD_ID,"killer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,KillerEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f,1.8f)).build());
    public  static final EntityType<TEST> TEST = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Country.MOD_ID,"test"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,TEST::new)
                    .dimensions(EntityDimensions.fixed(0.6f,1.8f)).build());
}
