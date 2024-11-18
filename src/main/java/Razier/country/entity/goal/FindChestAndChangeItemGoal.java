package Razier.country.entity.goal;

import Razier.country.entity.KillerEntity;
import Razier.country.mixin.ChestBlockEntityMixin;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.Set;

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
            getItemsFromChest(chestPos,itemStacks);
            //putItemsInChest(chestPos,itemStacks);
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

        DefaultedList<ItemStack> inventory = this.entity.inventory.stacks;  //提取背包内的物品列表

        Object[] temp = itemStacks.toArray();     //将列表转化为Object数组
        int sum = 0;
        ItemStack[] items = new ItemStack[itemStacks.size()];
        for (Object c : temp){
            items[sum++] = (ItemStack)c;    //将object数组转化为ItemStack数组
        }

        for (ItemStack a : itemStacks)//遍历箱子物品
        {
            int sum_b=0;
            for (ItemStack b : inventory){ //遍历背包物品
                // ItemStack itemStack = new ItemStack(b.getItem(),b.getCount(),b.getNbt());
                ItemStack itemStacka = a.copy();
                ItemStack itemStackb = b.copy();
                sum = 0;
                if (a.getItem()==b.getItem()&&a.getCount()+b.getCount()<=a.getMaxCount()&&a!=ItemStack.EMPTY&&this.entity.getMainHandStack()!=b&&!a.getItem().isDamageable()&&!KillerEntity.FOOD_ITEMS.contains(b.getItem())){//合并物品
                    //物品相同，物品数之和小于等于最大堆叠数，箱子格非空，箱子子内物品非手持物品，物品可堆叠,非食物
                    items[sum_a].setCount(a.getCount()+b.getCount());
                    //this.entity.getInventory().removeItem(a.getItem(),b.getCount());   一定要用removeStack，否则会出现0物品而非空物品
                    this.entity.getInventory().removeStack(sum_b,b.getCount());
                    //temp.remove(itemStacks.indexOf(a));
                }
                if(a.getItem()==b.getItem()&&a.getCount()+b.getCount()>64&&a!=ItemStack.EMPTY&&a.getCount()!=64&&this.entity.getMainHandStack()!=b&&!a.getItem().isDamageable()&&!KillerEntity.FOOD_ITEMS.contains(b.getItem())){//合并物品
                    //物品相同，物品数之和 大于 最大堆叠数，箱子格非空，箱子子内物品非手持物品，物品可堆叠，非食物
                    this.entity.getInventory().removeStack(sum_b,64-a.getCount());
                    items[sum_a].setCount(64);
                }
                DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(chestPos);
                if (chestBlockEntity != null) {
                    ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                }
                sum_b++;
            }
            for (int i = inventory.size()-1 ; i>=0 ; i --){
                ItemStack b = inventory.get(i);
                ItemStack itemStacka = a.copy();
                ItemStack itemStackb = b.copy();
                if (a.getItem()== Items.AIR){//主动放入
                    if (!KillerEntity.FOOD_ITEMS.contains(b.getItem())&&b.getItem()!=Items.AIR&&!KillerEntity.FIGHT_ITEMS.contains(b.getItem()))//非食物非战斗物品放入
                    {
                        items[sum_a]= itemStackb;
                        this.entity.getInventory().removeStack(i,b.getCount());
                    }
                    else if (KillerEntity.FIGHT_ITEMS.contains(b.getItem())&&b.getItem()!=Items.AIR&&this.entity.getMainHandStack()!=b)//放入非手持战斗物品
                    {
                        items[sum_a] = itemStackb;//不能用b，因为，当b被修改，items[]也会改变
                        this.entity.getInventory().removeStack(i, b.getCount());
                    }
                }
                DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(chestPos);
                if (chestBlockEntity != null) {
                    ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                }
            }
            sum_a++;
        }

    }

    public void getItemsFromChest(BlockPos chestPos,DefaultedList<ItemStack> itemStacks){
        int sum_a=0;
        for (ItemStack a : itemStacks){//遍历箱子物品
            Item item = a.getItem();
            if (KillerEntity.FIGHT_ITEMS.contains(item)&&this.entity.getMainHandStack()==ItemStack.EMPTY){//a为战斗物品
                Item preferItem = KillerEntity.preferFightItem(this.entity.getMainHandStack().getItem(),item); //获取更好的物品
                if (preferItem==item){//如果更好
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
                    for (ItemStack b :items){
                        if (b.getItem()==preferItem){
                            ItemStack itemStacka = a.copy();
                            ItemStack itemStackb = b.copy();
                            this.entity.getInventory().addStack(itemStacka);
                            items[sum]=ItemStack.EMPTY;
                            break;
                        }
                        sum++;
                    }
                    DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                    if (chestBlockEntity != null) {
                        ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                    }
                }
            }
            if (KillerEntity.FOOD_ITEMS.contains(item)&& getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS)<64)
            {
                ChestBlockEntity chestBlockEntity = (ChestBlockEntity) this.entity.getWorld().getBlockEntity(chestPos);
                Object[] temp = itemStacks.toArray();
                int sum = 0;
                ItemStack[] items = new ItemStack[itemStacks.size()];
                for (Object b : temp){
                    items[sum++] = (ItemStack)b;

                }

                if (a.getCount()+getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS)<=64){
                    items[sum_a]=ItemStack.EMPTY;
                    ItemStack itemStacka = a.copy();
                    this.entity.getInventory().addStack(itemStacka);
                    System.out.println(items);
                }
                else if (a.getCount()+getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS)>64&&getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS)<64){
                    items[sum_a].setCount(a.getCount()-64+getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS));
                    ItemStack itemStacka = a.copy();
                    itemStacka.setCount(64-getCount(this.entity.getInventory(),KillerEntity.FOOD_ITEMS));
                    this.entity.getInventory().addStack(itemStacka);
                }

                DefaultedList<ItemStack> list = DefaultedList.copyOf(null,items);
                if (chestBlockEntity != null) {
                    ((ChestBlockEntityMixin)chestBlockEntity).setInventory(list);
                }
            }
            sum_a++;
        }
    }

    public static int getCount(SimpleInventory inventory, Set<Item> Items) {
        int sum=0;
        for (ItemStack a : inventory.stacks){
            if (Items.contains(a.getItem())){
                sum+=a.getCount();
            }
        }
        return sum;
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
