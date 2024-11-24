package Razier.country.client;

import Razier.country.client.layers.ModModelLayers;
import Razier.country.client.model.FarmerModel;
import Razier.country.client.model.KillerEntityModel;
import Razier.country.client.model.TestModel;
import Razier.country.client.renderer.FarmerRenderer;
import Razier.country.client.renderer.KillerEntityRenderer;
import Razier.country.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import Razier.country.client.renderer.TestRenderer;

@Environment(EnvType.CLIENT)
public class CountryClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ModEntities.FARMER, FarmerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FARMER, FarmerModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(ModEntities.KILLER, KillerEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.KILLER, KillerEntityModel::getTexturedModelData);

        EntityRendererRegistry.INSTANCE.register(ModEntities.TEST, TestRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TEST, TestModel::getTexturedModelData);
    }
    /*@Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(Country.FARMER, new FarmerRenderer());
    }*/
        /*EntityRendererRegistry INSTANCE = new EntityRendererRegistry() {
            public <E extends Entity> void register(EntityType<? extends E> entityType, EntityRendererFactory<E> factory) {
                net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(Country.FARMER, factory);
            }
        };
    }*/
    /*
    @Override
    EntityRendererRegistry INSTANCE = new EntityRendererRegistry() {
        public <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererFactory<T> factory) {
            net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(entityType, factory);
        }
    };

    <E extends Entity> void register(EntityType<? extends E> var1, EntityRendererFactory<E> var2) {

    }*/
}
