package com.kylecorry.trail_sense.shared.commands

interface ValueCommand<T, S> {
    suspend fun execute(value: T): S
}

suspend fun <S> ValueCommand<Unit, S>.execute(): S {
    return execute(Unit)
}

fun <T, S, K> ValueCommand<T, S>.then(next: ValueCommand<S, K>): ValueCommand<T, K> {
    return CombinedCommand(this, next)
}

fun <T, K> ValueCommand<T, *>.switchTo(next: ValueCommand<Unit, K>): ValueCommand<T, K> {
    return SwitchCommand(this, next)
}

fun <T, S, K> ValueCommand<T, S>.map(map: (S) -> K): ValueCommand<T, K> {
    return MapCommand(this, map)
}

fun <T> ValueCommand<T, *>.ignore(): ValueCommand<T, Unit> {
    return MapCommand(this) { }
}

fun <T, S> ValueCommand<T, S?>.whenNotNull(
    next: ValueCommand<S, Unit>
): ValueCommand<T, Unit> {
    return MapCommand(this) {
        if (it != null) {
            next.execute(it)
        }
    }
}

class MapCommand<T, S, K>(
    private val command: ValueCommand<T, S>,
    private val map: suspend (S) -> K
) : ValueCommand<T, K> {
    override suspend fun execute(value: T): K {
        return map(command.execute(value))
    }

}

class CombinedCommand<T, S, K>(
    private val first: ValueCommand<T, S>,
    private val second: ValueCommand<S, K>
) : ValueCommand<T, K> {
    override suspend fun execute(value: T): K {
        return second.execute(first.execute(value))
    }

}

class SwitchCommand<T, K>(
    private val first: ValueCommand<T, *>,
    private val second: ValueCommand<Unit, K>
) : ValueCommand<T, K> {
    override suspend fun execute(value: T): K {
        first.execute(value)
        return second.execute()
    }

}

class DoNothingCommand<T> : ValueCommand<T, Unit> {
    override suspend fun execute(value: T) {
    }
}