package com.nhoryzon.mc.farmersdelight.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.nhoryzon.mc.farmersdelight.block.SkilletBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class SkilletItem extends BlockItem {

    public static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Set.of(Enchantments.VANISHING_CURSE, Enchantments.SHARPNESS,
            Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING,
            Enchantments.MENDING, Enchantments.UNBREAKING);

    public static final String TAG_KEY_SKILLET_COOK_TIME_HANDHELD = "CookTimeHandheld";
    public static final String TAG_KEY_SKILLET_COOKING = "Cooking";

    public static final ToolMaterials SKILLET_MATERIAL = ToolMaterials.IRON;

    private final Multimap<EntityAttribute, EntityAttributeModifier> toolAttributes;

    public SkilletItem() {
        super(BlocksRegistry.SKILLET.get(), new ModItemSettings().maxCount(1).maxDamageIfAbsent(SKILLET_MATERIAL.getDurability()));
        float attackDamage = 5.f + SKILLET_MATERIAL.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -3.1f, EntityAttributeModifier.Operation.ADDITION));
        this.toolAttributes = builder.build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, player -> player.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        return true;
    }

    private static boolean isPlayerNearHeatSource(PlayerEntity player, WorldView world) {
        if (player.isOnFire()) {
            return true;
        }

        BlockPos pos = player.getBlockPos();
        for (BlockPos nearbyPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (world.getBlockState(nearbyPos).isIn(TagsRegistry.HEAT_SOURCES)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        int fireAspectLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack);
        int cookingTime = stack.getOrCreateNbt().getInt(TAG_KEY_SKILLET_COOK_TIME_HANDHELD);

        return SkilletBlock.getSkilletCookingTime(cookingTime, fireAspectLevel);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack skilletStack = player.getStackInHand(hand);
        if (isPlayerNearHeatSource(player, world)) {
            Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack cookingStack = player.getStackInHand(otherHand);

            if (skilletStack.getOrCreateNbt().contains(TAG_KEY_SKILLET_COOKING)) {
                player.setCurrentHand(hand);

                return TypedActionResult.pass(skilletStack);
            }

            Optional<CampfireCookingRecipe> recipe = getCookingRecipe(cookingStack, world);
            if (recipe.isPresent()) {
                ItemStack cookingStackCopy = cookingStack.copy();
                ItemStack cookingStackUnit = cookingStackCopy.split(1);
                skilletStack.getOrCreateNbt().put(TAG_KEY_SKILLET_COOKING, cookingStackUnit.writeNbt(new NbtCompound()));
                skilletStack.getOrCreateNbt().putInt(TAG_KEY_SKILLET_COOK_TIME_HANDHELD, recipe.get().getCookTime());
                player.setCurrentHand(hand);
                player.setStackInHand(otherHand, cookingStackCopy);

                return TypedActionResult.consume(skilletStack);
            } else {
                player.sendMessage(FarmersDelightMod.i18n("item.skillet.how_to_cook"), true);
            }
        }

        return TypedActionResult.pass(skilletStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            Vec3d pos = player.getPos();
            double x = pos.getX() + .5d;
            double y = pos.getY();
            double z = pos.getZ() + .5d;
            if (world.random.nextInt(50) == 0) {
                world.playSound(x, y, z, SoundsRegistry.BLOCK_SKILLET_SIZZLE.get(), SoundCategory.BLOCKS, .4f,
                        world.random.nextFloat() * .2f + .9f, false);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        NbtCompound tag = stack.getOrCreateNbt();
        if (tag.contains(TAG_KEY_SKILLET_COOKING)) {
            ItemStack cookingStack = ItemStack.fromNbt(tag.getCompound(TAG_KEY_SKILLET_COOKING));
            player.getInventory().offerOrDrop(cookingStack);
            tag.remove(TAG_KEY_SKILLET_COOKING);
            tag.remove(TAG_KEY_SKILLET_COOK_TIME_HANDHELD);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof PlayerEntity player)) {
            return stack;
        }

        NbtCompound tag = stack.getOrCreateNbt();
        if (tag.contains(TAG_KEY_SKILLET_COOKING)) {
            ItemStack cookingStack = ItemStack.fromNbt(tag.getCompound(TAG_KEY_SKILLET_COOKING));
            Optional<CampfireCookingRecipe> recipe = getCookingRecipe(cookingStack, world);

            recipe.ifPresent(campfireCookingRecipe -> {
                ItemStack resultStack = campfireCookingRecipe.craft(new SimpleInventory());
                if (!player.getInventory().insertStack(resultStack)) {
                    player.dropItem(resultStack, false);
                }
                if (player instanceof ServerPlayerEntity) {
                    Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
                }
            });
            tag.remove(TAG_KEY_SKILLET_COOKING);
            tag.remove(TAG_KEY_SKILLET_COOK_TIME_HANDHELD);
        }


        return stack;
    }

    public static Optional<CampfireCookingRecipe> getCookingRecipe(ItemStack stack, World world) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SimpleInventory(stack), world);
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.postPlacement(pos, world, player, stack, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SkilletBlockEntity skilletBlockEntity) {
            skilletBlockEntity.setSkilletItem(stack);
            return true;
        }

        return false;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return SKILLET_MATERIAL.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && state.getHardness(world, pos) != .0f) {
            stack.damage(1, miner, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isSneaking()) {
            return super.place(context);
        }

        return ActionResult.PASS;
    }

    @Override
    public int getEnchantability() {
        return SKILLET_MATERIAL.getEnchantability();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? toolAttributes : super.getAttributeModifiers(slot);
    }

}
