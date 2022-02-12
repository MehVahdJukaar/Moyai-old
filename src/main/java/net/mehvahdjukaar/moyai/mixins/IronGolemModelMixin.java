package net.mehvahdjukaar.moyai.mixins;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IronGolemModel.class)
public abstract class IronGolemModelMixin<T extends IronGolem> extends HierarchicalModel<T> {

    @Shadow @Final private ModelPart head;

    @Override
    public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
        this.head.visible = pEntity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
    }
}
