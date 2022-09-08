package ph.mcmod.csd.game

import com.simibubi.create.AllBlocks
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.*
import net.minecraft.util.Identifier
import ph.mcmod.csd.MyRegistries

class RoastingRecipe(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double) : SingleRecipe(id, ingredient, result, duration) {
    
    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer
    }
    
    override fun getType(): RecipeType<*> {
        return MyRegistries.MyRecipeTypes.ROASTING
    }
    override fun addRequiredMachines(list: MutableSet<ItemConvertible>) {
        list += AllBlocks.SHAFT.get()
    }
    object Serializer : SingleRecipe.Serializer<RoastingRecipe>() {
        override fun new(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double): RoastingRecipe {
            return RoastingRecipe(id, ingredient, result, duration)
        }
    }
}