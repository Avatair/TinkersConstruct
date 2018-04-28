package slimeknights.tconstruct.library.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ToolASMHelper {
	private static boolean isTinkerSwordWeapon(Item itemIn) {
		if( !(itemIn instanceof ToolCore) )
			return false;
		return ((ToolCore)itemIn).canContainSwordEnchantments();
	}
	
	private static boolean isTinkerDigTool(Item itemIn) {
		if( !(itemIn instanceof ToolCore) )
			return false;
		return ((ToolCore)itemIn).canContainDigToolEnchantments();
	}
	
	private static boolean isTinkerRangeWeapon(Item itemIn) {
		if( !(itemIn instanceof ToolCore) )
			return false;
		return ((ToolCore)itemIn).canContainBowEnchantments();
	}
	
	// asm entry points
	
	public static boolean canEnchantWeaponItem(Item itemIn) {
		return /*itemIn instanceof ItemSword ||*/ isTinkerSwordWeapon(itemIn);	// 7
	}
	
	public static boolean canEnchantDiggerItem(Item itemIn) {
		return /*itemIn instanceof ItemTool ||*/ isTinkerDigTool(itemIn);	// 8
	}
	
	public static boolean canEnchantBowItem(Item itemIn) {
		return /*itemIn instanceof ItemBow ||*/ isTinkerRangeWeapon(itemIn);	// 11
	}

}
