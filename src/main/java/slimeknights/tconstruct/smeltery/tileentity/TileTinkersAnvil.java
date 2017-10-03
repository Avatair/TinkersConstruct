package slimeknights.tconstruct.smeltery.tileentity;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.common.client.GuiButtonRepair;
import slimeknights.tconstruct.tools.common.tileentity.TileToolForge;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;
import slimeknights.tconstruct.tools.tools.Hammer;

public class TileTinkersAnvil extends TileToolForge /* implements ISidedInventory */ {

	public TileTinkersAnvil() {
		super("gui.tinkersanvil.name"); // 2 slots. 0 == input, 1 == output

		// use a SidedInventory Wrapper to respect the canInsert/Extract calls
		// this.itemHandler = new SidedInvWrapper(this, EnumFacing.DOWN);
	}

	public boolean interact(EntityPlayer player) {

		// Special interactions
		if (!player.isSneaking()) {
			// Maybe use crafting
			boolean bHoldsHammer = player.getHeldItemMainhand().getItem() instanceof Hammer;
			if (bHoldsHammer) {
				return maybeCraft(player);
			}

			return false;
		}
		/*
		 * else { // Maybe open GUI instead if( player.getHeldItemMainhand().isEmpty() )
		 * return false; }
		 */

		// completely empty -> insert current item into input
		if (!isStackInSlot(0) && !isStackInSlot(1)) {
			ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
			if (stack.isEmpty())
				return false;
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
			if (stack.isEmpty())
				return false;
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

		return true;
	}

	/*
	 * @Override protected IExtendedBlockState
	 * setInventoryDisplay(IExtendedBlockState state) { PropertyTableItem.TableItems
	 * toDisplay = new PropertyTableItem.TableItems();
	 * 
	 * for (int i = 0; i < this.getSizeInventory(); i++) { if (isStackInSlot(i)) {
	 * PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i),
	 * this.getWorld(), null); assert item != null; // item.s = 0.875f;// * 0.875f;
	 * item.s = 0.5f; item.y -= 1 / 16f * 0.875f; //item.s;
	 * 
	 * // item.s = 1f; toDisplay.items.add(item); if (i == 0) { item.y -= 0.001f; //
	 * don't overlap } } }
	 * 
	 * return state.withProperty(BlockTable.INVENTORY, toDisplay); }
	 */

	@Override
	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
		PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

		ToolBuildGuiInfo info = GuiButtonRepair.info;
		/*
		 * Disabled for now // todo: evaluate this again
		 * if(Minecraft.getMinecraft().currentScreen instanceof GuiToolStation) { info =
		 * ((GuiToolStation) Minecraft.getMinecraft().currentScreen).currentInfo; }
		 */
		float s = 0.46875f;

		for (int i = 0; i < info.positions.size(); i++) {
			ItemStack stackInSlot = getStackInSlot(i);
			PropertyTableItem.TableItem item = getTableItem(stackInSlot, this.getWorld(), null);
			if (item != null) {
				item.x = (33 - info.positions.get(i).getX()) / 61f * 0.6f;
				item.z = (42 - info.positions.get(i).getY()) / 61f;
				item.s *= s * 0.4;

				if (i == 0 || info != GuiButtonRepair.info) {
					item.s *= 2.0f;
				}

				// correct itemblock because scaling
				if (stackInSlot.getItem() instanceof ItemBlock
						&& !(Block.getBlockFromItem(stackInSlot.getItem()) instanceof BlockPane)) {
					item.y = -(1f - item.s) / 2f;
				}
				
				item.y -= 1 / 16f * (0.875f + 0.3);

				// item.s *= 2/5f;
				toDisplay.items.add(item);
			}
		}

		// add inventory if needed
		return state.withProperty(BlockTable.INVENTORY, toDisplay);
	}

	/*
	 * @Nonnull
	 * 
	 * @Override public int[] getSlotsForFace(@Nonnull EnumFacing side) { return new
	 * int[] { 0, 1 }; }
	 * 
	 * @Override public boolean canInsertItem(int index, @Nonnull ItemStack
	 * itemStackIn, @Nonnull EnumFacing direction) { return index == 0 &&
	 * !isStackInSlot(1); }
	 * 
	 * @Override public boolean canExtractItem(int index, @Nonnull ItemStack
	 * stack, @Nonnull EnumFacing direction) { return index == 1; }
	 */

	public boolean maybeCraft(EntityPlayer playerIn) {
		ItemStack heldItem = playerIn.getHeldItemMainhand();
		Item item = heldItem.getItem();
		if (item instanceof Hammer) {
			if (playerIn.getCooldownTracker().hasCooldown(heldItem.getItem()))
				return true;

			if (world.isRemote) {
				Random rand = world.rand;

				if (rand.nextInt(2) == 0) {
					EnumParticleTypes type;
					if (rand.nextInt(4) != 0) {
						type = EnumParticleTypes.FLAME;
					} else
						type = EnumParticleTypes.LAVA;
					double d8 = getPos().getX() + 0.25 + (double) rand.nextFloat() * 0.5;
					double d4 = getPos().getY() + 1.0f;// stateIn.getBoundingBox(worldIn, pos).maxY;
					double d6 = getPos().getZ() + 0.25 + (double) rand.nextFloat() * 0.5;
					world.spawnParticle(type, d8, d4, d6, 0.0D, 0.0D, 0.0D, new int[0]);
				}

				this.world.playSound((double) this.getPos().getX() + 0.5D, (double) this.getPos().getY() + 0.5D,
						(double) this.getPos().getZ() + 0.5D, Sounds.anvil_hit, SoundCategory.BLOCKS, 0.25F, 1.0F,
						false);
			} else {
				playerIn.getCooldownTracker().setCooldown(heldItem.getItem(), 15);
				ToolHelper.damageTool(heldItem, 1, playerIn);
			}

			return true;
		}

		return false;
	}
}
