package net.claustra01.poweredtfc.mixin;

import com.mojang.datafixers.util.Pair;

import net.claustra01.poweredtfc.PoweredTFC;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuLapisMixin {
    private static final TagKey<Item> POWEREDTFC_ENCHANTING_LAPIS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(PoweredTFC.MODID, "enchanting_lapis"));

    // Fallback IDs so enchanting still works even if tags are missing/not synced (e.g. server doesn't provide our custom tag).
    private static final ResourceLocation TFC_GEM_LAPIS_LAZULI = ResourceLocation.fromNamespaceAndPath("tfc", "gem/lapis_lazuli");
    private static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI =
            ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");

    private static boolean isEnchantingLapis(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(POWEREDTFC_ENCHANTING_LAPIS)) {
            return true;
        }

        Item item = stack.getItem();
        if (item == Items.LAPIS_LAZULI) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return TFC_GEM_LAPIS_LAZULI.equals(id);
    }

    @ModifyArg(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/EnchantmentMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"
            ),
            index = 0
    )
    private Slot poweredtfc$replaceLapisSlot(Slot original) {
        // Identify the lapis slot by its menu coords and container index, so this keeps working even if other mods add slots.
        if (original.getContainerSlot() != 1 || original.x != 35 || original.y != 47) {
            return original;
        }

        Container container = original.container;
        int containerSlot = original.getContainerSlot();

        return new Slot(container, containerSlot, original.x, original.y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isEnchantingLapis(stack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_LAPIS_LAZULI);
            }
        };
    }

    @Redirect(
            method = "quickMoveStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean poweredtfc$quickMoveTreatTfcLapisAsVanilla(ItemStack stack, Item item) {
        // EnchantmentMenu#quickMoveStack hardcodes a check for Items.LAPIS_LAZULI.
        return item == Items.LAPIS_LAZULI ? isEnchantingLapis(stack) : stack.is(item);
    }
}
