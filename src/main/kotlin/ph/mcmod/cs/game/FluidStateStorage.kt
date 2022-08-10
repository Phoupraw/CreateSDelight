package ph.mcmod.cs.game

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.block.FluidDrainable
import net.minecraft.block.FluidFillable
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
@Deprecated("并不是所有流体都有方块形式，且不是所有的方块都能容纳任意流体。对于含水方块，还要考虑碰撞箱，判断是否能从某个面抽水或者灌水。这些都需要大量的特判，普适性很差。")
class FluidStateStorage(val world: World, val pos: BlockPos) : SnapshotParticipant<FluidState>(), SingleSlotStorage<FluidVariant> {
    val fluidState: FluidState
        get() = world.getFluidState(pos)
    
    override fun createSnapshot(): FluidState {
        return fluidState
    }
    
    override fun readSnapshot(snapshot: FluidState) {
        trySetFluid(snapshot)
    }
    
    override fun insert(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long {
        if (resource.isBlank || maxAmount != FluidConstants.BUCKET) {
            return 0
        }
        updateSnapshots(transaction)
        return if (trySetFluid(resource.fluid.defaultState)) FluidConstants.BUCKET else 0
    }
    
    override fun extract(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long {
        if (resource.fluid != fluidState.fluid || maxAmount != FluidConstants.BUCKET) {
            return 0
        }
        updateSnapshots(transaction)
        val blockState = world.getBlockState(pos)
        if (blockState is FluidDrainable) {
            if (blockState.tryDrainFluid(world, pos, blockState).isEmpty) {
                return 0
            } else {
                return FluidConstants.BUCKET
            }
        } else {
            return 0
        }
    }
    
    override fun isResourceBlank(): Boolean {
        return fluidState.isEmpty
    }
    
    override fun getResource(): FluidVariant {
        return FluidVariant.of(fluidState.fluid)
    }
    
    override fun getAmount(): Long {
        return FluidConstants.BUCKET
    }
    
    override fun getCapacity(): Long {
        return FluidConstants.BUCKET
    }
    
    fun trySetFluid(fluidState1: FluidState): Boolean {
        val blockState = world.getBlockState(pos)
        if (blockState is FluidFillable) {
            return blockState.tryFillWithFluid(world, pos, blockState, fluidState1)
        }
        return false
    }
}