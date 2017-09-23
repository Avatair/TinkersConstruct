package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;
import java.util.Random;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockDecoGround extends EnumBlock<BlockDecoGround.DecoGroundType> {

  public final static PropertyEnum<DecoGroundType> TYPE = PropertyEnum.create("type", DecoGroundType.class);

  public BlockDecoGround() {
    super(Material.ROCK, TYPE, DecoGroundType.class);

    this.setHardness(2.0f);

    this.setSoundType(SoundType.STONE);

    setHarvestLevel("pickaxe", -1);
    setCreativeTab(TinkerRegistry.tabGeneral);
  }
  
  public enum DecoGroundType implements IStringSerializable, EnumBlock.IEnumMeta {
    MUDBRICK_DRIED,
    MUDBRICK_COOKED;

    public final int meta;

    DecoGroundType() {
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
