package com.scaun.zorpal.datagen;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MyBlockTags extends BlockTagsProvider {

    public MyBlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, Zorpal.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.FIRST_BLOCK.get())
                .add(Registration.ZORPAL_ORE_STONE.get())
                .add(Registration.POWERGEN.get())
                .add(Registration.ZORPAL_BLOCK.get())
                .add(Registration.ZORP_TRANS.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(Registration.FIRST_BLOCK.get())
                .add(Registration.ZORPAL_ORE_STONE.get())
                .add(Registration.POWERGEN.get())
                .add(Registration.ZORPAL_BLOCK.get())
                .add(Registration.ZORP_TRANS.get());
        tag(Tags.Blocks.ORES)
                .add(Registration.FIRST_BLOCK.get())
                .add(Registration.ZORPAL_ORE_STONE.get());
        tag(Registration.ZORPAL_ORE)
                .add(Registration.ZORPAL_ORE_STONE.get());
    }

    @Override
    public String getName() {
        return "Zorpal Tags";
    }
}
