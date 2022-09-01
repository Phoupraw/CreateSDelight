package ph.mcmod.cs.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import ph.mcmod.cs.game.SingleRecipe
import java.util.*

open class BarbecueDisplay : SingleDisplay {
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, location: Optional<Identifier>) : super(inputs, outputs, location)
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, extra: NbtCompound) : super(inputs, outputs, extra)
    constructor(recipe: SingleRecipe) : super(recipe)
    
    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return MyREIClientPlugin.BARBECUE_ID
    }
}