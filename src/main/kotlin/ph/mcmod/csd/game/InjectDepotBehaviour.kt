package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour
import org.jetbrains.annotations.ApiStatus

interface InjectDepotBehaviour {
    var heldItem:TransportedItemStack?
    @Deprecated("")
    var startTime:Long
    var flippingCountdown: Int
    @ApiStatus.Internal   companion object {
        const val FLIPPING_TIME = 11
        var InjectDepotBehaviour.isFlipping: Boolean
            get() = flippingCountdown != -1
            set(value) {
                flippingCountdown = if (!value) -1 else FLIPPING_TIME
            }
        @JvmStatic
        fun center(behaviour: DepotBehaviour, heldItem: TransportedItemStack) {
            val te = behaviour.tileEntity
            val world = behaviour.world
            if (world.getBlockState(te.pos.down()).isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
                heldItem.sideOffset=0f
//                heldItem.angle=-8
//                behaviour.setCenteredHeldItem(heldItem)
            }
        }
    }
    
}