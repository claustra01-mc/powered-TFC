package net.claustra01.poweredtfc;

import java.util.List;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;

import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TemperatureDisplayStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PoweredTFCDisplaySources {
    private static final String CONFIG_MODE = "Mode";

    public static final DeferredRegister<DisplaySource> DISPLAY_SOURCES =
            DeferredRegister.create(CreateRegistries.DISPLAY_SOURCE, PoweredTFC.MODID);

    public static final DeferredHolder<DisplaySource, DisplaySource> TFC_CRUCIBLE = DISPLAY_SOURCES.register("tfc_crucible", () -> {
        DisplaySource source = new CrucibleDisplaySource();
        DisplaySource.BY_BLOCK.add(TFCBlocks.CRUCIBLE.get(), source);
        return source;
    });

    public static final DeferredHolder<DisplaySource, DisplaySource> TFC_BLAST_FURNACE = DISPLAY_SOURCES.register("tfc_blast_furnace", () -> {
        DisplaySource source = new BlastFurnaceDisplaySource();
        DisplaySource.BY_BLOCK.add(TFCBlocks.BLAST_FURNACE.get(), source);
        return source;
    });

    private PoweredTFCDisplaySources() {}

    public static void register(IEventBus modEventBus) {
        DISPLAY_SOURCES.register(modEventBus);
    }

    private static final class CrucibleDisplaySource extends SingleLineDisplaySource {
        @Override
        protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
            if (!(context.getSourceBlockEntity() instanceof CrucibleBlockEntity crucible)) {
                return Component.empty();
            }

            float temp = crucible.getTemperature();
            if (temp <= 1.0F) {
                return Component.translatable("poweredtfc.display.no_heat");
            }

            int mode = context.sourceConfig().getInt(CONFIG_MODE);
            if (mode == 1) {
                return TemperatureDisplayStyle.COLOR.formatColored(temp);
            }
            return TemperatureDisplayStyle.CELSIUS.format(temp);
        }

        @Override
        protected boolean allowsLabeling(DisplayLinkContext context) {
            return true;
        }

        @Override
        public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
            super.initConfigurationWidgets(context, builder, isFirstLine);
            if (!isFirstLine) {
                builder.addSelectionScrollInput(0, 137, (input, label) -> input
                        .forOptions(List.of(
                                Component.translatable("poweredtfc.display.temperature_number"),
                                Component.translatable("poweredtfc.display.temperature_color")))
                        .titled(Component.translatable("poweredtfc.display.mode")), CONFIG_MODE);
            }
        }

        @Override
        public Component getName() {
            return Component.translatable("block.tfc.crucible");
        }
    }

    private static final class BlastFurnaceDisplaySource extends SingleLineDisplaySource {
        @Override
        protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
            if (!(context.getSourceBlockEntity() instanceof BlastFurnaceBlockEntity blastFurnace)) {
                return Component.empty();
            }

            float temp = blastFurnace.getTemperature();
            int mode = context.sourceConfig().getInt(CONFIG_MODE);
            return switch (mode) {
                case 0 -> temp > 1.0F ? TemperatureDisplayStyle.CELSIUS.format(temp) : Component.translatable("poweredtfc.display.no_heat");
                case 1 -> temp > 1.0F ? TemperatureDisplayStyle.COLOR.formatColored(temp) : Component.translatable("poweredtfc.display.no_heat");
                case 2 -> Component.literal(String.valueOf(blastFurnace.getInputCount()));
                case 3 -> Component.literal(String.valueOf(blastFurnace.getCatalystCount()));
                case 4 -> Component.literal(String.valueOf(blastFurnace.getFuelCount()));
                case 5 -> {
                    int t = Math.round(temp);
                    yield Component.literal(t + "C | " + blastFurnace.getInputCount() + " | " + blastFurnace.getCatalystCount() + " | " + blastFurnace.getFuelCount());
                }
                default -> Component.empty();
            };
        }

        @Override
        protected boolean allowsLabeling(DisplayLinkContext context) {
            return true;
        }

        @Override
        public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
            super.initConfigurationWidgets(context, builder, isFirstLine);
            if (!isFirstLine) {
                builder.addSelectionScrollInput(0, 137, (input, label) -> input
                        .forOptions(List.of(
                                Component.translatable("poweredtfc.display.temperature_number"),
                                Component.translatable("poweredtfc.display.temperature_color"),
                                Component.translatable("poweredtfc.display.blast.input_count"),
                                Component.translatable("poweredtfc.display.blast.catalyst_count"),
                                Component.translatable("poweredtfc.display.blast.fuel_count"),
                                Component.translatable("poweredtfc.display.blast.compact")))
                        .titled(Component.translatable("poweredtfc.display.mode")), CONFIG_MODE);
            }
        }

        @Override
        public Component getName() {
            return Component.translatable("block.tfc.blast_furnace");
        }
    }
}

