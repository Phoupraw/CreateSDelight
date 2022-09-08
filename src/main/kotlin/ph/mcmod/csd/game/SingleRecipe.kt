package ph.mcmod.csd.game

import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType
import com.simibubi.create.content.contraptions.itemAssembly.IAssemblyRecipe
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

abstract class SingleRecipe(private val id: Identifier, val ingredient: Ingredient, val result: ItemVariant, val duration: Double) : Recipe<Inventory>,IAssemblyRecipe {
    override fun getId(): Identifier {
        return id
    }
    
    override fun getOutput(): ItemStack {
        return result.toStack()
    }
    
    override fun craft(inventory: Inventory): ItemStack {
        return output
    }
    
    override fun matches(inventory: Inventory, world: World): Boolean {
        return ingredient.test(inventory.getStack(0))
    }
    
    override fun fits(width: Int, height: Int): Boolean {
        return true
    }
    
    override fun getDescriptionForAssembly(): Text {
       return TranslatableText("recipe.${id.namespace}.${id.path}")
    }
    
    override fun addAssemblyIngredients(list: MutableList<Ingredient>) {
    
    }
    override fun getJEISubCategory(): SequencedAssemblySubCategoryType {
        return SequencedAssemblySubCategoryType.PRESSING//TODO
    }
    companion object {
        const val DEFUALT_DURATION = 100.0
    }
    
    abstract class Serializer<T : SingleRecipe> : RecipeSerializer<T> {
        
        override fun read(id: Identifier, json: JsonObject): T {
//            require(JsonHelper.hasJsonObject(json, "ingredient")) { "ingredient" }
//            require(JsonHelper.hasJsonObject(json, "result")) { "result" }
            return new(id,
              Ingredient.fromJson(json.getAsJsonObject("ingredient")),
              JsonHelper.getObject(json, "result").let {
                  ItemVariant.of(
                    Registry.ITEM.get(Identifier(JsonHelper.getString(it, "id"))),
                    if (JsonHelper.hasString(it, "nbt")) NbtCompoundArgumentType.nbtCompound().parse(StringReader(JsonHelper.getString(it, "nbt"))) else null
                  )
              },
              if (JsonHelper.hasNumber(json, "duration")) JsonHelper.getDouble(json, "duration") else DEFUALT_DURATION
            )
        }
        
        override fun read(id: Identifier, buf: PacketByteBuf): T {
            return new(id,
              Ingredient.fromPacket(buf),
              ItemVariant.fromPacket(buf),
              buf.readDouble()
            )
        }
        
        override fun write(buf: PacketByteBuf, recipe: T) {
            recipe.ingredient.write(buf)
            recipe.result.toPacket(buf)
            buf.writeDouble(recipe.duration)
        }
        
        abstract fun new(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double): T
    }
}