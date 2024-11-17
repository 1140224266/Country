package Razier.country.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlockEntity.class)
public interface ChestBlockEntityMixin {
    @Invoker("getInvStackList")
    public DefaultedList<ItemStack> getInventory();


    @Invoker("setInvStackList")
    public void setInventory(DefaultedList<ItemStack> list);
}
