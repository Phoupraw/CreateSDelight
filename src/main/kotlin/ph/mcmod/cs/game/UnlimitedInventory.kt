package ph.mcmod.cs.game

import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import ph.mcmod.kum.canCombine

open class UnlimitedInventory(size: Int) : SimpleInventory(size) {
	override fun getMaxCountPerStack(): Int = Int.MAX_VALUE
	
	override fun canInsert(stack: ItemStack): Boolean {
		for (i in 0 until size()) {
			getStack(i).let {
				if (it.canCombine(stack))
					return true
			}
		}
		return false
	}
	
	override fun addStack(stack: ItemStack): ItemStack {
		for (i in 0 until size()) {
			getStack(i).let {
				if (!it.isEmpty && it.canCombine(stack)) {
					it.count += stack.count
					stack.count = 0
				}
			}
		}
		if (stack.isEmpty)
			return ItemStack.EMPTY
		for (i in 0 until size()) {
			getStack(i).let {
				if (it.isEmpty)
					setStack(i, stack.copy())
			}
		}
		return stack
	}
}