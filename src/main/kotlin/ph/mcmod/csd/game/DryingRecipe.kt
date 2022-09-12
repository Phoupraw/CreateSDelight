package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import ph.mcmod.csd.MyRegistries

class DryingRecipe(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double) : SingleRecipe(id, ingredient, result, duration) {
    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer
    }
    
    override fun getType(): RecipeType<*> {
        return MyRegistries.MyRecipeTypes.DRYING
    }
    
    override fun addRequiredMachines(list: MutableSet<ItemConvertible>) {
        list += MyRegistries.MyBlocks.DRYING_RACK
    }
    
    object Serializer : SingleRecipe.Serializer<DryingRecipe>() {
        override fun new(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double): DryingRecipe {
            return DryingRecipe(id, ingredient, result, duration)
        }
    }
}