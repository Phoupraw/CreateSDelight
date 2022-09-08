package ph.mcmod.csd.game

import com.simibubi.create.content.contraptions.processing.BasinRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import ph.mcmod.csd.id

class SteepingRecipe(params: ProcessingRecipeBuilder.ProcessingRecipeParams) : BasinRecipe(RECIPE_TYPE_INFO, params) {
    companion object {
        val RECIPE_TYPE_INFO = RecipeTypeInfo<SteepingRecipe>(id("steeping"), ProcessingRecipeSerializer(::SteepingRecipe))
    }
}