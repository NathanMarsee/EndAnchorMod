package org.mbf.endanchor.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.mbf.endanchor.EndAnchor;

public class ModBlockEntities {
    public static BlockEntityType<EndArchorBlockEntity> END_ANCHOR_BLOCK_ENTITY;

    public static void registerBlockEntityTypes() {
        END_ANCHOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(EndAnchor.MOD_ID, "end_anchor_block_entity"),
                FabricBlockEntityTypeBuilder.create(EndArchorBlockEntity::new, ModBlocks.END_ANCHOR).build());
    }
}
