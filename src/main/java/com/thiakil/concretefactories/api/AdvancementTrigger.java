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

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.advancements.critereon.ItemPredicates;

import java.util.function.Function;

/**
 * Automatically registers an Advancement Trigger. Your class MUST implement {@link ICriterionTrigger}.
 * Must have a no-args constructor. It will be instantiated on {@link CriteriaTriggers} static init. This is USUALLY on world load,
 * but this is not guaranteed if another mod registers their own at some stage during init. DO NOT rely on your other mod fields being available!
 *
 * Please do NOT use this just to register an Item predicate for the Inventory Changed trigger,
 * use {@link ItemPredicates#register(ResourceLocation, Function)} for that instead.
 */
public @interface AdvancementTrigger {
    /**
     * @return the String version of the ResourceLocation for your trigger
     */
    String value();
}
