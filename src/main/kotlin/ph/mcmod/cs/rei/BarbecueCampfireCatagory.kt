package ph.mcmod.cs.rei

import com.simibubi.create.AllBlocks
import com.simibubi.create.compat.rei.DoubleItemIcon
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import net.minecraft.item.Items
import net.minecraft.text.Text

object BarbecueCampfireCatagory : SingleCatagory<BarbecueDisplay>() {
    override fun getCategoryIdentifier(): CategoryIdentifier<out BarbecueDisplay> {
        return MyREIClientPlugin.BARBECUE_CAMPFIRE_ID
    }
    
    override fun getTitle(): Text {
        return MyREIClientPlugin.BARBECUE_CAMPFIRE_TITLE
    }
    
    override fun getIcon(): Renderer {
        return DoubleItemIcon(BarbecueCatagory::ITEM_DRAIN_LAVA,Items.CAMPFIRE::getDefaultStack )
    }
}