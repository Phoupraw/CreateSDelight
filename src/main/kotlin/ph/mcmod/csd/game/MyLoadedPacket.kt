package ph.mcmod.csd.game

import com.simibubi.create.foundation.networking.SimplePacketBase
import net.minecraft.network.PacketByteBuf
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

class MyLoadedPacket<T : SimplePacketBase> (val type: Class<T>, factory: Function<PacketByteBuf, T>,val direction: SimplePacketBase.NetworkDirection) {
    val encoder: BiConsumer<T, PacketByteBuf> = BiConsumer { t, buffer -> t.write(buffer) }
    val decoder: Function<PacketByteBuf, T>
    val handler: BiConsumer<T, Supplier<SimplePacketBase.Context>>
    
    init {
        decoder = factory
        handler = BiConsumer { t, context -> t.handle(context) }
    }
    
    companion object {
         const val index = 0
    }
}