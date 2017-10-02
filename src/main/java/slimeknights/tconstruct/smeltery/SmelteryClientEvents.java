package slimeknights.tconstruct.smeltery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.shared.client.BakedTableModel;
import slimeknights.tconstruct.smeltery.block.BlockTinkerAnvil;
import slimeknights.tconstruct.tools.ToolClientEvents;
import slimeknights.tconstruct.tools.tools.Hammer;

public class SmelteryClientEvents {

  // casting table/basin
  private static final String LOCATION_CastingBlock = Util.resource("casting");
  public static final ModelResourceLocation locCastingTable = new ModelResourceLocation(LOCATION_CastingBlock, "type=table");
  public static final ModelResourceLocation locCastingBasin = new ModelResourceLocation(LOCATION_CastingBlock, "type=basin");

  // anvils
  private static final String LOCATION_TinkersAnvil = Util.resource("tinker_anvil");
  public static final ModelResourceLocation locTinkersAnvil = new ModelResourceLocation(LOCATION_TinkersAnvil, "type=anvil");
  
  // Blank Pattern
  private static final ResourceLocation MODEL_BlankCast = Util.getResource("item/cast");
  public static final ResourceLocation locBlankCast = Util.getResource("cast");
  public static final ResourceLocation locClayCast = Util.getResource("clay_cast");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // convert casting table and basin to bakedTableModel for the item-rendering on/in them
    wrap(event, locCastingTable);
    wrap(event, locCastingBasin);
    wrap(event, locTinkersAnvil);

    // add the extra cast models. See ToolClientEvents for more info with the pattern
    ToolClientEvents.replacePatternModel(locBlankCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString, TinkerRegistry.getCastItems());
    ToolClientEvents.replacePatternModel(locClayCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString, TinkerRegistry.getCastItems(), 0xa77498);
  }

  private void wrap(ModelBakeEvent event, ModelResourceLocation loc) {
    IBakedModel model = event.getModelRegistry().getObject(loc);
    if(model != null && model instanceof IPerspectiveAwareModel) {
      event.getModelRegistry().putObject(loc, new BakedTableModel((IPerspectiveAwareModel) model, null, DefaultVertexFormats.ITEM));
    }
  }
  
/*  @SubscribeEvent
  public void onLeftClickEvent(PlayerInteractEvent.LeftClickBlock event) {
	  if( event.getItemStack().getItem() instanceof Hammer ) {
		  World world = event.getWorld();
		  IBlockState state = world.getBlockState(event.getPos());
		  if( state.getBlock() instanceof BlockTinkerAnvil ) {
//			  event.getEntityPlayer().setActiveHand(event.getHand());
			  if( world.isRemote ) {
//				  Minecraft mc = Minecraft.getMinecraft(); 
				  PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;
//				  mc.
//				  controllerMP.currentItemHittingBlock = new ItemStack(Blocks.AIR, 0);
//				  controllerMP.isHittingBlock = true;
//				  controllerMP.curBlockDamageMP = 0.0f;
			  }
//			  event.setUseItem(Result.DENY);
		  }
	  }
  }*/
}
