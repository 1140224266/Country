package Razier.country.client.renderer;

import Razier.country.Country;
import Razier.country.client.layers.ModModelLayers;
import Razier.country.client.model.FarmerModel;
import Razier.country.client.model.KillerEntityModel;
import Razier.country.entity.Farmer;
import Razier.country.entity.KillerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/*public class FarmerRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public FarmerRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, new PlayerEntityModel(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5F);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        this.addFeature(new PlayerHeldItemFeatureRenderer(this, context.getHeldItemRenderer()));
        this.addFeature(new StuckArrowsFeatureRenderer(context, this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer(this, context.getModelLoader(), context.getHeldItemRenderer()));
        this.addFeature(new ElytraFeatureRenderer(this, context.getModelLoader()));
        this.addFeature(new ShoulderParrotFeatureRenderer(this, context.getModelLoader()));
        this.addFeature(new TridentRiptideFeatureRenderer(this, context.getModelLoader()));
        this.addFeature(new StuckStingersFeatureRenderer(this));
    }

    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(abstractClientPlayerEntity);
        super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Vec3d getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
        return abstractClientPlayerEntity.isInSneakingPose() ? new Vec3d(0.0, -0.125, 0.0) : super.getPositionOffset(abstractClientPlayerEntity, f);
    }

    private void setModelPose(AbstractClientPlayerEntity player) {
        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel)this.getModel();
        if (player.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        } else {
            playerEntityModel.setVisible(true);
            playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.sneaking = player.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (player.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }

    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    return BipedEntityModel.ArmPose.BLOCK;
                }

                if (useAction == UseAction.BOW) {
                    return BipedEntityModel.ArmPose.BOW_AND_ARROW;
                }

                if (useAction == UseAction.SPEAR) {
                    return BipedEntityModel.ArmPose.THROW_SPEAR;
                }

                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useAction == UseAction.SPYGLASS) {
                    return BipedEntityModel.ArmPose.SPYGLASS;
                }

                if (useAction == UseAction.TOOT_HORN) {
                    return BipedEntityModel.ArmPose.TOOT_HORN;
                }

                if (useAction == UseAction.BRUSH) {
                    return BipedEntityModel.ArmPose.BRUSH;
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedEntityModel.ArmPose.ITEM;
        }
    }

    public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getSkinTexture();
    }

    protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0) {
            Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
            if (scoreboardObjective != null) {
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
                super.renderLabelIfPresent(abstractClientPlayerEntity, Text.literal(Integer.toString(scoreboardPlayerScore.getScore())).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName()), matrixStack, vertexConsumerProvider, i);
                Objects.requireNonNull(this.getTextRenderer());
                matrixStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
            }
        }

        super.renderLabelIfPresent(abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).leftArm, ((PlayerEntityModel)this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(player);
        playerEntityModel.handSwingProgress = 0.0F;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0F;
        playerEntityModel.setAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.pitch = 0.0F;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0F;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
    }

    protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = abstractClientPlayerEntity.getLeaningPitch(h);
        float j;
        float k;
        if (abstractClientPlayerEntity.isFallFlying()) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            j = (float)abstractClientPlayerEntity.getRoll() + h;
            k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k * (-90.0F - abstractClientPlayerEntity.getPitch())));
            }

            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.lerpVelocity(h);
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0F) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            j = abstractClientPlayerEntity.isTouchingWater() ? -90.0F - abstractClientPlayerEntity.getPitch() : -90.0F;
            k = MathHelper.lerp(i, 0.0F, j);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }

    }
}*/
public class KillerEntityRenderer extends MobEntityRenderer<KillerEntity, KillerEntityModel<KillerEntity>> {

    public static final Identifier TEXTURE = Identifier.of(Country.MOD_ID,"texture/entity/farmer.png");

    public KillerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new KillerEntityModel<>(context.getPart(ModModelLayers.FARMER),false),0.5f);

        //From BipedEntityRenderer
        this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), 1, 1, 1, context.getHeldItemRenderer()));
        this.addFeature(new ElytraFeatureRenderer<>(this, context.getModelLoader()));
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(KillerEntity entity) {
        return DefaultSkinHelper.getTexture(UUID.fromString("ed91ea5b-2470-419e-850c-4528175a58c8"));
    }

    @Override
    public void render(KillerEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(1.0f,1.0f,1.0f);
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}