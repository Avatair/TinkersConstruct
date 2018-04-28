package slimeknights.tconstruct.tools.common.client;

import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;

public class GuiTinkersAnvil extends GuiToolStationBase {

	public GuiTinkersAnvil(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile) {
		super(playerInv, world, pos, tile, Util.getResource("textures/gui/tinkersanvil.png"));

		metal();

		xOffsetPartSlots += 47;
		bArrangeRemainingsVertically = true;
	}

	@Override
	public Set<ToolCore> getBuildableItems() {
		return TinkerRegistry.getToolForgeCrafting();
	}

}
