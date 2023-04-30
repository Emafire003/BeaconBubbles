package me.emafire003.dev.beaconbubbles.mixin;

import me.emafire003.dev.beaconbubbles.BeaconBubbles;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterFluid.class)
public abstract class WaterBlockMixin extends FlowableFluid {

    @Override
    public void onScheduledTick(World world, BlockPos pos, FluidState state) {
        /*BubbleColumnBlock.update(world, pos.up(), world.getBlockState(pos));*/
        super.onScheduledTick(world, pos, state);;
    }

}
