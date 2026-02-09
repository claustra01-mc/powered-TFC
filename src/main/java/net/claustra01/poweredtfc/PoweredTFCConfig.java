package net.claustra01.poweredtfc;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PoweredTFCConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue BLAZE_BURNER_HEAT_SMOULDERING;
    public static final ModConfigSpec.DoubleValue BLAZE_BURNER_HEAT_FADING;
    public static final ModConfigSpec.DoubleValue BLAZE_BURNER_HEAT_KINDLED;
    public static final ModConfigSpec.DoubleValue BLAZE_BURNER_HEAT_SEETHING;
    public static final ModConfigSpec.DoubleValue FAN_BASE_AIR;
    public static final ModConfigSpec.DoubleValue FAN_SPEED_MULTIPLIER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Heat delivered by Create Blaze Burner (per heat level step).")
                .push("blaze_burner");
        BLAZE_BURNER_HEAT_SMOULDERING = builder
                .comment("Temperature provided at Smouldering level. Default: 100C (Hot).")
                .defineInRange("smoulderingHeat", 100.0d, 0.0d, 10000.0d);
        BLAZE_BURNER_HEAT_FADING = builder
                .comment("Temperature provided at Fading level. Default: 1100C (Yellow).")
                .defineInRange("fadingHeat", 1100.0d, 0.0d, 10000.0d);
        BLAZE_BURNER_HEAT_KINDLED = builder
                .comment("Temperature provided at Kindled level. Default: 1300C (Yellow White).")
                .defineInRange("kindledHeat", 1300.0d, 0.0d, 10000.0d);
        BLAZE_BURNER_HEAT_SEETHING = builder
                .comment("Temperature provided at Seething level. Default: 1600C (Brilliant White).")
                .defineInRange("seethingHeat", 1600.0d, 0.0d, 10000.0d);
        builder.pop();

        builder.comment("Air delivered by Create Encased Fan when blowing horizontally.")
                .push("encased_fan");
        FAN_BASE_AIR = builder
                .comment("Base air amount. Default: 100.")
                .defineInRange("baseAir", 100.0d, 0.0d, 10000.0d);
        FAN_SPEED_MULTIPLIER = builder
                .comment("Multiplier applied to absolute fan speed before adding to baseAir. Default: 1.0.")
                .defineInRange("speedMultiplier", 1.0d, 0.0d, 100.0d);
        builder.pop();

        SPEC = builder.build();
    }

    private PoweredTFCConfig() {}
}
