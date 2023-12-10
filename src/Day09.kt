fun main() {
    fun List<Long>.diffAndExpand(): List<Long> {
        if (this.all { it == 0L }) {
            return this + 0L
        }

        val diffs = windowed(2)
            .map { it[1] - it[0] }
            .diffAndExpand()

        return this + (last() + diffs.last())
    }

    fun List<Long>.extrapolateNext(): Long {
        return diffAndExpand().last()
    }

    fun List<Long>.diffAndExpandBack(): List<Long> {
        if (this.all { it == 0L }) {
            return this + 0L
        }

        val diffs = windowed(2)
            .map { it[1] - it[0] }
            .diffAndExpandBack()

        return listOf((first() - diffs.first()), *this.toTypedArray())
    }

    fun List<Long>.extrapolatePrevious(): Long {
        return diffAndExpandBack().first()
    }

    fun part1(input: List<String>): Long {
        return input
            .map {
                it.split(" ").map { it.toLong() }
            }.sumOf {
                it.extrapolateNext()
            }
    }

    fun part2(input: List<String>): Long {
        return input
            .map {
                it.split(" ").map { it.toLong() }
            }.sumOf {
                it.extrapolatePrevious()
            }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput), 114L)
    check(part2(testInput), 2L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
