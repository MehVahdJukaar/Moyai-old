package net.mehvahdjukaar.moyai.mixins.fabric;

import net.mehvahdjukaar.moyai.Moyai;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockSoundMixin {

    @Inject(method = "triggerEvent",
            at = @At("HEAD"),cancellable = true, require = 0)
    public void triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        if (Moyai.onNotePlayed(level, blockPos, blockState)){
            cir.setReturnValue(false);
        }
    }
}
