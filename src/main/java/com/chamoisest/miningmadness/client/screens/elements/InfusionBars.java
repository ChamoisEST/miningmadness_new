package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.InfusionMenu;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import com.chamoisest.miningmadness.setup.Registration;
import com.chamoisest.miningmadness.util.MouseUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.geom.Point2D;
import java.util.*;

public class InfusionBars extends AbstractWidget {

    private static final int BAR_WIDTH = 23;
    private static final int BAR_HEIGHT = 3;

    private final ResourceLocation INFUSION_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/elements/gui_infusion_bar.png");

    private final InfusionMenu menu;
    private final Font font;

    private final Map<Infusion, Point2D.Double> barLocationMap = new HashMap<>();
    private Map<Class<? extends Infusion>, Integer> outputInfusions = new HashMap<>();
    private IInfusionStorage infusionStorage;
    private final boolean isJEI;

    //Used for JEI infusionBar display
    public InfusionBars(int x, int y, Font font, ItemStack stack, Map<ResourceLocation, Integer> outputInfusions) {
        super(x, y, BAR_WIDTH, 0, Component.literal(""));
        this.font = font;
        this.menu = null;

        if(stack.getItem() instanceof GemOfFocusItem){
            this.infusionStorage = new InfusionStorage(new ItemStack(Registration.INFUSING_STATION.get()));
        }else {
            this.infusionStorage = new InfusionStorage(stack);
        }

        this.outputInfusions = convertOutputInfusionMap(outputInfusions);
        this.isJEI = true;
    }

    public InfusionBars(int x, int y, Font font, InfusionMenu menu) {
        this(x, y, BAR_WIDTH, 0, Component.literal(""), font, menu);
    }

    public InfusionBars(int x, int y, int width, int height, Component message, Font font, InfusionMenu menu) {
        super(x, y, width, height, message);
        this.font = font;
        this.menu = menu;
        this.isJEI = false;
    }

    public List<Component> getWidgetTooltips(Infusion infusion) {
        List<Component> tooltips = new ArrayList<>();

        Infusion infusionInstance = infusionStorage.getInfusion(infusion);

        int tierPoints = infusionInstance.getTierPoints();
        int tier = infusionInstance.getTier();

        int pointsToTier = (isJEI) ? infusion.getPointsToTierByPoints(tierPoints) : infusionInstance.getPointsToTier(tier);
        int maxTier = infusionInstance.getMaxTier();

        MutableComponent component = Component.empty();
        component.append(Component.translatable("miningmadness.infusion.type"));
        component.append(Component.literal(": "));
        component.append(Component.translatable("miningmadness.infusion.type_" + infusionInstance.getName()).withColor(infusionInstance.getColor()));
        tooltips.add(component);

        component = Component.empty();
        component.append(Component.translatable("miningmadness.infusion.tier_points"));
        component.append(Component.literal(": "));

        if(!this.isJEI) component.append(Component.literal(tierPoints + "/" + pointsToTier));

        if(outputInfusions.containsKey(infusion.getClass())){
            int gainAmount = outputInfusions.get(infusion.getClass());
            if(this.isJEI){
                component.append(Component.literal("+" + gainAmount).withColor(0xff4CFF00));
            }else{
                component.append(Component.literal("(+" + gainAmount + ")").withColor(0xff4CFF00));
            }

        }

        tooltips.add(component);

        if(!this.isJEI) {
            component = Component.empty();
            component.append(Component.translatable("miningmadness.infusion.tier"));
            component.append(Component.literal(": " + tier + "/" + maxTier));
            tooltips.add(component);
        }

        return tooltips;
    }

    protected Map<Class<? extends Infusion>, Integer> convertOutputInfusionMap(Map<ResourceLocation, Integer> recipeOutputInfusions) {
        Map<Class<? extends Infusion>, Integer> outputInfusions = new HashMap<>();

        recipeOutputInfusions.forEach((k, v) -> {
            Infusion infusion = MiningMadnessRegistries.INFUSIONS.get(k);
            if(infusion != null){
                Class<? extends Infusion> infusionClass = infusion.getClass();
                outputInfusions.put(infusionClass, v);
            }
        });

        return outputInfusions;
    }

