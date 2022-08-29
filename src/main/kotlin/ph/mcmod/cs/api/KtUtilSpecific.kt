@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "OVERRIDE_DEPRECATION")

package ph.mcmod.cs.api

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Position
import net.minecraft.world.World
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.asStorage
import ph.mcmod.kum.spreadParticles
import java.util.*
import kotlin.NoSuchElementException
import kotlin.annotation.AnnotationTarget.*
import kotlin.math.floor
import kotlin.random.asJavaRandom

//TODO 为配方类型实现Identifiable
@ApiStatus.Experimental
infix fun Double.modAndDiv(divisor: Number): Double = this % divisor.toDouble() / divisor.toDouble()
@ApiStatus.Experimental
infix fun Float.modAndDiv(divisor: Number): Float = this % divisor.toFloat() / divisor.toFloat()
@ApiStatus.Experimental
fun simpleSlot(): SingleSlotStorage<ItemVariant> = SimpleInventory(1).asStorage().getSlot(0)
@ApiStatus.Experimental
class FixedSlotsFluidStorage(val size: Int, val capacityPerSlot: Long) : CombinedStorage<FluidVariant, SingleVariantStorage<FluidVariant>>(Array(size) { SingleFluidVariantStorage(capacityPerSlot) }.asList())
@ApiStatus.Experimental
class SingleFluidVariantStorage(val capacityPerSlot: Long) : SingleVariantStorage<FluidVariant>() {
    override fun getCapacity(variant: FluidVariant): Long {
        return capacityPerSlot
    }
    
    override fun getBlankVariant(): FluidVariant {
        return FluidVariant.blank()
    }
}
@ApiStatus.Experimental
fun <T : Any?> T.printS(): T {
    runAtDev { print("$this ") }
    return this
}
@ApiStatus.Experimental
fun <T : Any?> T.printL(): T {
    runAtDev { println(this) }
    return this
}
@ApiStatus.Experimental
inline fun <T> NbtCompound.getOrNull(key: String, getter: NbtCompound.(String) -> T): T? = if (!contains(key)) null else getter(key)
@ApiStatus.Experimental
inline fun <T> NbtCompound.putOrRemove(key: String, value: T?, setter: NbtCompound.(String, T) -> Unit) {
    if (value == null) {
        remove(key)
    } else {
        setter(key, value)
    }
}
@ApiStatus.Experimental
fun ItemStack.removeNbtIfEmpty(): ItemStack {
    if (nbt?.isEmpty == true) {
        nbt = null
    }
    return this
}
/** 适用于Kotlin的[ApiStatus.Experimental] */
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
@Retention(AnnotationRetention.SOURCE)
@ApiStatus.Experimental
annotation class Experimental
@ApiStatus.Experimental
inline fun runAtDev(runnable: () -> Unit) {
    if (FabricLoader.getInstance().isDevelopmentEnvironment) runnable()
}
@ApiStatus.Experimental
fun Double.random(random: Random = kotlin.random.Random.asJavaRandom()): Double {
    val chance = this - floor(this)
    return if (random.nextDouble() < chance) this + 1 else this
}
@ApiStatus.Experimental
fun Float.random(random: Random = kotlin.random.Random.asJavaRandom()): Float {
    val chance = this - floor(this)
    return if (random.nextFloat() < chance) this + 1 else this
}
//inline fun <T> MutableIterable<T>.forEach(consumer: (element: T, remove: () -> Unit, break_: () -> Unit) -> Unit) {
//    val iterator = this.iterator()
//    while (iterator.hasNext())
//        consumer(iterator.next(), iterator::remove) { break }
//}
@ApiStatus.Experimental
fun World.addParticles(particle: ParticleEffect, alwaysSpawn: Boolean, pos: Position, offset: Position, speed: Double, count: Int) {
    if (isClient) {
        if (count == 0) {
            addParticle(particle,
              alwaysSpawn,
              pos.x,
              pos.y,
              pos.z,
              speed * offset.x,
              speed * offset.y,
              speed * offset.z)
        } else {
            for (i in 0 until count) {
                addParticle(particle,
                  alwaysSpawn,
                  pos.x + random.nextGaussian() * offset.x,
                  pos.y + random.nextGaussian() * offset.y,
                  pos.z + random.nextGaussian() * offset.z,
                  random.nextGaussian() * speed,
                  random.nextGaussian() * speed,
                  random.nextGaussian() * speed)
            }
        }
    } else if (this is ServerWorld) {
        spreadParticles(particle, alwaysSpawn, pos, offset, speed, count)
    } else {
        throw IllegalArgumentException(toString())
    }
}

fun <K, V> Map<K, V>.getOrThrow(key: K): V {
    return this[key] ?: throw NoSuchElementException(key.toString())
}