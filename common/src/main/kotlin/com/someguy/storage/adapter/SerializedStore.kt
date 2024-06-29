package com.someguy.storage.adapter

// From the Cobblemon Mod I adapted this script from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * package com.cobblemon.mod.common.api.storage.adapter
 * data class SerializedStore<S>(val storeClass: Class<out PokemonStore<*>>, val uuid: UUID, val serializedForm: S)
 */

import com.someguy.storage.store.Store
import java.util.UUID

data class SerializedStore<Ser>(
    val storeClass: Class<out Store<*,*>>,
    val uuid: UUID,
    val serializedForm: Ser)
