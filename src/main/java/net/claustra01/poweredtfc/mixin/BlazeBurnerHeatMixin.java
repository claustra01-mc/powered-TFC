package net.claustra01.poweredtfc.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.claustra01.poweredtfc.PoweredTFCConfig;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerHeatMixin extends SmartBlockEntity {
    public BlazeBurnerHeatMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void poweredtfc$provideHeatToCrucible(CallbackInfo ci) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlazeBurnerBlock.HeatLevel heatLevel = ((BlazeBurnerBlockEntity) (Object) this).getHeatLevelFromBlock();
        int step = switch (heatLevel) {
            case SMOULDERING -> 1;
            case FADING -> 2;
            case KINDLED -> 3;
            case SEETHING -> 4;
            default -> 0;
        };
        float temperature = (float) (step * PoweredTFCConfig.BLAZE_BURNER_HEAT_PER_LEVEL.get());

        HeatCapability.provideHeatTo(level, worldPosition.above(), Direction.DOWN, temperature);
    }
}
