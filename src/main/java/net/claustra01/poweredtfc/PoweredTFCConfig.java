package net.claustra01.poweredtfc;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PoweredTFCConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue BLAZE_BURNER_HEAT_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue FAN_BASE_AIR;
    public static final ModConfigSpec.DoubleValue FAN_SPEED_MULTIPLIER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Heat delivered by Create Blaze Burner (per heat level step).")
                .push("blaze_burner");
        BLAZE_BURNER_HEAT_PER_LEVEL = builder
                .comment("Temperature (in degrees C) provided per heat level step. Default: 400 (Seething = 1600C).")
                .defineInRange("heatPerLevel", 400.0d, 0.0d, 10000.0d);
        builder.pop();

        builder.comment("Air delivered by Create Encased Fan when blowing horizontally.")
                .push("encased_fan");
        FAN_BASE_AIR = builder
                .comment("Base air amount (default matches SulidaeUtils 1.20.1 value: 100).")
                .defineInRange("baseAir", 100.0d, 0.0d, 10000.0d);
        FAN_SPEED_MULTIPLIER = builder
                .comment("Multiplier applied to absolute fan speed before adding to baseAir.")
                .defineInRange("speedMultiplier", 1.0d, 0.0d, 100.0d);
        builder.pop();

        SPEC = builder.build();
    }

    private PoweredTFCConfig() {}
}
