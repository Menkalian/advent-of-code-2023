fun main() {
    fun part1(input: List<String>): Long {
        val instructions = input[0]

        val nodeRegex = "(...) = \\((...), (...)\\)".toRegex()
        val nodes = input.drop(2)
            .map {
                val result = nodeRegex.matchEntire(it)!!
                result.groupValues[1] to (result.groupValues[2] to result.groupValues[3])
            }
            .toMap()

        var count = 0L
        var instructionIndex = 0
        var position = "AAA"
        while (position != "ZZZ") {
            val node = nodes[position]!!
            val instruction = instructions[instructionIndex]

            if (instruction == 'L') {
                position = node.first
            } else {
                position = node.second
            }
            instructionIndex = (instructionIndex + 1) % instructions.length
            count++
        }
        return count
    }

    fun part2(input: List<String>): Long {
        val instructions = input[0]

        val nodeRegex = "(...) = \\((...), (...)\\)".toRegex()
        val nodes = input.drop(2)
            .map {
                val result = nodeRegex.matchEntire(it)!!
                result.groupValues[1] to (result.groupValues[2] to result.groupValues[3])
            }
            .toMap()

        var instructionIndex = 0
        val positions = nodes.keys.filter { it.endsWith('A') }
        val roundTrips = positions.map { startPosition ->
            var count = 0L
            var position = startPosition
            while (!position.endsWith('Z')) {
                val node = nodes[position]!!
                val instruction = instructions[instructionIndex]

                if (instruction == 'L') {
                    position = node.first
                } else {
                    position = node.second
                }
                instructionIndex = (instructionIndex + 1) % instructions.length
                count++
            }
            count
        }
        return lcm(roundTrips)
    }

    val testInput = readInput("Day08_test")
    val testInput2 = readInput("Day08_test2")
    val testInput3 = readInput("Day08_test3")
    check(part1(testInput), 2L)
    check(part1(testInput2), 6L)
    check(part2(testInput3), 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}