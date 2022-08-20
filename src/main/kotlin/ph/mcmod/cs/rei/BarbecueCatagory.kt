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

object BarbecueCatagory : DisplayCategory<BarbecueDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<out BarbecueDisplay> {
        return MyRegistries.REIClient.BARBECUE
    }
    
    override fun getTitle(): Text {
        return MyRegistries.REIClient.BARBECUE_TITLE
    }
    
    override fun getIcon(): Renderer {
        return DoubleItemIcon(AllBlocks.ITEM_DRAIN::asStack, Items.LAVA_BUCKET::getDefaultStack)
    }
    
    override fun setupDisplay(display: BarbecueDisplay, bounds: Rectangle): MutableList<Widget> {
        val startPoint = Point(bounds.x + 7, bounds.y + 9)
        val duration: Double = display.duration
        val df = DecimalFormat("###.##")
        val widgets: MutableList<Widget> = Lists.newArrayList()
        widgets.add(Widgets.createRecipeBase(bounds))
        widgets.add(Widgets.createResultSlotBackground(Point(startPoint.x + 61, startPoint.y + 0)))
        widgets.add(Widgets.createSlot(Point(startPoint.x + 1, startPoint.y + 0))
          .entries(listOf(EntryStacks.of(Fluids.LAVA)))
          .disableBackground()
          .disableTooltips()
          .disableHighlight())
        widgets.add(Widgets.createLabel(Point(bounds.x + bounds.width - 5, bounds.y + 5), TranslatableText("category.rei.campfire.time", df.format(duration / 20.0)))
          .noShadow()
          .rightAligned()
          .color(-0xbfbfc0, -0x444445))
        widgets.add(Widgets.createArrow(Point(startPoint.x + 24, startPoint.y + 0))
          .animationDurationTicks(duration))
        widgets.add(Widgets.createSlot(Point(startPoint.x + 61, startPoint.y + 0))
          .entries(display.outputEntries[0])
          .disableBackground()
          .markOutput())
        widgets.add(Widgets.createSlot(Point(startPoint.x + 1, startPoint.y + 0))
          .entries(display.inputEntries[0])
          .markInput())
        return widgets
    }

//    override fun getDisplayRenderer(display: ToastingDisplay): DisplayRenderer {
//        return SimpleDisplayRenderer.from(display.inputEntries, display.outputEntries)
//    }
    
    override fun getDisplayHeight(): Int {
        return 34
    }
    
    override fun getDisplayWidth(display: BarbecueDisplay): Int {
        return 130
    }
}