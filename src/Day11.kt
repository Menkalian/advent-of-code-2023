import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun List<String>.findColIdx(): List<Int> {
        val doubleIndices = mutableListOf<Int>()
        val colIndices = this.first().indices
        colIndices.forEach { idx ->
            if (this.all { it[idx] == '.' }) {
                doubleIndices += idx
            }
        }
        return doubleIndices
    }

    fun List<String>.findRowIdx(): List<Int> {
        val doubleIndices = mutableListOf<Int>()
        forEachIndexed { idx, line ->
            if (line.all { it == '.' }) {
                doubleIndices += idx
            }
        }
        return doubleIndices
    }

    fun List<String>.expandInput(): List<String> {
        val doubleIndices = mutableListOf<Int>()

        val colIndices = this.first().indices
        colIndices.forEach { idx ->
            if (this.all { it[idx] == '.' }) {
                doubleIndices += idx
            }
        }

        val reverseSortedIdx = doubleIndices
                .sorted()
                .reversed()
        val expanded = this.map {
            val sb = StringBuilder(it)
            reverseSortedIdx.forEach { idx ->
                sb.insert(idx, '.')
            }
            sb.toString()
        }

        return buildList {
            expanded.forEach {
                add(it)
                if (it.all { it == '.' })
                    add(it) // Double empty lines
            }
        }
    }

    fun manhattanDistance(
            p1: Pair<Int, Int>, p2: Pair<Int, Int>,
            expandCols: Set<Int> = emptySet(),
            expandRows: Set<Int> = emptySet(),
            expandFactor: Long = 1L
    ): Long {
        val xRange = min(p1.first, p2.first)..max(p1.first, p2.first)
        val yRange = min(p1.second, p2.second)..max(p1.second, p2.second)

        var base = ((p1.first - p2.first).absoluteValue + (p1.second - p2.second).absoluteValue).toLong()

        expandCols.forEach {
            if (xRange.contains(it)) {
                base = base - 1L + expandFactor
            }
        }
        expandRows.forEach {
            if (yRange.contains(it)) {
                base = base - 1L + expandFactor
            }
        }

        return base
    }

    fun part1(input: List<String>): Long {
        val nodes = input
                .expandInput()
                .flatMapIndexed { yIdx, line ->
                    line.mapIndexed { xIdx, char ->
                        if (char == '#') {
                            xIdx to yIdx
                        } else {
                            null
                        }
                    }.filterNotNull()
                }
                .toSet()

        val combinations = mutableSetOf<Set<Pair<Int, Int>>>()
        nodes.forEach { g1 ->
            nodes.forEach { g2 ->
                combinations.add(setOf(g1, g2))
            }
        }
        combinations.removeIf { it.size != 2 }
        return combinations.sumOf {
            val list = it.toList()
            manhattanDistance(list[0], list[1])
        }
    }

    fun part2(input: List<String>, expansionFactor: Int = 1_000_000): Long {
        val nodes = input
                .flatMapIndexed { yIdx, line ->
                    line.mapIndexed { xIdx, char ->
                        if (char == '#') {
                            xIdx to yIdx
                        } else {
                            null
                        }
                    }.filterNotNull()
                }
                .toSet()
        val expandCols = input.findColIdx().toSet()
        val expandRows = input.findRowIdx().toSet()

        val combinations = mutableSetOf<Set<Pair<Int, Int>>>()
        nodes.forEach { g1 ->
            nodes.forEach { g2 ->
                combinations.add(setOf(g1, g2))
            }
        }
        combinations.removeIf { it.size != 2 }
        return combinations.sumOf {
            val list = it.toList()
            manhattanDistance(list[0], list[1], expandCols, expandRows, expansionFactor.toLong())
        }
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput), 374L)
    check(part2(testInput, 10), 1030L)
    check(part2(testInput, 100), 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}