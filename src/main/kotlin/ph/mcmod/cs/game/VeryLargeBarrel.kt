@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package ph.mcmod.cs.game

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import org.jetbrains.annotations.Range
import ph.mcmod.cs.LOGGER
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.MyRegistries.MyBlocks.VERY_LARGE_BARREL
import ph.mcmod.cs.MOD_ID
import ph.mcmod.cs.api.*
import ph.mcmod.kum.*
import java.util.*
import kotlin.Pair

object VeryLargeBarrel {
    @JvmField
    val CONCATENATION: IntProperty = IntProperty.of("concatenation", 0, 49)
    @JvmField
    val STORAGES: Table<MinecraftServer, UUID, TStorage> = HashBasedTable.create()
    @JvmField
    val REMOVAL = mutableSetOf<UUID>()
    @JvmField
    val SLOTS_PER = if (FabricLoader.getInstance().isModLoaded("create")) 20 else 27
    @JvmField
    val STORAGE_ID = Identifier(MOD_ID, "vault/storages")
    
    init {
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            server.dataCommandStorage[STORAGE_ID].getCompoundList("root").forCompound { _, compound, _, _ ->
                val uuid = compound.getUuid("uuid")
                if (uuid in REMOVAL)
                    return@forCompound
                STORAGES[server, uuid] = TStorage(compound.getInt("size") * SLOTS_PER f 1).apply { fromNbt(compound) }
            }
        }
        ServerLifecycleEvents.SERVER_STOPPING.register { server ->
            server.dataCommandStorage[STORAGE_ID] = NbtCompound().apply {
                put("root", NbtList().apply {
                    for ((uuid, storage) in STORAGES.row(server)) {
                        if (uuid in REMOVAL)
                            continue
                        add(NbtCompound().apply {
                            putUuid("uuid", uuid)
                            putInt("size", storage.capacity.toInt() / SLOTS_PER)
                            copyFrom(storage.toNbt())
//                            put("items", NbtList().append(storage))
//                            putInt("size", storage.slots.size)
                        })
                    }
                })
            }
            STORAGES -= server
        }
    }
    
    @JvmStatic
    var ItemStack.placing: BlockPos?
        get() =
            if (!orCreateNbt.containsCompound("placing")) null
            else NbtHelper.toBlockPos(getOrCreateSubNbt("placing"))
        set(value) {
            if (value == null) {
                removeSubNbt("placing")
                if (orCreateNbt.isEmpty)
                    nbt = null
            } else orCreateNbt.put("placing", NbtHelper.fromBlockPos(value))
        }
    
    @JvmStatic
    fun place(world: ServerWorld, pos1: BlockPos, pos2: BlockPos, axis0: Direction.Axis): Collection<Pair<BlockPos, BlockState>> {
        val obstacles = mutableListOf<Pair<BlockPos, BlockState>>()
        for (pos in BlockPos.iterate(pos1, pos2)) {
            val blockState = world.getBlockState(pos)
            if (!world.canReplace(pos, VERY_LARGE_BARREL.defaultState)) {
                obstacles += pos to blockState
            }
        }
        if (obstacles.isNotEmpty())
            return obstacles
        for (pos in BlockPos.iterate(pos1, pos2)) {
            if (!world.isAir(pos))
                world.breakBlock(pos, true)
        }
        val box: BlockBox = BlockBox.create(pos1, pos2)
        val dimensions = box.dimensions.toTriple().toList().map { it + 1 }
        val availableAxes = mutableListOf<Direction.Axis>()
        for (index in dimensions.indices) {
            val a = dimensions[(index + 1) % 3]
            val b = dimensions[(index + 2) % 3]
            if (a == 1 && b == 1 || a > 1 && b > 1) {
                availableAxes += Direction.Axis.values()[index]
            }
        }
        if (availableAxes.isEmpty())
            throw RuntimeException("availableAxes.isEmpty():$pos1,$pos2").also { LOGGER.warning(it.toString()) }
        val axis = if (axis0 in availableAxes) axis0 else availableAxes.first()
        val axis1 = Direction.Axis.values()[(axis.ordinal + 1) % 3]
        val axis2 = Direction.Axis.values()[(axis.ordinal + 2) % 3]
        val a = dimensions[axis1.ordinal]
        val b = dimensions[axis2.ordinal]
        val h = dimensions[axis.ordinal]
        val minPos = BlockPos(box.minX, box.minY, box.minZ)
        val maxPos = BlockPos(box.maxX, box.maxY, box.maxZ)
        val uuid = UUID.randomUUID()
        if (a == 1 && b == 1) {
            if (h == 1) {
                world.setBlockState(pos1, getBlockState(concatenation_one(axis)))
            } else {
                world.setBlockState(minPos, getBlockState(concatenation_front(axis[false])))
                world.setBlockState(maxPos, getBlockState(concatenation_front(axis[true])))
                if (h > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis, 1), maxPos.offset(axis, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_middle(axis)))
                }
            }
        } else {
            if (h == 1) {
                world.setBlockState(minPos, getBlockState(concatenation_pillar(axis, 0)))
                world.setBlockState(minPos.offset(axis1, a - 1), getBlockState(concatenation_pillar(axis, 1)))
                world.setBlockState(maxPos.offset(axis1, -a + 1), getBlockState(concatenation_pillar(axis, 2)))
                world.setBlockState(maxPos, getBlockState(concatenation_pillar(axis, 3)))
                if (a > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis1, 1), minPos.offset(axis1, a - 2)))
                        world.setBlockState(pos, getBlockState(concatenation_arris(axis, 2)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis1, 2 - a), maxPos.offset(axis1, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_arris(axis, 3)))
                }
                if (b > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis2, 1), minPos.offset(axis2, b - 2)))
                        world.setBlockState(pos, getBlockState(concatenation_arris(axis, 0)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis2, 2 - b), maxPos.offset(axis2, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_arris(axis, 1)))
                }
                if (a > 2 && b > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis1, 1).offset(axis2, 1), maxPos.offset(axis1, -1).offset(axis2, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_middle(axis)))
                }
            } else {
                world.setBlockState(minPos, getBlockState(concatenation_corner(axis, 0)))
                world.setBlockState(minPos.offset(axis1, a - 1), getBlockState(concatenation_corner(axis, 1)))
                world.setBlockState(minPos.offset(axis2, b - 1), getBlockState(concatenation_corner(axis, 2)))
                world.setBlockState(minPos.offset(axis1, a - 1).offset(axis2, b - 1), getBlockState(concatenation_corner(axis, 3)))
                world.setBlockState(maxPos, getBlockState(concatenation_corner(axis, 0)))
                world.setBlockState(maxPos.offset(axis1, 1 - a), getBlockState(concatenation_corner(axis, 1)))
                world.setBlockState(maxPos.offset(axis2, 1 - b), getBlockState(concatenation_corner(axis, 2)))
                world.setBlockState(maxPos.offset(axis1, 1 - a).offset(axis2, 1 - b), getBlockState(concatenation_corner(axis, 3)))
                if (a > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis1, 1), minPos.offset(axis1, a - 2)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[false], 2)))
                    for (pos in BlockPos.iterate(minPos.offset(axis1, 1).offset(axis2, b - 1), minPos.offset(axis1, a - 2).offset(axis2, b - 1)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[false], 3)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis1, 2 - a), maxPos.offset(axis1, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[true], 2)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis1, 2 - a).offset(axis2, 1 - b), maxPos.offset(axis1, -1).offset(axis2, 1 - b)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[true], 3)))
                }
                if (b > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis2, 1), minPos.offset(axis2, b - 2)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[false], 0)))
                    for (pos in BlockPos.iterate(minPos.offset(axis2, 1).offset(axis1, a - 1), minPos.offset(axis2, b - 2).offset(axis1, a - 1)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[false], 1)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis2, 2 - b), maxPos.offset(axis2, -1)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[true], 0)))
                    for (pos in BlockPos.iterate(maxPos.offset(axis2, 2 - b).offset(axis1, 1 - a), maxPos.offset(axis2, -1).offset(axis1, 1 - a)))
                        world.setBlockState(pos, getBlockState(concatenation_edge(axis[true], 1)))
                }
                if (a > 2 && b > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis1, 1).offset(axis2, 1), minPos.offset(axis1, a - 2).offset(axis2, b - 2)))
                        world.setBlockState(pos, getBlockState(9 + axis.ordinal))
                    for (pos in BlockPos.iterate(maxPos.offset(axis1, -1).offset(axis2, -1), maxPos.offset(axis1, 2 - a).offset(axis2, 2 - b)))
                        world.setBlockState(pos, getBlockState(9 + axis.ordinal))
                }
                if (h > 2) {
                    for (pos in BlockPos.iterate(minPos.offset(axis, 1), maxPos.offset(axis, -1)))
                        world.setBlockState(pos, getBlockState(9 + axis.ordinal))
                }
            }
        }
        for (pos in BlockPos.iterate(pos1, pos2)) {
            (world.getBlockEntity(pos) as? TBlockEntity)?.also {
                it.uuid = uuid
            }
        }
        STORAGES[world.server, uuid] = TStorage(a * b * h * SLOTS_PER f 1)
        return emptyList()
    }
    
    @JvmStatic
    fun getBlockState(concatenation: @Range(from = 0, to = 49) Int): BlockState = VERY_LARGE_BARREL.defaultState.with(CONCATENATION, concatenation)
    @JvmStatic
    fun concatenation_one(axis: Direction.Axis) = axis.ordinal
    @JvmStatic
    fun concatenation_front(direction: Direction) = 3 + direction.axis.ordinal * 2 + (1 - direction.direction.ordinal)
    @JvmStatic
    fun concatenation_middle(axis: Direction.Axis) = 9 + axis.ordinal
    @JvmStatic
    fun concatenation_pillar(axis: Direction.Axis, index: @Range(from = 0, to = 3) Int) = 12 + axis.ordinal * 4 + index
    @JvmStatic
    fun concatenation_corner(axis: Direction.Axis, index: @Range(from = 0, to = 3) Int) = 24 + axis.ordinal * 4 + index
    @JvmStatic
    fun concatenation_arris(axis: Direction.Axis, index: @Range(from = 0, to = 3) Int): Int {
        return 36 + when (axis) {
            Direction.Axis.X -> index
            Direction.Axis.Y -> when (index) {
                0 -> 3
                1 -> 2
                else -> index + 2
            }
            Direction.Axis.Z -> when (index) {
                0 -> 5
                1 -> 4
                else -> 3 - index
            }
        }
    }
    
    @JvmStatic
    fun concatenation_edge(direction: Direction, index: @Range(from = 0, to = 3) Int): Int {
        return 42 + when (direction) {
            Direction.WEST -> index
            Direction.EAST -> index + 4
            Direction.DOWN -> when (index) {
                0 -> 1
                1 -> 4
                2 -> 7
                3 -> 3
                else -> error("$direction $index")
            }
            Direction.UP -> when (index) {
                0 -> 0
                1 -> 5
                2 -> 2
                3 -> 6
                else -> error("$direction $index")
            }
            Direction.NORTH -> when (index) {
                0 -> 0
                1 -> 4
                2 -> 3
                3 -> 6
                else -> error("$direction $index")
            }
            Direction.SOUTH -> when (index) {
                0 -> 1
                1 -> 5
                2 -> 7
                3 -> 2
                else -> error("$direction $index")
            }
        }
    }
    
    class TBlock(settings: Settings) : BlockWithEntity(settings) {
        init {
            defaultState.with(CONCATENATION, 0)
        }
        
        override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
            super.appendProperties(builder)
            builder.add(CONCATENATION)
        }
        
        override fun getRenderType(state: BlockState): BlockRenderType {
            return BlockRenderType.MODEL
        }
        
        override fun createBlockEntity(pos: BlockPos, state: BlockState): TBlockEntity {
            return TBlockEntity(pos, state)
        }
        
        override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
            if (world is ServerWorld && !newState.isOf(this)) {
                (world.getBlockEntity(pos) as? TBlockEntity)?.apply {
                    uuid?.apply {
                        REMOVAL += this
                        storage?.also { ItemStorable.scatter(world, pos, it) }
                        STORAGES[world.server] -= this
                    }
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
        
        override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
            (world.getBlockEntity(pos) as? TBlockEntity)?.apply {
                uuid?.also {
                    if (it in REMOVAL) {
                        return Blocks.AIR.defaultState
                    }
                }
            }
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        }
    }
    
    class TItem(block: Block, settings: Settings) : DescriptedBlockItem(block, settings) {
        override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
            val stack = user.getStackInHand(hand)
            stack.placing?.apply {
                stack.placing = null
                if (stack.orCreateNbt.isEmpty)
                    stack.nbt = null
                user.sendMessage(TranslatableText("tooltip.c_storage.cancel"), true)
                return TypedActionResult.success(stack)
            }
            return super.use(world, user, hand)
        }
        
        override fun place(context0: ItemPlacementContext): ActionResult {
            val context = getPlacementContext(context0) ?: return ActionResult.FAIL
            val world = context.world
            if (world !is ServerWorld)
                return ActionResult.CONSUME
            val pos = context.blockPos
            val stack = context.stack
            return stack.placing?.run {
                val axis = context.side.axis
                val count = BlockBox.create(this, pos).run { blockCountX * blockCountY * blockCountZ }
                context.player?.apply {
                    if (inventory.count(this@TItem) < count && !isCreative) {
                        sendMessage(TranslatableText("tooltip.c_storage.scarcity", this@TItem.name).formatted(Formatting.RED), true)
                        return ActionResult.FAIL
                    }
                }
                if (place(world, pos, this, axis).isEmpty()) {
                    stack.placing = null
                    if (stack.orCreateNbt.isEmpty)
                        stack.nbt = null
                    context.player?.apply {
                        if (!isCreative)
                            inventory.remove({ it.isOf(this@TItem) }, count, inventory)
                    }
                    ActionResult.SUCCESS
                } else {
                    ActionResult.FAIL
                }
            } ?: run {
                stack.placing = pos
                context.player?.sendMessage(TranslatableText("tooltip.c_storage.set", LiteralText("[${pos.x},${pos.y},${pos.z}]").formatted(Formatting.AQUA)), false)
                ActionResult.SUCCESS
            }
        }
        
        override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
            super.appendTooltip(stack, world, tooltip, context)
            stack.placing?.also {
                tooltip += TranslatableText("tooltip.c_storage.vault", LiteralText("[${it.x},${it.y},${it.z}]").formatted(Formatting.AQUA))
            }
        }
        
        override fun getName(stack: ItemStack): Text {
            return super.getName(stack).run {
                stack.placing?.let {
                    LiteralText("").append(this).append(TranslatableText("item.c_storage.vault.set"))
                } ?: this
            }
        }
    }
    
    class TBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(MyRegistries.MyBlockEntityTypes.VERY_LARGE_BARREL, pos, state), ItemStorable<Storage<ItemVariant>> {
        var uuid: UUID? = null
        val storage by lazy { STORAGES[world?.server, uuid]?.also { it.blockEntities += this } }
        override val itemStorage: Storage<ItemVariant>
            get() = storage ?: Storage.empty()
        
        override fun writeNbt(root: NbtCompound) {
            super.writeNbt(root)
            uuid?.also { root.putUuid("storage", it) }
        }
        
        override fun readNbt(root: NbtCompound) {
            super.readNbt(root)
            uuid = root.getUuid("storage")
        }
    }
    
    class TStorage(val capacity: Fraction) : ExactViewStorage<ItemVariant, Fraction>(), NbtSerializable {
        var space = capacity
        val blockEntities = mutableListOf<BlockEntity>()
        override fun onFinalCommit() {
            super.onFinalCommit()
            for (blockEntity in blockEntities) {
                blockEntity.markDirty()
            }
        }
        
        override fun blankVariant(): ItemVariant {
            return ItemVariant.blank()
        }
        
        override fun toNbt(root: NbtCompound): NbtCompound {
            root.put("items", NbtList().also { it += map.map { (k, v) -> NbtCompound().from(k, v.amount) } })
            root.put("space", space.toNbt())
            return root
        }
        
        override fun fromNbt(root: NbtCompound) {
            map.clear()
            root.getCompoundList("items").forCompound { _, compound, _, _ ->
                val (variant, amount) = compound.toItem()
                map[variant] = Slot().also {
                    it.variant = variant
                    it.amount = amount
                }
            }
            space =
              if (!root.containsString("space")) capacity
              else root.getStringList("space").toFraction()
        }
    }
}