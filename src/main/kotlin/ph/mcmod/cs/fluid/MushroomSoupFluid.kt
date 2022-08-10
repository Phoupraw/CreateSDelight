package ph.mcmod.cs.fluid

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import ph.mcmod.cs.MyRegistries

abstract class TutorialFluid : FlowableFluid() {
    /**
     * @return whether the given fluid an instance of this fluid
     */
    override fun matchesType(fluid: Fluid): Boolean {
        return fluid === still || fluid === flowing
    }
    /**
     * @return whether the fluid infinite like water
     */
    override fun isInfinite(): Boolean {
        return false
    }
    /**
     * Perform actions when fluid flows into a replaceable block. Water drops
     * the block's loot table. Lava plays the "block.lava.extinguish" sound.
     */
    override fun beforeBreakingBlock(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity: BlockEntity? = if (state.hasBlockEntity()) world.getBlockEntity(pos) else null
        Block.dropStacks(state, world, pos, blockEntity)
    }
    /**
     * Lava returns true if its FluidState is above a certain height and the
     * Fluid is Water.
     *
     * @return whether the given Fluid can flow into this FluidState
     */
    override fun canBeReplacedWith(fluidState: FluidState, blockView: BlockView, blockPos: BlockPos, fluid: Fluid, direction: Direction): Boolean {
        return false
    }
    /**
     * Possibly related to the distance checks for flowing into nearby holes?
     * Water returns 4. Lava returns 2 in the Overworld and 4 in the Nether.
     */
    override fun getFlowSpeed(worldView: WorldView): Int {
        return 4
    }
    /**
     * Water returns 1. Lava returns 2 in the Overworld and 1 in the Nether.
     */
    override fun getLevelDecreasePerBlock(worldView: WorldView): Int {
        return 1
    }
    /**
     * Water returns 5. Lava returns 30 in the Overworld and 10 in the Nether.
     */
    override fun getTickRate(worldView: WorldView): Int {
        return 5
    }
    /**
     * Water and Lava both return 100.0F.
     */
    override fun getBlastResistance(): Float {
        return 100.0f
    }
}

abstract class AcidFluid : TutorialFluid() {
    override fun getStill(): Fluid {
        return MyRegistries.MyFluids.MUSHROOM_SOUP_STILL
    }
    
    override fun getFlowing(): Fluid {
        return MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING
    }
    
    override fun getBucketItem(): Item {
        return MyRegistries.MyItems.MUSHROOM_SOUP_BUCKET
    }
    
    override fun toBlockState(fluidState: FluidState): BlockState {
        return MyRegistries.MyBlocks.MUSHROOM_SOUP.defaultState.with(Properties.LEVEL_15, getBlockStateLevel(fluidState))
    }
    
    class Flowing : AcidFluid() {
        override fun appendProperties(builder: StateManager.Builder<Fluid, FluidState>) {
            super.appendProperties(builder)
            builder.add(LEVEL)
        }
        
        override fun getLevel(fluidState: FluidState): Int {
            return fluidState.get(LEVEL)
        }
        
        override fun isStill(fluidState: FluidState): Boolean {
            return false
        }
    }
    
    class Still : AcidFluid() {
        override fun getLevel(fluidState: FluidState): Int {
            return 8
        }
        
        override fun isStill(fluidState: FluidState): Boolean {
            return true
        }
    }
}