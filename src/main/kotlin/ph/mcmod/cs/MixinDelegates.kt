@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.cs

import com.jozufozu.flywheel.util.transform.TransformStack
import com.simibubi.create.AllTags
import com.simibubi.create.content.contraptions.base.KineticTileEntity
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainItemHandler
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity
import com.simibubi.create.content.contraptions.processing.BasinRenderer
import com.simibubi.create.content.contraptions.processing.BasinTileEntity
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.command.DataCommandStorage
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.CampfireCookingRecipe
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.FluidTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.*
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.invoke.arg.Args
import ph.mcmod.cs.game.FTemperature
import ph.mcmod.cs.game.RoastingGrill
import ph.mcmod.kum.*
import java.util.*
import kotlin.math.*

internal object MixinDelegates {
    @Deprecated("修不了BUG，不修了，直接放弃这个功能")
    @JvmStatic
    fun listenGet(dataCommandStorage: DataCommandStorage, id: Identifier, returnValue: NbtCompound) {
//        if (id == ContainerItemMaps.STORAGE_ID && !Throwable().stackTrace.asSequence().map(StackTraceElement::getClassName).contains("ContainerItemMaps")) {
//            ContainerItemMaps.writeStorages((dataCommandStorage as FieldingServer).server, returnValue)
//        }
    }
    @Deprecated("修不了BUG，不修了，直接放弃这个功能")
    @JvmStatic
    fun listenSet(dataCommandStorage: DataCommandStorage, id: Identifier, nbt: NbtCompound) {
//        if (id == ContainerItemMaps.STORAGE_ID && !Throwable().stackTrace.asSequence().map(StackTraceElement::getClassName).contains("ContainerItemMaps")) {
//            ContainerItemMaps.readStorages((dataCommandStorage as FieldingServer).server, nbt)
//        }
    }
    
    @JvmStatic
    fun useBowlOnWater(fluidBlock: FluidBlock, state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (fluidBlock.fluid.isIn(FluidTags.WATER)) {
            val stackInHand = player.getStackInHand(hand)
            if (stackInHand.isOf(Items.BOWL)) {
                if (player is ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(player, pos, stackInHand)
                }
                if (!player.isCreative) {
                    stackInHand.decrement(1)
                    player.inventory.offerOrDrop(MyRegistries.MyItems.WATER_BOWL.defaultStack)
                }
                return ActionResult.SUCCESS
            }
        }
        return ActionResult.PASS
    }
    
    fun getCampfireCookingRecipe(te: ItemDrainTileEntity, transaction: TransactionContext?, itemVariant: ItemVariant): CampfireCookingRecipe? {
        Transaction.openNested(Transaction.getCurrentUnsafe()).use { transaction1 ->
            if (getFluidStorageView(te).run {
                  amount >= 1000 && FluidVariantAttributes.getTemperature(resource) >= 1000
              }) {
                val world = te.world
                if (world is ServerWorld) {
                    val tempInv = SimpleInventory(1)
                    tempInv.setStack(0, itemVariant.toStack())
                    val optional = world.server.recipeManager.getFirstMatch(RecipeType.CAMPFIRE_COOKING, tempInv, world)
                    if (optional.isPresent) {
                        return optional.get()
                    }
                }
            }
        }
        return null
    }
    
    fun getFluidStorageView(te: ItemDrainTileEntity): StorageView<FluidVariant> {
        Transaction.openNested(Transaction.getCurrentUnsafe()).use { transaction ->
            te.getFluidStorage(Direction.DOWN)?.let { storage ->
                for (storageView in storage.iterable(transaction)) {
                    return storageView
                }
            }
        }
        return BlankVariantView(FluidVariant.blank(), 0)
    }
    
    fun shouldSteam(te: ItemDrainTileEntity): Boolean {
        return getFluidStorageView(te).run {
            resource.fluid.isIn(FluidTags.WATER) && te.world?.getBlockState(te.pos.down())?.isOf(Blocks.LAVA) == true
        }
    }
    
