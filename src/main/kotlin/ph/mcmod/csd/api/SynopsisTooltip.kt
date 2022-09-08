@file:Suppress("DEPRECATION")

package ph.mcmod.csd.api

import net.devtech.arrp.json.lang.JLang
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.kum.arrp.ConcurrentJTag
import ph.mcmod.kum.runAtClient
import ph.mcmod.csd.ARRP_HELPER
import ph.mcmod.csd.CSD

/**
 * 显示在工具提示的物品简介
 */
object SynopsisTooltip {
	//TODO 准备分离出去
	const val PATH = "synopsis"
	/**
	 * 在这个标签内的物品才会显示简介
	 */
	@JvmField
	val TAG: TagKey<Item> = TagKey.of(Registry.ITEM.key, Identifier(CSD, PATH))
	private val J_TAG = ConcurrentJTag()
	private val J_TAG_SET = mutableSetOf<Item>()
	
	init {
		ARRP_HELPER.tags[Identifier(CSD, "items/$PATH")] = J_TAG
	}
	/**
	 * 添加简介
	 * @param item 物品
	 * @param synopsis 简介
	 * @param jLang 语言，默认中文
	 */
	@JvmStatic
	fun addSynopsis(item: Item, synopsis: String, jLang: JLang = ARRP_HELPER.lang_zh_cn) {
		addSynopsis(item, getTranslationKey(item), synopsis, jLang)
	}
	/**
	 * 添加简介
	 * @param itemStack 物品
	 * @param synopsis 简介
	 * @param jLang 语言，默认中文
	 */
	@JvmStatic
	fun addSynopsis(itemStack: ItemStack, synopsis: String, jLang: JLang = ARRP_HELPER.lang_zh_cn) {
		addSynopsis(itemStack.item, getTranslationKey(itemStack), synopsis, jLang)
	}
	/**
	 * 添加简介
	 * @param item 物品
	 * @param translationKey 本地化键
	 * @param synopsis 简介
	 * @param jLang 语言
	 */
	private fun addSynopsis(item: Item, translationKey: String, synopsis: String, jLang: JLang) {
		if (item !in J_TAG_SET) {
			J_TAG.add(item.id)
			J_TAG_SET += item
		}
		runAtClient { jLang.entry(translationKey, synopsis) }
	}
	/**
	 * 获取简介的本地化键 （不是名称的！）
	 * @param item 物品
	 * @return 本地化键
	 */
	@JvmStatic
	fun getTranslationKey(item: Item): String {
		return "${item.translationKey}.$PATH"
	}
	/**
	 * 获取简介的本地化键 （不是名称的！）
	 * @param itemStack 物品
	 * @return 本地化键
	 */
	@JvmStatic
	fun getTranslationKey(itemStack: ItemStack): String {
		return "${itemStack.translationKey}.$PATH"
	}
	/**
	 * 不直接把具体代码写在Mixin类，而是委托至普通类，方便调试与排错
	 * @see ph.mcmod.csd.mixin
	 */
	@JvmStatic
	@Suppress("UNUSED_PARAMETER")
	@ApiStatus.Internal
	fun mixinDelegate(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext, item: Item) {
		if (item.registryEntry.isIn(TAG))
			tooltip.add(TranslatableText(getTranslationKey(stack), stack.toHoverableText(), stack.name).formatted(Formatting.GRAY))
	}
}