package ph.mcmod.csd.rei

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import com.simibubi.create.compat.rei.DoubleItemIcon
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import ph.mcmod.csd.rei.BarbecueCatagory.ITEM_DRAIN_LAVA

object RoastingCatagory : SingleCatagory<RoastingDisplay>() {
    
    override fun getCategoryIdentifier(): CategoryIdentifier<out RoastingDisplay> {
        return MyREIClientPlugin.ROASTING_ID
    }
    
    override fun getTitle(): Text {
        return MyREIClientPlugin.ROASTING_TITLE
    }
    
    override fun getIcon(): Renderer {
        return EntryStacks.of(AllBlocks.SHAFT.get())
    }
}