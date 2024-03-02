package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BeaconBlock.class)
public abstract class BeaconBlockMixin extends BlockWithEntity
        implements Stainable {


    protected BeaconBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BubbleColumnBlock.update(world, pos.up(), state);
    }

    //This is need to set back the water to be flowing
    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        //This should update the water blocks and turn them back into flowing state stuff
        BlockPos copy_pos = pos.mutableCopy();
        for(int i = pos.getY(); i < world.getTopY(); i++){
            copy_pos = copy_pos.up();/*
            if(world.getBlockState(copy_pos).isOf(Blocks.BUBBLE_COLUMN) && world.getBlockState(copy_pos).get(BeaconBubbles.HAS_BEAM)){
                if(!world.getBlockState(copy_pos.up()).isOf(Blocks.WATER) && !world.getBlockState(copy_pos.up()).isOf(Blocks.BUBBLE_COLUMN)){
                    world.setBlockState(copy_pos, Fluids.WATER.getStill(true).getBlockState(), Block.NOTIFY_LISTENERS);
                }else{
                    world.setBlockState(copy_pos, Fluids.FLOWING_WATER.getFlowing(7, true).getBlockState(), Block.NOTIFY_LISTENERS);
                }
            }*/
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        //TODO maybe could be disabled if it takes up too much memory?
        if (direction == Direction.UP && world.getBlockState(pos.up()).isOf(Blocks.WATER)) {
            world.scheduleBlockTick(pos, this, 20);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
