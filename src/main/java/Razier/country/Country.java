package Razier.country;

import Razier.country.block.BlockRegister;
import Razier.country.entity.Farmer;
import Razier.country.entity.KillerEntity;
import Razier.country.entity.ModEntities;
import Razier.country.entity.TEST;
import Razier.country.item.Register;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Country implements ModInitializer {
    public static final long time = System.currentTimeMillis();
    public static final String MOD_ID = "country";
    public static final String MOD_NAME = "Country";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    /*
     * 使用“entitytesting:cube”作为ID注册我们的实体
     *
     * 这个实体注册在了 SpawnGroup#CREATURE 类别下，大多数的动物和友好或中立的生物都注册在这个类别下。
     * 它有一个 0.75 × 0.75（或12个像素宽，即一个方块的3/4）大小的碰撞体积。
     */
    /*public static final EntityType<Farmer> FARMER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("razier", "farmer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Farmer::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );*/

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(ModEntities.FARMER,Farmer.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.KILLER, KillerEntity.createKillerAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TEST, TEST.creatTestAttributes());
        Register.Register();
        BlockRegister.register();
        //添加到物品组

    }
}
