package net.claustra01.poweredtfc.mixin;

import java.lang.reflect.Field;

import net.claustra01.poweredtfc.PoweredTFCConfig;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Compatibility for "More Create Burners" (mod id: moreburners).
 *
 * Its burners expose a continuous {@code heat} value (0..max_heat) and an optional upgrade state
 * (Electric Burner) which increases the maximum heat.
 *
 * Requirement:
 * - heat == 0 -> 0C
 * - non-upgraded max -> 1300C
 * - upgraded max -> 1500C
 */
@Pseudo
@Mixin(targets = "net.dragonegg.moreburners.content.block.entity.BaseBurnerBlockEntity", remap = false)
public abstract class MoreCreateBurnersHeatMixin extends BlockEntity {
    private static final String ELECTRIC_BE = "net.dragonegg.moreburners.content.block.entity.ElectricBurnerBlockEntity";
    private static volatile Field ELECTRIC_UPGRADED_FIELD;
    private static volatile boolean ELECTRIC_UPGRADED_LOOKED_UP;

    @Shadow public double heat;
    @Shadow public double max_heat;
    @Shadow protected int redstoneStrength;

    protected MoreCreateBurnersHeatMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void poweredtfc$provideHeatToTfc(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level == null || level.isClientSide()) {
            return;
        }

        // MoreBurners clamps heat to max_heat later in ElectricBurnerBlockEntity.tick().
        // If redstone throttling lowered max_heat this tick, ensure we don't overshoot for one tick.
        double effectiveHeat = Math.min(heat, max_heat);
        if (effectiveHeat <= 0.0d) {
            return;
        }

        double maxHeatUnscaled = max_heat;
        if (redstoneStrength != 0) {
            maxHeatUnscaled = (max_heat * 16.0d) / (double) redstoneStrength;
        }
        if (maxHeatUnscaled <= 0.0d) {
            return;
        }

        boolean upgraded = isElectricBurnerUpgraded(this);
        double maxTemp = upgraded
                ? PoweredTFCConfig.BLAZE_BURNER_HEAT_SEETHING.get()
                : PoweredTFCConfig.BLAZE_BURNER_HEAT_KINDLED.get();

        double ratio = clamp01(effectiveHeat / maxHeatUnscaled);
        double temperature = ratio * maxTemp;
        if (temperature <= 0.0d) {
            return;
        }

        HeatCapability.provideHeatTo(level, pos.above(), Direction.DOWN, (float) temperature);
    }

    private static double clamp01(double v) {
        if (v < 0.0d) return 0.0d;
        if (v > 1.0d) return 1.0d;
        return v;
    }

    private static boolean isElectricBurnerUpgraded(Object be) {
        if (!be.getClass().getName().equals(ELECTRIC_BE)) {
            return false;
        }

        Field f = ELECTRIC_UPGRADED_FIELD;
        if (f == null && !ELECTRIC_UPGRADED_LOOKED_UP) {
            ELECTRIC_UPGRADED_LOOKED_UP = true;
            try {
                f = be.getClass().getField("upgraded");
                ELECTRIC_UPGRADED_FIELD = f;
            } catch (NoSuchFieldException ignored) {
                // keep it null
            } catch (Throwable ignored) {
                // keep it null
            }
        }

        if (f == null) {
            return false;
        }
        try {
            return f.getBoolean(be);
        } catch (Throwable ignored) {
            return false;
        }
    }
}

