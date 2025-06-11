package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractSignBlock.class)
public abstract class SignBlocksMixin extends BlockWithEntity implements Waterloggable {

    protected SignBlocksMixin(Settings settings) {
        super(settings);
    }

    //this is need in order to call the method above and check
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BubbleColumnBlock.update(world, pos.up(), state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.UP && world.getBlockState(pos.up()).isOf(Blocks.WATER)) {
            tickView.scheduleBlockTick(pos, this, 20);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
}
