enum class CardinalDirection {
    NORTH,
    WEST,
    SOUTH,
    EAST
}

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
            .mapIndexed { idx, line ->
                line.count { c -> c == 'O' } * (input.size - idx).toLong()
            }
            .sum()
    }

    fun findNextIdx(staticData: String, width: Int, height: Int, xIdx: Int, yIdx: Int, isX: Boolean, update: (Pair<Int, Int>) -> Pair<Int, Int>): Int {
        var curPos = xIdx to yIdx
        var nextPos = update(curPos)
        while (staticData.getOrNull(nextPos.first + nextPos.second * width) == '.'
            && ((0 until width).contains(nextPos.first))
            && ((0 until height).contains(nextPos.second))
        ) {
            curPos = nextPos
            nextPos = update(curPos)
        }
        return curPos.let {
            if (isX) {
                it.first
            } else {
                it.second
            }
        }
    }

    fun buildUpdateMaps(input: List<String>): Map<CardinalDirection, MutableMap<Long, Int>> {
        val onlyStatic = input
            .map { it.replace('O', '.') }
        val staticData = onlyStatic.joinToString("")
        val width = onlyStatic.first().length
        val height = onlyStatic.size
        val staticLut = mutableMapOf<CardinalDirection, MutableMap<Long, Int>>()
        CardinalDirection.entries.forEach {
            staticLut[it] = mutableMapOf()
        }

        onlyStatic.forEachIndexed { yIdx, line ->
            line.forEachIndexed { xIdx, char ->
                // FulIdx will not work for all size grids.
                val fulIdx = yIdx.toLong().shl(16) + xIdx
                if (char == '#') {
                    CardinalDirection.entries.forEach {
                        staticLut[it]!![fulIdx] = -1
                    }
                } else {
                    staticLut[CardinalDirection.NORTH]!![fulIdx] = findNextIdx(staticData, width, height, xIdx, yIdx, isX = false) { (x, y) -> x to (y - 1) }
                    staticLut[CardinalDirection.WEST]!![fulIdx] = findNextIdx(staticData, width, height, xIdx, yIdx, isX = true) { (x, y) -> (x - 1) to y }
                    staticLut[CardinalDirection.SOUTH]!![fulIdx] = findNextIdx(staticData, width, height, xIdx, yIdx, isX = false) { (x, y) -> x to (y + 1) }
                    staticLut[CardinalDirection.EAST]!![fulIdx] = findNextIdx(staticData, width, height, xIdx, yIdx, isX = true) { (x, y) -> (x + 1) to y }
                }
            }
        }

        return staticLut
    }

    class Rock(var posX: Int, var posY: Int) {
        private val cycleTracker: MutableMap<Int, Long> = mutableMapOf()
        private var cycleOffset: Int? = null
        private var cycleLength: Int? = null
        val fulIdx
            get() = posY.toLong().shl(16) + posX

        fun updatePos(d: CardinalDirection, lut: Map<Long, Int>, takenPos: MutableSet<Long>): Rock {
            if (d == CardinalDirection.NORTH || d == CardinalDirection.SOUTH) {
                posY = lut[fulIdx]!!
                while (takenPos.contains(fulIdx)) {
                    if (d == CardinalDirection.NORTH) {
                        posY += 1
                    } else {
                        posY -= 1
                    }
                }
            } else {
                posX = lut[fulIdx]!!
                while (takenPos.contains(fulIdx)) {
                    if (d == CardinalDirection.WEST) {
                        posX += 1
                    } else {
                        posX -= 1
                    }
                }
            }

            takenPos.add(fulIdx)
            return this
        }

        fun calcScore(height: Int): Long {
            return (height - posY).toLong()
        }

        fun saveAndCheckCycle(iterationNo: Int) {
            if (cycleLength != null) {
                return
            }
            val curIdx = fulIdx
            cycleTracker.entries
                .firstOrNull { it.value == curIdx }
                ?.let { cycleStart ->
                    cycleOffset = cycleStart.key
                    cycleLength = iterationNo - cycleStart.key - 1
                }
            cycleTracker[iterationNo] = curIdx
        }

        fun setIterations(iteration: Int) {
            val cycleLength = cycleLength!!
            println(cycleLength)
            val iterationIdx: Int
            if (cycleLength > 0) {
                iterationIdx = (iteration) % cycleLength + cycleOffset!!
            } else {
                iterationIdx = cycleOffset!!
            }
            val pos = cycleTracker[iterationIdx]!!
            posX = pos.and(0xFFFF).toInt()
            posY = pos.shr(16).toInt()
        }

        override fun equals(other: Any?): Boolean {
            if (other is Rock) {
                return this.fulIdx == other.fulIdx
            } else {
                return false
            }
        }

        override fun hashCode(): Int {
            return fulIdx.toInt()
        }

        fun clone(): Rock {
            return Rock(posX, posY)
        }
    }

    fun part2(input: List<String>): Long {
        val lookupTables = buildUpdateMaps(input)

        var currentPositions: MutableList<Rock> = mutableListOf()
        input.forEachIndexed { yIdx, line ->
            line.forEachIndexed { xIdx, c ->
                if (c == 'O') {
                    currentPositions.add(Rock(posX = xIdx, posY = yIdx))
                }
            }
        }

        currentPositions.forEach {
            it.saveAndCheckCycle(0)
        }

        val cycleTracker = mutableMapOf<Int, Set<Rock>>()
        cycleTracker[-1] = currentPositions.map { it.clone() }.toSet()
        for (i in 0 until 500) {
            for (d in listOf(CardinalDirection.NORTH, CardinalDirection.WEST, CardinalDirection.SOUTH, CardinalDirection.EAST)) {
                val newPos = mutableListOf<Rock>()
                val takenPos = mutableSetOf<Long>()
                currentPositions.forEach {
                    newPos.add(it.updatePos(d, lookupTables[d]!!, takenPos))
                }
                currentPositions = newPos
            }
            val saveEntry = currentPositions.map { it.clone() }.toSet()
            cycleTracker.entries.firstOrNull {
                it.value == saveEntry
            }?.let { match ->
                val offset = match.key
                val finalCycle = offset - 1 + (1_000_000_000 - i) % (i - offset)
                println("Found Match: Cycle $i == ${match.key}. Length: ${i - match.key}")

                return cycleTracker[finalCycle]!!.sumOf { it.calcScore(input.size) }
            }
            cycleTracker[i] = saveEntry
        }

        return currentPositions.sumOf { it.calcScore(input.size) }
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput), 136L)
    check(part2(testInput), 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}

