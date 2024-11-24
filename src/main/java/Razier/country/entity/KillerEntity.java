package Razier.country.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Activity;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.FoodComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import Razier.country.entity.ModHungerManager;
import net.minecraft.item.SwordItem;
import net.minecraft.item.AxeItem;
import Razier.country.entity.goals.LootChestGoal;
import net.minecraft.entity.mob.HostileEntity;

public class KillerEntity extends PathAwareEntity implements InventoryOwner {
    private static final Set<Item> GATHERABLE_ITEMS = new HashSet<>();
    private static final Set<Item> FIGHT_ITEMS = new HashSet<>();
    private static final Set<Item> FOOD_ITEMS = new HashSet<>();
    
    static {
        // 怪物掉落物
        GATHERABLE_ITEMS.add(Items.ROTTEN_FLESH);
        GATHERABLE_ITEMS.add(Items.BONE);
        GATHERABLE_ITEMS.add(Items.STRING);
        GATHERABLE_ITEMS.add(Items.SPIDER_EYE);
        GATHERABLE_ITEMS.add(Items.GUNPOWDER);
        GATHERABLE_ITEMS.add(Items.ENDER_PEARL);
        GATHERABLE_ITEMS.add(Items.BLAZE_ROD);
        GATHERABLE_ITEMS.add(Items.GHAST_TEAR);
        GATHERABLE_ITEMS.add(Items.SLIME_BALL);
        GATHERABLE_ITEMS.add(Items.MAGMA_CREAM);
        GATHERABLE_ITEMS.add(Items.PHANTOM_MEMBRANE);
        GATHERABLE_ITEMS.add(Items.PRISMARINE_SHARD);
        GATHERABLE_ITEMS.add(Items.PRISMARINE_CRYSTALS);
        GATHERABLE_ITEMS.add(Items.SHULKER_SHELL);
        
        // 所有种类的剑
        FIGHT_ITEMS.add(Items.WOODEN_SWORD);
        FIGHT_ITEMS.add(Items.STONE_SWORD);
        FIGHT_ITEMS.add(Items.IRON_SWORD);
        FIGHT_ITEMS.add(Items.GOLDEN_SWORD);
        FIGHT_ITEMS.add(Items.DIAMOND_SWORD);
        FIGHT_ITEMS.add(Items.NETHERITE_SWORD);
        
        // 安全食物（排除有负面效果的）
        FOOD_ITEMS.add(Items.BREAD);
        FOOD_ITEMS.add(Items.APPLE);
        FOOD_ITEMS.add(Items.BAKED_POTATO);
        FOOD_ITEMS.add(Items.COOKED_BEEF);
        FOOD_ITEMS.add(Items.COOKED_CHICKEN);
        FOOD_ITEMS.add(Items.COOKED_MUTTON);
        FOOD_ITEMS.add(Items.COOKED_PORKCHOP);
        FOOD_ITEMS.add(Items.COOKED_RABBIT);
        FOOD_ITEMS.add(Items.COOKED_COD);
        FOOD_ITEMS.add(Items.COOKED_SALMON);
    }
    
    private int birthPositionX;
    private int birthPositionY;
    private int birthPositionZ;
    private boolean first_time = false;
    private final int defenceDistance = 32;
    private int[] cooldown = new int[6];
    private SimpleInventory inventory;
    private Activity currentBehavior = null;
    private Entity currentTarget = null;
    private BlockPos currentDestination = null;
    private int behaviorTimer = 0;
    private ModHungerManager hungerManager = new ModHungerManager();

