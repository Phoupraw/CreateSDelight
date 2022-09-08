package ph.mcmod.csd.game

import net.minecraft.block.BlockState
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.world.WorldView
import ph.mcmod.csd.MyRegistries
import ph.mcmod.csd.fluid.TutorialFluid

abstract class SunflowerOilFluid: TutorialFluid() {
    override fun getStill(): Fluid {
        return MyRegistries.MyFluids.SUNFLOWER_OIL
    }
    
    override fun getFlowing(): Fluid {
        return MyRegistries.MyFluids.SUNFLOWER_OIL_FLOWING
    }
    
    override fun getBucketItem(): Item {
        return MyRegistries.MyItems.SUNFLOWER_OIL_BUCKET
    }
    
    override fun toBlockState(fluidState: FluidState): BlockState {
        return MyRegistries.MyBlocks.SUNFLOWER_OIL.defaultState.with(Properties.LEVEL_15, getBlockStateLevel(fluidState))
    }
    
    override fun getFlowSpeed(worldView: WorldView): Int {
        return 3
    }
    
    override fun getTickRate(worldView: WorldView): Int {
        return 10
    }
    class Flowing : SunflowerOilFluid() {
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
    
    class Still : SunflowerOilFluid() {
        override fun getLevel(fluidState: FluidState): Int {
            return 8
        }
        
        override fun isStill(fluidState: FluidState): Boolean {
            return true
        }
    }
}