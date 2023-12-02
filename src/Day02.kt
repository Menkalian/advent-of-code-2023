fun main() {
    fun String.readGame(): List<Map<String, Int>> {
        return split(";")
            .map {
                it.split(",")
                    .map {
                        it.trim()
                            .split(" ")
                            .let { it[1] to it[0].toInt() }
                    }.toMap()
            }
    }

    fun List<String>.parseGames(): List<Pair<Int, List<Map<String, Int>>>> {
        return this.map {
            "Game (\\d+): (.+)".toRegex()
                .matchEntire(it)
                ?.groups
                ?.let { it.get(1)?.value.toString().toInt() to it.get(2)?.value.toString() }
        }
            .filterNotNull()
            .map { it.first to it.second.readGame() }
    }

    fun part1(input: List<String>): Int {
        return input
            .parseGames()
            .filter {
                it.second.none {
                    (it["red"] ?: 0) > 12
                            || (it["green"] ?: 0) > 13
                            || (it["blue"] ?: 0) > 14
                }
            }
            .sumOf { it.first }
    }

    fun part2(input: List<String>): Long {
        return input
            .parseGames()
            .map { it.second }
            .map {
                it.maxOf { c -> c["red"]?.toLong() ?: 0L } *
                        it.maxOf { c -> c["green"]?.toLong() ?: 0L } *
                        it.maxOf { c -> c["blue"]?.toLong() ?: 0L }
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286L)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
