package Razier.country.entity.goal;

import Razier.country.entity.KillerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ChaseItemEntityGoal extends MoveToTargetPosGoal {

    public int SEARCH_RANGE;
    public ItemEntity targetItem;
    public int cooldown=0;
    
    public ChaseItemEntityGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
        this.SEARCH_RANGE = range;
    }


    @Override
    protected void startMovingToTarget() {
        Entity Mob = this.mob.getWorld().getPlayers().get(0);
        this.targetPos=this.targetItem.getBlockPos();
        this.mob
                .getNavigation()
                .startMovingTo((double)(this.targetPos.getX()+0.5), (double)(this.targetPos.getY() ), (double)(this.targetPos.getZ())+0.5, this.speed);
                //.startMovingTo(Mob.getX(),Mob.getY(),Mob.getZ(),this.speed);
    }


    @Override
    public boolean canStart() {
        // 创建一个显式的 Predicate 而不是使用方法引用
        Predicate<ItemEntity> validator = (item) -> {
            /*if (item == null || !item.isAlive()) {
                return false;
            }

            if (this.mob instanceof KillerEntity) {
                return ((KillerEntity) mob).canGather(item.getStack());
            }

            return !item.getStack().isEmpty() && !item.cannotPickup();*/
            return true;
        };

        // 获取所有符合条件的物品
        List<ItemEntity> nearbyItems = mob.getWorld().getEntitiesByClass(
                ItemEntity.class,
                Box.from(mob.getPos()).expand(SEARCH_RANGE),
                validator
        );

        // 找到最近的物品
        if (!nearbyItems.isEmpty()&&cooldown--<=0) {
            targetItem = nearbyItems.get(0);
            cooldown=500;
            double minDistance = mob.squaredDistanceTo(targetItem);

            for (ItemEntity item : nearbyItems) {
                double distance = mob.squaredDistanceTo(item);
                if (distance < minDistance&& Objects.requireNonNull(this.targetItem.getRemovalReason()).shouldSave()) {
                    minDistance = distance;
                    targetItem = item;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void tick(){
        // MethodLogger.add("tick",time,"TTTTT" + "   cooldown:" + cooldown + "   movingtime:" + movingTime);
        //super.tick();
       /* {
            cout(String.valueOf(x0));
            cout(String.valueOf(y0));
            cout(String.valueOf(z0));
            cout(String.valueOf(x1));
            cout(String.valueOf(y1));
            cout(String.valueOf(z1));

            cout("\n");
        }*/
    this.startMovingToTarget();

    }


    @Override
    public boolean isTargetPos(WorldView world, BlockPos pos) {

        // 创建一个显式的 Predicate 而不是使用方法引用
        Predicate<ItemEntity> validator = (item) -> {
            /*if (item == null || !item.isAlive()) {
                return false;
            }

            if (this.mob instanceof KillerEntity) {
                return ((KillerEntity) mob).canGather(item.getStack());
            }

            return !item.getStack().isEmpty() && !item.cannotPickup();*/
            return true;
        };

        // 获取所有符合条件的物品
        List<ItemEntity> nearbyItems = mob.getWorld().getEntitiesByClass(
                ItemEntity.class,
                Box.from(mob.getPos()).expand(SEARCH_RANGE),
                validator
        );

        // 找到最近的物品
        if (!nearbyItems.isEmpty()) {
            targetItem = nearbyItems.get(0);
            double minDistance = mob.squaredDistanceTo(targetItem);

            for (ItemEntity item : nearbyItems) {
                double distance = mob.squaredDistanceTo(item);
                if (distance < minDistance&& Objects.requireNonNull(this.targetItem.getRemovalReason()).shouldSave()) {
                    minDistance = distance;
                    targetItem = item;
                }
            }
            return targetItem != null && pos.equals(targetItem.getBlockPos());
        }
        return targetItem != null && pos.equals(targetItem.getBlockPos());
    }
}
