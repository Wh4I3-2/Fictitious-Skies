package com.wh4i3.fictitiousskies.item;

import java.util.List;

import javax.annotation.Nonnull;


import com.wh4i3.fictitiousskies.FictitiousSkies;
import com.wh4i3.fictitiousskies.init.ModDataComponentType;
import com.wh4i3.fictitiousskies.init.ModItems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;

public class BRollItem extends Item {
    public BRollItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createBRollStack(ResourceLocation skyboxLocation, boolean blur, ResourceLocation modelLocation, String credit) {
        return createBRollStack(skyboxLocation, blur, modelLocation, credit, 0x7492bf, 0x82ddee);
    }

    public static ItemStack createBRollStack(ResourceLocation skyboxLocation, boolean blur, ResourceLocation modelLocation, String credit, int color) {
        return createBRollStack(skyboxLocation, blur, modelLocation, credit, color, color);
    }

    public static ItemStack createBRollStack(ResourceLocation skyboxLocation, boolean blur, ResourceLocation modelLocation, String credit, int color0, int color1) {
        ItemStack stack = new ItemStack(ModItems.B_ROLL.get());
        stack.set(ModDataComponentType.SKYBOX, new ModDataComponentType.Skybox(skyboxLocation, blur));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(), List.of(color0, color1)));
        if (modelLocation == null) {
            modelLocation = FictitiousSkies.id("b_roll");
        }
        stack.set(DataComponents.ITEM_MODEL, modelLocation);
        stack.set(ModDataComponentType.CREDIT, credit);
        return stack;
    }

    public static ItemStack empty() {
        return createBRollStack(null, false, null, null);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (!stack.has(ModDataComponentType.CREDIT)) return;
        Component text = Component.literal(stack.get(ModDataComponentType.CREDIT)).withColor(0xAAAAAA);
        tooltipComponents.add(text);
    }
}
