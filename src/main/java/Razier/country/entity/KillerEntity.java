package Razier.country.entity;

import Razier.country.MethodLogger;
import Razier.country.entity.goal.FindChestAndChangeItemGoal;
import Razier.country.entity.goal.ModAttackGoal;
import Razier.country.entity.goal.MoveToBirthPositionGoal;
import Razier.country.entity.goal.PutItemsInChest;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Objects;
import java.util.Set;

public class KillerEntity extends PathAwareEntity implements InventoryOwner {

    public int birthPositionX;
    public int birthPositionY;
    public int birthPositionZ;
    public int defenceDistance=32;
    public boolean first_time=false;
    public static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of(
            Items.IRON_INGOT, Items.ROTTEN_FLESH , Items.BONE, Items.STRING, Items.SLIME_BALL, Items.BLAZE_ROD, Items.NETHER_STAR, Items.REDSTONE, Items.GLOWSTONE,Items.SPIDER_EYE,Items.MAGMA_CREAM,Items.GHAST_TEAR,Items.PHANTOM_MEMBRANE
    );
    public static final Set<Item> FOOD_ITEMS = ImmutableSet.of(Items.APPLE, Items.BAKED_POTATO, Items.BEEF, Items.BEETROOT, Items.BEETROOT_SOUP, Items.BREAD, Items.CARROT, Items.CHICKEN, Items.CHORUS_FRUIT, Items.COD, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_COD, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.COOKIE, Items.DRIED_KELP, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT, Items.HONEY_BOTTLE, Items.MELON_SLICE, Items.MUSHROOM_STEW, Items.MUTTON, Items.POISONOUS_POTATO, Items.PORKCHOP, Items.POTATO, Items.PUFFERFISH, Items.PUMPKIN_PIE, Items.RABBIT, Items.RABBIT_STEW, Items.SALMON, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.TROPICAL_FISH
    );
    public static final Set<Item> FIGHT_ITEMS = ImmutableSet.of(Items.WOODEN_SWORD,Items.STONE_SWORD,Items.IRON_SWORD,Items.GOLDEN_SWORD,Items.DIAMOND_SWORD,Items.NETHERITE_SWORD);//Items.WOODEN_PICKAXE,Items.STONE_PICKAXE,Items.IRON_PICKAXE,Items.GOLDEN_PICKAXE,Items.DIAMOND_PICKAXE,Items.NETHERITE_PICKAXE);
    MethodLogger logger = new MethodLogger();
    private boolean removed=false;
    public SimpleInventory inventory = new SimpleInventory(36);

    /*
      1.在释放点周围游荡（能回到释放点）
      2.伤害敌对生物
      3.会愤怒（套用原版iron_golem）
      4.能拾取物品
     */

