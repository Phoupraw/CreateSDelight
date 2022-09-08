package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.*
import net.minecraft.util.Identifier
import ph.mcmod.csd.CSD
import ph.mcmod.csd.MyRegistries

class BarbecueRecipe(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double) : SingleRecipe(id, ingredient, result, duration) {
    constructor(recipe: CampfireCookingRecipe) : this(
      Identifier(CSD, "${recipe.id.namespace}/${recipe.id.path}"),
      recipe.ingredients[0],
      ItemVariant.of(recipe.output),
      recipe.cookTime / 5.0
    )
    
    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer
    }
    
    override fun getType(): RecipeType<*> {
        return MyRegistries.MyRecipeTypes.BARBECUE
    }
    
    override fun addRequiredMachines(list: MutableSet<ItemConvertible>) {
        list += AllBlocks.ITEM_DRAIN.get()
    }
    
    object Serializer : SingleRecipe.Serializer<BarbecueRecipe>() {
        override fun new(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double): BarbecueRecipe {
            return BarbecueRecipe(id, ingredient, result, duration)
        }
    }
}