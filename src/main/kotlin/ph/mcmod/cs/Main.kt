@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.cs

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.BlockHitResult
import ph.mcmod.kum.Asynchronization
import ph.mcmod.kum.Fraction
import ph.mcmod.kum.arrp.ArrpHelper
import ph.mcmod.kum.f
import ph.mcmod.kum.loadClass
import java.util.logging.Logger

@JvmField
val LOGGER: Logger = Logger.getLogger(MOD_ID)
@JvmField
val ARRP_HELPER = ArrpHelper(MOD_ID)

val Item.stackSpace: Fraction
    get() = 1 f maxCount
val ItemVariant.stackSpace: Fraction
    get() = item.stackSpace

object Main {
    @JvmStatic
    fun init() {
//        SpaceGroupedItemStorage().setCapacity(1)
//		MinecraftClient.getInstance().player?.isSpectator
        
        MyRegistries.loadClass()
        Asynchronization.loadClass()
        
        // 给予玩家此模组的所有配方
        fun giveRecipes(server: MinecraftServer, players: Collection<ServerPlayerEntity>) {
            val recipesIds = server.recipeManager.keys().toList().filter { it.namespace == MOD_ID }.toTypedArray()
            players.forEach { it.unlockRecipes(recipesIds) }
        }
        ServerPlayConnectionEvents.JOIN.register { playNetworkHandler, packetSender, server ->
            giveRecipes(server, listOf(playNetworkHandler.player))
        }
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { server, resourceManager, boolean ->
            giveRecipes(server, server.playerManager.playerList)
        }
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            CommandRegistrationCallback.EVENT.register { dispatcher, dedicated ->
                dispatcher.register(CommandManager.literal("getNbt")
                  .then(CommandManager.literal("hand")
                    .executes { it.source.server.commandManager.execute(it.source, "data get entity @s SelectedItem") })
                  .then(CommandManager.literal("block")
                    .executes {
                        it.source.server.commandManager.execute(it.source, "data get block ${
                            (MinecraftClient.getInstance().crosshairTarget as? BlockHitResult)?.blockPos?.run { "$x $y $z" } ?: "~ ~-0.05 ~"
                        }")
                    }))
            }
        }
    }
    
}


