package com.scaun.zorpal.datagen;

import java.util.function.Consumer;

import com.scaun.zorpal.setup.Registration;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public class MyRecipes extends RecipeProvider {

    public MyRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Registration.ZORPAL_ORE_ITEM),
                Registration.ZORP_INGOT.get(), 1.0f, 50)
                .unlockedBy("has_ore", has(Registration.ZORPAL_ORE_ITEM))
                .save(consumer, "zorp_ingot1");
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Registration.RAW_ZORP.get()),
                        Registration.ZORP_INGOT.get(), 1.0f, 50)
                .unlockedBy("has_chunk", has(Registration.RAW_ZORP.get()))
                .save(consumer, "zorp_ingot2");

        ShapelessRecipeBuilder.shapeless(Registration.ZORPAL_BLOCK.get())
             .requires(Registration.ZORP_INGOT.get(), 9)
             .unlockedBy("has_ingot", has(Registration.ZORP_INGOT.get()))
             .group("zorpal")
             .save(consumer);

        ShapelessRecipeBuilder.shapeless(Registration.ZORP_INGOT.get(), 9)
             .requires(Registration.ZORPAL_BLOCK_ITEM.get())
             .unlockedBy("has_ingot", has(Registration.ZORP_INGOT.get()))
             .group("zorpal")
             .save(consumer);
    }
}
