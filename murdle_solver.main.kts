#!/usr/bin/env kotlin

typealias Combo = List<Int>
typealias Solution = List<Combo>

class Constraint(val values: Combo) {
    val isNegative = values.any { it < 0 }

    fun isCompatibleWith(solution: Solution): Boolean = 
        if (isNegative) solution.none(this::matches) else solution.any(this::matches)

    fun matches(combo: Combo): Boolean {
        for (i in combo.indices) {
            if (values[i] > 0 && values[i] != combo[i]) return false
            if (values[i] < 0 && -values[i] != combo[i]) return false
        }
        return true
    }
}

fun <T> cartesianProduct(iterables: Iterable<Iterable<T>>) =
    iterables.fold(listOf(listOf<T>())) { acc, input ->
        acc.flatMap { output -> input.map { output + it } }
    }

fun generateValidSolutions(size: Int): Sequence<Solution> {
    var count = 0
    val buckets = List(size - 1) { (1..size).toMutableSet() }

    fun generateValidSolutionsInternal(): Sequence<Solution> = sequence {
       if (buckets[0].size == 1) {
            if (count++ % 100000 == 0) println(count - 1)
            yield(listOf(buckets.map { it.first() }))
        } else {
            for (combo in cartesianProduct(buckets)) {
                combo.forEachIndexed { index, it -> buckets[index].remove(it) }
                yieldAll(generateValidSolutionsInternal().map { listOf(combo) + it })
                combo.forEachIndexed { index, it -> buckets[index].add(it) }
            }
        }
    }

    return generateValidSolutionsInternal()
        .map { it.mapIndexed { index, it -> listOf(index + 1) + it } }
}

val lines = java.io.File(args[0]).readLines()
val constraints = lines.map { Constraint(it.split(',').map { it.toInt() }) }
val size = constraints[0].values.size
generateValidSolutions(size).filter { solution -> constraints.all { it.isCompatibleWith(solution) } }.forEach { println(it) }