    protected void renderInfusionBar(GuiGraphics guiGraphics, Infusion infusion){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.INFUSION_BAR_TEXTURE);

        Point2D.Double barLocation = barLocationMap.get(infusion);
        int xPos = (int) barLocation.getX();
        int yPos = (int) barLocation.getY();

        guiGraphics.blit(INFUSION_BAR_TEXTURE,xPos-1, yPos-1, 0, 0, BAR_WIDTH + 2, BAR_HEIGHT + 2, BAR_WIDTH + 2, BAR_HEIGHT + 2);

        Infusion infusionInstance = infusionStorage.getInfusion(infusion);

        int tierPoints = infusionInstance.getTierPoints();
        int tier = infusionInstance.getTier();
        int pointsToTier = infusionInstance.getPointsToTier(tier);

        if(isJEI && outputInfusions.containsKey(infusion.getClass())){
            pointsToTier = infusionInstance.getPointsToTierByPoints(outputInfusions.get(infusion.getClass()));
        }


        int storedWidth = BAR_WIDTH - (int)(BAR_WIDTH * (tierPoints/(float)pointsToTier));

        guiGraphics.fill(xPos, yPos, xPos + (BAR_WIDTH - storedWidth), yPos + BAR_HEIGHT, infusionInstance.getColor());

        if(outputInfusions.containsKey(infusion.getClass())){
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
            int gainAmount = outputInfusions.get(infusion.getClass());
            int gainWidth = 0;
            int realBarEndPos = xPos + (BAR_WIDTH - storedWidth);

            if(tierPoints + gainAmount <= pointsToTier) {
                gainWidth = Math.max((int) (BAR_WIDTH * (gainAmount / (float) pointsToTier)), 1);
            }else if(tierPoints <= pointsToTier - gainAmount) {
                gainWidth = Math.max((int) (BAR_WIDTH * ((pointsToTier - tierPoints) / (float) pointsToTier)), 1);
            }

            guiGraphics.fill(realBarEndPos, yPos, realBarEndPos + gainWidth, yPos + BAR_HEIGHT, infusionInstance.getColor());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(!isJEI) infusionStorage = menu.getInfusionStorage();
        barLocationMap.clear();

        if(infusionStorage != null) {
            SortedMap<String, Infusion> activeInfusionMap = infusionStorage.getSortedInfusions();
            if(menu instanceof InfusingStationMenu infusingStationMenu){
                if(infusingStationMenu.recipeOutputInfusions != null){
                    this.outputInfusions = convertOutputInfusionMap(infusingStationMenu.recipeOutputInfusions);
                }
            }

            int iterator = 0;
            for(Infusion infusion: activeInfusionMap.values()) {
                if(this.isJEI && !this.outputInfusions.containsKey(infusion.getClass())) continue;

                barLocationMap.put(infusion, new Point2D.Double(getX(), getY() + 6 * iterator));
                renderInfusionBar(guiGraphics, infusion);
                iterator++;
            }

            this.height = 6 * iterator;

            if (MouseUtil.isMouseOver(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                for(Map.Entry<Infusion, Point2D.Double> entry: barLocationMap.entrySet()){
                    if(isHoveredOverType(mouseX, mouseY, entry.getKey())) {
                        guiGraphics.renderTooltip(this.font, getWidgetTooltips(entry.getKey()), Optional.empty(), mouseX, mouseY);
                    }
                }
            }
        }
    }

    private boolean isHoveredOverType(int mouseX, int mouseY, Infusion infusion) {
        Point2D.Double barLocation = barLocationMap.get(infusion);
        int xPos = (int) barLocation.getX();
        int yPos = (int) barLocation.getY();

        return MouseUtil.isMouseOver(mouseX, mouseY, xPos, yPos, BAR_WIDTH, BAR_HEIGHT);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
