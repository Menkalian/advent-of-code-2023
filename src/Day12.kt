fun main() {
    fun part1(input: List<String>): Long {
        fun checkValid(line: String, groups: List<Int>): Boolean {
            var currentCount = 0
            val remainingGroups = groups.toMutableList()
            line.forEach {
                if (it == '#') {
                    currentCount++
                } else if (currentCount > 0) {
                    val expected = remainingGroups.removeFirstOrNull() ?: return false
                    if (currentCount != expected) {
                        return false
                    }
                    currentCount = 0
                }
            }

            if (currentCount > 0) {
                val expected = remainingGroups.removeFirstOrNull() ?: return false
                if (currentCount != expected) {
                    return false
                }
            }
            return true
        }

        fun backtrack(baseInput: String, groups: List<Int>, maxSprings: Int): Long {
            if (baseInput.count { it == '#' } > maxSprings || baseInput.count { it != '.' } < maxSprings) {
                return 0
            }

            val replace = baseInput.indexOf('?')
            if (replace < 0) {
                if (checkValid(baseInput, groups)) {
                    return 1
                } else {
                    return 0
                }
            } else {
                return backtrack(baseInput.replaceFirst('?', '.'), groups, maxSprings) +
                        backtrack(baseInput.replaceFirst('?', '#'), groups, maxSprings)
            }
        }

        return input
            .sumOf {
                val (inputLine, nongram) = it.split(" ", limit = 2)
                val groups = nongram.split(",").map { it.toInt() }
                val maxCount = groups.sum()

                backtrack(inputLine, groups, maxCount)
            }
    }

    fun part2(input: List<String>): Long {
        fun checkValid(line: String, groups: List<Int>): Boolean {
            var currentCount = 0
            val remainingGroups = groups.toMutableList()
            line.forEach {
                if (it == '#') {
                    currentCount++
                } else if (currentCount > 0) {
                    val expected = remainingGroups.removeFirstOrNull() ?: return false
                    if (currentCount != expected) {
                        return false
                    }
                    currentCount = 0
                }
            }

            if (currentCount > 0) {
                val expected = remainingGroups.removeFirstOrNull() ?: return false
                if (currentCount != expected) {
                    return false
                }
            }
            return true
        }

        fun getCountAndRemainingGroups(inputStart: String, groups: List<Int>): Pair<Int, List<Int>> {
            var currentCount = 0
            val remainingGroups = groups.toMutableList()
            inputStart.forEach {
                if (it == '#') {
                    currentCount++
                } else if (currentCount > 0) {
                    val expected = remainingGroups.removeFirstOrNull() ?: throw IllegalStateException()
                    if (currentCount != expected) {
                        throw IllegalStateException()
                    }
                    currentCount = 0
                }
            }

            return currentCount to remainingGroups
        }

        data class Key(
            val input: String,
            val prevCount: Int,
            val groups: List<Int>
        )

        val cache = mutableMapOf<Key, Long>()
        fun backtrack(baseInput: String, groups: List<Int>, maxSprings: Int): Long {
            if (baseInput.count { it == '#' } > maxSprings || baseInput.count { it != '.' } < maxSprings) {
                return 0
            }

            val replace = baseInput.indexOf('?')
            if (replace < 0) {
                if (checkValid(baseInput, groups)) {
                    return 1
                } else {
                    return 0
                }
            } else {
                try {
                    val (previousCount, remGroups) = getCountAndRemainingGroups(baseInput.substring(0, replace), groups)
                    val cacheString = baseInput.substring(replace)
                    val key = Key(cacheString, previousCount, remGroups)

                    if (cacheString.length > 5) {
                        // return from cache if known
                        cache[key]?.let {
//                        println("Using $it from cache for $key")
                            return it
                        }
                    }

                    val nextEmptyIndex = baseInput.substring(replace).indexOf('.')
                    if (previousCount != 0
                        && nextEmptyIndex >= 0
                        && previousCount + nextEmptyIndex < remGroups.first()
                    ) {
                        // Can not complete
                        return 0
                    }

                    val compResult = if (baseInput.count { it == '#' } == maxSprings
                        || remGroups.isEmpty()
                        || previousCount == remGroups.first()
                        || (previousCount == 0 && nextEmptyIndex >= 0 && nextEmptyIndex < remGroups.first())) {
                        backtrack(baseInput.replaceFirst('?', '.'), groups, maxSprings)
                    } else if (previousCount != 0 && previousCount < remGroups.first()) {
                        backtrack(baseInput.replaceFirst('?', '#'), groups, maxSprings)
                    } else {
                        backtrack(baseInput.replaceFirst('?', '.'), groups, maxSprings) +
                                backtrack(baseInput.replaceFirst('?', '#'), groups, maxSprings)
                    }

                    if (cache[key] != null && cache[key] != compResult) {
                        throw RuntimeException("Cache invalid for $key: ${cache.get(key)} != $compResult")
                    }
                    cache[key] = compResult

                    return compResult

                } catch (ise: IllegalStateException) {
                    // Group not valid until now.
                    return 0
                }
            }
        }

        return input
            .sumOf {
                val (inputLine, nongram) = it.split(" ", limit = 2)
                val unfoldInput = listOf(inputLine, inputLine, inputLine, inputLine, inputLine).joinToString("?")
                val unfoldNonogram = listOf(nongram, nongram, nongram, nongram, nongram).joinToString(",")
                val groups = unfoldNonogram.split(",").map { it.toInt() }
                val maxCount = groups.sum()

                val res = backtrack(unfoldInput, groups, maxCount)
                cache.clear() // Reset cache to avoid overmemory commitment
//                println("$it -> $res")
                res
            }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput), 21L)
    check(part2(testInput), 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}