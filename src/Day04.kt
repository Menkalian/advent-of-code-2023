fun main() {
    fun part1(input: List<String>): Long {
        return input.map {
            val split = it.substring(it.indexOf(":"))
                    .split("|")
            val winningNumbers = split[0]
                    .trim()
                    .split(" ")
                    .map { it.trim().toIntOrNull() }
                    .filterNotNull()
            val presentNumbers = split[1]
                    .trim()
                    .split(" ")
                    .map { it.trim().toIntOrNull() }
                    .filterNotNull()
            val intersection = winningNumbers.intersect(presentNumbers)
            Math.pow(2.0, (intersection.size - 1).toDouble()).toLong()
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val winningCount = input.map {
            val split = it.substring(it.indexOf(":"))
                    .split("|")
            val winningNumbers = split[0]
                    .trim()
                    .split(" ")
                    .map { it.trim().toIntOrNull() }
                    .filterNotNull()
            val presentNumbers = split[1]
                    .trim()
                    .split(" ")
                    .map { it.trim().toIntOrNull() }
                    .filterNotNull()
            val intersection = winningNumbers.intersect(presentNumbers)
            intersection.size
        }
        val counts = buildList {
            input.indices.forEach {
                add(1)
            }
        }.toMutableList()

        input.indices.forEach { idx ->
            val winning = winningCount[idx]
            val count = counts[idx]

            if (winning > 0) {
                for (i in 1..winning) {
                    counts[idx + i] += count
                }
            }
        }
        return counts.sum().toLong()
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput), 13L)
    check(part2(testInput), 30L)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}