package com.scaun.zorpal.datagen;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MyItemTags extends ItemTagsProvider {

    public MyItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, Zorpal.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Items.ORES)
                .add(Registration.FIRST_BLOCK_ITEM.get())
                .add(Registration.ZORPAL_ORE_STONE_ITEM.get());
        tag(Tags.Items.RAW_MATERIALS)
                .add(Registration.RAW_ZORP.get());
        tag(Tags.Items.OBSIDIAN)
                .add(Registration.ZORPAL_BLOCK_ITEM.get());
        tag(Tags.Items.INGOTS)
                .add(Registration.ZORP_INGOT.get());
        tag(Registration.ZORPAL_ORE_ITEM)
                .add(Registration.ZORPAL_ORE_STONE_ITEM.get())
                .add(Registration.ZORPAL_ORE_DEEPSLATE_ITEM.get());;
    }

    @Override
    public String getName() {
        return "Zorpal Tags";
    }
}