    fun getSteamRecipe(te: ItemDrainTileEntity, itemVariant: ItemVariant): Recipe<Inventory>? {
        Transaction.openNested(Transaction.getCurrentUnsafe()).use { transaction ->
            if (shouldSteam(te)) {
                val world = te.world
                if (world is ServerWorld) {
                    val tempInv = SimpleInventory(1)
                    tempInv.setStack(0, itemVariant.toStack())
                    val optional = world.server.recipeManager.getFirstMatch(RecipeType.CAMPFIRE_COOKING, tempInv, world)
                    if (optional.isPresent) {
                        return optional.get()
                    }
                }
            }
        }
        return null
    }
    @JvmStatic
    fun setCountForCampfireCookable(itemHandler: ItemDrainItemHandler, te: ItemDrainTileEntity, side: Direction, maxAmount: Int, maxCount: Int, resource: ItemVariant, amount: Long, transaction: TransactionContext): Int {
        if (getCampfireCookingRecipe(te, transaction, resource) != null) {
            return 1
        }
        return min(maxAmount, maxCount)
    }
    @JvmStatic
    fun particle(te: ItemDrainTileEntity, heldItem: TransportedItemStack?) {
        if (heldItem != null && getCampfireCookingRecipe(te, null, ItemVariant.of(te.heldItemStack)) != null) {
            (te.world as? ServerWorld)?.let { world ->
                val pos = TransportedItemStackHandlerBehaviour(te) { fl, function -> }.getWorldPositionOf(heldItem)
                world.spreadParticles(ParticleTypes.POOF, false, pos.add(0.0, 0.5, 0.0), Vec3d(0.0, 1.0 / 16, 0.0), 1.0, 0)
                world.spreadParticles(ParticleTypes.SMOKE, false, pos.add(0.0, 0.5, 0.0), Vec3d(0.0, 1.0 / 16, 0.0), 1.0, 0)
                if (world.random.nextInt(5) == 0)
                    world.playSound(null, pos.x, pos.y + 0.5, pos.z, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 4f, 1f)
            }
        } else if (shouldSteam(te)) {
            (te.world as? ServerWorld)?.let { world ->
                val pos = TransportedItemStackHandlerBehaviour(te) { fl, function -> }.getWorldPositionOf(heldItem)
                if (world.random.nextInt(5) == 0)
                    world.spreadParticles(ParticleTypes.POOF, false, pos.add(0.0, world.random.nextDouble(), 0.0), Vec3d(0.0, 1.0 / 16, 0.0), 1.0, 0)
//                    world.spreadParticles(ParticleTypes.SMOKE, false, pos.add(0.0, 0.5, 0.0), Vec3d(0.0, 1.0 / 16, 0.0), 1.0, 0)
//                    if (world.random.nextInt(5) == 0)
//                        world.playSound(null, pos.x, pos.y + 0.5, pos.z, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 4f, 1f)
            }
        }
    }
    @JvmStatic
    fun cook(te: ItemDrainTileEntity, world: World, stack: ItemStack, heldItem: TransportedItemStack): Boolean {
//        println(stack)
        if (EmptyingByBasin.canItemBeEmptied(world, stack)) {
            return true
        }
        getCampfireCookingRecipe(te, null, ItemVariant.of(stack))?.also { recipe ->
            heldItem.stack = recipe.output
            return false
        }
        getSteamRecipe(te, ItemVariant.of(stack))?.also { recipe ->
            stack.orCreateNbt.putInt("restTime", 100)
            return true
        }
        
        return false
    }
    
    @JvmStatic
    fun check(te: ItemDrainTileEntity, world: World, stack: ItemStack): Boolean {
        return EmptyingByBasin.canItemBeEmptied(world, stack) || getCampfireCookingRecipe(te, null, ItemVariant.of(stack)) != null
    }
    @JvmStatic
    fun steam(te: ItemDrainTileEntity, heldItem: TransportedItemStack, cir: CallbackInfoReturnable<Boolean>): Int? {
        getSteamRecipe(te, ItemVariant.of(heldItem.stack))?.also { recipe ->
            val restTime = heldItem.stack.orCreateNbt.getInt("restTime")
            if (restTime > 0) {
                if (restTime == 20) {
                    heldItem.stack = recipe.output
                }
                heldItem.stack.orCreateNbt.putInt("restTime", restTime - 1)
                cir.returnValue = true
                heldItem.beltPosition = 0.5f
                heldItem.prevBeltPosition = 0.5f
//                te.sendData()
                return 20
            } else {
            
            }
        }
        return null
    }
    
    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun renderRoastingItem(renderer: BracketedKineticTileRenderer, te: KineticTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
//        println(1)
        if (te is RoastingGrill) {
//            if (partialTicks <= 0.1) {
//                println(te.roastingStorage.resource)
//            }
//            println(2)
            te.roastingStorage.takeUnless { it.isEmpty }?.also { roastingStorage ->
//                println(3)
                val random = Random(0)
//                ms.push()
                val itemRenderer = MinecraftClient.getInstance().itemRenderer
                val modelStack = roastingStorage.resource.toStack().apply {
                    orCreateNbt.putInt("CustomModelData", 1)
                }
                ms.push()
                ms.translate(0.5, 0.5, 0.5)
                val axis = KineticTileEntityRenderer.getRotationAxisOf(te)
                val offset = BracketedKineticTileRenderer.getShaftAngleOffset(axis, te.pos)
                val time = AnimationTickHolder.getRenderTime(te.world)
                val angle = (time * te.speed * 3f / 10 + offset) % 360 / 180 * Math.PI.toFloat()
                ms.multiply(Quaternion(axis[true].unitVector, angle, false))
                ms.multiply(Quaternion(Vec3f(0f, 1f, 0f), 90f, true))
//                ms.translate(-0.5, -0.5, -0.5)
                ms.translate(0.0, 0.2, 0.0)
                val scale = 3f
                ms.scale(scale, scale, scale)
                itemRenderer.renderItem(modelStack, ModelTransformation.Mode.FIXED, light, overlay, ms, buffer, 0)
                ms.pop()
            }
        }
    }
    
