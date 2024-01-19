package org.mbf.endanchor.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.mbf.endanchor.EndAnchor;



public class ModBlocks {
    public static final Block END_ANCHOR = registerBlock("end_anchor", new EndAnchorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(9.0F, 1200.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS)));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(EndAnchor.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block) {
        BlockItem endAnchorItem = new BlockItem(block, new FabricItemSettings());
        return Registry.register(Registries.ITEM, new Identifier(EndAnchor.MOD_ID, name), endAnchorItem);

    }
    public static void registerModBlocks() {
        EndAnchor.LOGGER.info("Registering blocks");
    }

}
