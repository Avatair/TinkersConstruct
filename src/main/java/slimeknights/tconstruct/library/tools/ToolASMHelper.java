package slimeknights.tconstruct.library.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ToolASMHelper {
	public static boolean canEnchantWeaponItem(Item itemIn) {
		return itemIn instanceof ItemSword;	// 7
	}
	
	public static boolean canEnchantDiggerItem(Item itemIn) {
		return itemIn instanceof ItemTool;	// 8
	}
	
	public static boolean canEnchantBowItem(Item itemIn) {
		return itemIn instanceof ItemTool;	// 11
	}

}
