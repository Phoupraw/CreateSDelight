package ph.mcmod.cs.game

import net.minecraft.block.Block
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World

open class DescriptedBlockItem(block: Block, settings: Settings) : BlockItem(block, settings) {
    /**
     * 为物品添加描述。
     *
     * 版本：*2.0.0*
     * @see DescriptedItem.appendTooltip
     */
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += TranslatableText("item.${sId.replace(":", ".")}.desc")
    }
}