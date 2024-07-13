package com.cobblemonrental.common;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.factory.FileBackedPokemonStoreFactory;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemonrental.common.api.storage.RentalStorePosition;
import com.cobblemonrental.common.api.storage.pokemon.RentalPokemon;
import com.cobblemonrental.common.api.storage.pokemon.RentalPokemonStore;
import com.cobblemonrental.common.api.storage.team.RentalTeam;
import com.cobblemonrental.common.api.storage.team.RentalTeamStore;
import com.cobblemonrental.common.util.RentalDataKeys;
import com.cobblemonrental.common.util.RentalStoreUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.helpers.Util;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;

@SuppressWarnings({"unused","UNUSED_PARAMETER"})
public class RentalManager
{
    public static final RentalManager instance = new RentalManager();
    public MinecraftServer server;
    private Path savePath;
    
    @SuppressWarnings("unused")
    private static PokemonStoreManager storeManager;
    private static FileBackedPokemonStoreFactory<CompoundTag> rentalPokemonFactory;
    private static FileBackedPokemonStoreFactory<CompoundTag> rentalTeamFactory;
    private static UUID rentalPokemonStoreID;
    private static UUID rentalTeamStoreID;
    
    private RentalStoreUtil rentalStoreUtil()
    {
        return RentalStoreUtil.getInstance();
    }
    
    private RentalPokemonStore pokemonStore()
    {
        return rentalPokemonFactory.getCustomStore(RentalPokemonStore.class,rentalPokemonStoreID);
    }
    
    private RentalTeamStore teamStore()
    {
        return rentalTeamFactory.getCustomStore(RentalTeamStore.class,rentalTeamStoreID);
    }
    
    
    public void initialize(MinecraftServer server)
    {
        this.server = server;
        savePath = server.getWorldPath(LevelResource.ROOT);
        var keyRootFile = getFile("rentalKey");
        var pokemonRootFile = getFile("pokemonStore");
        var teamRootFile = getFile("teamStore");
        var gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        
        rentalPokemonStoreID = getKey(savePath.resolve(getFileString(keyRootFile,"pokemonStoreKey","json")).toFile(),gson);
        rentalTeamStoreID = getKey(savePath.resolve(getFileString(keyRootFile,"teamStoreKey","json")).toFile(),gson);
        
        rentalPokemonFactory = rentalStoreUtil().initializeFactory(pokemonRootFile.toString());
        rentalTeamFactory = rentalStoreUtil().initializeFactory(teamRootFile.toString());
        var rentalPokemonStore = rentalStoreUtil().initializePokemonStore(rentalPokemonStoreID,null);
        var rentalTeamStore = rentalStoreUtil().initializeTeamStore(rentalTeamStoreID,null);
        rentalPokemonFactory.track(rentalPokemonStore);
        rentalPokemonFactory.track(rentalTeamStore);
        
        // TODO ?? which priority level is best for this implementation ??
        rentalStoreUtil().registerFactory(Priority.NORMAL,rentalPokemonFactory);
    }
    
    
    public RentalPokemon addRentalPokemon(Pokemon pokemon)
    {
        var store = pokemonStore();
        var position = store.getFirstAvailablePosition();
        if (position == null) {
            return null;
        }
        store.set(position,pokemon);
        rentalPokemonFactory.save(store);
        return store.get(position);
    }
    
    public RentalPokemon getRentalPokemon(UUID rentalID)
    {
        return pokemonStore().get(new RentalStorePosition(rentalID));
    }
    
    public RentalPokemon getRandomRentalPokemon()
    {
        var rentalPokemonStore = pokemonStore();
        if (rentalPokemonStore != null && rentalPokemonStore.iterator().hasNext()) {
            return rentalPokemonStore.iterator().next();
        }
        return null;
    }
    
    // TODO test
    public RentalTeam addRentalTeam(Pokemon pokemon)
    {
        var store = teamStore();
        var position = store.getFirstAvailablePosition();
        if (position == null) {
            return null;
        }
        store.set(position,pokemon);
        rentalTeamFactory.save(store);
        return store.getTeam(position);
    }
    
    // TODO test
    public RentalTeam getRentalTeam(UUID rentalID)
    {
        return teamStore().getTeam(new RentalStorePosition(rentalID));
    }
    
    // TODO test
    public RentalTeam getRandomRentalTeam()
    {
        var teamStore = teamStore();
        if (teamStore != null && teamStore.mapSize() > 0) {
            return teamStore.getFirst();
        }
        return null;
    }
    
    private File getFile(String subFolderInsert)
    {
        var file = savePath.resolve("rental-data/" + subFolderInsert + "/").toFile();
        file.mkdirs();
        return file;
    }
    
    private String getFileString(File rootFile,String name, String extension)
    {
        var fileString = rootFile.toString();
        if (!fileString.endsWith("/")){
            fileString += "/";
        }
        fileString += (name + "." + extension);
        return fileString;
    }
    
    private UUID getKey(File file,Gson gson)
    {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        var json = rentalStoreUtil().getJSON(gson,br);
        
        UUID id;
        JsonElement idElement = null;
        if (json != null) {
            // cast to UUID & return
            idElement = json.get(RentalDataKeys.RENTAL_POKEMON_STORE_KEY);
        }
        if (idElement != null) {
            var element = idElement.toString();
            Util.report("Element: " + element + " :: UUID: " + element.substring(1,37));
            // 1,37 b/c "" are included -> maybe fix in future?
            id = UUID.fromString(element.substring(1,37));
        } else {
            // create new & save
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            var newJson = new JsonObject();
            id = UUID.randomUUID();
            newJson.addProperty(RentalDataKeys.RENTAL_POKEMON_STORE_KEY,id.toString());
            pw.write(newJson.toString());
            pw.flush();
            pw.close();
        }
        return id;
    }
    
}
