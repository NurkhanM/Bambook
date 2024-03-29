package kg.foodbambook.bambook.utils

import java.util.concurrent.atomic.AtomicBoolean

class ConsumableValue<T>(private val data: T) {

    private val consumed = AtomicBoolean(false)

    fun consume(block: ConsumableValue<T>.(T) -> Unit) {
        if (!consumed.getAndSet(true)) {
            block(data)
        }
    }
}