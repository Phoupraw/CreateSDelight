package ph.mcmod.cs.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack
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
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Direction
import org.apache.logging.log4j.ThreadContext.setStack
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.mixin.AccessDepotBehaviour
import ph.mcmod.kum.spreadParticles
import ph.mcmod.kum.toCenter
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
            val world = te.world as? ServerWorld ?: return
            val blockPos = te.pos
            if (!world.getBlockState(blockPos.down()).isOf(AllBlocks.LIT_BLAZE_BURNER.get())) {
                return
            }
            val fluidStorage = (te as FluidTransferable).getFluidStorage(null) ?: Storage.empty()
            Transaction.openOuter().use { transaction ->
                for (view in fluidStorage.iterator(transaction)) {
                    if (view.resource.fluid === MyRegistries.MyFluids.SUNFLOWER_OIL) {
                        world.spreadParticles(MyRegistries.MyParticles.OIL_BUBBLE, false, blockPos.toCenter().add(0.0, 0.3125, 0.0), 0.1, 0.0, 1)
                        break
                    }
                }
            }
            val behaviour = te.getBehaviour(DepotBehaviour.TYPE)
            (behaviour as AccessDepotBehaviour).heldItem?.also { heldItem->
                val heldItemStack = heldItem.stack ?: ItemStack.EMPTY
                val maker = SimpleInventory(1).apply { setStack(0, heldItemStack) }
                world.server.recipeManager.getFirstMatch(RecipeType.CAMPFIRE_COOKING, maker, world).takeIf { it.isPresent }?.also {
                    world.playSound(null, blockPos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1f, 1f)
                    val recipe = it.get()
                    val time = heldItemStack.time
                    if (time.isNaN()) {
                        val neededTime = (recipe.cookTime / 4.0)
                        heldItemStack.time = neededTime
                    } else if (time > 0) {
                        heldItemStack.time = time - 1 / sqrt(heldItemStack.count.toDouble())
                    } else if (time <= 0) {
                        heldItem.stack = recipe.craft(maker).apply { count = heldItemStack.count }
                        te.notifyUpdate()
                    }
                }
            }
            
        }

//        @JvmStatic
//        fun addBehaviours(te: DepotTileEntity, behaviours: MutableList<TileEntityBehaviour>) {
//            behaviours+=(te as InjectDepotTileEntity).tank
//        }
    }
}