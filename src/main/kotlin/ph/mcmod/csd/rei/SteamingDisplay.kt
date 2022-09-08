package ph.mcmod.csd.rei

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import ph.mcmod.csd.MyRegistries
import ph.mcmod.csd.game.SingleRecipe
import ph.mcmod.csd.game.BarbecueRecipe
import java.util.*

open class SteamingDisplay : SingleDisplay {
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, location: Optional<Identifier>) : super(inputs, outputs, location)
    constructor(inputs: List<EntryIngredient>, outputs: List<EntryIngredient>, extra: NbtCompound) : super(inputs, outputs, extra)
    constructor(recipe: SingleRecipe) : super(recipe)
    
    override fun getCategoryIdentifier(): CategoryIdentifier<*> {
        return MyREIClientPlugin.STEAMING_ID
    }
}