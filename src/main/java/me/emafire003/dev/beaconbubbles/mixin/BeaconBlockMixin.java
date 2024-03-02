package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Stainable;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
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

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && world.getBlockState(pos.up()).isOf(Blocks.WATER)) {
            world.scheduleBlockTick(pos, this, 20);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
