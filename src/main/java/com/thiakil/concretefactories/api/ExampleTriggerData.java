/*
 * This file is example code which you may modify as you see fit.
 * Please rename it and place in your own package.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.thiakil.concretefactories.api;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;

/**
 * Example data storage class for ExampleTrigger.
 */
public class ExampleTriggerData extends AbstractCriterionInstance {

    private int paramInt;

    public ExampleTriggerData(int paramIn) {
        super(ExampleTrigger.ID);
        paramInt = paramIn;
    }

    /**
     * Tests if the parameter matches this instance
     * @param testParam the value to test against this instance
     * @return true if matches, false otherwise
     */
    public boolean test(int testParam){
        return testParam == paramInt;
    }
}
