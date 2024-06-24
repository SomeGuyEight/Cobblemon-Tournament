package com.cobblemontournament.common.testing.command;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemonrental.common.CobblemonRental;
import com.cobblemonrental.common.RentalManager;
import com.cobblemontournament.common.testing.TournamentBuilderTest;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.helpers.Util;

import java.util.UUID;

public class TestTournamentCommand
{
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> buildTournamentDebugCommand = Commands.literal("tournament")
                .then(Commands.literal("testBuild")
                        .then(Commands.argument("maxPlayers", IntegerArgumentType.integer(0,64))
                                .then(Commands.literal("print")
                                        .then(Commands.argument("doPrint", BoolArgumentType.bool())
                                                .executes(c -> buildTournamentDebug(IntegerArgumentType.getInteger(c,"maxPlayers"),BoolArgumentType.getBool(c,"doPrint"))))))
                );
        
        LiteralArgumentBuilder<CommandSourceStack> registerRandomRentalPokemonCommand = Commands.literal("rentalPokemon")
                .then(Commands.literal("test")
                              .then(Commands.literal("save")
                                            .executes(c -> registerRandomRentalPokemon()))
                );
        LiteralArgumentBuilder<CommandSourceStack> loadRandomRentalPokemonCommand = Commands.literal("rentalPokemon")
                .then(Commands.literal("test")
                              .then(Commands.literal("load")
                                        .executes(c ->  loadRandomRentalPokemon()))
                );
        
        dispatcher.register(buildTournamentDebugCommand);
        dispatcher.register(registerRandomRentalPokemonCommand);
        dispatcher.register(loadRandomRentalPokemonCommand);
    }

    public static int buildTournamentDebug(int players,boolean doPrint)
    {
        TournamentBuilderTest.buildTournamentDebug(players,doPrint);
        return Command.SINGLE_SUCCESS;
    }
    
    public static int registerRandomRentalPokemon()
    {
        Util.report("At registerRandomRentalPokemon");
        var pokemon = new Pokemon().initialize();
        pokemon.setUuid(UUID.randomUUID());
        var rental = RentalManager.instance.addRentalPokemon(pokemon);
        if (rental == null){
            Util.report("Failed to Save Rental Pokemon Data");
            return 0;
        }
        Util.report("Successful Rental Pokemon Data Save id: " + rental.rentalID());
        return Command.SINGLE_SUCCESS;
    }
    
    public static int loadRandomRentalPokemon()
    {
        Util.report("At loadRandomRentalPokemon");
        var rental = RentalManager.instance.getRandomRentalPokemon();
        if (rental != null) {
            Util.report("Successful Rental Pokemon Load:: rental ID: " + rental.rentalID() + " :: original pokemon ID: " + rental.originalPokemon().getUuid());
            return Command.SINGLE_SUCCESS;
        } else {
            Util.report("Failed to load Rental Pokemon Load b/c list was empty");
            return 0;
        }
    }
}
