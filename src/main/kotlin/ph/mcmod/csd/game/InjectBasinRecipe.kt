package ph.mcmod.csd.game

import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock
import com.simibubi.create.foundation.fluid.FluidIngredient
import io.github.fabricators_of_create.porting_lib.util.FluidStack
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.forEach

interface InjectBasinRecipe {
    @ApiStatus.Internal
    companion object {
        private var isBowl = false
        fun removeContainer(ingredients: MutableList<Ingredient>, simulate: Boolean): Boolean {
            ingredients.forEach { element, remove ->
                for (stack in element.matchingStacks) {
                    if (stack.isOf(Items.BOWL)) {
                        if (!simulate)
                            remove()
                        return true
                    }
                }
            }
            return false
        }
        @JvmStatic
        fun removeContainer(te: BasinTileEntity, recipe: Recipe<*>, test: Boolean, isBasinRecipe: Boolean, availableItems: Storage<ItemVariant>, availableFluids: Storage<FluidVariant>, heat: BlazeBurnerBlock.HeatLevel, recipeOutputItems: MutableList<ItemStack>, recipeOutputFluids: MutableList<FluidStack>, ingredients: MutableList<Ingredient>) {
            isBowl = removeContainer(ingredients, false)
        }
        
        @JvmStatic
        fun modifyResult(te: BasinTileEntity, recipe: Recipe<*>, test: Boolean, isBasinRecipe: Boolean, availableItems: Storage<ItemVariant>, availableFluids: Storage<FluidVariant>, heat: BlazeBurnerBlock.HeatLevel, recipeOutputItems: MutableList<ItemStack>, recipeOutputFluids: MutableList<FluidStack>, ingredients: MutableList<Ingredient>, fluidIngredients: MutableList<FluidIngredient>, t: Transaction, fluidsAffected: Boolean) {
            if (isBowl) {
                if (recipeOutputItems.size == 1) {
                    recipeOutputItems[0].orCreateNbt.apply {
                        putInt("CustomModelData", 1)
                        putString("needItem",Items.BOWL.sId)
                    }
                }
            }
        }
        
        val BOWL_RECIPES = mutableSetOf<Identifier>()
    }
}