    @JvmStatic
    fun <T : BlockEntity> testSafeRender(renderer: SafeTileEntityRenderer<T>, te: T, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
//        if (te is BracketedKineticTileRenderer) {
//            println(renderer.isInvalid(te))
//        }
        if (te is RoastingGrill) {
//            println(renderer.isInvalid(te))
            if (!te.roastingStorage.isEmpty) {
//                println(renderer.isInvalid(te))
            }
        }
    }
    
    @JvmStatic
    fun writeRoastingNbt(te: BracketedKineticTileEntity, compound: NbtCompound, clientPacket: Boolean) {
        if (te is RoastingGrill) {
            compound.put("roastingItem", te.roastingStorage.resource.toNbt())
        }
    }
    
    @JvmStatic
    fun readRoastingNbt(te: BracketedKineticTileEntity, compound: NbtCompound, clientPacket: Boolean) {
        if (te is RoastingGrill) {
            val itemVariant = ItemVariant.fromNbt(compound.getCompound("roastingItem"))
//            if (!itemVariant.isBlank) {
//                println(te.pos)
//            }
            te.roastingStorage.variant = itemVariant
            te.roastingStorage.amount = (!itemVariant.isBlank).toInt().toLong()
        }
    }
    
    @JvmStatic
    fun tickRoasting(te: BracketedKineticTileEntity) {
        if (te is RoastingGrill) {
            te.roastingStorage.resource.nbt?.apply {
                putInt("roastingCountdown", getInt("roastingCountdown") - 1)
            }
        }
    }
    
    private val boilingPoses = mutableSetOf<BlockPos>()
    private fun mapTemperature(te: BasinTileEntity): Double {
        return ((te as FTemperature).temperature - 25) / 75
    }
    @JvmStatic
    fun tickBoil(te: BasinTileEntity) {
        val t1 = mapTemperature(te)
        val t2 = t1.pow(3)
        (te as FTemperature).apply {
            animationTicks += t2
        }
        val world = te.world as? ServerWorld ?: return
        val blockPos = te.pos
        if (world.getBlockState(blockPos.down()).isIn(AllTags.AllBlockTags.FAN_HEATERS.tag) && run {
              Transaction.openOuter().use { transaction ->
                  for (view in (te.getFluidStorage(null) ?: return@use).iterator(transaction)) {
                      if (view.resource.fluid.isIn(FluidTags.WATER)) {
                          return@run true
                      }
                  }
              }
              false
          }) {
            
            (te as FTemperature).apply {
                temperature = min(100.0, temperature + 0.5)
            }
//            boilingPoses += blockPos
            val pressure = run {
                val upPos = blockPos.up()
                VoxelShapes.adjacentSidesCoverSquare(te.cachedState.getOutlineShape(world, blockPos), world.getBlockState(upPos).getOutlineShape(world, upPos), Direction.UP)
            }
            if (pressure) {
            
            }
            val pos = blockPos.toCenter()
            val random = world.random
            if (random.nextDouble() < t2) {
                if (random.nextInt(2) == 0) {
                    world.spreadParticles(ParticleTypes.SPLASH, false, pos, 0.1, 0.0, 1)
                    world.spreadParticles(ParticleTypes.BUBBLE, false, pos, 0.1, 0.0, 1)
                    if (random.nextInt(4) == 0) {
                        world.spreadParticles(ParticleTypes.POOF, false, pos, 0.1, 0.0, 1)
                    }
                }
            }
        } else {
            (te as FTemperature).apply {
                temperature = max(25.0, temperature - 0.5)
            }
//            boilingPoses-=blockPos
        }
        te.notifyUpdate()
    }
    
