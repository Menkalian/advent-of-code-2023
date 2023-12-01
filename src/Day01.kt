fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.toCharArray().filter { it.isDigit() } }
            .map { it.first().digitToInt() * 10 + it.last().digitToInt() }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val validStrings = buildMap<String, Int> {
            for (n in 0..9) {
                put(n.toString(), n)
            }
            put("one", 1)
            put("two", 2)
            put("three", 3)
            put("four", 4)
            put("five", 5)
            put("six", 6)
            put("seven", 7)
            put("eight", 8)
            put("nine", 9)
        }
        return input
            .map { validStrings[it.findAnyOf(validStrings.keys)?.second]!! * 10 + validStrings[it.findLastAnyOf(validStrings.keys)?.second]!! }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day01_test_02")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
