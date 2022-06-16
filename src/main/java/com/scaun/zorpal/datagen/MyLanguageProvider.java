package com.scaun.zorpal.datagen;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.setup.Registration;
import com.scaun.zorpal.setup.ModSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class MyLanguageProvider extends LanguageProvider {

    public MyLanguageProvider(DataGenerator gen, String locale) {
        super(gen, Zorpal.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ModSetup.TAB_NAME, "Eric's Zorpal Mod");

        add(Registration.FIRST_BLOCK.get(), "The First Block I made :)");
        add(Registration.ZORPAL_ORE_STONE.get(), "Zorpal Ore");
        add(Registration.ZORPAL_BLOCK.get(), "God");

        add(Registration.POWERGEN.get(), "Power Generator");

        add(Registration.RAW_ZORP.get(), "Raw Zorp Chunk");
        add(Registration.ZORP_INGOT.get(), "Zorpal Ingot");
    }
}
