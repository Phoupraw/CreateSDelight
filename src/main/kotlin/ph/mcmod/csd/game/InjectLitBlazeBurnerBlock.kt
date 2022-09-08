package ph.mcmod.csd.game

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import java.util.*

interface InjectLitBlazeBurnerBlock {
    companion object {
        @JvmStatic
        fun modifyY(y: Double, state: BlockState, world: World, pos: BlockPos, random: Random): Double {
            return if (VoxelShapes.adjacentSidesCoverSquare(state.getOutlineShape(world, pos), world.getBlockState(pos.up()).getOutlineShape(world, pos.up()), Direction.UP)) {
                pos.y + (random.nextDouble() + random.nextDouble()) / 2
            } else
                y
        }
    }
}