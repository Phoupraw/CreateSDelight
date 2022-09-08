package ph.mcmod.csd.game

import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.simibubi.create.AllBlocks
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline.result
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import ph.mcmod.csd.MyRegistries
import ph.mcmod.kum.id

class SteamingRecipe(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double) : SingleRecipe(id, ingredient, result, duration) {
    
    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer
    }
    
    override fun getType(): RecipeType<*> {
        return MyRegistries.MyRecipeTypes.STEAMING
    }
    override fun addRequiredMachines(list: MutableSet<ItemConvertible>) {
        list += AllBlocks.ITEM_DRAIN.get()
    }
    object Serializer : SingleRecipe.Serializer<SteamingRecipe>() {
        override fun new(id: Identifier, ingredient: Ingredient, result: ItemVariant, duration: Double): SteamingRecipe {
            return SteamingRecipe(id, ingredient, result, duration)
        }
    }
}