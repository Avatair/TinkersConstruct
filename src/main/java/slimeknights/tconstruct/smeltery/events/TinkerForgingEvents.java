package slimeknights.tconstruct.smeltery.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockTinkerAnvil;
import slimeknights.tconstruct.tools.tools.Hammer;

public class TinkerForgingEvents {
	  @SubscribeEvent
	  public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
		  EntityPlayer player = event.getEntityPlayer();
		  World world = player.getEntityWorld();
		  Block block = world.getBlockState(event.getPos()).getBlock();
		  ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		  
		  if( (block instanceof BlockTinkerAnvil) && (stack.getItem() instanceof Hammer) ) {
			  event.setUseItem(Result.DENY);
		  }
	  }
}
