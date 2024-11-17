package Razier.country;

import Razier.country.entity.KillerEntity;
import Razier.country.mixin.ChestBlockEntityMixin;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        getItemsFromChest();
    }
    public KillerEntity entity = new KillerEntity(EntityType.PIG,null);
    public static void getItemsFromChest(){
        int[] d  = {1,5,9,8,4,6};

        System.out.println(Arrays.binarySearch(d,5));
    }
}
