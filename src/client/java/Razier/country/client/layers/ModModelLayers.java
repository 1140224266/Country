package Razier.country.client.layers;

import Razier.country.Country;
import com.google.common.collect.Sets;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ModModelLayers {
    public static final EntityModelLayer FARMER = new EntityModelLayer(new Identifier(Country.MOD_ID,"farmer"),"main");
    public static final EntityModelLayer KILLER = new EntityModelLayer(new Identifier(Country.MOD_ID,"killer"),"main");
    public static final EntityModelLayer TEST = new EntityModelLayer(new Identifier(Country.MOD_ID,"test"),"main");
}
