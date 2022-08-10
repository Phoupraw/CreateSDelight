package ph.mcmod.registry

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.lang.reflect.Modifier

@Target(AnnotationTarget.CLASS)
annotation class RegistryContainer(val modId: String) {
    companion object {
        fun register(cls: Class<*>, modId: String) {
            ServerLifecycleEvents.SERVER_STARTING.register {
                for (field in cls.fields) {
                    if (!Modifier.isStatic(field.modifiers) || !Modifier.isPublic(field.modifiers) || !Modifier.isFinal(field.modifiers)) continue
                    val name = field.name
                    val path = name.lowercase()
                    val id = Identifier(modId, path)
                    val value = field.get(null)
                    val registry = when (value) {
                        is Block -> Registry.BLOCK
                        is BlockEntityType<*> -> Registry.BLOCK_ENTITY_TYPE
                        is Item -> Registry.ITEM
                        is EntityType<*> -> Registry.ENTITY_TYPE
                        else -> null
                    }
                    registry?.also { Registry.register(it as Registry<Any>, id, value ) }
                }
            }
        }
    }
}
