package com.turtlehoarder.cobblemonchallenge.common.util;

import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FakeStore extends PlayerPartyStore {
    public FakeStore(@NotNull UUID uuid) {
        super(uuid, uuid);
    }

    @Override
    public boolean add(Pokemon p) {
        return true;
    }
}