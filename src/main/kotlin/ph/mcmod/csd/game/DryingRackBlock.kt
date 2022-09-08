@file:Suppress("OVERRIDE_DEPRECATION")

package ph.mcmod.csd.game

import com.simibubi.create.AllShapes
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.math.Direction.DOWN
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import ph.mcmod.kum.rotate

class DryingRackBlock(settings: Settings?) : Block(settings) {
    init {
        defaultState = defaultState.with(AXIS_X, true)
    }
    
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(AXIS_X)
    }
    
    override fun getOutlineShape(state: BlockState, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return if (state.get(AXIS_X)) SHAPE_X else SHAPE_Y
    }
    
    companion object {
        val AXIS_X: BooleanProperty = BooleanProperty.of("axis_x")
        val SHAPE_X: VoxelShape = createCuboidShape(0.0, 12.0, 6.0, 16.0, 16.0, 10.0)
        val SHAPE_Y: VoxelShape = SHAPE_X.rotate(DOWN)
    }
}