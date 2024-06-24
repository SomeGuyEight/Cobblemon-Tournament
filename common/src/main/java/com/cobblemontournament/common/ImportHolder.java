package com.cobblemontournament.common;

// import com.bedrockk.molang.runtime.struct.VariableStruct;
//import com.bedrockk.molang.runtime.struct.VariableStruct;
//import com.bedrockk.molang.runtime.value.DoubleValue;


// import net.minecraft.block.*;

// import net.minecraft.server.network.ServerPlayerEntity;
// import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.Entity; // super of LivingEntity
import  net.minecraft.world.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.CompoundTag;
// import net.minecraft.nbt.NbtElement.COMPOUND_TYPE;

// import net.minecraft.nbt.NbtList;

// import net.minecraft.nbt.NbtString;

// import net.minecraft.registry.tag.FluidTags;

// import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.level.ServerLevel;
// import net.minecraft.text.TextContent; & net.minecraft.text.MutableText;
import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.text.Text;
import net.minecraft.network.chat.Component;
// import net.minecraft.util.Hand;

// ??
// import net.minecraft.util.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
// ??
// import net.minecraft.util.InvalidIdentifierException;

// import net.minecraft.util.math.BlockPos;

// import net.minecraft.util.math.MathHelper.ceil;

// import net.minecraft.util.math.MathHelper.clamp;

// import net.minecraft.util.math.Vec3d;
import net.minecraft.world.phys.Vec3;
// import net.minecraft.world.World;

// import net.minecraft.network.PacketByteBuf
import net.minecraft.network.FriendlyByteBuf;

//import kotlin.math.absoluteValue;
//import kotlin.math.max;
//import kotlin.math.min;
//import kotlin.math.roundToInt;


// unknown but referenced?
//import net.minecraft.server.Bootstrap;
//import net.minecraft.util.Mth;
//import net.minecraft.util.RandomSource;
//import net.minecraft.util.SingleKeyCache;
//import net.minecraft.util.TimeSource;
//import net.minecraft.util.datafix.DataFixers;
//import net.minecraft.world.level.block.state.properties.Property;


@SuppressWarnings("unused")
public class ImportHolder
{
    
//    VariableStruct variableStruct; // imported on Forge || Fabric TODO figure out & import to the one not importing
//    DoubleValue doubleValue; // TODO same as above
    
    // found this in decompiled a lot
    // java.util.concurrent.CompletableFuture<?>
    MutableComponent mutableComponent;
    Component component;
    ServerPlayer player;
    Entity entity;
    LivingEntity livingEntity;
    ItemStack itemStack;
    CompoundTag compoundTag;
    ServerLevel serverLevel;
    ResourceKey<?> resourceKey;
    ResourceLocation resourceLocation;
    Vec3 vec3;
    FriendlyByteBuf friendlyByteBuf;
    
    Iterable<?> iterable;
//    Set<?> set;
//    MutableList<?> mutableList;
//    MutableSet<?> mutableSet;
//    Unit unit;
    
    // ability
    // com.cobblemon.mod.common.api.reactive.SimpleObservable<com.cobblemon.mod.common.api.abilities.Ability>
    
    // pokemon stats
    // com.cobblemon.mod.common.api.reactive.SimpleObservable<com.cobblemon.mod.common.pokemon.PokemonStats>
    
    // nature
    // com.cobblemon.mod.common.api.reactive.SimpleObservable<com.cobblemon.mod.common.pokemon.Nature?>
    
    // move-set
    // com.cobblemon.mod.common.api.reactive.SimpleObservable<com.cobblemon.mod.common.api.moves.MoveSet>
    
    // allAccessibleMoves
    // kotlin.collections.Set<com.cobblemon.mod.common.api.moves.MoveTemplate>
    
    // set-able coords
    // com.cobblemon.mod.common.api.reactive.SettableObservable<com.cobblemon.mod.common.api.storage.StoreCoordinates<*>?>
    
    // species features
    // kotlin.collections.MutableList<com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature>
}
