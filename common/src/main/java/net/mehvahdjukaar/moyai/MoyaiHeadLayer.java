package net.mehvahdjukaar.moyai;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MoyaiHeadLayer<T extends LivingEntity, M extends HierarchicalModel<T>> extends RenderLayer<T, M> {

    private final ModelPart head;
    private final ItemInHandRenderer itemRenderer;

    public MoyaiHeadLayer(RenderLayerParent<T, M> parent) {
        super(parent);
        this.itemRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        this.head = this.getParentModel().root().getChild("head");
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {

            pMatrixStack.pushPose();

            this.head.translateAndRotate(pMatrixStack);

            translateToHead(pMatrixStack, false);
            itemRenderer.renderItem(pLivingEntity, itemstack, ItemTransforms.TransformType.HEAD, false, pMatrixStack, pBuffer, pPackedLight);

            pMatrixStack.popPose();
        }
    }

    public static void translateToHead(PoseStack stack, boolean p_174485_) {
        float f = 0.625F;
        stack.translate(0.0D, -0.25D, 0.0D);
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        stack.scale(0.625F, -0.625F, -0.625F);

        stack.translate(0.0D, 0.25 + 3 / 64f, -0.125D);
    }
}
