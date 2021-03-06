package assertk

import kotlin.native.concurrent.ThreadLocal
import kotlin.native.concurrent.AtomicInt

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun failWithNotInStacktrace(error: AssertionError): Nothing {
    throw error
}

/*
 * Copyright (C) 2018 Touchlab, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
actual open class ThreadLocalRef<T> actual constructor() {
    private val threadLocalId = ThreadLocalIdCounter.nextThreadLocalId()

    actual fun get(): T? {
        return if (ThreadLocalState.threadLocalMap.containsKey(threadLocalId)) {
            @Suppress("UNCHECKED_CAST")
            ThreadLocalState.threadLocalMap[threadLocalId] as T
        } else {
            null
        }
    }

    actual fun set(value: T?) {
        if (value == null) {
            ThreadLocalState.threadLocalMap.remove(threadLocalId)
        } else {
            ThreadLocalState.threadLocalMap.put(threadLocalId, value)
        }
    }
}

@ThreadLocal
private object ThreadLocalState {
    val threadLocalMap = HashMap<Int, Any>()
}

private object ThreadLocalIdCounter {
    val threadLocalId = AtomicInt(0)
    fun nextThreadLocalId(): Int = threadLocalId.addAndGet(1)
}
