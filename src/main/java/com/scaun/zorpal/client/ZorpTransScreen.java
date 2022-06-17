package com.scaun.zorpal.client;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.blocks.ZorpContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ZorpTransScreen extends AbstractContainerScreen<ZorpContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans_gui.png");
    private final ResourceLocation ARR = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans_arrow.png");

    public ZorpTransScreen(ZorpContainer container, Inventory inv, Component name) {
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

        // progress is at 8x7 at 79, 30
        int pxpos = 79;
        int pypos = 30;
        int pw = 8;
        int ph = 8;
        int ptrueY = (int)(pypos + (ph - (float)ph * progressPercent()));

        DecimalFormat oneDecimal = new DecimalFormat("#.0");

        String percentString = oneDecimal.format((energyPercent() * 100)) + "%";
        String energyString = menu.getEnergy() + "RF";
        // String completeString = percentString + " - " + energyString;

        // Draws the String containing RF and percent information
        drawString(matrixStack, Minecraft.getInstance().font, percentString, 30, trueY, 0xffffff);

        // Draw the "energy bar"
        //                x1        y2     x2            y2         color
        fill(matrixStack, xpos,     trueY, xpos + w,     ypos + h,  0xffff0000);
        fill(matrixStack, xpos + o, trueY, xpos + w - o, ypos + h,  0xffff4040);
        // Draw the "progress bar"
        fill(matrixStack, pxpos,    pypos, pxpos + pw,   ptrueY,    0xffffffff);
        RenderSystem.setShaderTexture(0, ARR);
        int relX = 0; // (this.width - this.imageWidth) / 2;
        int relY = 0; // (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

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

    private float progressPercent() {
        System.out.println("CLIENT: " + menu.getCounter());
        return (float)menu.getCounter() / (float)menu.getCounterMax();
    }
    
}
