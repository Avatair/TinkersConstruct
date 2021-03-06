package slimeknights.tconstruct.smeltery.tileentity;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.tools.common.client.GuiButtonRepair;
import slimeknights.tconstruct.tools.common.client.GuiTinkersAnvil;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkersAnvil;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;
import slimeknights.tconstruct.tools.tools.Hammer;

public class TileTinkersAnvil extends TileToolStation /* implements ISidedInventory */ {

	public static final String PROGRESS_TAG = "progress";
	
	private long lastHitTime = -1000;

	public TileTinkersAnvil() {
		super("gui.tinkersanvil.name"); // 2 slots. 0 == input, 1 == output

		// use a SidedInventory Wrapper to respect the canInsert/Extract calls
		// this.itemHandler = new SidedInvWrapper(this, EnumFacing.DOWN);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiTinkersAnvil(inventoryplayer, world, pos, this);
	}

	@Override
	public ContainerTinkersAnvil createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerTinkersAnvil(inventoryplayer, this, false);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		NBTBase progress = tag.getTag(PROGRESS_TAG);
		if (progress != null) {
			getTileData().setTag(PROGRESS_TAG, progress);
		}
		super.onDataPacket(net, pkt);
	}

	public void setProgress(int progress) {
		getTileData().setInteger(PROGRESS_TAG, progress);
	}

	public int getProgress() {
		return getTileData().getInteger(PROGRESS_TAG);
	}

	public boolean interact(EntityPlayer player) {
		ItemStack heldItemStack = player.getHeldItemMainhand();
		Item heldItem = heldItemStack.getItem();

		// Special interactions
//		if (!player.isSneaking()) {
//			// Maybe use crafting
//			if (heldItem instanceof Hammer) {
//				return maybeCraft(player);
//			}
//		} else {
//			if (!(heldItem instanceof Hammer))
//				return false;
//		}
		
		if (player.isSneaking())
			return false;

		if (!heldItemStack.isEmpty()) {
			// find next empty space
			int emptySlot = -1;
			for (int i = 0; i < this.getSizeInventory(); i++) {
				if (!isStackInSlot(i)) {
					emptySlot = i;
					break;
				}
			}

			if (emptySlot != -1) {
				// put on anvil by prio
				if (heldItem instanceof ToolPart) {
					ToolPart toolPartItem = (ToolPart) heldItem;
					int[] slotPrio = toolPartItem.getSlotPrio();

					boolean foundSlot = false;
					for (int i : slotPrio) {
						if (!isStackInSlot(i)) {
							ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
							setInventorySlotContents(i, stack);
							foundSlot = true;
							break;
						}
					}

					if (!foundSlot) {
						ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
						setInventorySlotContents(emptySlot, stack);
					}
				} else if (heldItem instanceof TinkerToolCore) {
					// try to add to slot 0 first
					if (!isStackInSlot(0)) {
						ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
						setInventorySlotContents(0, stack);
					} else {
						ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
						setInventorySlotContents(emptySlot, stack);
					}
				} else {
					// otherwise put into next empty slot, but to slot 0 at least
					// TODO: Stack up for stackable items
					ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
					if (emptySlot == 0) {
						boolean bFound = false;
						for (int i = 1; i < this.getSizeInventory(); i++) {
							if (!isStackInSlot(i)) {
								setInventorySlotContents(i, stack);
								bFound = true;
								break;
							}
						}

						if (!bFound)
							setInventorySlotContents(0, stack);
					} else
						setInventorySlotContents(emptySlot, stack);
				}

				return true;
			}
		} else {
			// try to take item out from first slot
			if (isStackInSlot(0)) {
				ItemStack stack = getStackInSlot(0);
				ItemHandlerHelper.giveItemToPlayer(player, stack);
				setInventorySlotContents(0, ItemStack.EMPTY);
				return true;
			} else {
				// take item out from last non empty slot
				// find next empty space
				for (int i = this.getSizeInventory() - 1; i >= 0; i--) {
					if (isStackInSlot(i)) {
						ItemStack stack = getStackInSlot(i);
						ItemHandlerHelper.giveItemToPlayer(player, stack);
						setInventorySlotContents(i, ItemStack.EMPTY);
						return true;
					}
				}
			}
		}

		// // completely empty -> insert current item into input
		// if (!isStackInSlot(0) && !isStackInSlot(1)) {
		// ItemStack stack =
		// player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
		// if (stack.isEmpty())
		// return false;
		// setInventorySlotContents(0, stack);
		// }
		// // take item out
		// else {
		// // take out of stack 1 if something is in there, 0 otherwise
		// int slot = isStackInSlot(1) ? 1 : 0;
		//
		// // Additional Info: Only 1 item can only be put into the casting block
		// usually,
		// // however recipes
		// // can have Itemstacks with stacksize > 1 as output
		// // we therefore spill the whole contents on extraction
		// ItemStack stack = getStackInSlot(slot);
		// if (stack.isEmpty())
		// return false;
		// /*
		// * if(slot == 1) { FMLCommonHandler.instance().firePlayerSmeltedEvent(player,
		// * stack); }
		// */
		// ItemHandlerHelper.giveItemToPlayer(player, stack);
		// setInventorySlotContents(slot, ItemStack.EMPTY);
		//
		// // send a block update for the comparator, needs to be done after the stack
		// is
		// // removed
		// if (slot == 1) {
		// this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(),
		// true);
		// }
		// }

		return false;
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

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
		// if inventory has changed, reset progress
		if (world != null && !world.isRemote)
			setProgress(0);

		super.setInventorySlotContents(slot, itemstack);
	}

	public boolean maybeCraft(EntityPlayer playerIn) {
		ItemStack heldItem = playerIn.getHeldItemMainhand();
		Item item = heldItem.getItem();
		if (item instanceof Hammer) {
			if (ToolHelper.isBroken(heldItem))
				return true;
//			if (playerIn.getCooldownTracker().hasCooldown(heldItem.getItem()))
//				return true;
			if ( getWorld().getTotalWorldTime() - lastHitTime < 6 )
				return true;
			lastHitTime = getWorld().getTotalWorldTime();

			int progress = getProgress();
			progress++;

			boolean bCraftingFinished = progress >= 25;

			ContainerTinkersAnvil theContainer = createContainer(playerIn.inventory, world, getPos());
			boolean bIsValidRecipe = !theContainer.getResult().isEmpty();
			if (world.isRemote) {
				Random rand = world.rand;

				for (int j = 0; j < rand.nextInt(3) + 2; j++) {
					double d8 = getPos().getX() + 0.25 + (double) rand.nextFloat() * 0.5;
					double d4 = getPos().getY() + 1.0f;// stateIn.getBoundingBox(worldIn, pos).maxY;
					double d6 = getPos().getZ() + 0.25 + (double) rand.nextFloat() * 0.5;
					world.spawnParticle(EnumParticleTypes.BLOCK_DUST, d8, d4, d6,
							(double) rand.nextFloat() * 0.10 - 0.05, (double) rand.nextFloat() * 0.10,
							(double) rand.nextFloat() * 0.10 - 0.05,
							new int[] { Block.getStateId(world.getBlockState(getPos())) });
				}

				if (!bCraftingFinished) {
					if (bIsValidRecipe) {
						if (rand.nextInt(2) == 0) {
							EnumParticleTypes type;
							double ySpeed = 0;
							if (rand.nextInt(4) != 0) {
								type = EnumParticleTypes.FLAME;
							} else {
								type = EnumParticleTypes.LAVA;
								ySpeed = -8.0d;
							}
							double d8 = getPos().getX() + 0.25 + (double) rand.nextFloat() * 0.5;
							double d4 = getPos().getY() + 1.0f;// stateIn.getBoundingBox(worldIn, pos).maxY;
							double d6 = getPos().getZ() + 0.25 + (double) rand.nextFloat() * 0.5;
							world.spawnParticle(type, d8, d4, d6, 0.0D, ySpeed, 0.0D, new int[0]);
						}
					}
				} else {
					for (int j = 0; j < 5; j++) {
						double d8 = getPos().getX() + 0.25 + (double) rand.nextFloat() * 0.5;
						double d4 = getPos().getY() + 1.1f;// stateIn.getBoundingBox(worldIn, pos).maxY;
						double d6 = getPos().getZ() + 0.25 + (double) rand.nextFloat() * 0.5;
						world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, d8, d4, d6, 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}

				this.world.playSound((double) this.getPos().getX() + 0.5D, (double) this.getPos().getY() + 0.5D,
						(double) this.getPos().getZ() + 0.5D, Sounds.anvil_hit, SoundCategory.BLOCKS, 0.25F, 1.0F,
						false);
			} else {
//				playerIn.getCooldownTracker().setCooldown(heldItem.getItem(), 15);
				ToolHelper.damageTool(heldItem, 1, playerIn);

				if (bCraftingFinished || theContainer.getResult().isEmpty())
					progress = 0;
				setProgress(progress);
			}

			// perform crafting
			if (bCraftingFinished) {
				theContainer.performAnvilCrafting(playerIn);
			}

			return true;
		}

		return false;
	}
}
