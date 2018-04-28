/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package slimeknights.tconstruct.library.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import slimeknights.tconstruct.library.asm.ASMHelper;
import slimeknights.tconstruct.library.asm.ObfHelper;

public class EnumEnchantmentTypeTransformer implements IClassTransformer
{
	private static final String[] CAN_ENCHANT_ITEM_NAMES = new String[] { "canEnchantItem", "func_77557_a", "a" };
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		// TODO Auto-generated method stub
		boolean isObfuscated = !transformedName.equals(name);
		
		if (transformedName.equals("net.minecraft.enchantment.EnumEnchantmentType$7")) {
			return transformInjectIsEnchantableMethod(basicClass, "canEnchantWeaponItem", isObfuscated);
		}
		else if (transformedName.equals("net.minecraft.enchantment.EnumEnchantmentType$8")) {
			return transformInjectIsEnchantableMethod(basicClass, "canEnchantDiggerItem", isObfuscated);
		}
		else if (transformedName.equals("net.minecraft.enchantment.EnumEnchantmentType$11")) {
			return transformInjectIsEnchantableMethod(basicClass, "canEnchantBowItem", isObfuscated);
		}
		return basicClass;
	}
	
	private byte[] transformInjectIsEnchantableMethod(byte[] bytes, String methodName, boolean isObfuscated) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
		
		final String className = "slimeknights/tconstruct/library/tools/ToolASMHelper";
		final String methodDescriptor = ObfHelper.createMethodDescriptor(isObfuscated, "Z", "net/minecraft/item/Item"); 
		
        //Iterate over the methods in the class
        for (MethodNode methodNode : classNode.methods)
        {
            if (ASMHelper.methodEquals(methodNode, CAN_ENCHANT_ITEM_NAMES, methodDescriptor)) {
            	
            	LabelNode defaultBranch = new LabelNode();
            	
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, className, methodName, methodDescriptor, false) );
                insnList.add(new InsnNode(Opcodes.ICONST_1));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPNE, defaultBranch));
                insnList.add(new InsnNode(Opcodes.ICONST_1));
                insnList.add(new InsnNode(Opcodes.IRETURN));
                insnList.add(defaultBranch);
                
                //Insert our new instructions before returning
                methodNode.instructions.insertBefore(methodNode.instructions.get(0), insnList);
            }
        }
		
        //Encode the altered class back into bytes
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        bytes = writer.toByteArray();
        
        return bytes;
	}
}
