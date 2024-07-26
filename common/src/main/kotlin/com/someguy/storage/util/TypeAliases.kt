package com.someguy.storage.util

import com.someguy.storage.Store
import java.util.UUID

typealias PlayerID = UUID
typealias StoreID = UUID
typealias InstanceID = UUID
typealias NameSet = Set<String>

typealias AnyStore = Store<*, *>
