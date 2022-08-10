package ph.mcmod.cs.game

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class ItemEntityInventory(val itemEntity: ItemEntity): Inventory {
    var thisStack: ItemStack
        get() = itemEntity.stack
        set(value) {
            itemEntity.stack = value
        }
    
    override fun clear() {
        thisStack = ItemStack.EMPTY
    }
    
    override fun size(): Int {
        return 1
    }
    
    override fun isEmpty(): Boolean {
        return thisStack.isEmpty
    }
    
    override fun getStack(slot: Int): ItemStack {
        return thisStack
    }
    
    override fun removeStack(slot: Int, amount: Int): ItemStack {
        return thisStack.split(amount)
    }
    
    override fun removeStack(slot: Int): ItemStack {
        return removeStack(slot, thisStack.count)
    }
    
    override fun setStack(slot: Int, stack: ItemStack) {
        thisStack = stack
    }
    
    override fun markDirty() {
    
    }
    
    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return true
    }
}