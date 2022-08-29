package ph.mcmod.cs.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.CampfireCookingRecipe
import net.minecraft.util.Identifier
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.game.BarbecueRecipe
import java.util.*

class BarbecueCampfireDisplay(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, location: Optional<Identifier>) : BarbecueDisplay(inputs, outputs, location) {
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, extra: NbtCompound) : this(inputs, outputs, Optional.empty()) {
        duration = extra.getDouble("duration")
    }
    
    constructor(recipe: CampfireCookingRecipe) : this((recipe.ingredients.map { EntryIngredients.ofIngredient(it) }), listOf(EntryIngredients.of(recipe.output)), Optional.empty()) {
        duration = recipe.cookTime.toDouble() / 5
    }
    
    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return MyRegistries.REIClient.BARBECUE_CAMPFIRE
    }
}