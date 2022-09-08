package ph.mcmod.csd

import net.devtech.arrp.api.RuntimeResourcePack
import net.minecraft.util.Identifier
import ph.mcmod.kum.Registerable

const val CSD = "csd"
val RRP: RuntimeResourcePack = RuntimeResourcePack.create(id("rrp"))
internal fun id(path: String) = Identifier(CSD, path)
private fun <T : Registerable<T>> T.register(path: String) = register(id(path))

object CsdItems {
    init {
    
    }
}