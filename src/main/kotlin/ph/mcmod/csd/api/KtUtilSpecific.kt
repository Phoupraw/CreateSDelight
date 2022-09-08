@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "OVERRIDE_DEPRECATION")

package ph.mcmod.csd.api

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.painting.PaintingMotive
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleType
import net.minecraft.potion.Potion
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import ph.mcmod.kum.*
import java.util.*

var ItemStack.customModelData: Int?
    get() = orCreateNbt.getOrNull("CustomModelData", NbtCompound::getInt)
    set(value) {
        orCreateNbt.putOrRemove("CustomModelData", value, NbtCompound::putInt)
        removeNbtIfEmpty()
    }

fun <T> Optional<T>.getOrNull(): T? = orElse(null)

fun ItemStack.modelHasDepth(world: World? = null) = MinecraftClient.getInstance().itemRenderer.getModel(this, world, null, 0).hasDepth()

class HandStackStorage(val livingEntity: LivingEntity, val hand: Hand) : SingleStackStorage() {
    override fun getStack(): ItemStack {
        return livingEntity.getStackInHand(hand)
    }
    
    override fun setStack(stack: ItemStack?) {
        livingEntity.setStackInHand(hand, stack)
    }
}

//@Suppress("UNCHECKED_CAST")
//val <T : Identifiable> T.registry: Registry<T>
//    get() = when (this) {
//        is Block -> Registry.BLOCK
//        is Item -> Registry.ITEM
//        is Fluid -> Registry.FLUID
//        is EntityType<*> -> Registry.ENTITY_TYPE
//        is BlockEntityType<*> -> Registry.BLOCK_ENTITY_TYPE
//        is Enchantment -> Registry.ENCHANTMENT
//        is StatusEffect -> Registry.STATUS_EFFECT
//        is Potion -> Registry.POTION
//        is ParticleType<*> -> Registry.PARTICLE_TYPE
//        is PaintingMotive -> Registry.PAINTING_MOTIVE
//        is RecipeType<*> -> Registry.RECIPE_TYPE
//        is RecipeSerializer<*> -> Registry.RECIPE_SERIALIZER
//        else -> throw IllegalArgumentException("$javaClass $this")
//    } as Registry<T>

//fun <T : Identifiable> T.register(id: Identifier): T {
//    return Registry.register(registry, id, this)
//}

//interface Registerable<T : Registerable<T>> {
//    val registry: Registry<T>
//    fun register(id: Identifier)
//}