package org.mbf.endanchor.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class EndAnchorBlock extends BlockWithEntity implements InventoryProvider {
    public static final BooleanProperty CHARGED = BooleanProperty.of("charged");
    private Random random = new Random();


    public EndAnchorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CHARGED, false));

    }




    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof EndArchorBlockEntity blockEntity) {
            if (CompassItem.hasLodestone(itemStack)) {
                blockEntity.setLodestonePos(NbtHelper.toBlockPos(itemStack.getNbt().getCompound(CompassItem.LODESTONE_POS_KEY)));
                blockEntity.setCompassed(true);
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof EndArchorBlockEntity) {
            EndArchorBlockEntity endArchorBlockEntity = (EndArchorBlockEntity) blockEntity;
            if(!world.isClient && !player.getAbilities().creativeMode) {
                ItemStack itemStack = new ItemStack(ModBlocks.END_ANCHOR);
                blockEntity.setStackNbt(itemStack);
                if (endArchorBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(endArchorBlockEntity.getCustomName());
                }
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
            super.onBreak(world, pos, state, player);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof EndArchorBlockEntity blockEntity && !world.isClient()) {
            if (player.getStackInHand(hand).getItem() == Items.END_CRYSTAL && !world.getBlockState(pos).get(CHARGED)) {
                if (!player.getAbilities().creativeMode)
                    player.getStackInHand(hand).decrement(1);
                EndAnchorBlock.setCharged(world, pos, true);

                return ActionResult.SUCCESS;
            }
            if (!EndAnchorBlock.isEndDimension(world) && world.getBlockState(pos).get(CHARGED)) {
                this.explode(world, pos);
                return ActionResult.PASS;
            }
            if (world.getBlockState(pos).get(CHARGED)) {
                BlockPos lodestonePos = blockEntity.getLodestonePos();
                Block block = world.getBlockState(lodestonePos).getBlock();
                if (block == Blocks.LODESTONE) {
                    Vec3d vec3d = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, (CollisionView) world, lodestonePos).get();
                    EndAnchorBlock.setCharged(world, pos, false);
                    player.teleport(vec3d.getX(), vec3d.getY(), vec3d.getZ());
                    player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    ServerWorld world1 = (ServerWorld) world;
                    world1.spawnParticles(ParticleTypes.PORTAL, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 256, 0.5D, 0.5D, 0.5D, 0.2D);
                }
                    else
                        player.sendMessage(Text.of("Lodestone attached to this End Anchor is destroyed!"), false);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        }
        return ActionResult.SUCCESS;
    }


    private static boolean isEndDimension(World world) {
        return world.getRegistryKey() == World.END;
    }

    private void explode(World world, final BlockPos explodedPos) {
        world.removeBlock(explodedPos, false);
        boolean bl = Direction.Type.HORIZONTAL.stream().map(explodedPos::offset).anyMatch(pos -> EndAnchorBlock.hasStillWater(pos, world));
        final boolean bl2 = bl || world.getFluidState(explodedPos.up()).isIn(FluidTags.WATER);
        ExplosionBehavior explosionBehavior = new ExplosionBehavior() {

            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                if (pos.equals(explodedPos) && bl2) {
                    return Optional.of(Float.valueOf(Blocks.WATER.getBlastResistance()));
                }
                return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
            }
        };
        Vec3d vec3d = explodedPos.toCenterPos();
        world.createExplosion(null, world.getDamageSources().badRespawnPoint(vec3d), explosionBehavior, vec3d, 5.0f, true, World.ExplosionSourceType.BLOCK);
    }

    public static void setCharged(World world, BlockPos pos, boolean charged) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof EndAnchorBlock) {
            world.setBlockState(pos, blockState.with(CHARGED, charged), 3);
            if(charged)
                world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }



    private static boolean hasStillWater(BlockPos pos, World world) {
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isIn(FluidTags.WATER)) {
            return false;
        }
        if (fluidState.isStill()) {
            return true;
        }
        float f = fluidState.getLevel();
        if (f < 2.0f) {
            return false;
        }
        FluidState fluidState2 = world.getFluidState(pos.down());
        return !fluidState2.isIn(FluidTags.WATER);
    }

    public static ItemStack getItemStack() {
        return new ItemStack(ModBlocks.END_ANCHOR);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndArchorBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return new EndAnchorInventory(state, world, pos);
    }

    static class EndAnchorInventory extends SimpleInventory implements SidedInventory {
        private boolean dirty;
        BlockPos pos;
        WorldAccess world;
        BlockState blockState;
        public EndAnchorInventory(BlockState state, WorldAccess world, BlockPos pos) {
            super(1);
            this.pos = pos;
            this.world = world;
            blockState = state;
        }





        @Override
        public void markDirty() {
            super.markDirty();
            if(this.getStack(0).getItem() == Items.END_CRYSTAL) {
                blockState = blockState.with(CHARGED, true);
                dirty = true;
                EndAnchorBlock.setCharged((World) world, pos, true);
            }
        }

        @Override
        public int[] getAvailableSlots(Direction side) {
            return new int[1];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return !this.dirty && stack.getItem() == Items.END_CRYSTAL && !blockState.get(CHARGED);
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }


        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }
    }
}
