import kotlin.math.max
import kotlin.math.min

fun main() {
    fun String.parseMap(map: MutableList<Pair<LongRange, Long>>): Boolean {
        val regex = "(\\d+)\\s+(\\d+)\\s+(\\d+)".toRegex()
        return regex.matchEntire(this)
            ?.groups
            ?.let {
                val dest = it[1]?.value?.toLong() ?: 0L
                val src = it[2]?.value?.toLong() ?: 0L
                val len = it[3]?.value?.toLong() ?: 0L

                map.add((src..src + len) to dest)
                true
            } ?: false
    }

    fun LongRange.isCompletelyExclusiveTo(other: LongRange): Boolean {
        return this.last < other.first || this.first > other.last
    }

    fun LongRange.containsAtLeastPartially(other: LongRange): Boolean {
        return !this.isCompletelyExclusiveTo(other)
    }

    fun LongRange.containsFully(other: LongRange): Boolean {
        return this.first <= other.first && this.last >= other.last
    }

    operator fun LongRange.minus(other: LongRange): Set<LongRange> {
        if (other.containsFully(this)) {
            return emptySet()
        }

        if (this.isCompletelyExclusiveTo(other)) {
            return setOf(this)
        }

        if (this.containsFully(other)) {
            // Split in two
            return setOf(
                this.first until other.first,
                other.last + 1..this.last
            )
        } else {
            if (other.first > this.first) {
                return setOf(this.first until other.first)
            } else {
                return setOf(other.last + 1..this.last)
            }
        }
    }

    fun LongRange.rangeIntersect(other: LongRange): LongRange {
        if (this.isCompletelyExclusiveTo(other)) {
            return LongRange.EMPTY
        } else {
            return max(this.first, other.first)..min(this.last, other.last)
        }
    }

    fun List<Pair<LongRange, Long>>.mapInput(input: Long): Long {
        return firstOrNull {
            it.first.contains(input)
        }?.let {
            it.second + (input - it.first.first)
        } ?: input
    }

    fun List<Pair<LongRange, Long>>.mapInput(input: LongRange): Set<LongRange> {
        if (input.isEmpty()) {
            return emptySet()
        }

        var unprocessedInputs = setOf(input)
        val resultRanges = mutableSetOf<LongRange>()

        this.forEach { option ->
            val optionRange = option.first
            if (optionRange.isCompletelyExclusiveTo(input).not()) {
                unprocessedInputs = unprocessedInputs
                    .flatMap {
                        it - optionRange
                    }
                    .filter { it.isEmpty().not() }
                    .toSet()

                val intersection = input.rangeIntersect(optionRange)
                val offset = option.second - option.first.first

                resultRanges.add(intersection.first + offset..intersection.last + offset)
            }
        }

        return resultRanges + unprocessedInputs
    }

    fun part1(input: List<String>): Long {
        val seeds = input[0].substringAfter(':')
            .split(" ")
            .mapNotNull { it.toLongOrNull() }
        val mappingList = mutableListOf<MutableList<Pair<LongRange, Long>>>()

        input.forEach {
            if (mappingList.isEmpty()) {
                mappingList.add(mutableListOf())
            }

            if (!it.parseMap(mappingList.last()) && mappingList.last().isEmpty().not()) {
                mappingList.add(mutableListOf())
            }
        }

        return seeds.minOfOrNull {
            var key = it
            mappingList.forEach {
                key = it.mapInput(key)
            }
            key
        } ?: -1L
    }

    fun part2(input: List<String>): Long {
        val mappingList = mutableListOf<MutableList<Pair<LongRange, Long>>>()
        input.forEach {
            if (mappingList.isEmpty()) {
                mappingList.add(mutableListOf())
            }

            if (!it.parseMap(mappingList.last()) && mappingList.last().isEmpty().not()) {
                mappingList.add(mutableListOf())
            }
        }

        val seeds = input[0].substringAfter(':')
            .split(" ")
            .map { it.toLongOrNull() }
            .filterNotNull()
            .chunked(2)
            .map {
                it[0].rangeUntil(it[0] + it[1])
            }

        var values = seeds
        mappingList.forEach { mapping ->
            values = values.flatMap {
                mapping.mapInput(it)
            }.filter { it.isEmpty().not() }
        }

        return values.minOf { it.first }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput), 35L)
    check(part2(testInput), 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}