@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.base.RotatedPillarKineticBlock
import com.simibubi.create.content.contraptions.wrench.IWrenchable
import com.simibubi.create.foundation.block.ITE
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Direction.Axis
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import ph.mcmod.csd.MyRegistries
import ph.mcmod.kum.ItemStorable
import ph.mcmod.kum.always
import ph.mcmod.kum.asStorage
import ph.mcmod.kum.isEmpty

class DryingRackBlock(settings: Settings?) : Block(settings), ITE<DryingRackBlockEntity>, IWrenchable {
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
    
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        if (rotation == BlockRotation.CLOCKWISE_90 || rotation == BlockRotation.COUNTERCLOCKWISE_90)
            return state.with(AXIS_X, !state.get(AXIS_X))
        return state
    }
    
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        (world.getBlockEntity(pos) as? DryingRackBlockEntity)?.also { te ->
            val handStack = player.getStackInHand(hand)
            val handStorage = player.inventory.asStorage().getHandSlot(hand)
            return if (!handStack.isEmpty) {
                if (te.canInsert(handStack)) {
                    if (StorageUtil.move(handStorage, te.filtering, always(), 1, null) > 0) ActionResult.SUCCESS.apply { te.notifyUpdate() } else ActionResult.FAIL
                } else {
                    ActionResult.PASS
                }
            } else {
                if (StorageUtil.move(te.inventory, player.inventory.asStorage(), always(), 1, null) > 0) ActionResult.SUCCESS.apply { te.notifyUpdate() } else ActionResult.FAIL
            }
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }
    
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!newState.isOf(this)) {
            (world.getBlockEntity(pos) as? DryingRackBlockEntity)?.also { te ->
                ItemStorable.scatter(world, pos, te.filtering)
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }
    
    override fun onWrenched(state: BlockState, context: ItemUsageContext): ActionResult {
        if (context.side.axis == Axis.Y) return super.onWrenched(state, context)
        context.world.setBlockState(context.blockPos, AllBlocks.SHAFT.defaultState.with(RotatedPillarKineticBlock.AXIS, if (state.get(AXIS_X)) Axis.X else Axis.Z))
        return ActionResult.SUCCESS
    }
    
    override fun getRotatedBlockState(originalState: BlockState, targetedFace: Direction): BlockState {
        return if (targetedFace.axis == Axis.Y) originalState.rotate(BlockRotation.CLOCKWISE_90) else originalState
    }
    
    override fun getTileEntityClass(): Class<DryingRackBlockEntity> {
        return DryingRackBlockEntity::class.java
    }
    
    override fun getTileEntityType(): BlockEntityType<out DryingRackBlockEntity> {
        return MyRegistries.MyBlockEntityTypes.DRYING_RACK
    }
    
    companion object {
        val AXIS_X: BooleanProperty = BooleanProperty.of("axis_x")
        val SHAPE_X: VoxelShape = createCuboidShape(0.0, 12.0, 6.0, 16.0, 16.0, 10.0)
        val SHAPE_Y: VoxelShape = createCuboidShape(6.0, 12.0, 0.0, 10.0, 16.0, 16.0)
    }
}