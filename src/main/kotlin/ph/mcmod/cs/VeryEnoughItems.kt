package ph.mcmod.cs

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.server.MinecraftServer

object VeryEnoughItems {
    interface AccessLootManager {
    
    }
    fun searchItem(server:MinecraftServer,item: Item) {
        val block:Block?
        if (item is BlockItem) {
            block = item.block
        }else {
            block = null
        }
        val lootTables:MutableCollection<LootTable> = mutableListOf()
        
    }
}