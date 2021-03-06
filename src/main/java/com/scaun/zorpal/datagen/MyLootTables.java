package com.scaun.zorpal.datagen;

import com.scaun.zorpal.setup.Registration;
import net.minecraft.data.DataGenerator;

public class MyLootTables extends BaseLootTableProvider {

    public MyLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(Registration.POWERGEN.get(), createStandardTable("powergen", Registration.POWERGEN.get(), Registration.POWERGEN_BE.get()));
        lootTables.put(Registration.ZORPAL_ORE_STONE.get(), createSilkTouchTable("zorpal_ore", Registration.ZORPAL_ORE_STONE.get(), Registration.RAW_ZORP.get(), 0, 1));
        lootTables.put(Registration.ZORPAL_ORE_DEEPSLATE.get(), createSilkTouchTable("zorpal_ore_deepslate", Registration.ZORPAL_ORE_DEEPSLATE.get(), Registration.RAW_ZORP.get(), 1, 1));
        lootTables.put(Registration.ZORPAL_ORE_DENSE.get(), createSilkTouchTable("zorpal_ore_dense", Registration.ZORPAL_ORE_DENSE.get(), Registration.RAW_ZORP.get(), 1, 3));
        lootTables.put(Registration.ZORPAL_BLOCK.get(), createSimpleTable("zorpal_block", Registration.ZORPAL_BLOCK.get()));

        lootTables.put(Registration.ZORP_TRANS.get(), createStandardTable("zorp_trans", Registration.ZORP_TRANS.get(), Registration.ZORP_TRANS_BE.get()));
    }
}
