package ph.mcmod.cs.game

import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
import com.simibubi.create.content.logistics.packet.TunnelFlapPacket
import com.simibubi.create.foundation.networking.AllPackets
import com.simibubi.create.foundation.tileEntity.SmartTileEntity
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour
import com.simibubi.create.foundation.utility.Iterate
import com.simibubi.create.foundation.utility.animation.LerpedFloat
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser
import com.tterrag.registrate.fabric.EnvExecutor
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.apache.commons.lang3.tuple.Pair
import ph.mcmod.cs.MyRegistries
import java.util.*

class CopperTunnelBlockEntity(pos: BlockPos?, state: BlockState?) : SmartTileEntity(MyRegistries.MyBlockEntityTypes.COPPER_TUNNEL, pos, state), ItemTransferable {
    val flaps: MutableMap<Direction, LerpedFloat> = EnumMap(Direction::class.java)
    val sides: MutableSet<Direction> = HashSet()
    
    var belowStorageCache: BlockApiCache<Storage<ItemVariant>, Direction>? = null
    val flapsToSend: MutableList<Pair<Direction, Boolean>> = LinkedList()
    override fun write(compound: NbtCompound, clientPacket: Boolean) {
        val flapsNBT = NbtList()
        for (direction in flaps.keys) flapsNBT.add(NbtInt.of(direction.id))
        compound.put("Flaps", flapsNBT)
        val sidesNBT = NbtList()
        for (direction in sides) sidesNBT.add(NbtInt.of(direction.id))
        compound.put("Sides", sidesNBT)
        super.write(compound, clientPacket)
    }
    
    override fun read(compound: NbtCompound, clientPacket: Boolean) {
        val newFlaps: MutableSet<Direction> = HashSet(6)
        val flapsNBT = compound.getList("Flaps", NbtElement.INT_TYPE.toInt())
        for (inbt in flapsNBT) if (inbt is NbtInt) newFlaps.add(Direction.byId(inbt.intValue()))
        sides.clear()
        val sidesNBT = compound.getList("Sides", NbtElement.INT_TYPE.toInt())
        for (inbt in sidesNBT) if (inbt is NbtInt) sides.add(Direction.byId(inbt.intValue()))
        for (d in Iterate.directions) if (!newFlaps.contains(d)) flaps.remove(d) else if (!flaps.containsKey(d)) flaps.put(d, createChasingFlap())
        
        // Backwards compat
        if (!compound.contains("Sides") && compound.contains("Flaps")) sides.addAll(flaps.keys)
        super.read(compound, clientPacket)
        if (clientPacket) EnvExecutor.runWhenOn(EnvType.CLIENT) { Runnable { InstancedRenderDispatcher.enqueueUpdate(this) } }
    }
    
    override fun tick() {
        super.tick()
        if (!(world ?: return).isClient) {
            if (!flapsToSend.isEmpty()) sendFlaps()
            return
        }
        flaps.forEach { (d: Direction?, value: LerpedFloat) -> value.tickChaser() }
    }
    
    override fun notifyUpdate() {
        super.notifyUpdate()
        updateFlaps()
    }
    
    override fun addBehaviours(behaviours: MutableList<TileEntityBehaviour>?) {
    
    }
    
    override fun getItemStorage(face: Direction?): Storage<ItemVariant>? {
        return (world?.getBlockEntity(pos.down()) as? ItemDrainTileEntity)?.getItemStorage(face)
    }
    
    fun sendFlaps() {
        AllPackets.channel.sendToClientsTracking(CopperTunnelFlapPacket(this, flapsToSend), world as? ServerWorld, getPos())
        flapsToSend.clear()
    }
    
    fun flap(side: Direction, inward: Boolean) {
        if ((world ?: return).isClient) {
            if (flaps.containsKey(side))
                flaps[side]?.setValue(if (inward xor (side.axis === Direction.Axis.Z)) -1.0 else 1.0)
            return
        }
        flapsToSend.add(Pair.of(side, inward))
    }
    
    fun createChasingFlap(): LerpedFloat {
        return LerpedFloat.linear()
          .startWithValue(.25)
          .chase(0.0, .05, Chaser.EXP)
    }
    
    fun updateFlaps() {
        for ((side, property) in CopperTunnelBlock.OPEN_STATES) {
            if (cachedState[property] == CopperTunnelBlock.OpenState.CURTAIN) {
                flaps.putIfAbsent(side,createChasingFlap())
            } else {
                flaps -= side
            }
        }
    }
}