    public KillerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    //尝试攻击
    /*@Override
    public boolean tryAttack(Entity target) {
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.AttackDamage;
        float g = f;
        boolean bl = target.damage(this.getDamageSources().mobAttack(this), g);
        /*if (bl) {
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setVelocity(target.getVelocity().add(0.0, 0.4F * e, 0.0));
            this.applyDamageEffects(this, target);
        }*/
/*
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
        return bl;
    }*/



    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand){

        return ActionResult.success(this.getWorld().isClient);
    }


    //设置目标
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MoveToBirthPositionGoal<KillerEntity>(this.birthPositionX,this.birthPositionY,this.birthPositionZ,this , 1.0, 128 ,30));
        this.goalSelector.add(2, new ModAttackGoal(this, 1.2, true));  //近战攻击行为
        this.goalSelector.add(1,new FindChestAndChangeItemGoal(this,1,24,24));
        this.goalSelector.add(3,new PutItemsInChest(this,1.0,28,24));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0));
        //this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        //this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        //this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector
                .add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
        //this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));

    }

    public static DefaultAttributeContainer.Builder creatKillerAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,25)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.32f)
                .add(EntityAttributes.GENERIC_ARMOR,0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,3);
    }
    //保存数据
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BirthPositionX", this.birthPositionX);
        nbt.putInt("BirthPositionY", this.birthPositionY);
        nbt.putInt("BirthPositionZ", this.birthPositionZ);

    }

    /*public boolean wantItem(DefaultedList<ItemStack> item){
        for (ItemStack a : item){
            //if (this.getEquippedStack(EquipmentSlot.HEAD).getItem().getRecipeRemainder())
        }
    }*/

    /*public Item getItemLevel(Item a1,Item a2){
        return
    }*/

    //读取数据
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.birthPositionX =nbt.getInt("BirthPositionX");
        this.birthPositionY =nbt.getInt("BirthPositionY");
        this.birthPositionZ =nbt.getInt("BirthPositionZ");

    }

    @Override
    public boolean canPickUpLoot(){

        return true;
    }

    public static Item preferFightItem(Item a , Item b){
        if (b==Items.AIR&&a!=Items.AIR)
            return a;
        /*if (b==Items.AIR){
            return a;
        }*/
        int preference_a=0;
        int preference_b=0;

        Item[] item = FIGHT_ITEMS.toArray(new Item[0]);
        for (int i = 0 ; i < FIGHT_ITEMS.size() ; i ++)
        {
            if (a==item[i])
                preference_a=i+1;
            if (b==item[i])
                preference_b=i+1;

        }
        return preference_a<preference_b?b:a;

    }

    public void setMainHandItem(String s){


        if (Objects.equals(s, "fight"))
        {
            for (int i = 0 ; i < inventory.size() ; i ++){
                for (int j = 0 ; j <= FIGHT_ITEMS.size() ; j++)
                {
                    ItemStack mainHandItem = this.getMainHandStack();
                    ItemStack itemStack = inventory.getStack(i);
                    Item item1 = mainHandItem.getItem();
                    Item item2 = inventory.getStack(i).getItem();
                    if(FIGHT_ITEMS.contains(item2));
                    {
                        if (preferFightItem(item1,item2) == item2)
                        {
                            this.setStackInHand(Hand.MAIN_HAND,itemStack);
                        }
                    }
                }
            }
        }
    }
    //---------------------------ACTION---------------------------
    @Override
    public void tickMovement() {

        super.tickMovement();
        if (!first_time)
        {
            this.birthPositionX= (int) this.getX();
            this.birthPositionY= (int) this.getY();
            this.birthPositionZ= (int) this.getZ();
            first_time=true;

        }

        BlockPos pos = this.getBlockPos();
        WorldView world = this.getWorld();
        if (world.getBlockState(pos.down(1)).isOf(Blocks.CHEST)){
            this.getNavigation().stop();
        }

        this.setMainHandItem("fight");
        /*int x1=this.birthPositionX;
        int y1=this.birthPositionY;
        int z1=this.birthPositionZ;
        int x0= (int) this.getX();
        int y0= (int) this.getY();
        int z0= (int) this.getZ();
        double distance = sqrt(pow(x1-x0,2)+pow(y1-y0,2)+pow(z1-z0,2));
        if (distance<defenceDistance&&!removed){
            Set<PrioritizedGoal> goals = this.goalSelector.getGoals();
            for (PrioritizedGoal a : goals)
            {
                if(a.getGoal() instanceof MoveToBirthPositionGoal<?>){
                    MethodLogger.add("tickMovement",0,"find out");
                    this.goalSelector.remove(a.getGoal());
                    MethodLogger.add("tickMovement",0,"remove goal successfully");

                    System.out.println("find out");

                }
            }
            removed=true;
        }不允许修改。。。。*/
    }
    //---------------------------ACTION---------------------------


    public int getBirthPositionX(){
        return birthPositionX;
    }

    public int getBirthPositionY(){
        return birthPositionY;
    }
    public int getBirthPositionZ(){
        return birthPositionZ;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    public void loot(ItemEntity item){
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    public boolean canGather(ItemStack stack){
        Item item = stack.getItem();
        return (GATHERABLE_ITEMS.contains(item) || FIGHT_ITEMS.contains(item));
    }

    //---------------------------angry---------------------------
/*
    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }*/
    //---------------------------angry---------------------------
}
