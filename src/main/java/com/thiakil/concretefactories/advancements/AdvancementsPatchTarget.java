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

package com.thiakil.concretefactories.advancements;

import com.thiakil.concretefactories.ConcreteFactoriesMod;
import com.thiakil.concretefactories.api.AdvancementTrigger;
import com.thiakil.concretefactories.asm.ASMHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.util.HashMap;
import java.util.Map;

public class AdvancementsPatchTarget extends ASMHelper {
    private static Map<ResourceLocation, Class<? extends ICriterionTrigger>> triggerClasses = new HashMap<>();
    private static Map<ResourceLocation, ICriterionTrigger> triggers = new HashMap<>();
    private static boolean instantiated = false;

    public static void setup(ASMDataTable asmData){
        gatherAnnotations(AdvancementTrigger.class, ICriterionTrigger.class, asmData, triggerClasses);
    }

    public static void inject(){
        if (triggerClasses.size() == 0){
            return;
        }
        if (!instantiated) {
            ConcreteFactoriesMod.logger.info("Constructing annotation defined advancement triggers");
            constructList(triggerClasses, triggers);
            instantiated = true;
        }
        if (triggers.size() == 0){
            return;
        }
        ConcreteFactoriesMod.logger.info("Injecting annotation defined advancement triggers");
        triggers.forEach((loc,trigger)-> {
            try {
                CriteriaTriggers.register(trigger);
            } catch (IllegalArgumentException e){
                ConcreteFactoriesMod.logger.error("Error registering trigger: ", e);
            }
        });
    }
}
