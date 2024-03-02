package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.BubbleColumnBlock.DRAG;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin extends Block implements FluidDrainable{
    public BubbleColumnBlockMixin(Settings settings) {
        super(settings);
    }

    @Unique
    private static boolean hasBeam(BlockPos pos, BlockState state, WorldAccess world){
        boolean cond2 = state.isOf(Blocks.WATER) && (state.getFluidState().get(Properties.FALLING) || state.getFluidState().isStill());

        if(state.isOf(Blocks.BUBBLE_COLUMN) || cond2){

            for(int i = pos.getY(); i>world.getBottomY(); i--){
                pos = pos.add(0, -1, 0);
                BlockEntity block = world.getBlockEntity(pos);
                if(block instanceof BeaconBlockEntity){

                    return !((BeaconBlockEntity) block).getBeamSegments().isEmpty();
                }
            }
        }
        return false;

    }

    @Inject(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at = @At("RETURN"), cancellable = true)
    private static void injectUpdate(WorldAccess world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci) {

        boolean flowingWithBeam = hasBeam(pos, water, world);

        if(flowingWithBeam){
            //Drag false so it doesn't drag you down but push up instead.
            //TODO find a way to implement the DRAG down too.
            BlockState state = Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, false);
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
            BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);

            //Updates the blocks above
            while (hasBeam(mutable, world.getBlockState(mutable), world)) {
                if (!world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS)) {
                    ci.cancel();
                    return;
                }
                mutable.move(Direction.UP);
            }

            //I need to check downwards as well because there could be more blocks before the actual beacon, unlike the soulsand/magma
            mutable = pos.mutableCopy().move(Direction.DOWN);
            //Also checks air to make sure there aren't any floaters
            while (world.getBlockState(mutable).isOf(Blocks.AIR) || hasBeam(mutable, world.getBlockState(mutable), world)) {
                if (!world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS)) {
                    ci.cancel();
                    return;
                }
                mutable.move(Direction.DOWN);
            }
            ci.cancel();
        }
    }
}
