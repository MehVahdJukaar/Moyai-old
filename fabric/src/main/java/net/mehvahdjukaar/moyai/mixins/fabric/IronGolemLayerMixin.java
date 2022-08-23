package net.mehvahdjukaar.moyai.mixins.fabric;

import net.mehvahdjukaar.moyai.Moyai;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemModel.class)
public abstract class IronGolemLayerMixin<T extends IronGolem> extends HierarchicalModel<T> implements HeadedModel {

    @Shadow
    @Final
    private ModelPart head;

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/IronGolem;FFF)V", at = @At("HEAD"), require = 0)
    public void prepareMobModel(T ironGolem, float f, float g, float h, CallbackInfo ci) {
        this.head.visible = !(ironGolem.getItemBySlot(EquipmentSlot.HEAD).getItem() == Moyai.MOYAI_ITEM);
    }

    @Override
    public ModelPart getHead() {
        return head;
    }
}
