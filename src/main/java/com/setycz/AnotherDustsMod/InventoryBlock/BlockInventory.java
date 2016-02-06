package com.setycz.AnotherDustsMod.InventoryBlock;

import com.setycz.AnotherDustsMod.ModAnotherDusts;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by setyc on 29.01.2016.
 */
public abstract class BlockInventory extends BlockContainer implements ITileEntityProvider {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockInventory() {
        super(Material.rock);
        this.setHardness(3.5F);
        this.setStepSound(soundTypePiston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking()) {
            return false;
        }

        if (!worldIn.isRemote) {
            playerIn.openGui(ModAnotherDusts.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasDisplayName()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);

            if (tileEntity instanceof TileEntityInventory) {
                ((TileEntityInventory)tileEntity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }


    protected void breakBlock(World worldIn, BlockPos pos, IBlockState state, boolean keepInventory) {
        if (!keepInventory) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityInventory) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        breakBlock(worldIn, pos, state, false);
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public IBlockState getStateForEntityRender(IBlockState state) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }
}
