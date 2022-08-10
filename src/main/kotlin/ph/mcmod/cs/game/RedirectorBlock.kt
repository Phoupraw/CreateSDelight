@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package ph.mcmod.cs.game

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import ph.mcmod.kum.FacingBlock
import ph.mcmod.kum.facing
import java.util.*

open class RedirectorBlock<T>(settings: Settings, val lookup: BlockApiLookup<Storage<T>, Direction>) : FacingBlock(settings) {
    private val passed = mutableSetOf<BlockPos>()
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        super.scheduledTick(state, world, pos, random)
        world.breakBlock(pos, true)
    }
    
    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
        if (pos.offset(state.facing) == fromPos) {
            update(world, pos)
        }
    }
    
    fun find(world: World, pos: BlockPos, state: BlockState = world.getBlockState(pos)): Storage<T>? {
        println("passed = ${passed}")
        println("find starts")
        var pos1 = pos
        var state1 = state
        var facing: Direction
        do {
            if (pos1 in passed) {
                passed.clear()
                world.createAndScheduleBlockTick(pos1, this, 1)
                println("find ends")
                return null
            }
            passed += pos1
            facing = state1.facing
            pos1 = pos1.offset(facing)
            state1 = world.getBlockState(pos1)
        } while (state1.isOf(this))
        return lookup.find(world, pos1, state1, null, facing.opposite).apply { passed.clear()
            println("find ends")}
    }
    
    fun update(world: World, pos: BlockPos) {
        println("update starts")
        val queue: Queue<BlockPos> = ArrayDeque()
        queue.offer(pos)
        val set = mutableSetOf<BlockPos>()
        set += pos
        while (!queue.isEmpty()) {
            val pos1 = queue.poll()
            for (direction in Direction.values()) {
                val pos2 = pos1.offset(direction)
                if (pos2 in set) continue
                val state2 = world.getBlockState(pos2)
                if (state2.isOf(this)) {
                    if (state2.facing == direction.opposite) {
                        queue.offer(pos2)
                        set += pos2
                    }
                } else {
                    world.updateNeighbor(pos2, this, pos1)
                }
            }
        }
        println("update ends")
    }
}