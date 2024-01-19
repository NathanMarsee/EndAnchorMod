/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package org.mbf.endanchor.block.recipe;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.mbf.endanchor.block.ModBlocks;

public class EndAnchorRecipe extends SpecialCraftingRecipe {
    public EndAnchorRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        if(!CompassItem.hasLodestone(inventory.getStack(4))) {
            return false;
        }
        Optional<RegistryKey<World>> optional = EndAnchorRecipe.getLodestoneDimension(inventory.getStack(4).getNbt());
        if(optional.isPresent() && optional.get() != World.END) {
            return false;
        }
        return inventory.getStack(0).getItem() == Items.OBSIDIAN && inventory.getStack(1).getItem() == Items.POPPED_CHORUS_FRUIT &&
                inventory.getStack(2).getItem() == Items.OBSIDIAN && inventory.getStack(3).getItem() == Items.POPPED_CHORUS_FRUIT &&
                inventory.getStack(4).getItem() == Items.COMPASS &&
                inventory.getStack(5).getItem() == Items.POPPED_CHORUS_FRUIT && inventory.getStack(6).getItem() == Items.OBSIDIAN &&
                inventory.getStack(7).getItem() == Items.POPPED_CHORUS_FRUIT && inventory.getStack(8).getItem() == Items.OBSIDIAN;


    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack endAnchor = new ItemStack(ModBlocks.END_ANCHOR);
        endAnchor.getOrCreateNbt();
        NbtCompound nbt = inventory.getStack(4).getNbt();
            endAnchor.getNbt().put(CompassItem.LODESTONE_POS_KEY, inventory.getStack(4).getNbt().get(CompassItem.LODESTONE_POS_KEY));
            endAnchor.getNbt().put(CompassItem.LODESTONE_DIMENSION_KEY, inventory.getStack(4).getNbt().get(CompassItem.LODESTONE_DIMENSION_KEY));
        return endAnchor;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.END_ANCHOR_RECIPE_SERIALIZER;
    }

    private static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get(CompassItem.LODESTONE_DIMENSION_KEY)).result();
    }


}
