package org.example.scanners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class Scanner: CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {

    abstract suspend fun load()

    open suspend fun use(block: suspend Scanner.() -> Unit) {
        block()
        cancel()
    }

}