package ph.mcmod.csd.game

import com.simibubi.create.content.contraptions.processing.BasinRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer
import com.simibubi.create.foundation.item.SmartInventory
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import io.github.fabricators_of_create.porting_lib.util.FluidStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.World
import ph.mcmod.csd.id
import ph.mcmod.kum.iterator
import ph.mcmod.kum.simpleSlot

class BraisingRecipe(params: ProcessingRecipeBuilder.ProcessingRecipeParams) : ProcessingRecipe<SmartInventory>(RECIPE_TYPE_INFO, params) {
    
    override fun matches(inventory: SmartInventory, world: World): Boolean {
        Transaction.openOuter().use { transaction ->
            for (ingredient in ingredients) {
                if (!run {
                      for (view in inventory.iterator(transaction)) {
                          if (ingredient.test(view.resource.toStack())) {
                              if (view.extract(view.resource, 1, transaction) >= 1) {
                                  return@run true
                              }
                          }
                      }
                      false
                  }) {
                    return false
                }
            }
        }
        return true
    }
    
    override fun getMaxInputCount(): Int {
        return 9
    }
    
    override fun getMaxOutputCount(): Int {
        return 4
    }
    
    fun matches(fluidStorage: Storage<FluidVariant>): Boolean {
        Transaction.openOuter().use { transaction ->
            for (ingredient in fluidIngredients) {
                if (!run {
                      for (view in fluidStorage.iterator(transaction)) {
                          if (ingredient.test(FluidStack(view))) {
                              if (view.extract(view.resource, 1, transaction) >= 1) {
                                  return@run true
                              }
                          }
                      }
                      false
                  }) {
                    return false
                }
            }
        }
        return true
    }
    
    companion object {
        val RECIPE_TYPE_INFO = RecipeTypeInfo<BraisingRecipe>(id("braising"), ProcessingRecipeSerializer(::BraisingRecipe))
    }
}