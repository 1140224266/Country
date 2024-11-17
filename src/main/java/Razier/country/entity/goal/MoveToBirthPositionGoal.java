package Razier.country.entity.goal;

import Razier.country.MethodLogger;
import Razier.country.entity.KillerEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MoveToBirthPositionGoal<T extends KillerEntity> extends MoveToTargetPosGoal {

    private double x0 ;
    private double y0 ;
    private double z0 ;
    private double x1;
    private double y1;
    private double z1;
    private int movingTime= 0;
    private T mob;
    public int sum =0;
    public MoveToBirthPositionGoal(int x, int y, int z, T mob, double speed, int range, int maxYDifference) {
        super((PathAwareEntity) mob, speed, range, maxYDifference);
        this.mob = mob;
    }

    /*void cout(String s)
    {
        if (sum%20==0)
            System.out.println(s);
    }*/
    @Override
    public boolean canStart(){

        x1=mob.birthPositionX;
        y1=mob.birthPositionY;
        z1=mob.birthPositionZ;
        x0=mob.getX();
        y0=mob.getY();
        z0=mob.getZ();
        sum++;
        double distance = sqrt(pow(x1-x0,2)+pow(y1-y0,2)+pow(z1-z0,2));
        if (distance>mob.defenceDistance && cooldown >2000)
        {
            cooldown=0;
            //cout(String.valueOf(movingTime));
            //MethodLogger.add("canStart",time,"TTTTTDC  distance:" + distance + "   cooldown:" + cooldown + "   movingtime:" + movingTime);
            return true;
        }
        else if (movingTime>2&&distance<=mob.defenceDistance){

            movingTime=movingTime-3;
            //MethodLogger.add("canStart",time,"TTTTTMovingtime  distance:" + distance + "   cooldown:" + cooldown + "   movingtime:" + movingTime);

            return true;
        }
        else {
            cooldown++;
        }

        //MethodLogger.add("canStart",time,"FFFFF  distance:" + distance + "   cooldown:" + cooldown + "   movingtime:" + movingTime);
        return false;
    }

    @Override
    public void start(){

        //MethodLogger.add("start",time);
    }

    @Override
    public void stop() {
        //MethodLogger.add("stop",time);
        super.stop();
    }
    public int time=0;
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

        this.mob.getNavigation().startMovingTo(x1,y1,z1,1.2);
        if (movingTime<60)
            movingTime++;
        time ++;

    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return false;
    }
}
