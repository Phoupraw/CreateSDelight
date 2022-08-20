package ph.mcmod.cs.game

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry
import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
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
        
        fun <T : Recipe<Inventory>> getRecipe(world: ServerWorld, ingredient: ItemStack, recipeType: RecipeType<T>): T? = world.server.recipeManager.getFirstMatch(recipeType, SimpleInventory(1).apply { setStack(0, ingredient) }, world).orElse(null)
        
        //        fun interface Switch1 : Function7<ServerWorld, FluidVariant, Long, Random, Double, BlockPos, Int, Unit> {
//            override operator fun invoke(world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)
//        }
        //        inline fun switch1(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, toaster: (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)->Unit, steamer: (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int)->Unit){}
        inline fun switch1(te: ItemDrainTileEntity, heldItem: TransportedItemStack?, toaster: Switch1, steamer: Switch1) {
            val world = te.world as? ServerWorld ?: return
            val fluidStorage = te.getFluidStorage(null) ?: return
            StorageUtil.findExtractableContent(fluidStorage, null)?.also { (fluidVariant, amount) ->
                val random = world.random
                val chance = amount.toDouble() / (FluidConstants.BUCKET * 1.5)
                val blockPos = te.pos
                val temperature = FluidVariantAttributes.getTemperature(fluidVariant)
                if (temperature >= LOWEST_TEMPERATURE) {
                    toaster(world, fluidVariant, amount, random, chance, blockPos, temperature)
                } else if (fluidVariant.isOf(Fluids.WATER) && world.getBlockState(blockPos.down()).isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
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
                        world.spreadParticles(ParticleTypes.LAVA, false, Vec3d(x, y, z), Vec3d.ZERO, 1.0, 1)
                        world.playSound(null, x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f)
                    }
                    if (random.nextInt(200) == 0) {
                        world.playSound(null, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f)
                    }
                }
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature ->
                if (random.nextDouble() < chance) {
                    if (random.nextInt(5) == 0)
                        world.spreadParticles(ParticleTypes.POOF, false, blockPos.toCenter().add(0.0, 0.3125 + world.random.nextDouble() * 0.7, 0.0), Vec3d(0.0, 0.1, 0.0), 1.0, 0)
                }
            })
        }
        
        @JvmStatic
        fun process(te: ItemDrainTileEntity, heldItem0: TransportedItemStack?, cir: CallbackInfoReturnable<Boolean>, processingTicks: Ref.IntRef) {
            te as InjectItemDrainTileEntity
            switch2(te, heldItem0, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                val duration = heldItemStack.toastingDuration ?: recipe.duration
                if (duration > 0) {
                    val a = 4.3322725541611750040909696772139
                    val b = 0.50987589830933713890560128694813
                    val c = 0.87746897457733428472640032173703
                    val heat = (temperature.toDouble() - LOWEST_TEMPERATURE) / (LAVA_TEMPERATURE - LOWEST_TEMPERATURE) *
                      when (val x = amount.toDouble() / FluidConstants.BUCKET) {
                          in 0.0..1.0 -> x
                          else -> a * (x - b).pow(5) + c
                      }//.printS()
                    val cargo = heldItemStack.count.toDouble().pow(-1 / 3.0)
                    heldItemStack.toastingDuration = (duration - cargo * heat)
                    cir.returnValue = true
                    heldItem.beltPosition = 0.5f
                    heldItem.prevBeltPosition = 0.5f
                    processingTicks.element = 20
                    
                    val insertedFrom: Direction = heldItem.insertedFrom
                    var sideOffset = heldItem.sideOffset.toDouble()
                    val alongX = insertedFrom.rotateYClockwise().axis === Direction.Axis.X
                    if (!alongX) sideOffset *= -1
                    val pos = blockPos.toCenter().add(if (alongX) sideOffset else 0.0, 0.32, if (alongX) 0.0 else sideOffset) + (Vec3d.of(heldItem.insertedFrom.opposite.vector) * (0.5 - heldItem.beltPosition))
                    for (i in 0 until heat.random().toInt()) {
                        if (random.nextInt(5) == 0) {
                            world.spreadParticles(ParticleTypes.POOF, false, pos, Vec3d.ZERO, 0.01, 1)
                            world.spreadParticles(ParticleTypes.SMOKE, false, pos, Vec3d.ZERO, 0.01, 1)
                            world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1f, 1f)
                        }
                        if (random.nextInt(2) == 0) {
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
                //TODO 蒸
            })
        }
        
        @JvmStatic
        fun enableProcessing(te: ItemDrainTileEntity, world0: World, heldItemStack0: ItemStack, heldItem0: TransportedItemStack?): Boolean {
            te as InjectItemDrainTileEntity
            te.toastingStage = 0
            if (EmptyingByBasin.canItemBeEmptied(world0, heldItemStack0)) {
                return true
            }
            switch2(te, heldItem0, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                if (te.toastingStage == 0)
                    te.toastingStage = 1
                return true
            }, { world, fluidVariant, amount, random, chance, blockPos, temperature, heldItem, heldItemStack, recipe ->
                return true
            })
            return false
        }
        
        @JvmStatic
        fun modifyAngle(te: ItemDrainTileEntity, heldItem: TransportedItemStack, insertedFrom: Direction) {
            if (heldItem.stack.isOf(ItemsRegistry.BARBECUE_STICK.get())) {
//                te.world.printS()
//                heldItem.angle.printS()
                heldItem.angle = (insertedFrom.horizontal) * 90 - 45
//                heldItem.angle.printS()
//                te.notifyUpdate()
            }
        }
    }
}

/**
 * @param world
 * @see InjectItemDrainTileEntity.switch1
 */
private typealias Switch1 = (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance: Double, blockPos: BlockPos, temperature: Int) -> Unit
/**
 * @param world
 * @see InjectItemDrainTileEntity.switch2
 */
private typealias Switch2 = (world: ServerWorld, fluidVariant: FluidVariant, amount: Long, random: Random, chance/*TODO 换个名字，比如占用率？*/: Double, blockPos: BlockPos, temperature: Int, heldItem: TransportedItemStack, heldItemStack: ItemStack, recipe: SingleRecipe) -> Unit