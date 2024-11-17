package Razier.country.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/*
 * 我们创建的实体继承自 PathAwareEntity, 它继承自 MobEntity, 而 MobEntity 继承自 LivingEntity.
 *
 * LivingEntity 拥有生命值，并且可以造成伤害。
 * MobEntity 具有AI逻辑和移动控制。
 * PathAwareEntity 提供额外的寻路系统，很多AI任务都需要用到寻路。
 */
public class Farmer extends PathAwareEntity {

    public Farmer(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    //设置目标
    @Override
    protected void initGoals() {
        this.goalSelector.add(0,new SwimGoal(this)); //不至于淹死
    }

    public static DefaultAttributeContainer.Builder creatFarmerAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,25)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.3f)
                .add(EntityAttributes.GENERIC_ARMOR,0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,3);
    }


}
