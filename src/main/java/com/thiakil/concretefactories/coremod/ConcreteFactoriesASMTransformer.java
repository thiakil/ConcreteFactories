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

package com.thiakil.concretefactories.coremod;

import com.thiakil.concretefactories.ConcreteFactoriesMod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;
import java.util.function.Predicate;

public class ConcreteFactoriesASMTransformer implements IClassTransformer {
    private static String CRAFTINGHELPER_CLASS = "net.minecraftforge.common.crafting.CraftingHelper";
    private static String CRITERIATRIGGERS_CLASS = "net.minecraft.advancements.CriteriaTriggers";

    private static Predicate<AbstractInsnNode> LOADFACTORIES_PREDICATE = abstractInsnNode -> {
        if (!(abstractInsnNode instanceof InvokeDynamicInsnNode)){
            return false;
        }
        for (Object o : ((InvokeDynamicInsnNode) abstractInsnNode).bsmArgs){
            if (o instanceof Handle){
                Handle handle = (Handle)o;
                if (handle.getTag()==Opcodes.H_INVOKESTATIC && handle.getName().equals("loadFactories")){
                    return true;
                }
            }
        }
        return false;
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(CRAFTINGHELPER_CLASS) && !transformedName.equals(CRITERIATRIGGERS_CLASS)) {
            return basicClass;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader( basicClass );
        classReader.accept( classNode, 0 );

        boolean needsRewrite = false;

        if (transformedName.equals(CRAFTINGHELPER_CLASS)){
            needsRewrite = patchLoadRecipesZ(classNode);
        } else if (transformedName.equals(CRITERIATRIGGERS_CLASS)){
            needsRewrite = patchTriggersInit(classNode);
        }

        if (needsRewrite){
            final ClassWriter writer = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
            classNode.accept( writer );
            return writer.toByteArray();
        } else {
            return basicClass;
        }
    }

    private boolean patchLoadRecipesZ(ClassNode classNode){
        boolean foundMethod = false;
        for (MethodNode methodNode : classNode.methods){
            if (methodNode.name.equals("loadRecipes") && methodNode.desc != null && methodNode.desc.equals("(Z)V")){
                ConcreteFactoriesMod.logger.info("patching net.minecraftforge.common.crafting.CraftingHelper.loadRecipes(boolean)");
                foundMethod = true;
                AbstractInsnNode insertAfter = null;
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode instruction = iterator.next();
                    /*if (instruction instanceof MethodInsnNode &&
                            ((MethodInsnNode) instruction).name.equals("setActiveModContainer") &&
                            ((MethodInsnNode) instruction).owner.equals("net/minecraftforge/fml/common/Loader")){
                        insertAfter = instruction;
                        break;
                    }*/
                    //find the InvokeDynamicInsnNode that calls CraftingHelper.loadFactories
                    if (LOADFACTORIES_PREDICATE.test(instruction)){
                        //found the node, but that's not the last instruction of that line
                        //keep going until we find a label node, which is usually a sign of a new code line
                        AbstractInsnNode localInst = instruction.getNext();
                        while (!(localInst instanceof LabelNode) && localInst != null){
                            localInst = localInst.getNext();
                        }
                        if (localInst != null) {
                            insertAfter = localInst.getPrevious();
                            break;
                        }
                    }
                }
                if (insertAfter == null){
                    ConcreteFactoriesMod.logger.error("Couldn't find the instruction to insert after :(");
                    ConcreteFactoriesMod.logger.error("Please report this");
                    return false;
                }
                methodNode.instructions.insert(insertAfter, callHook("com.thiakil.concretefactories.recipes.CraftingPatchTarget", "inject", "()V"));
                break;
            }
        }
        if (!foundMethod) {
            ConcreteFactoriesMod.logger.error("Couldn't find the loadRecipes method to patch :(");
            ConcreteFactoriesMod.logger.error("Please report this");
        }
        return foundMethod;
    }

    private boolean patchTriggersInit(ClassNode classNode){
        boolean foundMethod = false;
        for (MethodNode methodNode : classNode.methods){
            if (methodNode.name.equals("<clinit>") && methodNode.desc != null && methodNode.desc.equals("()V")){
                ConcreteFactoriesMod.logger.info("patching net.minecraft.advancements.CriteriaTriggers init");
                foundMethod = true;
                AbstractInsnNode insertAfter = null;
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode instruction = iterator.next();
                    if (instruction instanceof InsnNode && instruction.getOpcode() == Opcodes.RETURN){
                        insertAfter = instruction.getPrevious();
                        break;
                    }
                }
                if (insertAfter == null){
                    ConcreteFactoriesMod.logger.error("Couldn't find the instruction to insert after :(");
                    ConcreteFactoriesMod.logger.error("Please report this");
                    return false;
                }
                methodNode.instructions.insert(insertAfter, callHook("com.thiakil.concretefactories.advancements.AdvancementsPatchTarget", "inject", "()V"));
                break;
            }
        }
        if (!foundMethod) {
            ConcreteFactoriesMod.logger.error("Couldn't find the loadRecipes method to patch :(");
            ConcreteFactoriesMod.logger.error("Please report this");
        }
        return foundMethod;
    }

    /**
     * Make a bytecode call to the specified static function
     * @param className target classname (FQ dot notation)
     * @param method target method name
     * @param desc target method desc
     * @return and instruction list that contains a call to the target function
     */
    private static InsnList callHook(String className, String method, String desc){
        MethodNode mv = new MethodNode();
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(0xC0DE, l0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, className.replaceAll("\\.", "/"), method, desc, false);
        return mv.instructions;
    }
}
