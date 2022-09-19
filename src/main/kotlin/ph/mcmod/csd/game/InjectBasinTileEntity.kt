package ph.mcmod.csd.game

import com.simibubi.create.AllTags
import com.simibubi.create.content.contraptions.processing.BasinBlock
import com.simibubi.create.content.contraptions.processing.BasinRecipe
import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import com.simibubi.create.foundation.utility.recipe.RecipeFinder
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.inventory.Inventory
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Recipe
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.FluidTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShapes
import ph.mcmod.kum.containsString
import ph.mcmod.kum.spreadParticles
import ph.mcmod.kum.toCenter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

interface InjectBasinTileEntity {
    var temperature: Double
    var animationTicks: Double
    val steepingKey: Any
    var steepingDuration: Double?
    var youtiaoDuration: Double?
    
    companion object {
        const val BOILING = false
        fun mapTemperature(te: BasinTileEntity): Double {
            te as InjectBasinTileEntity
            return (te.temperature - 25) / 75
        }
        @JvmStatic
        fun tick(te: BasinTileEntity) {
            te as InjectBasinTileEntity
            val world = te.world ?: return
            val facing = te.cachedState.get(BasinBlock.FACING)
            
            if (facing != Direction.DOWN) {
                val youtiaoProgress = te.youtiaoDuration ?: if (world.time % 100 == 0L) 0.0 else null
                if (youtiaoProgress != null) {
                    if (youtiaoProgress > 0) {
                        te.youtiaoDuration = youtiaoProgress - 1
                    } else {
                        te.youtiaoDuration = null
                    }
                }
            }
            if (BOILING) {
                val t1 = mapTemperature(te)
                val t2 = t1.pow(3)
                te.animationTicks += t2
                
                val blockPos = te.pos
                if (world.getBlockState(blockPos.down()).isIn(AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag) && run {
                      Transaction.openOuter().use { transaction ->
                          for (view in (te.getFluidStorage(null) ?: return@use).iterator(transaction)) {
                              if (view.resource.fluid.isIn(FluidTags.WATER)) {
                                  return@run true
                              }
                          }
                      }
                      false
                  }) {
                    
                    te.temperature = min(100.0, te.temperature + 0.5)
                    
                    //            boilingPoses += blockPos
                    val pressure = run {
                        val upPos = blockPos.up()
                        VoxelShapes.adjacentSidesCoverSquare(te.cachedState.getOutlineShape(world, blockPos), world.getBlockState(upPos).getOutlineShape(world, upPos), Direction.UP)
                    }
                    if (pressure) {
                    
                    }
                    val pos = blockPos.toCenter()
                    val random = world.random
                    if (world is ServerWorld) {
                        if (random.nextDouble() < t2) {
                            if (random.nextInt(2) == 0) {
                                world.spreadParticles(ParticleTypes.SPLASH, false, pos, 0.1, 0.0, 1)
                                world.spreadParticles(ParticleTypes.BUBBLE, false, pos, 0.1, 0.0, 1)
                                if (random.nextInt(4) == 0) {
                                    world.spreadParticles(ParticleTypes.POOF, false, pos, 0.1, 0.0, 1)
                                }
                            }
                        }
                    }
                } else {
                    te.apply {
                        temperature = max(25.0, temperature - 0.5)
                    }
                    //            boilingPoses-=blockPos
                }
                te.notifyUpdate()
            }
        }
        
        @JvmStatic
        fun modifyOutput(te: BasinTileEntity, targetInv: Storage<ItemVariant>, itemVariant: ItemVariant, amount: Long, nested: TransactionContext): Long {
            val nbt = itemVariant.nbt
            if (nbt?.containsString("needItem") == true) {
                val need = Registry.ITEM.get(Identifier(nbt.getString("needItem")))
                if (need != Items.AIR) {
                    var result = 0L
                    StorageUtil.findExtractableContent(targetInv, { it.item == need }, nested)?.also { content ->
                        if (amount == content.amount) {
                            Transaction.openNested(nested).use { transaction ->
                                val newNbt = nbt.copy()
                                newNbt.remove("needItem")
                                newNbt.remove("CustomModelData")
                                newNbt.putInt("render", BasinTileEntity.OUTPUT_ANIMATION_TIME - 4)
                                val newVariant = ItemVariant.of(itemVariant.item, newNbt)
                                targetInv.extract(content.resource, amount, transaction)
                                if (targetInv.insert(newVariant, amount, transaction) == amount) {
                                    result = amount
                                    transaction.commit()
                                }
                            }
                        }
                    }
                    return result
                }
            }
            return targetInv.insert(itemVariant, amount, nested)
        }
        
        @JvmStatic
        fun tickSteeping(te: BasinTileEntity) {
            val world = te.world ?: return
            te as InjectBasinTileEntity
            findSteepingRecipe(te)?.also { recipe ->
                val duration = te.steepingDuration ?: recipe.processingDuration.toDouble()
                if (duration > 0) {
                    te.steepingDuration = duration - 1
                } else {
                    applyBasinRecipe(te)
                }
            } ?: run {
                te.steepingDuration = null
            }
        }
        
        fun findSteepingRecipe(te: BasinTileEntity): SteepingRecipe? {
            return getMatchingRecipes(te).getOrNull(0) as? SteepingRecipe
        }
        
        fun getMatchingRecipes(te: BasinTileEntity): List<Recipe<*>> {
            val world = te.world ?: return emptyList()
            te as InjectBasinTileEntity
            val list = RecipeFinder.get(te.steepingKey, world) { it.type == SteepingRecipe.RECIPE_TYPE_INFO.getType() }
            return list.filter { matchBasinRecipe(te, it) }
//              .sortedWith { r1, r2 -> (r2.ingredients.size - r1.ingredients.size) }
        }
        
        fun <C : Inventory> matchBasinRecipe(te: BasinTileEntity, recipe: Recipe<C>): Boolean {
            return BasinRecipe.match(te, recipe)
        }
        
        fun applyBasinRecipe(te: BasinTileEntity) {
            val recipe = findSteepingRecipe(te) ?: return
            val wasEmpty = te.canContinueProcessing()
            if (!BasinRecipe.apply(te, recipe)) return
            te.inputTank.sendDataImmediately()
            // Continue mixing
            if (wasEmpty && matchBasinRecipe(te, recipe)) {
                te.sendData()
            }
            te.notifyChangeOfContents()
        }
    }
}