package slimeknights.tconstruct.tools.common.client;

import java.io.IOException;
import java.util.Set;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.common.inventory.ContainerToolStation;
import slimeknights.tconstruct.tools.common.network.ToolStationTextPacket;
import slimeknights.tconstruct.tools.common.tileentity.TileToolStation;

public class GuiToolStation extends GuiToolStationBase {

	private static final GuiElement TextFieldActive = new GuiElement(0, 210, 102, 12, 256, 256);

	public GuiTextField textField;

	public GuiToolStation(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile,
			ResourceLocation backgroundTexture) {
		super(playerInv, world, pos, tile, backgroundTexture);

//		metal();
	}

	public GuiToolStation(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile) {
		super(playerInv, world, pos, tile, Util.getResource("textures/gui/toolstation.png"));

//		metal();
	}

	@Override
	public void initGui() {
		super.initGui();

		textField = new GuiTextField(0, fontRenderer, cornerX + 70, cornerY + 7, 92, 12);
		// textField.setFocused(true);
		// textField.setCanLoseFocus(false);
		textField.setEnableBackgroundDrawing(false);
		textField.setMaxStringLength(40);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!textField.isFocused()) {
			super.keyTyped(typedChar, keyCode);
		} else {
			if (keyCode == 1) {
				this.mc.player.closeScreen();
			}

			textField.textboxKeyTyped(typedChar, keyCode);
			TinkerNetwork.sendToServer(new ToolStationTextPacket(textField.getText()));
			((ContainerToolStation) container).setToolName(textField.getText());
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		textField.updateCursorCounter();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		if (textField.isFocused()) {
			TextFieldActive.draw(cornerX + 68, cornerY + 6);
		}

		// draw textfield
		textField.drawTextBox();
	}

	@Override
	public Set<ToolCore> getBuildableItems() {
		return TinkerRegistry.getToolForgeCrafting();
	}

}
