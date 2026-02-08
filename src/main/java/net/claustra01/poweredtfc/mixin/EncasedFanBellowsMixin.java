package net.claustra01.poweredtfc.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;

import net.claustra01.poweredtfc.PoweredTFCConfig;
import net.dries007.tfc.common.blocks.devices.IBellowsConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EncasedFanBlockEntity.class, remap = false)
public abstract class EncasedFanBellowsMixin extends KineticBlockEntity {
    public EncasedFanBellowsMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void poweredtfc$provideBellowsAir(CallbackInfo ci) {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (getSpeed() == 0) {
            return;
        }

        Direction direction = ((EncasedFanBlockEntity) (Object) this).getAirFlowDirection();
        if (direction.getAxis().isVertical()) {
            return;
        }

        double baseAir = PoweredTFCConfig.FAN_BASE_AIR.get();
        double speedMultiplier = PoweredTFCConfig.FAN_SPEED_MULTIPLIER.get();
        int air = (int) Math.round(baseAir + (Math.abs(getSpeed()) * speedMultiplier));
        for (IBellowsConsumer.Offset offset : IBellowsConsumer.offsets()) {
            BlockPos airPos = worldPosition.above(offset.up())
                    .relative(direction, offset.out())
                    .relative(direction.getClockWise(), offset.side());
            BlockState state = level.getBlockState(airPos);

            if (!(state.getBlock() instanceof IBellowsConsumer consumer)) {
                continue;
            }
            if (!consumer.canAcceptAir(level, airPos, state)) {
                continue;
            }

            consumer.intakeAir(level, airPos, state, air);
        }
    }
}
