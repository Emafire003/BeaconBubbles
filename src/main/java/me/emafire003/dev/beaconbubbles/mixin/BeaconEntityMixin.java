package me.emafire003.dev.beaconbubbles.mixin;

import me.emafire003.dev.beaconbubbles.BeaconBubbles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class BeaconEntityMixin {

	//This is need to set back the water to be flowing
	@Inject(
			method = "tick",
			at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"
					)
	)
	private static void injectTickOff(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos copy_pos = pos.mutableCopy();
		for(int i = pos.getY(); i < world.getTopY(); i++){
			copy_pos = copy_pos.up();
			if(world.getBlockState(copy_pos).isOf(Blocks.BUBBLE_COLUMN) && world.getBlockState(copy_pos).get(BeaconBubbles.HAS_BEAM)){
				if(!world.getBlockState(copy_pos.up()).isOf(Blocks.WATER) && !world.getBlockState(copy_pos.up()).isOf(Blocks.BUBBLE_COLUMN)){
					world.setBlockState(copy_pos, Fluids.WATER.getStill(true).getBlockState(), Block.NOTIFY_LISTENERS);
				}else{
					world.setBlockState(copy_pos, Fluids.FLOWING_WATER.getFlowing(7, true).getBlockState(), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	@Inject(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeaconBlockEntity$BeamSegment;increaseHeight()V"
			)
	)
	private static void injectTickOn(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos copy_pos = pos.mutableCopy();
		for(int i = pos.getY(); i < world.getTopY(); i++){
			copy_pos = copy_pos.add(0, 1, 0);
			state.updateNeighbors(world, copy_pos, Block.NOTIFY_LISTENERS);
		}
	}

}


