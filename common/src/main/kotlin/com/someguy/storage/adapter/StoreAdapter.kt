package com.someguy.storage.adapter

// From the Cobblemon Mod I adapted this script from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 * package com.cobblemon.mod.common.api.storage.adapter
 * interface CobblemonAdapter<S>
 */

import com.someguy.storage.store.Store
import com.someguy.storage.position.StorePosition
import com.someguy.storage.classstored.ClassStored
import java.util.UUID

// Eight's implementation
interface StoreAdapter<Ser>
{

    /**
     * Attempts to load a store using the specified class and UUID. This would return null if
     * the file does not exist or if this store adapter doesn't know how to load this storage class.
     */
    fun <P: StorePosition,T: ClassStored, St: Store<P,T>> load(storeClass: Class<St>, uuid: UUID): St?
}
