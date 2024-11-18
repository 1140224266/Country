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
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class KillerEntity extends PathAwareEntity implements InventoryOwner {

    private static final int INVENTORY_SIZE = 36;
    private static final int DEFAULT_DEFENCE_RANGE = 32;
    private static final float DEFAULT_HEALTH = 25.0f;
    private static final float DEFAULT_SPEED = 0.32f;
    private static final float DEFAULT_ARMOR = 0.0f;
    private static final float DEFAULT_ATTACK_DAMAGE = 3.0f;

    private BlockPos birthPosition;
    private final int defenceRange;
    private boolean isFirstSpawn;
    private final SimpleInventory inventory;
    private final MethodLogger logger;
    private boolean isRemoved;

    public static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.<Item>builder()
            .add(Items.IRON_INGOT)
            .add(Items.ROTTEN_FLESH)
            .add(Items.BONE)
            .add(Items.STRING)
            .add(Items.SLIME_BALL)
            .add(Items.BLAZE_ROD)
            .add(Items.NETHER_STAR)
            .add(Items.REDSTONE)
            .add(Items.GLOWSTONE)
            .add(Items.SPIDER_EYE)
            .add(Items.MAGMA_CREAM)
            .add(Items.GHAST_TEAR)
            .add(Items.PHANTOM_MEMBRANE)
            .build();

    public static final Set<Item> FOOD_ITEMS = ImmutableSet.<Item>builder()
            .add(Items.APPLE)
            .add(Items.BAKED_POTATO)
            .add(Items.BEEF)
            .add(Items.BEETROOT)
            .add(Items.BEETROOT_SOUP)
            .add(Items.BREAD)
            .add(Items.CARROT)
            .add(Items.CHICKEN)
            .add(Items.CHORUS_FRUIT)
            .add(Items.COD)
            .add(Items.COOKED_BEEF)
            .add(Items.COOKED_CHICKEN)
            .add(Items.COOKED_COD)
            .add(Items.COOKED_MUTTON)
            .add(Items.COOKED_PORKCHOP)
            .add(Items.COOKED_RABBIT)
            .add(Items.COOKED_SALMON)
            .add(Items.COOKIE)
            .add(Items.DRIED_KELP)
            .add(Items.ENCHANTED_GOLDEN_APPLE)
            .add(Items.GOLDEN_APPLE)
            .add(Items.GOLDEN_CARROT)
            .add(Items.HONEY_BOTTLE)
            .add(Items.MELON_SLICE)
            .add(Items.MUSHROOM_STEW)
            .add(Items.MUTTON)
            .add(Items.POISONOUS_POTATO)
            .add(Items.PORKCHOP)
            .add(Items.POTATO)
            .add(Items.PUFFERFISH)
            .add(Items.PUMPKIN_PIE)
            .add(Items.RABBIT)
            .add(Items.RABBIT_STEW)
            .add(Items.SALMON)
            .add(Items.SUSPICIOUS_STEW)
            .add(Items.SWEET_BERRIES)
            .add(Items.GLOW_BERRIES)
            .add(Items.TROPICAL_FISH)
            .build();

    public static final Set<Item> FIGHT_ITEMS = ImmutableSet.<Item>builder()
            .add(Items.WOODEN_SWORD)
            .add(Items.STONE_SWORD)
            .add(Items.IRON_SWORD)
            .add(Items.GOLDEN_SWORD)
            .add(Items.DIAMOND_SWORD)
            .add(Items.NETHERITE_SWORD)
            .build();

    public KillerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.birthPosition = new BlockPos(0, 0, 0);
        this.defenceRange = DEFAULT_DEFENCE_RANGE;
        this.isFirstSpawn = false;
        this.inventory = new SimpleInventory(INVENTORY_SIZE);
        this.logger = new MethodLogger();
        this.isRemoved = false;
    }

    private static final class GoalPriority {
        static final int RETURN_TO_BIRTH = 1;
        static final int ATTACK = 2;
        static final int FIND_CHEST = 3;
        static final int STORE_ITEMS = 4;
        static final int WANDER = 5;
        static final int LOOK = 7;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(GoalPriority.RETURN_TO_BIRTH, 
            new MoveToBirthPositionGoal<>(
                birthPosition.getX(),
                birthPosition.getY(),
                birthPosition.getZ(),
                this,
                1.0,
                128,
                30
            )
        );
        this.goalSelector.add(GoalPriority.ATTACK, new ModAttackGoal(this, 1.2, true));
        this.goalSelector.add(GoalPriority.FIND_CHEST, new FindChestAndChangeItemGoal(this, 1, 24, 24));
        this.goalSelector.add(GoalPriority.STORE_ITEMS, new PutItemsInChest(this, 1.0, 28, 24));
        this.goalSelector.add(GoalPriority.WANDER, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(GoalPriority.LOOK, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(GoalPriority.LOOK, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector
                .add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
    }

    public static DefaultAttributeContainer.Builder creatKillerAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, DEFAULT_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, DEFAULT_SPEED)
                .add(EntityAttributes.GENERIC_ARMOR, DEFAULT_ARMOR)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, DEFAULT_ATTACK_DAMAGE);
    }

    public static Item preferFightItem(Item a, Item b) {
        if (b == Items.AIR && a != Items.AIR) {
            return a;
        }

        return getFightItemPreference(a) >= getFightItemPreference(b) ? a : b;
    }

    private static int getFightItemPreference(Item item) {
        List<Item> fightItems = new ArrayList<>(FIGHT_ITEMS);
        int index = fightItems.indexOf(item);
        return index == -1 ? 0 : index + 1;
    }

    public void setMainHandItem(String type) {
        if ("fight".equals(type)) {
            Item bestWeapon = findBestWeapon();
            if (bestWeapon != null) {
                setStackInHand(Hand.MAIN_HAND, new ItemStack(bestWeapon));
            }
        }
    }

    private Item findBestWeapon() {
        Item currentBest = Items.AIR;
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.getStack(i).getItem();
            if (FIGHT_ITEMS.contains(item)) {
                currentBest = preferFightItem(currentBest, item);
            }
        }
        return currentBest;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        
        if (!isFirstSpawn) {
            initializeBirthPosition();
        }

        handleChestInteraction();
        updateMainHandItem();
    }

    private void initializeBirthPosition() {
        BlockPos currentPos = this.getBlockPos();
        this.birthPosition = currentPos;
        isFirstSpawn = true;
    }

    private void handleChestInteraction() {
        BlockPos pos = this.getBlockPos();
        if (this.getEntityWorld().getBlockState(pos.down()).isOf(Blocks.CHEST)) {
            this.getNavigation().stop();
        }
    }

    private void updateMainHandItem() {
        setMainHandItem("fight");
    }

    @Override
    public boolean canGather(ItemStack stack){
        Item item = stack.getItem();
        return (GATHERABLE_ITEMS.contains(item) || FIGHT_ITEMS.contains(item));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BirthPositionX", birthPosition.getX());
        nbt.putInt("BirthPositionY", birthPosition.getY());
        nbt.putInt("BirthPositionZ", birthPosition.getZ());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.birthPosition = new BlockPos(nbt.getInt("BirthPositionX"), nbt.getInt("BirthPositionY"), nbt.getInt("BirthPositionZ"));
    }

    @Override
    public boolean canPickUpLoot(){

        return true;
    }

    public BlockPos getBirthPosition() {
        return birthPosition;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    public void loot(ItemEntity item){
        InventoryOwner.pickUpItem(this, this, item);
    }
}
