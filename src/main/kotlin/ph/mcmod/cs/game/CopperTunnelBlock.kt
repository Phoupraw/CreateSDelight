@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package ph.mcmod.cs.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainBlock
import com.simibubi.create.content.contraptions.wrench.IWrenchable
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelBlock
import com.simibubi.create.foundation.block.ITE
import com.simibubi.create.foundation.tileEntity.SmartTileEntity
import com.simibubi.create.foundation.tileEntity.behaviour.belt.DirectBeltInputBehaviour
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemUsageContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.api.printS
import ph.mcmod.kum.EnumStringIdentifiable
import ph.mcmod.kum.get

class CopperTunnelBlock(settings: Settings?) : Block(settings), ITE<CopperTunnelBlockEntity>, IWrenchable {
    init {
        defaultState = OPEN_STATES.values.fold(defaultState) { blockState, property ->
            blockState.with(property, OpenState.CURTAIN)
        }
    }
    
    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*OPEN_STATES.values.toTypedArray())
    }
    
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).isOf(AllBlocks.ITEM_DRAIN.get())
    }
    
    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return SHAPE
    }
    
    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        var resultState = super.getPlacementState(ctx) ?: return null
        val world = ctx.world
        val pos = ctx.blockPos
        for ((direction, property) in OPEN_STATES) {
            val neighborTunnel = world.getBlockState(pos.offset(direction))
            if (neighborTunnel.block is CopperTunnelBlock) {
                resultState = resultState.with(property, OpenState.NONE)
            } else {
                val neighborDrain = world.getBlockState(pos.offset(direction).down())
                resultState = resultState.with(property, OpenState.WINDOW)
                (world.getBlockEntity(pos.offset(direction).down()) as? SmartTileEntity)?.also { te ->
                    if (te.getBehaviour(DirectBeltInputBehaviour.TYPE) != null) {
                        resultState = resultState.with(property, OpenState.CURTAIN)
                    }
                }
            }
        }
        return resultState
    }
    
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        var resultState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        if (direction in OPEN_STATES) {
            val property = OPEN_STATES[direction]
            if (neighborState.block is CopperTunnelBlock) {
//                println("$pos $direction $neighborState")
                val property1 = OPEN_STATES[direction.opposite]
                if (neighborState.get(property1) == OpenState.CURTAIN || neighborState.get(property1) == OpenState.NONE) {
                    resultState = resultState.with(property, OpenState.NONE)
                } else {
                    resultState = resultState.with(property, neighborState.get(property1))
                }
            } else {
//                println(1   )
                resultState = resultState.with(property, OpenState.WINDOW)
                (world.getBlockEntity(neighborPos.down()) as? SmartTileEntity)?.also { te ->
                    if (te.getBehaviour(DirectBeltInputBehaviour.TYPE) != null) {
                        resultState = resultState.with(property, OpenState.CURTAIN)
                    }
                }
            }
        }
        (world.getBlockEntity(pos) as? CopperTunnelBlockEntity)?.apply {
            cachedState=resultState
            updateFlaps()
        }
        return resultState
    }
    
    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos, isMoving: Boolean) {
        if (world.isClient) return
        (world.getBlockEntity(pos) as? CopperTunnelBlockEntity)?.updateFlaps()
        if (fromPos == pos.down()) {
            if (!canPlaceAt(state, world, pos)) {
                world.breakBlock(pos, true)
            }
        }
    }
    
    override fun onBlockAdded(state: BlockState?, world: World, pos: BlockPos, oldState: BlockState?, notify: Boolean) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        (world.getBlockEntity(pos) as? CopperTunnelBlockEntity)?.updateFlaps()
    }
    
    override fun getTileEntityClass(): Class<CopperTunnelBlockEntity> {
        return CopperTunnelBlockEntity::class.java
    }
    
    override fun getTileEntityType(): BlockEntityType<out CopperTunnelBlockEntity> {
        return MyRegistries.MyBlockEntityTypes.COPPER_TUNNEL
    }
    
    override fun onWrenched(state: BlockState, context: ItemUsageContext): ActionResult {
        val side = context.side
        context.world.setBlockState(context.blockPos, state.cycle(OPEN_STATES[side]))
        ( context.world.getBlockEntity(context.blockPos) as? CopperTunnelBlockEntity)?.updateFlaps()
        return ActionResult.SUCCESS
    }
    
    companion object {
        val SHAPE: VoxelShape = createCuboidShape(0.0, -3.0, 0.0, 16.0, 16.0, 16.0)
        val OPEN_STATES = Direction.Type.HORIZONTAL.associate { it to EnumProperty.of(it.asString(), OpenState::class.java) }
        fun getOpenState(blockState: BlockState, face: Direction): OpenState {
            if (blockState.block is CopperTunnelBlock) {
                return blockState.get(OPEN_STATES[face])
            } else if (blockState.block is BeltTunnelBlock) {
                val axis = blockState.get(BeltTunnelBlock.HORIZONTAL_AXIS)
                val shape = blockState.get(BeltTunnelBlock.SHAPE)
                if (axis == face.axis && shape == BeltTunnelBlock.Shape.STRAIGHT) {
                    return OpenState.CURTAIN
                }
                if (axis != face.axis && shape == BeltTunnelBlock.Shape.STRAIGHT) {
                    return OpenState.CLOSE
                }
                if (axis == face.axis && shape == BeltTunnelBlock.Shape.WINDOW) {
                    return OpenState.NONE
                }
                if (axis != face.axis && shape == BeltTunnelBlock.Shape.WINDOW) {
                    return OpenState.WINDOW
                }
                if (shape == BeltTunnelBlock.Shape.CROSS) {
                    return OpenState.CURTAIN
                }
                if (shape == BeltTunnelBlock.Shape.T_LEFT) {
                    if (axis[false] == face) {
                        return OpenState.CLOSE
                    } else {
                        return OpenState.CURTAIN
                    }
                }
                if (shape == BeltTunnelBlock.Shape.T_RIGHT) {
                    if (axis == Direction.Axis.X) {
                        if (face == Direction.NORTH) {
                            return OpenState.CLOSE
                        } else {
                            return OpenState.CURTAIN
                        }
                    } else {
                        if (face == Direction.EAST) {
                            return OpenState.CLOSE
                        } else {
                            return OpenState.CURTAIN
                        }
                    }
                }
            }
            TODO()
        }
    }
    
    enum class OpenState : EnumStringIdentifiable {
        CLOSE, WINDOW, CURTAIN, NONE;
    }
    
}