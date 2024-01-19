package org.mbf.endanchor;

import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mbf.endanchor.block.ModBlockEntities;
import org.mbf.endanchor.block.ModBlocks;
import org.mbf.endanchor.block.recipe.EndAnchorRecipe;
import org.mbf.endanchor.block.recipe.ModRecipes;

public class EndAnchor implements ModInitializer {
    public static final String MOD_ID = "endanchor";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntityTypes();
        RecipeSerializer<EndAnchorRecipe> endAnchorRecipeSerializer = ModRecipes.END_ANCHOR_RECIPE_SERIALIZER;
    }

}
