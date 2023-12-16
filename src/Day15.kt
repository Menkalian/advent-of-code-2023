fun main() {
    fun Long.hash(c: Char): Long {
        return ((this + c.code) * 17L) % 256
    }

    fun String.hash1A(): Long {
        var value = 0L
        toCharArray().forEach {
            value = value.hash(it)
        }
        return value
    }

    fun part1(input: List<String>): Long {
        val initializationLine = input.first()
        return initializationLine.split(",")
            .sumOf { it.hash1A() }
    }

    fun part2(input: List<String>): Long {
        val boxes = buildList<MutableList<Pair<String, Int>>> {
            for (i in 0..255) {
                add(mutableListOf())
            }
        }

        input.first()
            .split(",")
            .forEach {
                val eqIdx = it.indexOf('=')
                if (eqIdx >= 0) {
                    val label = it.substring(0, eqIdx)
                    val lens = it.substring(eqIdx + 1).toInt()
                    val boxIdx = label.hash1A().toInt()
                    val box = boxes[boxIdx]
                    if (box.firstOrNull { it.first == label } != null) {
                        box.replaceAll {
                            if (it.first == label) {
                                label to lens
                            } else {
                                it
                            }
                        }
                    } else {
                        box.add(label to lens)
                    }
                } else {
                    val minIdx = it.indexOf('-')
                    if (minIdx >= 0) {
                        val label = it.substring(0, minIdx)
                        val boxIdx = label.hash1A().toInt()
                        val box = boxes[boxIdx]
                        box.firstOrNull {
                            it.first == label
                        }?.let { matchingLens ->
                            box.remove(matchingLens)
                        }
                    }
                }
            }

        return boxes.mapIndexed { boxIdx, box ->
            box.mapIndexed { lensIdx, lens ->
                (boxIdx + 1).toLong() * (lensIdx + 1).toLong() * lens.second
            }.sum()
        }.sum()
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput), 1320L)
    check(part2(testInput), 145L)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}