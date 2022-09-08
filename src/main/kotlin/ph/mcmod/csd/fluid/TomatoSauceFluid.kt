package ph.mcmod.csd.fluid

import net.minecraft.block.BlockState
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import ph.mcmod.csd.MyRegistries

abstract class TomatoSauceFluid: TutorialFluid() {
    override fun getStill(): Fluid {
        return MyRegistries.MyFluids.TOMATO_SAUCE
    }
    
    override fun getFlowing(): Fluid {
        return MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING
    }
    
    override fun getBucketItem(): Item {
        return MyRegistries.MyItems.TOMATO_SAUCE_BUCKET
    }
    
    override fun toBlockState(fluidState: FluidState): BlockState {
        return MyRegistries.MyBlocks.TOMATO_SAUCE.defaultState.with(Properties.LEVEL_15, getBlockStateLevel(fluidState))
    }
    
    class Flowing : TomatoSauceFluid() {
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
    
    class Still : TomatoSauceFluid() {
        override fun getLevel(fluidState: FluidState): Int {
            return 8
        }
        
        override fun isStill(fluidState: FluidState): Boolean {
            return true
        }
    }
}