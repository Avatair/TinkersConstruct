package slimeknights.tconstruct.smeltery.block;

import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.smeltery.block.BlockCasting.CastingType;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkersAnvil;

public class BlockTinkerAnvil extends BlockInventory {

	public static final PropertyEnum<AnvilType> TYPE = PropertyEnum.create("type", AnvilType.class);
    protected static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
    protected static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);

	public BlockTinkerAnvil() {
		super(Material.ANVIL);
		setHardness(3F);
		setResistance(20F);
		setCreativeTab(TinkerRegistry.tabSmeltery);
		setSoundType(SoundType.ANVIL);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (AnvilType type : AnvilType.values()) {
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { TYPE },
				new IUnlistedProperty[] { BlockTable.INVENTORY, BlockTable.FACING });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta < 0 || meta >= CastingType.values().length) {
			meta = 0;
		}
		return getDefaultState().withProperty(TYPE, AnvilType.values()[meta]);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTinkersAnvil();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileTinkersAnvil) {
			((TileTinkersAnvil) te).interact(playerIn);
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		// we have rotation for the stuff too so the items inside rotate according to
		// placement!
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileTinkersAnvil) {
			((TileTinkersAnvil) te).setFacing(placer.getHorizontalFacing().getOpposite());
		}
	}
	
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	TileEntity te = source.getTileEntity(pos);
    	if (te != null && te instanceof TileTinkersAnvil) {
            EnumFacing enumfacing = ((TileTinkersAnvil)te).getFacing();
            return enumfacing.getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
        }
    	
    	return super.getBoundingBox(state, source, pos);
    }

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileTinkersAnvil) {
			TileTinkersAnvil tile = (TileTinkersAnvil) te;
			return tile.writeExtendedBlockState(extendedState);
		}

		return super.getExtendedState(state, world, pos);
	}

	@Override
	protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return false;
	}

	// @Override
	// public int getComparatorInputOverride(IBlockState blockState, World world,
	// BlockPos pos) {
	// TileEntity te = world.getTileEntity(pos);
	// if(!(te instanceof TileTinkersAnvil)) {
	// return 0;
	// }
	//
	// return ((TileTinkersAnvil) te).comparatorStrength();
	// }

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess,
			@Nonnull BlockPos pos, EnumFacing side) {
		return true;
	}

	public enum AnvilType implements IStringSerializable, EnumBlock.IEnumMeta {
		ANVIL;

		public final int meta;

		AnvilType() {
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
