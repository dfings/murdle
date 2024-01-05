#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("com.google.guava:guava:33.0.0-jre")

import com.google.common.collect.Collections2.permutations

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

fun <T> cartesianProduct(iterables: Iterable<Iterable<T>>): Sequence<List<T>> =
    iterables.asSequence().fold(sequenceOf(listOf<T>())) { acc, input ->
        acc.flatMap { output -> input.map { output + it } }
    }

fun generateValidSolutions(size: Int): Sequence<Solution> {
    val permutations = permutations((1..size).toList())
    val products = cartesianProduct(List(size - 1) { permutations })
    val prefixes = (1..size).map { listOf(it) }
    return products
        .map { list -> (0..size-1).map { i -> prefixes[i] + list.map { it[i] } } }
}

val lines = java.io.File(args[0]).readLines()
val constraints = lines.map { Constraint(it.split(',').map { it.toInt() }) }
val size = constraints[0].values.size
generateValidSolutions(size).filter { solution -> constraints.all { it.isCompatibleWith(solution) } }.forEach { println(it) }
