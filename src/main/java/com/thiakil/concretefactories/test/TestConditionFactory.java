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

package com.thiakil.concretefactories.test;

import com.google.gson.JsonObject;
import com.thiakil.concretefactories.api.RecipeFactory;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * Created by Thiakil on 13/11/2017.
 */
@RecipeFactory.Condition("ajhafjhdshj:test")
public class TestConditionFactory implements IConditionFactory{
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        return null;
    }
}
