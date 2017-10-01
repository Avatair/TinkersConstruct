package slimeknights.tconstruct.smeltery.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class TileTinkersAnvil extends TileTable implements ISidedInventory {

	public TileTinkersAnvil() {
		super("tinker_anvil", 2, 1); // 2 slots. 0 == input, 1 == output

		// use a SidedInventory Wrapper to respect the canInsert/Extract calls
		this.itemHandler = new SidedInvWrapper(this, EnumFacing.DOWN);
	}

	public void interact(EntityPlayer player) {
		// completely empty -> insert current item into input
		if (!isStackInSlot(0) && !isStackInSlot(1)) {
			ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
			setInventorySlotContents(0, stack);
		}
		// take item out
		else {
			// take out of stack 1 if something is in there, 0 otherwise
			int slot = isStackInSlot(1) ? 1 : 0;

			// Additional Info: Only 1 item can only be put into the casting block usually,
			// however recipes
			// can have Itemstacks with stacksize > 1 as output
			// we therefore spill the whole contents on extraction
			ItemStack stack = getStackInSlot(slot);
			/*
			 * if(slot == 1) { FMLCommonHandler.instance().firePlayerSmeltedEvent(player,
			 * stack); }
			 */
			ItemHandlerHelper.giveItemToPlayer(player, stack);
			setInventorySlotContents(slot, ItemStack.EMPTY);

			// send a block update for the comparator, needs to be done after the stack is
			// removed
			if (slot == 1) {
				this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			}
		}
	}

	@Override
	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
		PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (isStackInSlot(i)) {
				PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i), this.getWorld(), null);
				assert item != null;
//				item.s = 0.875f;// * 0.875f;
				item.s = 0.3f;
				item.y -= 1 / 16f * 0.875f; //item.s;

				// item.s = 1f;
				toDisplay.items.add(item);
				if (i == 0) {
					item.y -= 0.001f; // don't overlap
				}
			}
		}

		return state.withProperty(BlockTable.INVENTORY, toDisplay);
	}

	@Nonnull
	@Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
		return index == 0 && !isStackInSlot(1);
	}

	@Override
	public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
		return index == 1;
	}
}