    @JvmStatic
    fun changeLevel(renderer: BasinRenderer, value: Float, min: Float, max: Float, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int): Float {
//        println(partialTicks)
        if (te.pos.x == 23) {
//            println(partialTicks)
        }
        val origin = MathHelper.clamp(value, min, max)
        return origin
    }
    
    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun changeHeight(renderer: BasinRenderer, args: Args, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int, fluidLevel: Float, level: Float, pos: BlockPos, random: Random, inv: Storage<ItemVariant>, stacksSize: Int, stacks: List<ItemStack>, anglePartition: Float, stack: ItemStack) {
        val t1 = mapTemperature(te)
        val world = te.world ?: return
        val hashCode = stack.item.hashCode()
        val hashOffset = hashCode.toDouble() / Int.MAX_VALUE
        val amplitude = min(min(fluidLevel, 0.15f), 1 - fluidLevel) * t1.pow(3)
        val baseVector = args.get<Vec3d>(0)
        val degree = args.get<Double>(1)
        val cycle0 = 229
        val ticks = (te as FTemperature).animationTicks + partialTicks * t1.pow(3)
        val partial0 = (ticks % cycle0 / cycle0 + hashOffset) * PI * 2
        val sin0 = sin(sin(partial0) * PI * 2)
        val floating = (sin0 * amplitude)
        val value = baseVector.y
        val min = 0.125
        val max = 0.6
        val offset =
          if (value - amplitude < min)
              min - (value - amplitude)
          else if (value + amplitude > max)
              max - (value + amplitude)
          else 0.0
        args[0] = Vec3d(baseVector.x/* (1 + sin0 * 0.5) */, value + floating + offset, baseVector.z)
        val cycle1 = 1397
        val partial1 = ((world.time + partialTicks) % cycle1 / cycle1 + hashOffset) * PI * 2
        val sin1 = sin(partial1)
//        args[1] = degree + sin1 * 360 * hashOffset.sign
        
    }
    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun rotate(renderer: BasinRenderer, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int, fluidLevel: Float, level: Float, pos: BlockPos, random: Random, inv: Storage<ItemVariant>, stackCount: Int, stacks: MutableList<ItemStack>, anglePartition: Float, stack: ItemStack) {
        val world = te.world ?: return
        val hashCode = stack.item.hashCode()
        val hashOffset = hashCode.toDouble() / Int.MAX_VALUE
        val t1 = mapTemperature(te)
        val ticks = (te as FTemperature).animationTicks + partialTicks * t1.pow(3)
        fun cycle(period: Int) = sin((ticks % period / period + hashOffset) * PI * 2) * 360
//        infix fun Float.cycle(cycle:Int) = this modAndDiv cycle
        val amplitude = min(min(fluidLevel, 0.15f), 1 - fluidLevel)

//        val cycle1 = 697
//        val partial1 = ((world.time + partialTicks) % cycle1 / cycle1 + hashOffset) * PI * 2
//        val sin1 = sin(partial1)
        val translation = 4.0 / 30
//        ms.translate(0.0,0.0,0.0)
//        val degree = sin(partial1) * 360
//        val radian = degree / 180 * PI
//        ms.translate(0.0,2.0,0.0)
        ms.translate(0.0, translation, 0.0)
        TransformStack.cast(ms)
          .rotateX(cycle(397))
          .rotateY(cycle(597))
          .rotateZ(cycle(797))
        ms.translate(0.0, -translation, 0.0)

//        ms.translate(-translation,-translation,-translation)
    }
    
    @JvmStatic
    fun renderFluids(renderer: BasinRenderer, te: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int) {
//        BasinRenderer
    }
    
    @JvmStatic
    fun calcFluidLevel(renderer: BasinRenderer, basin: BasinTileEntity, partialTicks: Float, ms: MatrixStack, buffer: VertexConsumerProvider, light: Int, overlay: Int): Float {
        val inputFluids = basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
        val outputFluids = basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
        val tanks = arrayOf(inputFluids, outputFluids)
        val totalUnits: Float = basin.getTotalFluidUnits(partialTicks)
        if (totalUnits < 1)
            return 0f
        
        var fluidLevel = MathHelper.clamp(totalUnits / (FluidConstants.BUCKET * 2), 0f, 1f)
        
        fluidLevel = 1 - (1 - fluidLevel) * (1 - fluidLevel)
        
        val xMin = 2 / 16f
        val xMax = 2 / 16f
        val yMin = 2 / 16f
        val yMax = yMin + 12 / 16f * fluidLevel
        val zMin = 2 / 16f
        val zMax = 14 / 16f
        return yMax
    }
//    private fun lerpTemperature(te:BasinTileEntity,):Double{
//        return MathHelper.clamp()
//    }
}