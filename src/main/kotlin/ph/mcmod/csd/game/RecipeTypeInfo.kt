package ph.mcmod.csd.game

import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import ph.mcmod.kum.Identifiable

class RecipeTypeInfo<R : Recipe<*>>(id: Identifier, private val serializer: RecipeSerializer<R>) : IRecipeTypeInfo, Identifiable {
    private val type: RecipeType<R>
    
    init {
        Registry.register(Registry.RECIPE_SERIALIZER, id, serializer)
        this.type = RecipeType.register(id.toString())
    }
    
    override fun getId(): Identifier {
        return type.id
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecipeSerializer<*>> getSerializer(): T {
        return serializer as T
    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecipeType<*>> getType(): T {
        return type as T
    }
}