package Razier.country.entity.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class ChaseItemEntityGoal extends MoveToTargetPosGoal {

    public ChaseItemEntityGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean isTargetPos(WorldView world, BlockPos pos) {
        //if(world.)
        this.mob.getServer().
        return false;
    }
}
