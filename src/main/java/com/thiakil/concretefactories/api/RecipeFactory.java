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

package com.thiakil.concretefactories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of annotations for auto registration of factory classes. Follows same rules as standard Forge;
 * Classes annotated with these must have a no-args constructor and implement the relevant interface.
 * They are inserted into the registry after JSON loaded factories are.
 */
public @interface RecipeFactory {

    /**
     * {@link net.minecraftforge.common.crafting.IConditionFactory} interface implementer
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Condition {
        /**
         * @return String version of the ResourceLocation to register under. e.g. <code>yourmod:yourfactory</code>
         */
        String value();
    }

    /**
     * {@link net.minecraftforge.common.crafting.IRecipeFactory} interface implementor.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Recipe {
        /**
         * @return String version of the ResourceLocation to register under. e.g. <code>yourmod:yourfactory</code>
         */
        String value();
    }

    /**
     * {@link net.minecraftforge.common.crafting.IIngredientFactory} interface implementor.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Ingredient {
        /**
         * @return String version of the ResourceLocation to register under. e.g. <code>yourmod:yourfactory</code>
         */
        String value();
    }
}
