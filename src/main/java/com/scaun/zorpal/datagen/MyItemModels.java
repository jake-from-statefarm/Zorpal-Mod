package com.scaun.zorpal.datagen;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MyItemModels extends ItemModelProvider {
    public MyItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Zorpal.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.FIRST_BLOCK_ITEM.get().getRegistryName().getPath(), modLoc("block/erics_first_block"));
        withExistingParent(Registration.ZORPAL_ORE_STONE_ITEM.get().getRegistryName().getPath(), modLoc("block/zorpal_ore"));
        withExistingParent(Registration.ZORPAL_BLOCK_ITEM.get().getRegistryName().getPath(), modLoc("block/zorpal_block"));
        withExistingParent(Registration.POWERGEN_ITEM.get().getRegistryName().getPath(), modLoc("block/powergen_off"));
        withExistingParent(Registration.ZORP_TRANS_ITEM.get().getRegistryName().getPath(), modLoc("block/zorp_trans_off"));

        singleTexture(Registration.RAW_ZORP.get().getRegistryName().getPath(),
                mcLoc("item/generated"),
                "layer0", modLoc("item/raw_zorp"));
        singleTexture(Registration.ZORP_INGOT.get().getRegistryName().getPath(),
                mcLoc("item/generated"),
                "layer0", modLoc("item/zorp_ingot"));
    }
}
