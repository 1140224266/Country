package Razier.country.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.World;

public class TEST extends PathAwareEntity implements InventoryOwner {
    public SimpleInventory inventory = new SimpleInventory(27);
    protected TEST(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder creatTestAttributes() {
        return MobEntity.createMobAttributes()
                // 基础属性
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)          // 增加生命值到40
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)     // 提高移动速度
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)        // 提高基础攻击力
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)                // 增加护甲值
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0)      // 添加护甲韧性
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6) // 提高击退抗性
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)        // 增加追踪范围
                // 额外属性
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0);     // 添加击退力度
    }

    @Override
    protected void loot(ItemEntity item) {
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }
}
