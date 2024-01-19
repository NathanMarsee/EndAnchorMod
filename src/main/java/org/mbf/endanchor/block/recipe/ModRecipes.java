package org.mbf.endanchor.block.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.mbf.endanchor.EndAnchor;

public class ModRecipes {
    public static final RecipeSerializer<EndAnchorRecipe> END_ANCHOR_RECIPE_SERIALIZER = ModRecipes.register("end_anchor_recipe", new SpecialRecipeSerializer<EndAnchorRecipe>(EndAnchorRecipe::new));


    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        Identifier identifier = new Identifier(EndAnchor.MOD_ID, id);
        return (S)Registry.register(Registries.RECIPE_SERIALIZER, identifier, serializer);
    }

}
