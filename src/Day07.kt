fun main() {
    class Hand(input: String, useJoker: Boolean) {
        val type: Int
        val jokerType: Int
        val cards: List<Int>
        val bid: Int

        init {
            val split = input.split(" ")
            cards = split[0].toCharArray()
                .map {
                    when {
                        it.isDigit() -> it.digitToInt()
                        it == 'T'    -> 10
                        it == 'J'    -> if (useJoker) -1 else 11
                        it == 'Q'    -> 12
                        it == 'K'    -> 13
                        it == 'A'    -> 14
                        else         -> 0
                    }
                }
            bid = split[1].toInt()

            val sorted = cards.sorted()
            val grouped = cards
                .groupBy { it }
                .mapValues { it.value.size }
            type = when {
                sorted.toSet().size == 1              -> 6
                grouped.containsValue(4)              -> 5
                sorted.toSet().size == 2              -> 4
                grouped.containsValue(3)              -> 3
                grouped.values.count { it == 2 } == 2 -> 2
                grouped.containsValue(2)              -> 1
                else                                  -> 0
            }
            if (!useJoker) {
                jokerType = 0
            } else {
                jokerType = getValueWithReplacedJoker(cards)
            }
        }

        private fun getValueWithReplacedJoker(cards: List<Int>): Int {
            val jokerIndex = cards.indexOf(-1)
            if (jokerIndex == -1) {
                val sorted = cards.sorted()
                val grouped = cards
                    .groupBy { it }
                    .mapValues { it.value.size }
                return when {
                    sorted.toSet().size == 1              -> 6
                    grouped.containsValue(4)              -> 5
                    sorted.toSet().size == 2              -> 4
                    grouped.containsValue(3)              -> 3
                    grouped.values.count { it == 2 } == 2 -> 2
                    grouped.containsValue(2)              -> 1
                    else                                  -> 0
                }
            } else {
                return (2..14).toList()
                    .filter { it != 11 }
                    .maxOf {
                        val replacedCards = cards.toMutableList()
                        replacedCards[jokerIndex] = it
                        getValueWithReplacedJoker(replacedCards)
                    }
            }
        }
    }

    fun part1(input: List<String>): Long {
        return input.map { Hand(it, false) }
            .sortedWith { h1, h2 ->
                if (h1.type != h2.type) {
                    return@sortedWith h1.type.compareTo(h2.type)
                } else {
                    val diffCard = h1.cards.zip(h2.cards).first { it.first != it.second }
                    return@sortedWith diffCard.first.compareTo(diffCard.second)
                }
            }
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .map { it.toLong() }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input.map { Hand(it, true) }
            .sortedWith { h1, h2 ->
                if (h1.jokerType != h2.jokerType) {
                    return@sortedWith h1.jokerType.compareTo(h2.jokerType)
                } else {
                    val diffCard = h1.cards.zip(h2.cards).first { it.first != it.second }
                    return@sortedWith diffCard.first.compareTo(diffCard.second)
                }
            }
//            .onEach {
//                println("Hand: ${it.cards} JokerType: ${it.jokerType} OT: ${it.type}")
//            }
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .map { it.toLong() }
            .sum()
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput), 6440L)
    check(part2(testInput), 5905L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}