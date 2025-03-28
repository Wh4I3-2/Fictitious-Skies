package com.wh4i3.fictitiousskies.init;

import com.wh4i3.fictitiousskies.FictitiousSkies;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FictitiousSkies.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static final DeferredItem<Item> B_ROLL = ITEMS.registerSimpleItem("b_roll");

    public static <T extends Block> DeferredItem<BlockItem> registerSimpleBlockItem(String id, DeferredBlock<T> block){
        DeferredItem<BlockItem> simpleBlockItem = ITEMS.registerSimpleBlockItem(id, block);
        BLOCK_ITEMS.add(simpleBlockItem);
        return simpleBlockItem;
    }

    public static ItemStack createBRoll(ResourceLocation location, boolean blur, String id) {
        ItemStack stack = new ItemStack(B_ROLL.get());
        Component displayName = Component.translatable("item.fictitious_skies.b_roll")
            .append(" (")
            .append(Component.translatable("b_roll." + id))
            .append(")");
        stack.set(ModDataComponentType.SKYBOX.get(), new ModDataComponentType.Skybox(location, blur));
        stack.set(DataComponents.ITEM_NAME, displayName);

        return stack;
    }

}