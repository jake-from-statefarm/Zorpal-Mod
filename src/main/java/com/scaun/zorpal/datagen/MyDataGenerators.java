package com.scaun.zorpal.datagen;

import com.scaun.zorpal.Zorpal;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Zorpal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MyDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new MyRecipes(generator));
            generator.addProvider(new MyLootTables(generator));
            MyBlockTags blockTags = new MyBlockTags(generator, event.getExistingFileHelper());
            generator.addProvider(blockTags);
            generator.addProvider(new MyItemTags(generator, blockTags, event.getExistingFileHelper()));
        }
        if (event.includeClient()) {
            generator.addProvider(new MyBlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new MyItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(new MyLanguageProvider(generator, "en_us"));
        }
    }
}
