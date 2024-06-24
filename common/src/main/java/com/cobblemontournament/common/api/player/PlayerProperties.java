package com.cobblemontournament.common.api.player;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;

import java.util.UUID;

/** naming is hard... this one is definitely WIP :( lol */
public record PlayerProperties(UUID id,ActorType actorType,Integer seed) { }
