package ph.mcmod.cs.rei

import com.simibubi.create.AllBlocks
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.fluid.Fluids
import net.minecraft.recipe.CampfireCookingRecipe
import net.minecraft.recipe.RecipeType
import net.minecraft.text.TranslatableText
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.game.BarbecueRecipe
import ph.mcmod.cs.game.RoastingRecipe
import ph.mcmod.cs.game.SteamingRecipe

object MyREIClientPlugin : REIClientPlugin {
    val BARBECUE_ID: CategoryIdentifier<BarbecueDisplay> = CategoryIdentifier.of(MyRegistries.MyRecipeTypes.BARBECUE.toString())
    val BARBECUE_TITLE = TranslatableText("category.${BARBECUE_ID.namespace}.${BARBECUE_ID.path}")
    val BARBECUE_CAMPFIRE_ID: CategoryIdentifier<BarbecueDisplay> = CategoryIdentifier.of(MyRegistries.id("barbecue_campfire"))
    val BARBECUE_CAMPFIRE_TITLE = TranslatableText("category.${BARBECUE_CAMPFIRE_ID.namespace}.${BARBECUE_CAMPFIRE_ID.path}")
    val STEAMING_ID: CategoryIdentifier<SteamingDisplay> = CategoryIdentifier.of(MyRegistries.MyRecipeTypes.STEAMING.toString())
    val STEAMING_TITLE = TranslatableText("category.${STEAMING_ID.namespace}.${STEAMING_ID.path}")
    val ROASTING_ID: CategoryIdentifier<RoastingDisplay> = CategoryIdentifier.of(MyRegistries.MyRecipeTypes.ROASTING.toString())
    val ROASTING_TITLE = TranslatableText("category.${ROASTING_ID.namespace}.${ROASTING_ID.path}")
    
    init {
        MyRegistries.arrpHelper.getLang()
          .entry(BARBECUE_TITLE.key, "烧烤")
          .entry(BARBECUE_CAMPFIRE_TITLE.key, "烧烤（营火烹饪）")
          .entry(STEAMING_TITLE.key, "蒸")
          .entry(ROASTING_TITLE.key, "炙烤")
        MyRegistries.arrpHelper.getLang("en_us")
          .entry(BARBECUE_TITLE.key, "Barbecue")
          .entry(BARBECUE_CAMPFIRE_TITLE.key, "Barbecue (Campfire Cooking)")
          .entry(STEAMING_TITLE.key, "Steaming")
          .entry(ROASTING_TITLE.key, "Roasting")
    }
    
    override fun getPluginProviderName(): String {
        return MyRegistries.id("rei_client").toString()
    }
    
    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(BarbecueCatagory)
        registry.addWorkstations(BarbecueCatagory.categoryIdentifier, EntryStacks.of(AllBlocks.ITEM_DRAIN.get()), EntryStacks.of(Fluids.LAVA))
        registry.add(BarbecueCampfireCatagory)
        registry.addWorkstations(BarbecueCampfireCatagory.categoryIdentifier, EntryStacks.of(AllBlocks.ITEM_DRAIN.get()), EntryStacks.of(Fluids.LAVA))
        registry.add(SteamingCatagory)
        registry.addWorkstations(SteamingCatagory.categoryIdentifier, EntryStacks.of(AllBlocks.ITEM_DRAIN.get()), EntryStacks.of(Fluids.WATER))
        registry.add(RoastingCatagory)
        registry.addWorkstations(RoastingCatagory.categoryIdentifier, EntryStacks.of(AllBlocks.SHAFT.get()))
    }
    
    override fun registerScreens(registry: ScreenRegistry) {
//            registry.registerContainerClickArea(Rectangle(78, 32, 28, 23), BlastFurnaceScreen::class.java, TOASTING)
    }
    
    override fun registerDisplays(registry: DisplayRegistry) {
        registry.registerRecipeFiller(BarbecueRecipe::class.java, MyRegistries.MyRecipeTypes.BARBECUE, ::BarbecueDisplay)
        registry.registerRecipeFiller(CampfireCookingRecipe::class.java, RecipeType.CAMPFIRE_COOKING, ::BarbecueCampfireDisplay)
        registry.registerRecipeFiller(SteamingRecipe::class.java, MyRegistries.MyRecipeTypes.STEAMING, ::SteamingDisplay)
        registry.registerRecipeFiller(RoastingRecipe::class.java, MyRegistries.MyRecipeTypes.ROASTING, ::RoastingDisplay)
    }
}