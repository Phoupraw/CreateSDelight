package ph.mcmod.cs.game

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.foundation.ponder.element.InputWindowElement
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour
import com.simibubi.create.foundation.utility.Pointing
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import ph.mcmod.cs.MyRegistries

object BarbecueScenes {
    fun drain(scene: SceneBuilder, util: SceneBuildingUtil) {
        val inputPos = util.grid.at(4, 1, 2)
        val drainPos = util.grid.at(2, 1, 2)
        
        fun showIngredient() {
            scene.overlay.showControls(InputWindowElement(util.vector.blockSurface(inputPos, Direction.UP), Pointing.DOWN).withItem(MyRegistries.MyItems.RAW_CHICKEN_STICK.defaultStack), 15)
        }
        fun showResult() {
            scene.overlay.showControls(InputWindowElement(util.vector.blockSurface(drainPos, Direction.UP), Pointing.DOWN).withItem(MyRegistries.MyItems.CHICKEN_STICK.defaultStack), 15)
        }
        scene.title("item_drain_barbecue", "使用分液池烧烤")
    
        scene.world.modifyTileEntity(drainPos, ItemDrainTileEntity::class.java) { te ->
            te.world?.recipeManager?.apply {
                val recipes = values().associateByTo(hashMapOf()) { it.id }
                val id = Identifier("1")
                recipes[id] = BarbecueRecipe(id, Ingredient.ofItems(MyRegistries.MyItems.RAW_CHICKEN_STICK), ItemVariant.of(MyRegistries.MyItems.CHICKEN_STICK), 100.0)
                setRecipes(recipes.values)
            }
            te.getBehaviour(SmartFluidTankBehaviour.TYPE).allowInsertion()
        }
        
        scene.world.showSection(util.select.everywhere(), Direction.UP)
        scene.idle(20)
        
        //1
        scene.overlay.showControls(InputWindowElement(util.vector.blockSurface(drainPos, Direction.UP), Pointing.DOWN).withItem(Items.LAVA_BUCKET.defaultStack), 15)
        scene.idle(15)
        showIngredient()
        scene.idle(10)
        insert(scene.world,inputPos, ItemDrainTileEntity::class.java,Direction.EAST, ItemVariant.of(MyRegistries.MyItems.RAW_CHICKEN_STICK), 1)
        scene.idle(15)
        scene.overlay.showText(100)
          .text("装有高温液体的分液池可以烧烤")
          .attachKeyFrame()
          .placeNearTarget()
          .pointAt(util.vector.blockSurface(drainPos, Direction.UP))
        scene.idle(85)
        showResult()
        scene.idle(30)
        
        //2
        scene.overlay.showControls(InputWindowElement(util.vector.blockSurface(drainPos, Direction.UP), Pointing.DOWN).withItem(Items.LAVA_BUCKET.defaultStack), 15)
        scene.world.modifyTileEntity(drainPos, ItemDrainTileEntity::class.java) { te ->
            TransferUtil.insert(te.getFluidStorage(null), FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET / 2)
        }
        scene.idle(15)
        scene.overlay.showText(50)
          .text("液体越多，烧烤越快，1.5桶液体的速度是1桶的5倍")
          .attachKeyFrame()
          .placeNearTarget()
          .pointAt(util.vector.blockSurface(drainPos, Direction.UP))
        scene.idle(20)
        showIngredient()
        scene.idle(15)
        insert(scene.world,inputPos, ItemDrainTileEntity::class.java,Direction.EAST, ItemVariant.of(MyRegistries.MyItems.RAW_CHICKEN_STICK), 1)
        scene.idle(25)
        showResult()
        scene.idle(30)
        
        //3
        showIngredient()
        scene.idle(10)
        insert(scene.world,inputPos, ItemDrainTileEntity::class.java,Direction.EAST, ItemVariant.of(MyRegistries.MyItems.RAW_CHICKEN_STICK), 64)
        scene.idle(5)
        scene.overlay.showText(70)
          .text("物品越多，烧烤越慢，1个物品的速度是64个物品的4倍")
          .attachKeyFrame()
          .placeNearTarget()
          .pointAt(util.vector.blockSurface(drainPos, Direction.UP))
        scene.idle(80)
        showResult()
        scene.idle(15)
        
        scene.markAsFinished()
    }
    
    fun <T> insert(world: SceneBuilder.WorldInstructions, blockPos: BlockPos, blockEntityClass: Class<T>, face: Direction?, itemVariant: ItemVariant, amount: Long) where T : BlockEntity, T : ItemTransferable {
        world.modifyTileEntity(blockPos, blockEntityClass) { te ->
            TransferUtil.insert(te.getItemStorage(face), itemVariant, amount)
        }
    }
}
