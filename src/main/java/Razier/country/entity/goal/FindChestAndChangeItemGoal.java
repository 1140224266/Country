package Razier.country.entity.goal;

import Razier.country.entity.KillerEntity;
import Razier.country.mixin.ChestBlockEntityMixin;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import javax.swing.*;
import java.util.Arrays;

public class FindChestAndChangeItemGoal extends MoveToTargetPosGoal {

    private int cooldown=0;
    public KillerEntity entity;
    public FindChestAndChangeItemGoal(KillerEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
        this.entity=mob;
    }


    @Override
    public boolean canStart(){
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        } else {
            this.cooldown = 10;
            return this.findTargetPos();
        }
    }

    @Override
    public void start(){
        super.start();
    }

    @Override
    public void tick(){
        super.tick();
        if (getChestPos()!=null){
            BlockPos chestPos = getChestPos();
            DefaultedList<ItemStack> itemStacks = getItems(chestPos);
            //getItemsFromChest(chestPos,itemStacks);
            putItemsInChest(chestPos,itemStacks);
        }
    }
    public int search(Object a , Object[] b){
        int sum=0;
        for (Object i : b){

            if (a==i)
                return sum;
        }
        return -1;
    }
    public void putItemsInChest(BlockPos chestPos,DefaultedList<ItemStack> itemStacks){
        int sum_a=0;
        for (ItemStack a : itemStacks)
        {
            DefaultedList<ItemStack> inventory = this.entity.inventory.stacks;

            Object[] temp = itemStacks.toArray();
            int sum = 0;
            ItemStack[] items = new ItemStack[itemStacks.size()];
            for (Object c : temp){
                items[sum++] = (ItemStack)c;
            }
            int sum_b=0;
            for (ItemStack b : inventory){
                ItemStack itemStack = new ItemStack(b.getItem(),b.getCount(),b.getNbt());
                sum = 0;
                /*if (a.getItem()==b.getItem()&&a.getCount()+b.getCount()<=64&&a!=ItemStack.EMPTY){//合并
                    items[sum_a].setCount(a.getCount()+b.getCount());
                    this.entity.getInventory().removeItem(a.getItem(),b.getCount());
                    //temp.remove(itemStacks.indexOf(a));
                }*/
                /*if(a.getItem()==b.getItem()&&a.getCount()+b.getCount()>64&&a!=ItemStack.EMPTY&&a.getCount()!=64){
                    this.entity.getInventory().removeItem(a.getItem(),64-a.getCount());
                    items[sum_a].setCount(64);
                }*/
                if (a.getItem()== Items.AIR){//主动放入
                    if (!KillerEntity.FOOD_ITEMS.contains(b.getItem())&&b!=ItemStack.EMPTY&&!KillerEntity.FIGHT_ITEMS.contains(b.getItem()))
                    {
                        items[sum_a]= itemStack;
                        //this.entity.getInventory().removeStack(sum_b,b.getCount());
                    }
                    else if (KillerEntity.FIGHT_ITEMS.contains(b.getItem())&&this.entity.getMainHandStack()!=b)
                    {
                        items[sum_a]= itemStack;//不能用b，因为，当b被修改，items[]也会改变
                        this.entity.getInventory().removeStack(sum_b,b.getCount());
                    }
                }
                DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(chestPos);
                if (chestBlockEntity != null) {
                    ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                }
                sum_b++;
            }
            sum_a++;
        }
    }

    public void getItemsFromChest(BlockPos chestPos,DefaultedList<ItemStack> itemStacks){
        for (ItemStack a : itemStacks){
            Item item = a.getItem();
            if (KillerEntity.FIGHT_ITEMS.contains(item)){
                Item preferItem = KillerEntity.preferFightItem(this.entity.getMainHandStack().getItem(),item);
                if (preferItem==item){
                    //getItems(chestPos).remove(a);不许更改chestBlockEntity的inventory为private
                    ChestBlockEntity chestBlockEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(chestPos);
                    //itemStacks.remove(a);
                    //DefaultedList<ItemStack> cc = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    Object[] temp = itemStacks.toArray();
                    int sum = 0;
                    ItemStack[] items = new ItemStack[itemStacks.size()];
                    for (Object b : temp){
                        items[sum++] = (ItemStack)b;
                    }
                    sum = 0;
                    for (Object b :temp){
                        if (items[sum].getItem()==preferItem){
                            items[sum]=ItemStack.EMPTY;
                            break;
                        }
                        sum++;
                    }
                    DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                    //temp.remove(itemStacks.indexOf(a));
                    if (chestBlockEntity != null) {
                        ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                    }
                    this.entity.getInventory().addStack(a);
                }
            }
        }
    }

    public BlockPos getChestPos(){
        BlockPos pos = this.entity.getBlockPos();
        WorldView world = this.entity.getWorld();
        int x = this.targetPos.getX();
        int y = this.targetPos.getY();
        int z = this.targetPos.getZ();
        for (int i = x-3 ; i <= x+3 ; i++)
        {
            for (int j = y-1 ; j <= y+3 ; j++){
                for (int k = z- 3 ; k <= z+3 ; k++){
                    BlockPos blockPos = new BlockPos(i,j,k);
                    if (world.getBlockState(blockPos).isOf(Blocks.CHEST))

                        return blockPos;
                }
            }
        }

        return null;
    }
    public DefaultedList<ItemStack> getItems(BlockPos pos){
        WorldView world = this.entity.getWorld();
        if (!world.getBlockState(pos).isOf(Blocks.CHEST))
            return null;

        ChestBlockEntity chestEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(pos);
        if (chestEntity != null) {
            return ((ChestBlockEntityMixin) chestEntity).getInventory();
        }
        return null;
    }

    @Override
    public boolean shouldContinue(){
        return false;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {

        return world.getBlockState(pos).isOf(Blocks.CHEST)||world.getBlockState(pos.up(1)).isOf(Blocks.CHEST)||world.getBlockState(pos.down(1)).isOf(Blocks.CHEST)
        ||world.getBlockState(pos.east(1)).isOf(Blocks.CHEST)||world.getBlockState(pos.west(1)).isOf(Blocks.CHEST)||world.getBlockState(pos.north(1)).isOf(Blocks.CHEST)||world.getBlockState(pos.south(1)).isOf(Blocks.CHEST);
    }


}
