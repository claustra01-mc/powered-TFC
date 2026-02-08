package net.claustra01.poweredtfc;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PoweredTFC.MODID)
public class PoweredTFC {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "poweredtfc";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus and pass them in automatically.
    public PoweredTFC(IEventBus modEventBus, ModContainer modContainer) {
        PoweredTFCDisplaySources.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, PoweredTFCConfig.SPEC);
    }
}
