package ph.mcmod.cs.game

import com.mojang.blaze3d.systems.RenderSystem
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class ItemPortsScreen<T : SyncedGuiDescription>(description: T, inventory: PlayerInventory, title: Text) : CottonInventoryScreen<T>(description, inventory, title) {
    val grid0X: Int
        get() = x + 7
    val grid0Y: Int
        get() = y + 17
    abstract val texture: Identifier
    override fun drawBackground(matrices: MatrixStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(matrices, partialTicks, mouseX, mouseY)
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader)
        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        drawTexture(matrices, grid0X, grid0Y, 0f, 0f, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }
    
    companion object {
        const val TEXTURE_WIDTH = 18 * 9
        const val TEXTURE_HEIGHT = 18 * 4
    }
}