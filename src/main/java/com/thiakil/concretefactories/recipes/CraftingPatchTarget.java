/*
 * This file is part of ConcreteFactories. Copyright 2017 Thiakil
 *
 * ConcreteFactories is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConcreteFactories is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ConcreteFactories.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thiakil.concretefactories.recipes;

import com.thiakil.concretefactories.ConcreteFactoriesMod;
import com.thiakil.concretefactories.api.RecipeFactory;
import com.thiakil.concretefactories.asm.ASMHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thiakil on 13/11/2017.
 */
public class CraftingPatchTarget extends ASMHelper {
    private static Map<ResourceLocation, Class<? extends IRecipeFactory>> recipeFactoriesClasses = new HashMap<>();
    private static Map<ResourceLocation, Class<? extends IConditionFactory>> conditionFactoriesClasses = new HashMap<>();
    private static Map<ResourceLocation, Class<? extends IIngredientFactory>> ingredientFactoriesClasses = new HashMap<>();

    private static Map<ResourceLocation, IRecipeFactory> recipeFactories = new HashMap<>();
    private static Map<ResourceLocation, IConditionFactory> conditionFactories = new HashMap<>();
    private static Map<ResourceLocation, IIngredientFactory> ingredientFactories = new HashMap<>();

    private static boolean instantiated = false;

    public static void setup(ASMDataTable asmData){
        gatherAnnotations(RecipeFactory.Recipe.class, IRecipeFactory.class, asmData, recipeFactoriesClasses);
        gatherAnnotations(RecipeFactory.Condition.class, IConditionFactory.class, asmData, conditionFactoriesClasses);
        gatherAnnotations(RecipeFactory.Ingredient.class, IIngredientFactory.class, asmData, ingredientFactoriesClasses);
    }

    public static void inject(){
        if (recipeFactoriesClasses.size() == 0 && conditionFactoriesClasses.size() == 0 && ingredientFactoriesClasses.size() == 0){
            return;
        }
        if (!instantiated){
            ConcreteFactoriesMod.logger.info("Constructing annotation defined recipe helpers");
            constructList(recipeFactoriesClasses, recipeFactories);
            constructList(conditionFactoriesClasses, conditionFactories);
            constructList(ingredientFactoriesClasses, ingredientFactories);
            instantiated = true;
        }
        if (recipeFactories.isEmpty() && conditionFactories.isEmpty() && ingredientFactories.isEmpty()){
            return;//no point
        }
        ConcreteFactoriesMod.logger.info("Injecting annotation defined recipe helpers");
        recipeFactories.forEach(CraftingPatchTarget::register);
        conditionFactories.forEach(CraftingPatchTarget::register);
        ingredientFactories.forEach(CraftingPatchTarget::register);
    }

    /** wrapper methods to prevent crashes borking the whole process **/
    public static void register(ResourceLocation key, IConditionFactory factory)
    {
        try {
            CraftingHelper.register(key, factory);
        } catch (IllegalStateException e){
            ConcreteFactoriesMod.logger.error("Error registering factory: ", e);
        }
    }
    public static void register(ResourceLocation key, IRecipeFactory factory)
    {
        try {
            CraftingHelper.register(key, factory);
        } catch (IllegalStateException e){
            ConcreteFactoriesMod.logger.error("Error registering factory: ", e);
        }
    }
    public static void register(ResourceLocation key, IIngredientFactory factory)
    {
        try {
            CraftingHelper.register(key, factory);
        } catch (IllegalStateException e){
            ConcreteFactoriesMod.logger.error("Error registering factory: ", e);
        }
    }
}
