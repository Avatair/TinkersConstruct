package slimeknights.tconstruct.shared.block;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;

public class BlockDecoGroundRaw extends EnumBlock<BlockDecoGroundRaw.DecoGroundTypeRaw> {

	  public final static PropertyEnum<DecoGroundTypeRaw> TYPE = PropertyEnum.create("type", DecoGroundTypeRaw.class);

	  public BlockDecoGroundRaw() {
	    super(Material.GROUND, TYPE, DecoGroundTypeRaw.class);

	    this.setHardness(2.0f);

	    this.setSoundType(SoundType.GROUND);
	    this.setTickRandomly(true);

	    setHarvestLevel("shovel", -1);
	    setCreativeTab(TinkerRegistry.tabGeneral);
	  }
	  
	  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		  if( worldIn.canSeeSky(pos.up()) && worldIn.isDaytime() && rand.nextInt(10) < 2 ) {
			  if( state.getValue(TYPE) == DecoGroundTypeRaw.MUDBRICK_RAW ) {
				  IBlockState newState = TinkerCommons.blockDecoGround.getDefaultState().withProperty(BlockDecoGround.TYPE, BlockDecoGround.DecoGroundType.MUDBRICK_DRIED);
				  worldIn.setBlockState(pos, newState, 2);
			  }
		  }
	  }

	  public enum DecoGroundTypeRaw implements IStringSerializable, EnumBlock.IEnumMeta {
	    MUDBRICK_RAW;

	    public final int meta;

	    DecoGroundTypeRaw() {
	      meta = ordinal();
	    }

	    @Override
	    public String getName() {
	      return this.toString().toLowerCase(Locale.US);
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }
	  }
	}
