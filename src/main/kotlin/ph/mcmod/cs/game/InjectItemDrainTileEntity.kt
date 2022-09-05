package ph.mcmod.cs.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack
import com.simibubi.create.foundation.ponder.PonderWorld
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType
import com.simibubi.create.foundation.utility.BlockHelper
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.BlockState
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.api.*
import ph.mcmod.kum.*
import java.util.*
import kotlin.*
import kotlin.jvm.internal.Ref
import kotlin.math.pow

interface InjectItemDrainTileEntity {
    @Deprecated("问题过多，暂不使用")
    var toastingStage: Int
    
    companion object {
        const val LOWEST_TEMPERATURE = 400
        val LAVA_TEMPERATURE get() = FluidVariantAttributes.getTemperature(FluidVariant.of(Fluids.LAVA))
        var ItemStack.toastingDuration: Double?
            get() = nbt?.getOrNull("toastingDuration", NbtCompound::getDouble)
            set(value) {
                orCreateNbt.putOrRemove("toastingDuration", value, NbtCompound::putDouble)
                removeNbtIfEmpty()
            }
        var ItemStack.steamingDuration: Double?
            get() = nbt?.getOrNull("steamingDuration", NbtCompound::getDouble)
            set(value) {
                orCreateNbt.putOrRemove("steamingDuration", value, NbtCompound::putDouble)
                removeNbtIfEmpty()
            }
        
        fun <T : Recipe<Inventory>> getRecipe(world: World, recipeManager: RecipeManager, ingredient: ItemStack, recipeType: RecipeType<T>): T? {
            return recipeManager.getFirstMatch(recipeType, SimpleInventory(1).apply { setStack(0, ingredient) }, world).orElse(null)
        }
        
        fun <T : Recipe<Inventory>> getRecipe(world: World, ingredient: ItemStack, recipeType: RecipeType<T>): T? {
            return world.recipeManager?.getFirstMatch(recipeType, SimpleInventory(1).apply { setStack(0, ingredient) }, world)?.orElse(null)
        }
        
        //        fun interface Switch1 : Function7<ServerWorld, FluidVariant, Long, Random, Double, BlockPos, Int, Unit> {
//            override operator fun invoke(world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)
//        }
        //        inline fun switch1(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, toaster: (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)->Unit, steamer: (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)->Unit){}
        inline fun switch1(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, toaster: Switch1, steamer: Switch1) {
            val world = te.world ?: return
            val fluidStorage = te.getFluidStorage(null) ?: return
            StorageUtil.findExtractableContent(fluidStorage, null)?.also { (fluidVariant, amount) ->
                val random = world.random
                val chance = amount.toDouble() / (FluidConstants.BUCKET * 1.5)
                val blockPos = te.pos
                val temperature = FluidVariantAttributes.getTemperature(fluidVariant)
                if (temperature >= LOWEST_TEMPERATURE) {
                    toaster(world, fluidVariant, amount, random, chance, blockPos, temperature)
                } else if (fluidVariant.isOf(Fluids.WATER) && world.getBlockState(blockPos.down()).isOf(AllBlocks.LIT_BLAZE_BURNER.get()) && world.getBlockState(blockPos.up()).run { isOf(MyRegistries.MyBlocks.COPPER_TUNNEL) && CopperTunnelBlock.OPEN_STATES.values.fold(true) { b, property -> b && get(property) != CopperTunnelBlock.OpenState.NONE } }) {
                    steamer(world, fluidVariant, amount, random, chance, blockPos, temperature)
                }
            }
        }
        
        inline fun switch2(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, toaster: Switch2, steamer: Switch2) {
            val heldItemStack = heldItem?.stack?.takeUnless { it.isEmpty } ?: return
            switch1(te, heldItem, { world, fluidVariant, amount, random, chance, blockPos, temperature ->
                (getRecipe(world, heldItemStack, MyRegistries.MyRecipeTypes.BARBECUE) ?: getRecipe(world, heldItemStack, RecipeType.CAMPFIRE_COOKING)?.let { BarbecueRecipe(it) })?.also { recipe ->
                    toaster(world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe)
                }
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature ->
                getRecipe(world, heldItemStack, MyRegistries.MyRecipeTypes.STEAMING)?.also { recipe ->
                    steamer(world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe)
                }
            })
        }
        @JvmStatic
        fun particle(te: ItemDrainTileEntity, heldItem: TransportedItemStack?) {
            switch1(te, heldItem, { world, fluidVariant, amount, random, chance, blockPos, temperature ->
                if (random.nextDouble() < chance) {
                    if (random.nextInt(100) == 0) {
                        val x = blockPos.x + random.nextDouble()
                        val y = blockPos.y + 1.0
                        val z = blockPos.z + random.nextDouble()
                        world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0)
//                            world.addParticles(ParticleTypes.LAVA, false, Vec3d(x, y, z), Vec3d.ZERO, 1.0, 1)
                        world.playSound(null, x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f)
                        
                    }
                    if (random.nextInt(200) == 0) {
                        world.playSound(null, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f)
                    }
                }
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature ->
                
                    if (random.nextInt(5) == 0) {
                        val pos = blockPos.toCenter().add(0.0, 0.3125 + world.random.nextDouble() * 0.7, 0.0)
                        world.addParticle(ParticleTypes.POOF, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
                        
                    }
                    te . getFluidStorage (null)?.also { fluidStorage ->
                        TransferUtil.extract(fluidStorage, FluidVariant.of(Fluids.WATER), 1)
                    }
                
            })
        }
        
        @JvmStatic
        fun process(te: ItemDrainTileEntity, heldItem0: TransportedItemStack?, cir: CallbackInfoReturnable<Boolean>, processingTicks: Ref.IntRef) {
            te as InjectItemDrainTileEntity
            switch2(te, heldItem0, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                val duration = heldItemStack.toastingDuration ?: recipe.duration
                if (duration > 0) {
                    val c1 = 4.332272554161175
                    val x2 = 0.5098758983093371
                    val c3 = 0.8774689745773343
                    val temperatureMultiplier = (temperature.toDouble() - LOWEST_TEMPERATURE) / (LAVA_TEMPERATURE - LOWEST_TEMPERATURE)
                    val fluidAmountMultiplier = when (val x = amount.toDouble() / FluidConstants.BUCKET) {
                        in 0.0..1.0 -> x
                        else -> c1 * (x - x2).pow(5) + c3
                    }
                    val itemCountMultiplier = heldItemStack.count.toDouble().pow(-1 / 3.0)
                    val step = temperatureMultiplier * fluidAmountMultiplier * itemCountMultiplier
                    heldItemStack.toastingDuration = (duration - step)
                    cir.returnValue = true
                    heldItem.beltPosition = 0.5f
                    heldItem.prevBeltPosition = 0.5f
                    processingTicks.element = 20
                    
                    val pos = getPos(blockPos, heldItem)
                    for (i in 0 until step.random().toInt()) {
                        if (random.nextInt(10) == 0) {
                            if (world is ServerWorld) {
                                world.spreadParticles(ParticleTypes.POOF, false, pos, Vec3d.ZERO, 0.0, 1)
                                world.spreadParticles(ParticleTypes.SMOKE, false, pos, Vec3d.ZERO, 0.0, 1)
                            } else if (world is PonderWorld) {
                                world.addParticle(ParticleTypes.POOF, pos.z, pos.y, pos.z, 0.0, 0.0, 0.0)
                                world.addParticle(ParticleTypes.SMOKE, pos.z, pos.y, pos.z, 0.0, 0.0, 0.0)
                            }
                            if (random.nextInt(5) == 0) {
                                world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1f, 1f)
                            }
                        }
                    }
                    if (duration / recipe.duration <= 0.5 && te.toastingStage < 6) {
                        te.toastingStage++
                        te.notifyUpdate()
                    }
                } else {
                    heldItem.stack = recipe.output.copy().apply { count = heldItemStack.count }
                }
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                val duration = heldItemStack.steamingDuration ?: recipe.duration
                if (duration > 0) {
                    val step = heldItemStack.count.toDouble().pow(-1 / 3.0)
                    heldItemStack.steamingDuration = duration - step
                    cir.returnValue = true
                    heldItem.beltPosition = 0.5f
                    heldItem.prevBeltPosition = 0.5f
                    processingTicks.element = 20
                    
                    val pos = getPos(blockPos, heldItem)
                    for (i in 0 until step.random().toInt()) {
                        if (random.nextInt(20) == 0) {
                            if (world is ServerWorld) {
                                world.spreadParticles(ParticleTypes.POOF, false, pos, Vec3d.ZERO, 0.0, 1)
                            } else if (world is PonderWorld) {
                                world.addParticle(ParticleTypes.POOF, pos.z, pos.y, pos.z, 0.0, 0.0, 0.0)
                            }
                        }
                    }
                } else {
                    heldItem.stack = recipe.output.copy().apply { count = heldItemStack.count }
                }
            })
        }
        
        fun getPos(blockPos: BlockPos, heldItem: TransportedItemStack): Vec3d {
            val insertedFrom = heldItem.insertedFrom
            var sideOffset = heldItem.sideOffset.toDouble()
            val alongX = insertedFrom.rotateYClockwise().axis === Direction.Axis.X
            if (!alongX) sideOffset *= -1
            return blockPos.toCenter().add(if (alongX) sideOffset else 0.0, 0.32, if (alongX) 0.0 else sideOffset) + (Vec3d.of(heldItem.insertedFrom.opposite.vector) * (0.5 - heldItem.beltPosition))
        }
        @JvmStatic
        fun enableProcessing(te: ItemDrainTileEntity, world0: World, heldItemStack0: ItemStack, heldItem0: TransportedItemStack?): Boolean {
            te as InjectItemDrainTileEntity
            te.toastingStage = 0
            if (EmptyingByBasin.canItemBeEmptied(world0, heldItemStack0)) {
                return true
            }
            switch2(te, heldItem0, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                if (te.toastingStage == 0) te.toastingStage = 1
                return true
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                return true
            })
            return false
        }
        
        @JvmStatic
        fun modifyAngle(te: ItemDrainTileEntity, heldItem: TransportedItemStack, insertedFrom: Direction) {
            if (heldItem.stack.isIn(MyRegistries.MyItemTags.ANGLE_ON_DRAIN)) {
                heldItem.angle = (insertedFrom.horizontal) * 90 - 45
            }
        }
        
        @JvmStatic
        fun flapWhenInput(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, transportedStack: TransportedItemStack?, side: Direction, simulate: Boolean) {
            val world = te.world ?: return
            (world.getBlockEntity(te.pos.up()) as? CopperTunnelBlockEntity)?.also { tunnel ->
                tunnel.flap(side.opposite, true)
            }
        }
        
        @JvmStatic
        fun flapWhenOutput(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, side: Direction) {
            val world = te.world ?: return
            (world.getBlockEntity(te.pos.up()) as? CopperTunnelBlockEntity)?.also { tunnel ->
                tunnel.flap(side, false)
            }
        }
        
        @JvmStatic
        fun cancelOutput(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, world: BlockView, pos: BlockPos, type: BehaviourType<TileEntityBehaviour>): TileEntityBehaviour? {
            if (heldItem != null) {
                world.getBlockState(te.pos.up()).takeIf { it.block is CopperTunnelBlock }?.also { blockState ->
                    val openState = blockState.get(CopperTunnelBlock.OPEN_STATES[heldItem.insertedFrom])
                    if (openState == CopperTunnelBlock.OpenState.CLOSE || openState == CopperTunnelBlock.OpenState.WINDOW) {
                        return null
                    }
                }
            }
            return TileEntityBehaviour.get(world, pos, type)
        }
        
        @JvmStatic
        fun cancelThrow(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, blockState: BlockState, world: BlockView, pos: BlockPos, side: Direction): Boolean {
            return isBlocked(te, heldItem)
        }
        
        @JvmStatic
        fun flapWhenThrow(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, side: Direction) {
            flapWhenOutput(te, heldItem, side)
        }
        
        fun isBlockedBySolid(te: ItemDrainTileEntity, heldItem: TransportedItemStack?): Boolean {
            heldItem ?: return false
            val world = te.world ?: return false
            val insertedFrom = heldItem.insertedFrom
            val nextPos = te.pos.offset(insertedFrom)
            return BlockHelper.hasBlockSolidSide(world.getBlockState(nextPos), world, nextPos, insertedFrom.opposite)
        }
        
        fun isBlockedByTunnel(te: ItemDrainTileEntity, heldItem: TransportedItemStack?): Boolean {
            heldItem ?: return false
            val world = te.world ?: return false
            val insertedFrom = heldItem.insertedFrom
            return isBlockedByTunnel(te, heldItem.insertedFrom)
        }
        
        fun isBlockedByTunnel(te: ItemDrainTileEntity, direction: Direction): Boolean {
            if (direction.axis.isVertical) return false
            val world = te.world ?: return false
            world.getBlockState(te.pos.up()).takeIf { it.block is CopperTunnelBlock }?.also { blockState1 ->
                val openState = blockState1.get(CopperTunnelBlock.OPEN_STATES[direction])
                if (openState == CopperTunnelBlock.OpenState.CLOSE || openState == CopperTunnelBlock.OpenState.WINDOW) {
                    return true
                }
            }
            return false
        }
        
        fun isBlocked(te: ItemDrainTileEntity, heldItem: TransportedItemStack?): Boolean {
            return isBlockedBySolid(te, heldItem) || isBlockedByTunnel(te, heldItem)
        }
        
        @JvmStatic
        fun cancelMovement(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, itemMovementPerTick: Float): Float {
            heldItem ?: return itemMovementPerTick
            if (heldItem.beltPosition >= 5f / 8) {
                if (isBlocked(te, heldItem)) {
                    return (0.75f - heldItem.beltPosition)
                }
            }
            return itemMovementPerTick
        }
        
        @JvmStatic
        fun cancelInput(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, transportedStack: TransportedItemStack, side: Direction, simulate: Boolean, cir: CallbackInfoReturnable<ItemStack>) {
            if (isBlockedByTunnel(te, side.opposite)) {
                cir.returnValue = transportedStack.stack
            }
        }
    }
}

/**
 * @param world
 * @see InjectItemDrainTileEntity.switch1
 */
private typealias Switch1 = (world: World, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int) -> Unit
/**
 * @param world
 * @see InjectItemDrainTileEntity.switch2
 */
private typealias Switch2 = (world: World, fluidVariant: FluidVariant, amount: Long, random: Random, chance/*TODO 换个名字，比如液体容量百分比？*/: Double, blockPos: BlockPos, temperature: Int, heldItem: TransportedItemStack, heldItemStack: ItemStack, recipe: SingleRecipe) -> Unit