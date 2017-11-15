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

package com.thiakil.concretefactories.asm;

import com.thiakil.concretefactories.ConcreteFactoriesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ASMHelper {

    /** safely construct each class, placing it in the dest map if it didnt error **/
    protected static <T> void constructList(Map<ResourceLocation, Class<? extends T>> src, Map<ResourceLocation, T> dest){
        src.forEach((resourceLocation, clazz) -> {
            try {
                dest.put(resourceLocation, clazz.getConstructor().newInstance());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassCastException e){
                ConcreteFactoriesMod.logger.error("Error constructing "+clazz.getName()+": ", e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected static <T,U> void gatherAnnotations(Class<T> clazzAnnotation, Class<U> clazzImpl, ASMDataTable datas, Map<ResourceLocation, Class<? extends U>> dest){
        datas.getAll(clazzAnnotation.getName()).forEach(data->{
            Class<?> clazz = Object.class;//dummy value to make IDEA shutup
            try {
                clazz = Class.forName(data.getClassName());
            } catch (ClassNotFoundException e) {
                ConcreteFactoriesMod.logger.error("Somehow could not load Class object for "+data.getClassName()+", your install must be borked.");
            }
            if (clazzImpl.isAssignableFrom(clazz)) {
                if (data.getAnnotationInfo().containsKey("value")) {
                    String val = (String) data.getAnnotationInfo().get("value");
                    try {
                        dest.put(new ResourceLocation(val), (Class<? extends U>)clazz);
                        ConcreteFactoriesMod.logger.info("Found new annotated "+clazzImpl.getSimpleName()+" : "+data.getClassName());
                    } catch (ClassCastException e){
                        ConcreteFactoriesMod.logger.error("Could not load "+data.getClassName()+"???", e);
                    }
                } else {
                    ConcreteFactoriesMod.logger.error("Internal annotation processing failure. Annotation value not found. WTF?!");
                }
            } else {
                ConcreteFactoriesMod.logger.error("@"+clazzAnnotation.getSimpleName()+" annotated class does not implement "+clazzImpl.getSimpleName()+": "+data.getClassName()+", skipping.");
            }
        });
    }
}
