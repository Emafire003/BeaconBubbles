package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BubbleColumnBlock.DRAG;
import static me.emafire003.dev.beaconbubbles.BeaconBubbles.LOGGER;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin extends Block implements FluidDrainable{

    private static final BooleanProperty HAS_BEAM = BooleanProperty.of("has_beam");

    @Shadow
    private static boolean isStillWater(BlockState state) {
        return false;
    }

    public BubbleColumnBlockMixin(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HAS_BEAM, false));
    }


    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    public void getFluidStateInject(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        if(state.getProperties().contains(HAS_BEAM)){
            if(state.get(HAS_BEAM).booleanValue()){
                LOGGER.info("RETURNING CUSTOM FLUID STATE, Level 7 falling true");
                cir.setReturnValue(Fluids.WATER.getFlowing(7, true));
            }
        }
    }


    private static boolean isFlowingWithBeam(BlockPos pos, BlockState state, WorldAccess world){
        if(state.isOf(Blocks.BUBBLE_COLUMN) || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() <= 8 && !state.getFluidState().isStill()){
            //TODO boolean found = false;
           //TODO BlockPos pos_copy = pos.mutableCopy();
            for(int i = pos.getY(); i>world.getBottomY(); i--){
                pos = pos.add(0, -1, 0);
                BlockEntity block = world.getBlockEntity(pos);
                if(block instanceof BeaconBlockEntity){
                    //TODO found = true;
                    //Check if the beacon is active
                    boolean isBeamActive = !((BeaconBlockEntity) block).getBeamSegments().isEmpty();

                    //Updwards from the beacon it checks if the blocks above are bubblecolumns
                    for(int j = pos.getY(); j<world.getHeight(); j++){
                        pos = pos.add(0, 1, 0);
                        state = world.getBlockState(pos);
                        //If the block is a bubble column, it sets the HAS_BEAM property to isBeamActive (If there is a beacon but it's not active it will set false, if it's active true
                        if(state.getBlock() instanceof BubbleColumnBlock){
                            world.setBlockState(pos,  state.with(HAS_BEAM, isBeamActive), Block.NOTIFY_LISTENERS);
                        }
                    }

                    return isBeamActive;
                }
            }
            //TODO if(!found){

            //}
        }
        return false;

    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_BEAM);
        builder.add(DRAG);
    }

    @Inject(method = "update(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"), cancellable = true)
    private static void injectUpdate(WorldAccess world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci) {
        boolean flowingWithBeam = isFlowingWithBeam(pos, water, world);
        if(flowingWithBeam){
            BlockState state = Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, false);
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
            BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);

            //Updates the blocks above
            while (isStillWater(world.getBlockState(mutable)) || isFlowingWithBeam(mutable, world.getBlockState(mutable), world)) {
                if (!world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS)) {
                    ci.cancel();
                    return;
                }
                mutable.move(Direction.UP);
            }

            //I need to check downwards as well because there could be more blocks before the actual beacon, unlike the soulsand/magma
            mutable = pos.mutableCopy().move(Direction.DOWN);
            while (isStillWater(world.getBlockState(mutable)) || isFlowingWithBeam(mutable, world.getBlockState(mutable), world)) {
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
