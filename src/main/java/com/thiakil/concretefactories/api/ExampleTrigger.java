/*
 * This file is example code which you may modify as you see fit.
 * Please rename it and place in your own package.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.thiakil.concretefactories.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Predicate;


/**
 * Basic example which takes a single int 'from' value from the JSON.
 * In your class, you supply the ResourceLocation to the super(), and implement a deserializeInstance method.
 * An 'Instance' (MCP name) stores the data from the requirements specified in the advancement json. This example calls it 'data'
 *
 * You SHOULD probably also make a method to test & trigger the advancement if the criteria are met.
 * In contrast with achievements, you don't grant the advancement directly. Instead, you keep track of whatever data is
 * necessary (potentially persistently, e.g. apples eaten), and test for that value reaching the json-defined value using this class.
 *
 * See {@link ExampleTrigger#trigger(net.minecraft.entity.player.EntityPlayerMP, int)} for an example of this function.
 */
public class ExampleTrigger extends AbstractAdvancementTrigger<ExampleTriggerData> {

    public static final ResourceLocation ID = new ResourceLocation("concretefactories", "example_trigger");

    public ExampleTrigger(){
        super(ID);
    }

    /**
     * Read your data from the JSON object here.
     * @return an instance of your data object with the configured values.
     * Throw a {@link JsonSyntaxException} on parsing failure (required field missing, etc).
     */
    @Override
    @Nonnull
    public ExampleTriggerData deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        int paramInt = JsonUtils.getInt(json, "from");
        return new ExampleTriggerData(paramInt);
    }

    /**
     * Public function to easily grant the appropriate critereon for the particular player's advancements tracker.
     * See {@link AbstractAdvancementTrigger#grant(EntityPlayerMP, Predicate)} for the helper function to use.
     *
     * In this example, the Data object has its own method for testing (e.g. input item matches specified item),
     * but you could just as easily use the fields of your Data object directly in your predicate.
     *
     * Call yours whatever you like. E.g blockBroken(player, block) could be called from your special tool, and checks
     * if any advancements are listening for that block.
     *
     * @param player player to test
     * @param testParam 'from' value to test
     */
    public void trigger(EntityPlayerMP player, int testParam)
    {
        grant(player, instance->instance.test(testParam));
    }
}
