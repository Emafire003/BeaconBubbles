package me.emafire003.dev.beaconbubbles.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class BeaconEntityMixin {

	@Inject(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeamEmitter$BeamSegment;increaseHeight()V"
			)
	)
	private static void injectTickOn(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos copy_pos = pos.mutableCopy();
		for(int i = pos.getY(); i < world.getTopYInclusive(); i++){
			copy_pos = copy_pos.add(0, 1, 0);
			state.updateNeighbors(world, copy_pos, Block.NOTIFY_LISTENERS);
		}
	}

}


