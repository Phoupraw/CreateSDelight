package ph.mcmod.cs.game

import com.simibubi.create.foundation.networking.TileEntityDataPacket
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelTileEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Direction
import org.apache.commons.lang3.tuple.Pair
import java.util.ArrayList

class CopperTunnelFlapPacket : TileEntityDataPacket<BeltTunnelTileEntity> {
    private var flaps: MutableList<Pair<Direction, Boolean>>
    
    constructor(buffer: PacketByteBuf) : super(buffer) {
        val size = buffer.readByte()
        flaps = ArrayList<Pair<Direction, Boolean>>(size.toInt())
        for (i in 0 until size) {
            val direction = Direction.byId(buffer.readByte().toInt())
            val inwards = buffer.readBoolean()
            flaps.add(Pair.of(direction, inwards))
        }
    }
    
    constructor(tile: CopperTunnelBlockEntity, flaps: List<Pair<Direction, Boolean>>) : super(tile.pos) {
        this.flaps = ArrayList(flaps)
    }
    
    override fun writeData(buffer: PacketByteBuf) {
        buffer.writeByte(flaps.size)
        for (flap in flaps) {
            buffer.writeByte(flap.left.id)
            buffer.writeBoolean(flap.right)
        }
    }
    
    override fun handlePacket(tile: BeltTunnelTileEntity) {
        for (flap in flaps) {
            tile.flap(flap.left, flap.right)
        }
    }
}