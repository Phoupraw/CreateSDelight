package ph.mcmod.csd.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import ph.mcmod.csd.game.SingleRecipe
import ph.mcmod.csd.game.BarbecueRecipe
import java.util.*

abstract class SingleDisplay(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, location: Optional<Identifier>) : BasicDisplay(inputs, outputs, location), SimpleGridMenuDisplay {
    var duration = SingleRecipe.DEFUALT_DURATION
    
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, extra: NbtCompound) : this(inputs, outputs, Optional.empty()) {
        duration = extra.getDouble("duration")
    }
    
    constructor(recipe: SingleRecipe) : this(listOf(EntryIngredients.ofIngredient(recipe.ingredient)), listOf(EntryIngredients.of(recipe.output)), Optional.empty()) {
        duration = recipe.duration
    }
    
    override fun getWidth(): Int {
        return 1
    }
    
    override fun getHeight(): Int {
        return 1
    }
    
    companion object {
        fun <R : SingleDisplay> serializer(constructor: Serializer.RecipeLessConstructor<R>): Serializer<R> {
            return Serializer.ofRecipeLess(constructor) { display, extra ->
                extra.putDouble("duration", display.duration)
            }
        }
    }
    
}