
fun main() {
    fun CharArray.findNewY(x: Int, y: Int, width: Int): Int {
        for (y1 in y - 1 downTo 0) {
            if (this[y1 * width + x] != '.') {
                return y1 + 1
            }
        }
        return 0
    }

    fun part1(input: List<String>): Long {
        val width = input.first().length
        val updatedInput = input.joinToString("").toCharArray()

        println(input.joinToString("\n"))
        println()

        input.indices.forEach { y ->
            for (x in 0 until width) {
                val c = updatedInput[y * width + x]
                if (c == 'O') {
                    val newY = updatedInput.findNewY(x, y, width)
                    updatedInput[y * width + x] = '.'
                    updatedInput[newY * width + x] = 'O'
                }
            }
        }
        return updatedInput
                .joinToString("")
                .chunked(width)
                .onEach {
                    println(it)
                }
                .mapIndexed { idx, line ->
                    line.count { c -> c == 'O' } * (input.size - idx).toLong()
                }
                .sum()
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput), 136L)
    check(part2(testInput), 64L)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}