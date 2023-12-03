
fun main() {
    fun part1(input: List<String>): Long {
        var sum = 0L
        input.forEachIndexed { rowIdx, line ->
            val pattern = "[0-9]+".toPattern()
            val matcher = pattern.matcher(line)
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()

                val match = matcher.group()
                val rowRange = (rowIdx - 1..rowIdx + 1).intersect(input.indices).sorted()
                val colRange = (start - 1 until end + 1).intersect(line.indices).sorted()
                val isValid = input.subList(rowRange.first(), rowRange.last() + 1)
                    .map { it.substring(colRange.first(), colRange.last() + 1) }
                    .any { it.any { it.isDigit().not() && it != '.' } }
                if (isValid) {
                    sum += match.toInt()
                }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val numberPositions: MutableMap<Int, MutableMap<IntRange, String>> = mutableMapOf()
        val possibleGears = mutableListOf<Pair<Int, Int>>()

        input.forEachIndexed { rowIdx, line ->
            val rowPositions = numberPositions.getOrPut(rowIdx) { mutableMapOf() }
            val pattern = "[0-9]+".toPattern()
            val matcher = pattern.matcher(line)
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val match = matcher.group()
                rowPositions[start until end] = match
            }

            line.forEachIndexed { charIdx, c ->
                if (c == '*') {
                    possibleGears += rowIdx to charIdx
                }
            }
        }

        return possibleGears.map { gear ->
            val gearRow = gear.first
            val gearColumn = gear.second
            val gearRowRange = gearRow - 1..gearRow + 1
            val gearColumnRange = gearColumn - 1..gearColumn + 1
            val matches = numberPositions
                .filterKeys { key -> gearRowRange.contains(key) }
                .mapValues { entry ->
                    entry.value
                        .filterKeys { key -> gearColumnRange.intersect(key).isEmpty().not() }
                        .values
                }
                .values
                .flatten()
                .map { it.toInt() }
            if (matches.size == 2) {
                matches[0] * matches[1].toLong()
            } else {
                0L
            }
        }.sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput), 4361L)
    check(part2(testInput), 467835L)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}