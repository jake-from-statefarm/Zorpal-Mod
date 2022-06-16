package com.scaun.zorpal.setup;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.client.PowergenScreen;
import com.scaun.zorpal.client.ZorpTransScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Zorpal.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.POWERGEN_CONTAINER.get(), PowergenScreen::new);
            MenuScreens.register(Registration.ZORP_TRANS_CONTAINER.get(), ZorpTransScreen::new);
            //ItemBlockRenderTypes.setRenderLayer(Registration.POWERGEN.get(), RenderType.translucent());
        });
    }
}
