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

package com.thiakil.concretefactories;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.thiakil.concretefactories.advancements.AdvancementsPatchTarget;
import com.thiakil.concretefactories.recipes.CraftingPatchTarget;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarFile;

/*@Mod(
        modid = ConcreteFactoriesMod.MOD_ID,
        name = ConcreteFactoriesMod.MOD_NAME,
        version = ConcreteFactoriesMod.VERSION
)*/
public class ConcreteFactoriesMod extends DummyModContainer {

    public static final String MOD_ID = "concretefactories";
    public static final String MOD_NAME = "Concrete Factories";
    public static final String VERSION = "1.0";

    public static Logger logger = LogManager.getLogger("ConcreteFactories");

    public static File myModFile;

    public ConcreteFactoriesMod(){
        super(loadMcmodInfo());
    }

    public static ConcreteFactoriesMod INSTANCE = new ConcreteFactoriesMod();

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void handleModStateEvent(FMLEvent event){
        if (event instanceof FMLPreInitializationEvent){
            CraftingPatchTarget.setup(((FMLPreInitializationEvent) event).getAsmData());
            AdvancementsPatchTarget.setup(((FMLPreInitializationEvent) event).getAsmData());
        }
    }

    private static ModMetadata loadMcmodInfo(){
        InputStream is = null;
        if (myModFile != null) {
            try {
                if (myModFile.isDirectory()){
                    //this will probably never be reached, oh well
                    is = new FileInputStream(new File(myModFile, "mcmod.info"));
                } else {
                    JarFile myjar = new JarFile(myModFile);
                    is = myjar.getInputStream(myjar.getJarEntry("mcmod.info"));
                }
            } catch (Exception e) {
                ConcreteFactoriesMod.logger.error("Could not load mcmod.info", e);
            }
        }
        Map<String, Object> dummyMeta = ImmutableMap.<String, Object>builder().put("name", MOD_NAME).put("version", VERSION).build();
        return MetadataCollection.from(is, MOD_ID).getMetadataForId(MOD_ID, dummyMeta);
    }

}
