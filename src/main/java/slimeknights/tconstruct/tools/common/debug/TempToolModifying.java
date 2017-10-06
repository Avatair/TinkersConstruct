package slimeknights.tconstruct.tools.common.debug;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class TempToolModifying implements IRecipe {

  static {
    // register the recipe with the recipesorter
    RecipeSorter.register("tcon:mod", TempToolModifying.class, RecipeSorter.Category.SHAPELESS, "");
  }

  private ItemStack outputTool;

  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting p_77572_1_) {
    return outputTool;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    outputTool = null;

    NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    ItemStack tool = ItemStack.EMPTY;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks.set(i, inv.getStackInSlot(i));
      if(!stacks.get(i).isEmpty() && stacks.get(i).getItem() instanceof TinkersItem) {
        tool = stacks.get(i);
        stacks.set(i, ItemStack.EMPTY);
      }
    }

    if(tool.isEmpty()) {
      return false;
    }

    try {
      outputTool = ToolBuilder.tryModifyTool(stacks, tool, false, false);
    } catch(TinkerGuiException e) {
      System.out.println(e.getMessage());
    }

    return outputTool != null;
  }

  @Override
  public int getRecipeSize() {
    return 2;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return outputTool;
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    ItemStack tool = null;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      stacks.set(i, inv.getStackInSlot(i));
      if(!stacks.get(i).isEmpty() && stacks.get(i).getItem() instanceof TinkersItem) {
        tool = stacks.get(i);
        stacks.set(i, ItemStack.EMPTY);
      }
    }

    try {
      ToolBuilder.tryModifyTool(stacks, tool, true, false);
    } catch(TinkerGuiException e) {
      e.printStackTrace();
    }

    return stacks;
  }
}
