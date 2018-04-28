package slimeknights.tconstruct.tools.common.inventory;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;

public class ContainerTinkersAnvil extends ContainerToolStation {

	public ContainerTinkersAnvil(InventoryPlayer playerInventory, TileToolStation tile, boolean bHasOut) {
		super(playerInventory, tile, bHasOut, false);
	}

	@Override
	protected void playCraftSound(EntityPlayer player) {
		// No sound
	}

	@Override
	protected Set<ToolCore> getBuildableTools() {
		return TinkerRegistry.getToolForgeCrafting();
	}

	public ItemStack performAnvilCrafting(EntityPlayer player) {
		ItemStack stack = out.getStack();
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		stack = stack.copy();

		ItemStack stack2 = out.onTake(player, stack);
		if (!tile.isStackInSlot(0)) {
			tile.setInventorySlotContents(0, stack2);
		} else {
			player.dropItem(stack2, false);
		}

		return stack2;
	}
}