    protected KillerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
        this.setPersistent();
        this.setCustomNameVisible(true);
        this.experiencePoints = 50;
        this.inventory = new SimpleInventory(27);
    }

    public static DefaultAttributeContainer.Builder createKillerAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)          // 增加生命值到40
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)     // 提高移动速度
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)        // 提高基础攻击力
            .add(EntityAttributes.GENERIC_ARMOR, 4.0)                // 增加护甲值
            .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0)      // 添加护甲韧性
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6) // 提高击退抗性
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0);     // 添加击退力
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new LootChestGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2D, false));
        
        // 将目标选择器改为只针对敌对生物
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true));
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return !stack.isEmpty() && (GATHERABLE_ITEMS.contains(stack.getItem()) || 
               FIGHT_ITEMS.contains(stack.getItem()) || 
               FOOD_ITEMS.contains(stack.getItem()));
    }

    @Override
    protected void loot(ItemEntity item) {
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        // 死亡时掉落物品栏中的物品
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.getStack(i);
            if (!stack.isEmpty()) {
                this.dropStack(stack);
            }
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false; // 防止实体消失
    }

    @Override
    public boolean isFireImmune() {
        return false; // 免疫火焰伤害
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.74F; // 设置眼睛高度
    }

    @Override
    public int getXpToDrop() {
        return this.experiencePoints;
    }

    @Override
    public void tick() {
        super.tick();
        
        // 更新饱食度和生命恢复
        hungerManager.update(this);
        if (this.age % 10 == 0) {  // 每10tick更新一次
            // 模拟玩家的饱食度自然消耗
            if (this.isMoving()) {
                hungerManager.addExhaustion(0.01F);
            }
        }
        
        // 生命恢复逻辑
        if (hungerManager.getFoodLevel() >= 18 && this.getHealth() < this.getMaxHealth()) {
            if (this.age % 20 == 0) {  // 每4秒回复1点生命值
                this.heal(1.0F);
                hungerManager.addExhaustion(3.0F);
                System.out.println(hungerManager.getExhaustion());
                System.out.println(hungerManager.getFoodLevel());
                System.out.println(hungerManager.getSaturationLevel());
            }
        }
        
        // 当饱食度低时尝试吃东西
        if (hungerManager.getFoodLevel() < 20) {
            tryEat();
        }
        
        // 第一次tick时记录出生点位置
        if (!first_time) {
            birthPositionX = (int) this.getX();
            birthPositionY = (int) this.getY();
            birthPositionZ = (int) this.getZ();
            first_time = true;
        }

        // 行为优先级处理系统
        if (--behaviorTimer <= 0) {
            // 优先级1：返回出生点
            double distanceFromBirth = this.squaredDistanceTo(birthPositionX, birthPositionY, birthPositionZ);
            if (distanceFromBirth > defenceDistance * defenceDistance) {
                currentBehavior = Activity.IDLE;
                currentDestination = new BlockPos(birthPositionX, birthPositionY, birthPositionZ);
                this.getNavigation().startMovingTo(birthPositionX, birthPositionY, birthPositionZ, 1.0);
                behaviorTimer = 100;  // 5秒冷却
                return;
            }

            // 优先级2：战斗行为
            Box searchBox = this.getBoundingBox().expand(16.0);
            List<Entity> nearbyEntities = this.getWorld().getEntitiesByClass(
                Entity.class, 
                searchBox,
                entity -> entity instanceof Monster && !(entity instanceof KillerEntity)
            );
            
            if (!nearbyEntities.isEmpty()) {
                currentBehavior = Activity.FIGHT;
                currentTarget = nearbyEntities.get(0);
                this.setTarget((LivingEntity)currentTarget);
                behaviorTimer = 60;  // 3秒冷却
                return;
            }

            // 优先级3：收集物品
            List<ItemEntity> items = this.getWorld().getEntitiesByClass(
                ItemEntity.class,
                this.getBoundingBox().expand(12.0),  // 增加检测范围
                item -> canPickupItem(item.getStack())
            );

            if (!items.isEmpty()) {
                ItemEntity nearestItem = items.get(0);
                currentBehavior = Activity.IDLE;
                currentDestination = nearestItem.getBlockPos();
                this.getNavigation().startMovingTo(nearestItem.getX(), nearestItem.getY(), nearestItem.getZ(), 1.0);
                behaviorTimer = 40;  // 2秒冷却
                return;
            }

            // 优先级4：存储物品
            if (shouldStoreItems()) {
                Box chestSearchBox = this.getBoundingBox().expand(8.0);
                BlockPos nearestChest = findNearestChest(chestSearchBox);
                
                if (nearestChest != null) {
                    currentBehavior = Activity.WORK;
                    currentDestination = nearestChest;
                    this.getNavigation().startMovingTo(nearestChest.getX(), nearestChest.getY(), nearestChest.getZ(), 1.0);
                    behaviorTimer = 80;  // 4秒冷却
                    
                    // 当靠近箱子时执行存储
                    if (this.squaredDistanceTo(Vec3d.of(nearestChest)) < 2.0) {
                        storeItemsInChest(nearestChest);
                    }
                }
            }
        }

        // 每tick都更新佳武器装备
        updateBestWeapon();
    }

    private boolean shouldStoreItems() {
        int foodCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (FOOD_ITEMS.contains(stack.getItem())) {
                foodCount += stack.getCount();
            }
        }
        return foodCount > 64 || inventory.size() > 20; // 如果食物超过64个或背包快满了
    }

    private BlockPos findNearestChest(Box searchBox) {
        BlockPos entityPos = this.getBlockPos();
        BlockPos nearestChest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int x = (int)searchBox.minX; x < (int)searchBox.maxX; x++) {
            for (int y = (int)searchBox.minY; y < (int)searchBox.maxY; y++) {
                for (int z = (int)searchBox.minZ; z < (int)searchBox.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (this.getWorld().getBlockEntity(pos) instanceof ChestBlockEntity) {
                        double distance = pos.getSquaredDistance(entityPos);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestChest = pos;
                        }
                    }
                }
            }
        }
        return nearestChest;
    }

    private void storeItemsInChest(BlockPos chestPos) {
        BlockEntity blockEntity = this.getWorld().getBlockEntity(chestPos);
        if (!(blockEntity instanceof ChestBlockEntity)) return;
        
        ChestBlockEntity chest = (ChestBlockEntity)blockEntity;
        ItemStack mainHandItem = this.getMainHandStack();
        
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && !stack.equals(mainHandItem)) {
                if (FOOD_ITEMS.contains(stack.getItem())) {
                    // 保留64个食物
                    if (countFoodItems() > 64) {
                        transferToChest(stack, chest);
                    }
                } else {
                    // 其他物品都存入箱子
                    transferToChest(stack, chest);
                }
            }
        }
    }

    private int countFoodItems() {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (FOOD_ITEMS.contains(stack.getItem())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void transferToChest(ItemStack stack, ChestBlockEntity chest) {
        // 遍历箱子的所有槽位
        for (int slot = 0; slot < 27; slot++) {  // 单个箱子有27个槽位
            ItemStack chestStack = chest.getStack(slot);
            
            // 如果箱子槽位为空，直接放入物品
            if (chestStack.isEmpty()) {
                chest.setStack(slot, stack.copy());
                stack.setCount(0);
                return;
            }
            
            // 如果是相同物品且未达到堆叠上限，尝试合并
            if (ItemStack.canCombine(chestStack, stack)) {
                int spaceLeft = chestStack.getMaxCount() - chestStack.getCount();
                if (spaceLeft > 0) {
                    int transferAmount = Math.min(spaceLeft, stack.getCount());
                    chestStack.increment(transferAmount);
                    stack.decrement(transferAmount);
                    
                    if (stack.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    private void updateBestWeapon() {
        ItemStack currentMainHand = this.getMainHandStack();
        ItemStack bestWeapon = currentMainHand;
        int bestPower = getWeaponPower(currentMainHand);

        // 检查背包中所有物品
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (FIGHT_ITEMS.contains(stack.getItem())) {
                int power = getWeaponPower(stack);
                if (power > bestPower) {
                    bestPower = power;
                    bestWeapon = stack;
                }
            }
        }

        // 如果找到更好的武器，则装备它
        if (bestWeapon != currentMainHand) {
            this.equipStack(EquipmentSlot.MAINHAND, bestWeapon);
        }
    }

    private int getWeaponPower(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        
        Item item = stack.getItem();
        if (item == Items.WOODEN_SWORD) return 1;
        if (item == Items.STONE_SWORD) return 2;
        if (item == Items.IRON_SWORD) return 3;
        if (item == Items.GOLDEN_SWORD) return 1;
        if (item == Items.DIAMOND_SWORD) return 4;
        if (item == Items.NETHERITE_SWORD) return 5;
        return 0;
    }

    private void tryEat() {
        SimpleInventory inventory = this.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().isFood()) {  // 使用原版的食物判断
                FoodComponent food = stack.getItem().getFoodComponent();
                if (food != null) {
                    hungerManager.eat(stack.getItem(), stack);
                    stack.decrement(1);
                    break;
                }
            }
        }
    }

    // 保存饱食度数据
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound hungerNbt = new NbtCompound();
        hungerManager.writeNbt(hungerNbt);
        nbt.put("Hunger", hungerNbt);
        return nbt;
    }

    // 读取饱食度数据
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Hunger")) {
            hungerManager.readNbt(nbt.getCompound("Hunger"));
        }
    }

    // 添加辅助方法判断实体是否在移动
    private boolean isMoving() {
        return this.getVelocity().lengthSquared() > 0.0001D;
    }

    // 辅助方法
    public boolean hasWeapon() {
        return !getMainHandStack().isEmpty() && (getMainHandStack().getItem() instanceof SwordItem 
            || getMainHandStack().getItem() instanceof AxeItem);
    }

    public boolean hasFood() {
        SimpleInventory inv = getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isFood()) return true;
        }
        return false;
    }
}