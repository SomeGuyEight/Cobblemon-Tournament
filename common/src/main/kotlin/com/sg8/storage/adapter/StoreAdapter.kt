package com.sg8.storage.adapter

// From the Cobblemon Mod I adapted this script from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/.
 *
 *
 * package com.cobblemon.mod.common.api.storage.adapter
 * interface CobblemonAdapter<S>
 */

import com.sg8.storage.Store
import com.sg8.storage.StorePosition
import com.sg8.storage.TypeStored
import java.util.UUID

// Eight's implementation
interface StoreAdapter<Ser> {

    /**
     * Attempts to load a store using the specified class and UUID. This would return null if
     * the file does not exist or if this store adapter doesn't know how to load this storage class.
     */
    fun <P : StorePosition, T : TypeStored, S : Store<P, T>> load(
        storeClass: Class<S>,
        uuid: UUID,
    ): S?

}