// Foreign Code. https://github.com/dayanruben/aoc-kotlin/blob/main/src/aoc2023/Day14.kt
//
//fun part2(input: List<String>) = loadSum(input, 1_000_000_000)
//
//fun loadSum(input: List<String>, maxCycles: Int): Int {
//    var state = PlatformState(input.map { it.toCharArray() }.toTypedArray())
//
//    var totalLoad: Int? = null
//    when (maxCycles) {
//        0    -> state = state.tilt()
//
//        else -> {
//            val cycleForState = mutableMapOf(state to 0)
//            val stateForCycle = mutableMapOf(0 to state)
//            val loadForState = mutableMapOf(state to 0)
//            for (currentCycle in 1..maxCycles) {
//                repeat(4) {
//                    state = state.tilt().rotate()
//                }
//                if (state in cycleForState) {
//                    val repeatedCycle = cycleForState.getValue(state)
//                    val finalCycle = repeatedCycle - 1 + (maxCycles - currentCycle + 1) % (currentCycle - repeatedCycle)
//                    val finalState = stateForCycle.getValue(finalCycle)
//                    totalLoad = finalState.totalLoad()
//                    break
//                } else {
//                    cycleForState[state] = currentCycle
//                    stateForCycle[currentCycle] = state
//                    loadForState[state] = state.totalLoad()
//                }
//            }
//        }
//    }
//
//    return totalLoad ?: state.totalLoad()
//}
//
//data class PlatformState(val state: Array<CharArray>) {
//    fun tilt(): PlatformState {
//        val state = this.state
//        val numRows = state.size
//        val numCols = state[0].size
//        val cols = state.first().indices.map { colIndex ->
//            state.mapIndexed { rowIndex, row ->
//                row[colIndex] to rowIndex
//            }.filter {
//                it.first != '.'
//            }
//        }
//        val newState = Array(numRows) { CharArray(numCols) { '.' } }
//        cols.forEachIndexed { colIndex, col ->
//            var row = 0
//            for ((cell, rowIndex) in col) {
//                when (cell) {
//                    '#'  -> {
//                        newState[rowIndex][colIndex] = '#'
//                        row = rowIndex + 1
//                    }
//
//                    else -> {
//                        newState[row][colIndex] = 'O'
//                        row++
//                    }
//                }
//            }
//        }
//        return PlatformState(newState)
//    }
//
//    fun rotate(): PlatformState {
//        val state = this.state
//        val numRows = state.size
//        val numCols = state[0].size
//        val newState = Array(numCols) { CharArray(numRows) }
//        for (row in 0 until numRows) {
//            for (col in 0 until numCols) {
//                newState[col][numRows - 1 - row] = state[row][col]
//            }
//        }
//        return PlatformState(newState)
//    }
//
//    fun totalLoad(): Int = state.mapIndexed { index, row ->
//        row.count { it == 'O' } * (state.size - index)
//    }.sum()
//
//    override fun equals(other: Any?): Boolean = (other as? PlatformState)?.state.contentDeepEquals(state)
//    override fun hashCode(): Int = state.contentDeepHashCode()
//    override fun toString() = state.map { it.joinToString("") }.joinToString("\n")
//}