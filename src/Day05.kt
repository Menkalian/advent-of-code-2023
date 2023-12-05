fun main() {
    fun String.parseMap(map: MutableList<Pair<LongRange, Long>>): Boolean {
        val regex = "(\\d+)\\s+(\\d+)\\s+(\\d+)".toRegex()
        return regex.matchEntire(this)
                ?.groups
                ?.let {
                    val dest = it[1]?.value?.toLong() ?: 0L
                    val src = it[2]?.value?.toLong() ?: 0L
                    val len = it[3]?.value?.toLong() ?: 0L

                    map.add((src..src + len) to dest)
                    true
                } ?: false
    }

    fun List<Pair<LongRange, Long>>.mapInput(input: Long): Long {
        return firstOrNull {
            it.first.contains(input)
        }?.let {
            it.second + (input - it.first.first)
        } ?: input
    }

    fun part1(input: List<String>): Long {
        val seeds = input[0].substringAfter(':')
                .split(" ")
                .map { it.toLongOrNull() }
                .filterNotNull()
        val mappingList = mutableListOf<MutableList<Pair<LongRange, Long>>>()

        input.forEach {
            if (mappingList.isEmpty()) {
                mappingList.add(mutableListOf())
            }

            if (!it.parseMap(mappingList.last()) && mappingList.last().isEmpty().not()) {
                mappingList.add(mutableListOf())
            }
        }

        return seeds.minOfOrNull {
            var key = it
            mappingList.forEach {
                key = it.mapInput(key)
            }
            key
        } ?: -1L
    }

    fun part2(input: List<String>): Long {
        val mappingList = mutableListOf<MutableList<Pair<LongRange, Long>>>()
        input.forEach {
            if (mappingList.isEmpty()) {
                mappingList.add(mutableListOf())
            }

            if (!it.parseMap(mappingList.last()) && mappingList.last().isEmpty().not()) {
                mappingList.add(mutableListOf())
            }
        }

        // TODO: Do math with ranges...
        val seeds = input[0].substringAfter(':')
                .split(" ")
                .map { it.toLongOrNull() }
                .filterNotNull()
                .chunked(2)
                .map {
                    it[0].rangeUntil(it[0] + it[1])
                }

        return seeds.parallelStream().mapToLong {
            it.minOf {
                var key = it
                mappingList.forEach {
                    key = it.mapInput(key)
                }
                key
            }
        }.min().asLong
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput), 35L)
    check(part2(testInput), 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}