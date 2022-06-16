package com.scaun.zorpal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.blocks.PowergenContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

public class PowergenScreen extends AbstractContainerScreen<PowergenContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Zorpal.MODID, "textures/gui/powergen_gui2.png");

    public PowergenScreen(PowergenContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        // bar is wxh px at xpos,ypos
        int xpos = 43;
        int ypos = 8;
        int w = 10;
        int h = 52;
        int o = 1; // amount to offset the "inner" energy bar
        int trueY = (int)(ypos + (h - ((float)h * energyPercent())));

        DecimalFormat oneDecimal = new DecimalFormat("#.0");

        String percentString = oneDecimal.format((energyPercent() * 100)) + "%";
        String energyString = menu.getEnergy() + "RF";
        String completeString = percentString + " - " + energyString;

        // Draws the String containing RF and percent information
        drawString(matrixStack, Minecraft.getInstance().font, completeString, 60, 10, 0xffffff);

        // Draw the "energy bar"
        //                x1        y2     x2            y2         color
        fill(matrixStack, xpos,     trueY, xpos + w,     ypos + h,  0xffff0000);
        fill(matrixStack, xpos + o, trueY, xpos + w - o, ypos + h,  0xffff4040);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    private float energyPercent() {
        return (float)menu.getEnergy() / (float)menu.getMaxEnergy();
    }
}