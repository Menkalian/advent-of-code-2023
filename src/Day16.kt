import java.awt.Event.DOWN
import java.awt.Event.UP
import kotlin.experimental.and

enum class Direction(val update: (Pair<Int, Int>) -> Pair<Int, Int>, val bitFlag: Byte) {
    LEFT({ (x, y) -> x - 1 to y }, 0x01),
    UP({ (x, y) -> x to y - 1 }, 0x02),
    DOWN({ (x, y) -> x to y + 1 }, 0x04),
    RIGHT({ (x, y) -> x + 1 to y }, 0x08);

    fun mirrorSlash(): Direction = when (this) {
        LEFT  -> DOWN
        UP    -> RIGHT
        RIGHT -> UP
        DOWN  -> LEFT
    }

    fun mirrorBackslash(): Direction = when (this) {
        LEFT  -> UP
        UP    -> LEFT
        RIGHT -> DOWN
        DOWN  -> RIGHT
    }
}

fun main() {
    fun trackBeam(pair: Pair<Int, Int>, dir: Direction, input: List<String>, marker: Array<Array<Byte>>) : List<Pair<Pair<Int, Int>, Direction>> {
        val pos = dir.update(pair)
        if (marker.indices.contains(pos.second).not()
            || marker[pos.second].indices.contains(pos.first).not()
        ) {
            return emptyList()
        }

        if (marker[pos.second][pos.first].and(dir.bitFlag) != (0).toByte()) {
            // already handled
            return emptyList()
        }

        marker[pos.second][pos.first] = (marker[pos.second][pos.first] + dir.bitFlag).toByte()

        val char = input[pos.second][pos.first]
        when (char) {
            '.'  -> {
                return listOf(pos to dir)
            }

            '/'  -> {
                return listOf(pos to dir.mirrorSlash())
            }

            '\\' -> {
                return listOf(pos to dir.mirrorBackslash())
            }

            '|'  -> when (dir) {
                Direction.LEFT, Direction.RIGHT -> {
                    return listOf(
                        pos to Direction.DOWN,
                        pos to Direction.UP
                    )
                }

                Direction.UP, Direction.DOWN    -> {
                    return listOf(pos to dir)
                }
            }

            '-'  -> when (dir) {
                Direction.LEFT, Direction.RIGHT -> {
                    return listOf(pos to dir)
                }

                Direction.UP, Direction.DOWN    -> {
                    return listOf(
                        pos to Direction.LEFT,
                        pos to Direction.RIGHT
                    )
                }
            }

            else -> throw IllegalStateException("Unknown char $char")
        }
    }

    fun part1(input: List<String>): Long {
        val marker = input
            .map { it.map { (0).toByte() }.toTypedArray() }
            .toTypedArray()

        var positions = listOf(
            (-1 to 0) to Direction.RIGHT
        )

        var iteration = 0L
        while (positions.isNotEmpty()) {
            iteration++
            positions = positions
                .flatMap {
                    trackBeam(it.first, it.second, input, marker)
                }
        }

        return marker.sumOf {
            it.count { it > 0L }.toLong()
        }
    }

    fun part2(input: List<String>): Long {
        val startPositions = buildList<Pair<Pair<Int, Int>, Direction>> {
            val width = input.first().length
            val height = input.size

            for (y in input.indices) {
                add((-1 to y) to Direction.RIGHT)
                add((width to y) to Direction.LEFT)
            }

            for (x in input.first().indices) {
                add((x to -1) to Direction.DOWN)
                add((x to height) to Direction.UP)
            }
        }

        return startPositions.maxOf {
            var positions = listOf(it)
            val marker = input
                .map { it.map { (0).toByte() }.toTypedArray() }
                .toTypedArray()

            var iteration = 0L
            while (positions.isNotEmpty()) {
                iteration++
                positions = positions
                    .flatMap {
                        trackBeam(it.first, it.second, input, marker)
                    }
            }

            marker.sumOf {
                it.count { it > 0L }.toLong()
            }
        }
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput), 46L)
    check(part2(testInput), 51L)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}