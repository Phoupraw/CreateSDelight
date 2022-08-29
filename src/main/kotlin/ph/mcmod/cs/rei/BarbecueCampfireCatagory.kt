package ph.mcmod.cs.rei

import com.google.common.collect.Lists
import com.simibubi.create.AllBlocks
import com.simibubi.create.compat.rei.DoubleItemIcon
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.fluid.Fluids
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import ph.mcmod.cs.MyRegistries
import java.text.DecimalFormat

object BarbecueCampfireCatagory : BarbecueCatagory() {
    override fun getCategoryIdentifier(): CategoryIdentifier<out BarbecueDisplay> {
        return MyRegistries.REIClient.BARBECUE_CAMPFIRE
    }
    
    override fun getTitle(): Text {
        return MyRegistries.REIClient.BARBECUE_CAMPFIRE_TITLE
    }
    
    override fun getIcon(): Renderer {
        return DoubleItemIcon(Items.CAMPFIRE::getDefaultStack, AllBlocks.ITEM_DRAIN::asStack)
    }
}