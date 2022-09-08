package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour
import com.simibubi.create.content.logistics.block.depot.DepotTileEntity
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.csd.MyRegistries
import ph.mcmod.kum.containsInt
import ph.mcmod.kum.spreadParticles
import ph.mcmod.kum.toCenter
import kotlin.math.pow
import kotlin.math.sqrt

interface InjectDepotTileEntity {
    val tank: SmartFluidTankBehaviour
    var temperature: Double
    @ApiStatus.Internal
    companion object {
        private var ItemStack.time: Double
            get() = nbt?.getDouble("time") ?: Double.NaN
            set(value) {
                orCreateNbt.putDouble("time", value)
            }
        private var ItemStack.flipped: Boolean
            get() = nbt?.getBoolean("flipped") ?: false
            set(value) {
                orCreateNbt.putBoolean("flipped", value)
            }
        internal val ItemStack.needingFlipping: Boolean
            get() = time <= 0 && !flipped
        @JvmStatic
        fun newTank(te: DepotTileEntity): SmartFluidTankBehaviour {
            return SmartFluidTankBehaviour(SmartFluidTankBehaviour.TYPE, te, 2, FluidConstants.INGOT, true).whenFluidUpdates { te.notifyUpdate() }
        }
        
        @JvmStatic
        fun getFluidStorage(te: DepotTileEntity, face: Direction?): Storage<FluidVariant>? {
            return if (face !== Direction.UP) (te as InjectDepotTileEntity).tank.capability else null
        }
        
        @JvmStatic
        fun tick(te: DepotTileEntity) {
            te as InjectDepotTileEntity
            val behaviour = te.getBehaviour(DepotBehaviour.TYPE)
            behaviour as InjectDepotBehaviour
            if (behaviour.flippingCountdown >= 0) {
                behaviour.flippingCountdown--
            }
            val world = te.world as? ServerWorld ?: return
            val blockPos = te.pos
            val heldItem = behaviour.heldItem ?: return
            val heldItemStack = heldItem.stack ?: ItemStack.EMPTY
            if (heldItemStack.nbt?.containsInt("render") == true) {
                val render = heldItemStack.orCreateNbt.getInt("render")
                if (render > 0) {
                    heldItemStack.orCreateNbt.putInt("render", render - 1)
                } else {
                    heldItemStack.orCreateNbt.remove("render")
                    if (heldItemStack.orCreateNbt.isEmpty) {
                        heldItemStack.nbt = null
                    }
                    te.notifyUpdate()
                }
            }
            if (world.getBlockState(blockPos.down()).isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
                val fluidStorage = (te as FluidTransferable).getFluidStorage(null) ?: Storage.empty()
                Transaction.openOuter().use { transaction ->
                    for (view in fluidStorage.iterator(transaction)) {
                        if (view.resource.fluid === MyRegistries.MyFluids.SUNFLOWER_OIL) {
                            val offset = (view.amount.toDouble() / view.capacity).pow(2) * 0.12
                            //                        print("$offset ")
                            val chance = (view.amount / view.capacity.toDouble())
                            if (world.random.nextDouble() < chance.pow(3))
                                world.spreadParticles(MyRegistries.MyParticles.OIL_BUBBLE, false, blockPos.toCenter().add(0.0, 0.35, 0.0), Vec3d(offset, 0.0, offset), 0.0, 1)
                            break
                        }
                    }
                }
                
                val maker = SimpleInventory(1).apply { setStack(0, heldItemStack) }
                world.server.recipeManager.getFirstMatch(RecipeType.CAMPFIRE_COOKING, maker, world).takeIf { it.isPresent }?.also {
                    val recipe = it.get()
                    val time = heldItemStack.time
                    if (time.isNaN()) {
                        val neededTime = (recipe.cookTime / 5.0)
                        heldItemStack.time = neededTime / 2
                    } else if (time > 0) {
                        heldItemStack.time = time - 1 / sqrt(heldItemStack.count.toDouble())
                    } else if (time <= 0) {
                        if (heldItemStack.flipped) {
                            heldItem.stack = recipe.craft(maker).apply { count = heldItemStack.count }
                            te.notifyUpdate()
                            world.spreadParticles(ParticleTypes.POOF, false, blockPos.toCenter().add(0.0, 0.35, 0.0), Vec3d(0.1, 0.0, 0.1), 0.0, 10)
                        }
                    }
                    if (time > 0) {
                        world.playSound(null, blockPos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1f, 1f)
                    }
                }
                if (behaviour.flippingCountdown == 0) {
                    heldItemStack.flipped = true
                    heldItemStack.time = Double.NaN
                    var angle = heldItem.angle
                    angle -= 90
                    angle *= -1
                    angle += 90
                    heldItem.angle = angle
                    te.notifyUpdate()
                }
                
            }
        }

//        @JvmStatic
//        fun addBehaviours(te: DepotTileEntity, behaviours: MutableList<TileEntityBehaviour>) {
//            behaviours+=(te as InjectDepotTileEntity).tank
//        }
    }
}