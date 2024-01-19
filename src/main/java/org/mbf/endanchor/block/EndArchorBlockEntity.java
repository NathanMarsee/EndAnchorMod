package org.mbf.endanchor.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EndArchorBlockEntity extends BlockEntity {
    private BlockPos lodestonePos;
    private SimpleInventory inventory = new SimpleInventory(1);
    private static final String LODESTONE_POS_KEY = "LodestonePos";
    private  static final String IS_COMPASSED_KEY = "IsCompassed";
    private boolean isCompassed = false;

    public EndArchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.END_ANCHOR_BLOCK_ENTITY, pos, state);
    }

    public BlockPos getLodestonePos() {
        return lodestonePos;
    }

    public void setLodestonePos(BlockPos lodestonePos) {
        this.lodestonePos = lodestonePos;

        this.markDirty();
    }

    public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    @Nullable
    public Text getCustomName() {
        return null;
    }



    public boolean isCompassed() {
        return isCompassed;
    }

    public SimpleInventory getInventory() {
        return inventory;
    }





    public void setCompassed(boolean compassed) {
        isCompassed = compassed;
        this.markDirty();
    }



    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        lodestonePos = NbtHelper.toBlockPos(nbt.getCompound(LODESTONE_POS_KEY));
        isCompassed = nbt.getBoolean(IS_COMPASSED_KEY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean(IS_COMPASSED_KEY, isCompassed);
        if(lodestonePos != null)
            nbt.put(LODESTONE_POS_KEY, NbtHelper.fromBlockPos(lodestonePos));
        super.writeNbt(nbt);
    }
}
