package ph.mcmod.csd.rei

import com.simibubi.create.AllBlocks
import com.simibubi.create.compat.rei.DoubleItemIcon
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

object BarbecueCatagory : SingleCatagory<BarbecueDisplay>() {
    val ITEM_DRAIN_LAVA: ItemStack = AllBlocks.ITEM_DRAIN.asStack().apply {
        orCreateNbt.putInt("CustomModelData", 2)
    }
    
    override fun getCategoryIdentifier(): CategoryIdentifier<out BarbecueDisplay> {
        return MyREIClientPlugin.BARBECUE_ID
    }
    
    override fun getTitle(): Text {
        return MyREIClientPlugin.BARBECUE_TITLE
    }
    
    override fun getIcon(): Renderer {
        return EntryStacks.of(ITEM_DRAIN_LAVA)
    }
}