/*
 * This file is part of ConcreteFactories. Copyright 2017 Thiakil
 *
 * You MAY (in addition to the licence terms) repackage this class in order to use it without the whole API.
 * Repackaging means to COPY/MOVE it INTO YOU OWN package name, in place of com.thiakil.concretefactories.api
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A helper class for implementing a new advancement trigger, without all the boilerplate that vanilla seems to include
 * in every trigger instance.
 *
 * It simplifies the granting of criteria using the {@link AbstractAdvancementTrigger#grant(net.minecraft.entity.player.EntityPlayerMP, java.util.function.Predicate)}
 * method which simply needs you to implement a Predicate. See the grant method for more details.
 *
 * @param <T> the class that implements {@link ICriterionInstance} for storing deserialised trigger conditions.
 *
 * See {@link ExampleTrigger} for an example implementation.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAdvancementTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

    private final ResourceLocation ID;
    private final Map<PlayerAdvancements, Set<Listener<T>>> listeners = Maps.newHashMap();

    protected AbstractAdvancementTrigger(ResourceLocation loc){
        this.ID = loc;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> relevantListeners = this.listeners.get(playerAdvancementsIn);

        if (relevantListeners == null)
        {
            relevantListeners = Sets.newHashSet();
            this.listeners.put(playerAdvancementsIn, relevantListeners);
        }

        relevantListeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> relevantListeners = this.listeners.get(playerAdvancementsIn);

        if (relevantListeners != null)
        {
            relevantListeners.remove(listener);

            if (relevantListeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    /**
     * Grants the criterion to the advancements if the predicate evaluates to true
     * @param player the player we're testing/granting
     * @param instancePredicate a predicate that takes your instance data as input and does whatever is necessary to check if the player has achieved it.
     */
    protected void grant(EntityPlayerMP player, Predicate<T> instancePredicate){
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        Set<Listener<T>> relevantListeners = this.listeners.get(player.getAdvancements());

        if (relevantListeners != null)
        {
            for (Listener<T> listener : relevantListeners)
            {
                if (instancePredicate.test(listener.getCriterionInstance()))
                {
                    listener.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}
