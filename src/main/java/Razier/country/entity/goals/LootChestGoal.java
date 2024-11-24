package Razier.country.entity.goals;

import Razier.country.entity.KillerEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Items;

public class LootChestGoal extends Goal {
    private final KillerEntity killer;
    private BlockPos targetChestPos;
    private int searchCooldown = 0;

    public LootChestGoal(KillerEntity killer) {
        this.killer = killer;
    }

    @Override
    public boolean canStart() {
        if (searchCooldown > 0) {
            searchCooldown--;
            return false;
        }
        
        if (killer.hasWeapon() && killer.hasFood()) {
            return false;
        }

        targetChestPos = findNearestChest();
        return targetChestPos != null;
    }

    @Override
    public void start() {
        killer.getNavigation().startMovingTo(targetChestPos.getX(), targetChestPos.getY(), targetChestPos.getZ(), 1.0);
    }

    @Override
    public void tick() {
        if (targetChestPos != null && killer.squaredDistanceTo(Vec3d.of(targetChestPos)) < 2.0) {
            lootChest(targetChestPos);
            searchCooldown = 200;
            targetChestPos = null;
        }
    }

    private BlockPos findNearestChest() {
        int searchRadius = 10;
        BlockPos entityPos = killer.getBlockPos();
        
        for (BlockPos pos : BlockPos.iterate(
            entityPos.add(-searchRadius, -searchRadius, -searchRadius),
            entityPos.add(searchRadius, searchRadius, searchRadius))) {
                
            BlockState state = killer.getWorld().getBlockState(pos);
            if (state.getBlock() instanceof ChestBlock) {
                return pos.toImmutable();
            }
        }
        return null;
    }

    private void lootChest(BlockPos pos) {
        if (!(killer.getWorld().getBlockState(pos).getBlock() instanceof ChestBlock)) {
            return;
        }

        ChestBlockEntity chest = (ChestBlockEntity) killer.getWorld().getBlockEntity(pos);
        if (chest == null) return;

        for (int i = 0; i < chest.size(); i++) {
            ItemStack stack = chest.getStack(i);
            if (!stack.isEmpty()) {
                if (!killer.hasWeapon() && isWeapon(stack)) {
                    killer.equipStack(EquipmentSlot.MAINHAND, stack);
                    chest.removeStack(i);
                } else if (!killer.hasFood() && isFood(stack)) {
                    killer.getInventory().addStack(stack);
                    chest.removeStack(i);
                }
            }
        }
    }

    private boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }

    private boolean isFood(ItemStack stack) {
        if (!stack.isFood()) return false;
        
        if (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            return true;
        }
        
        FoodComponent food = stack.getItem().getFoodComponent();
        if (food == null) return false;
        
        return food.getStatusEffects().isEmpty();
    }
} 