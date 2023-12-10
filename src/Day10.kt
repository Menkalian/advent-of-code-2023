fun main() {
    class Node(val value: Char, val position: Pair<Int, Int>) {
        var visited: Boolean = false
        var distance: Int = 0

        fun getConnectedPositions(grid: List<List<Node>>): List<Pair<Int, Int>> {
            return when (value) {
                '|' -> listOf(
                    position.first to position.second + 1,
                    position.first to position.second - 1,
                )

                '-' -> listOf(
                    position.first + 1 to position.second,
                    position.first - 1 to position.second,
                )

                'L' -> listOf(
                    position.first to position.second - 1,
                    position.first + 1 to position.second,
                )

                'J' -> listOf(
                    position.first to position.second - 1,
                    position.first - 1 to position.second,
                )

                '7' -> listOf(
                    position.first to position.second + 1,
                    position.first - 1 to position.second,
                )

                'F' -> listOf(
                    position.first to position.second + 1,
                    position.first + 1 to position.second,
                )

                'S' -> {
                    val possibles = listOf(
                        position.first - 1 to position.second,
                        position.first to position.second - 1,
                        position.first + 1 to position.second,
                        position.first to position.second + 1,
                    )

                    possibles
                        .filter { it.first >= 0 && it.second >= 0 }
                        .map { grid[it.second][it.first] }
                        .filter {
                            it.getConnectedPositions(grid)
                                .contains(position)
                        }
                        .map { it.position }
                }

                else -> emptyList()
            }
        }

        fun findRealValue(grid: List<List<Node>>): Char {
            if (value != 'S') {
                return value
            } else {
                val positions = getConnectedPositions(grid).toSet()
                val possibles = listOf('|', '-', 'F', 'J', 'L', '7')
                return possibles
                    .first {
                        Node(it, position).getConnectedPositions(grid).toSet() == positions
                    }
            }
        }
    }

    fun scanLoopWithoutRecursion(grid: List<List<Node>>, start: Node): Int {
        var currentPosition = start
        var loopSize = 0

        while (currentPosition.visited.not()) {
            currentPosition.visited = true
            currentPosition.distance = loopSize

            val nodes = currentPosition
                .getConnectedPositions(grid)
                .map { grid[it.second][it.first] }
            val next = nodes.firstOrNull() { it.visited.not() }
                ?: nodes.firstOrNull { it.value == 'S' }

            if (next != null) {
                loopSize += 1
                currentPosition = next
            }
        }

        return loopSize
    }

    fun scanDepth(grid: List<List<Node>>, start: Pair<Int, Int>) {
        val nodeList = mutableSetOf<Pair<Int, Int>>()
        var node: Node? = grid[start.second][start.first]

        while (node != null) {
            if (node.visited.not()) {
                val pos = node.position
                node.visited = true
                nodeList.addAll(
                    listOf(
                        pos.first - 1 to pos.second,
                        pos.first + 1 to pos.second,
                        pos.first to pos.second - 1,
                        pos.first to pos.second + 1,
                    ).filter {
                        grid.indices.contains(it.second) && grid[it.second].indices.contains(it.first)
                    }
                )
            }

            val nextPos = nodeList.firstOrNull()
            node = nextPos?.let { grid[nextPos.second][nextPos.first] }
            nodeList.remove(nextPos)
        }
    }

    fun scanRemainingNodes(grid: List<List<Node>>) {
        val height = grid.size
        val width = grid[0].size

        val positions = buildSet<Pair<Int, Int>> {
            for (x in 0 until width) {
                add(x to 0)
                add(x to (height - 1))
            }
            for (y in 0 until height) {
                add(0 to y)
                add((width - 1) to y)
            }
        }
        positions.forEach {
            scanDepth(grid, it)
        }
    }

    fun part1(input: List<String>): Long {
        var startNode: Node? = null
        val grid = input.mapIndexed { posY, line ->
            line.mapIndexed { posX, value ->
                val node = Node(value, posX to posY)
                if (node.value == 'S') {
                    startNode = node
                }
                node
            }
        }

        val loopSize = scanLoopWithoutRecursion(grid, startNode!!)

        return (loopSize.toLong()) / 2
    }

    fun part2(input: List<String>): Long {
        var startNode: Node? = null
        val originalGrid = input.mapIndexed { posY, line ->
            line.mapIndexed { posX, value ->
                val node = Node(value, posX to posY)
                if (node.value == 'S') {
                    startNode = node
                }
                node
            }
        }
        val replacement = startNode?.findRealValue(originalGrid)

        val interlacedInput = buildList<String> {
            input.forEachIndexed { idx, line ->
                if (idx != 0) {
                    add("|".repeat(line.length))
                }
                add(line)
            }
        }.map {
            it.flatMapIndexed { idx, c ->
                if (idx == 0) {
                    listOf(c)
                } else {
                    listOf('-', c)
                }
            }.joinToString("")
        }
        val grid = interlacedInput.mapIndexed { posY, line ->
            line.mapIndexed { posX, value ->
                val node = Node(
                    if (value == 'S') (replacement ?: 'E') else value,
                    posX to posY
                )
                if (value == 'S') {
                    startNode = node
                }
                node
            }
        }

        // Mark Loop as visited
        scanLoopWithoutRecursion(grid, startNode!!)
//        println(grid.joinToString("\n") {
//            it.joinToString("") {
//                if (it.visited) {
//                    "*"
//                } else {
//                    "."
//                }
//            }
//        })
        // Mark outside
        scanRemainingNodes(grid)
//        println(grid.joinToString("\n") {
//            it.joinToString("") {
//                if (it.visited) {
//                    "*"
//                } else {
//                    "."
//                }
//            }
//        })

        // Perform breadth-search
        return grid.mapIndexed { yIdx, nodes ->
//            println()
            if (yIdx % 2 == 1) {
//                print("INTERLACE")
                // Interlaced
                0
            } else {
                nodes
                    .mapIndexed { xIdx, node ->
                        if (xIdx % 2 == 1) {
//                            print(" ")
                            // Interlaced
                            0
                        } else if (node.visited) {
//                            print(node.value)
                            0
                        } else {
//                            print(node.value)
                            1
                        }
                    }
//                    .onEach { print(it) }
                    .sumOf { it }
            }
        }.sum().toLong()
    }

    val testInput = readInput("Day10_test")
    val testInput2 = readInput("Day10_test2")
    val testInput3 = readInput("Day10_test3")
    val testInput4 = readInput("Day10_test4")
    check(part1(testInput), 4L)
    check(part1(testInput2), 8L)
    check(part2(testInput3), 4L)
    check(part2(testInput4), 8L)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}