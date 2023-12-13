fun main() {
    class Image(partInput: List<String>) {
        private val data = partInput.joinToString("")
        private val width = partInput.first().length
        private val height = partInput.size

        fun findMirrorValue(): Long {
            var value: Long = 0L

            for (x in 1 until width) {
                if (checkVerticalMirror(x)) {
                    value += x.toLong()
                }
            }

            for (y in 1 until height) {
                if (checkHorizontalMirror(y)) {
                    value += y.toLong() * 100L
                }
            }

            return value
        }

        private fun checkHorizontalMirror(yIdx: Int): Boolean {
            if (yIdx <= 0 || yIdx >= height) {
                return false
            }

            for (y in (yIdx - 1) downTo 0) {
                val yInv = y.invertIdx(yIdx)
                val yOff = y * width
                val invOff = yInv * width

                if (yInv >= height) {
                    break
                }

                for (x in 0 until width) {
                    if (data[yOff + x] != data[invOff + x]) {
                        return false
                    }
                }
            }

            return true
        }

        private fun checkVerticalMirror(xIdx: Int): Boolean {
            if (xIdx <= 0 || xIdx >= width) {
                return false
            }

            for (x in (xIdx - 1) downTo 0) {
                val xInv = x.invertIdx(xIdx)

                if (xInv >= width) {
                    break
                }

                for (y in 0 until height) {
                    val yOff = y * width
                    if (data[yOff + x] != data[yOff + xInv]) {
                        return false
                    }
                }
            }

            return true

        }

        fun findSmudgeMirrorValue(): Long {
            var value: Long = 0L

            for (x in 1 until width) {
                if (checkVerticalSmudgeMirror(x)) {
                    value += x.toLong()
                }
            }

            for (y in 1 until height) {
                if (checkHorizontalSmudgeMirror(y)) {
                    value += y.toLong() * 100L
                }
            }

            return value
        }

        private fun checkHorizontalSmudgeMirror(yIdx: Int): Boolean {
            if (yIdx <= 0 || yIdx >= height) {
                return false
            }

            var oneWrong = false

            for (y in (yIdx - 1) downTo 0) {
                val yInv = y.invertIdx(yIdx)
                val yOff = y * width
                val invOff = yInv * width

                if (yInv >= height) {
                    break
                }

                for (x in 0 until width) {
                    if (data[yOff + x] != data[invOff + x]) {
                        if (oneWrong) {
                            // Second "smudge"
                            return false
                        } else {
                            oneWrong = true
                        }
                    }
                }
            }

            return oneWrong
        }

        private fun checkVerticalSmudgeMirror(xIdx: Int): Boolean {
            if (xIdx <= 0 || xIdx >= width) {
                return false
            }

            var oneWrong = false

            for (x in (xIdx - 1) downTo 0) {
                val xInv = x.invertIdx(xIdx)

                if (xInv >= width) {
                    break
                }

                for (y in 0 until height) {
                    val yOff = y * width
                    if (data[yOff + x] != data[yOff + xInv]) {
                        if (oneWrong) {
                            // Second "smudge"
                            return false
                        } else {
                            oneWrong = true
                        }
                    }
                }
            }

            return oneWrong
        }

        private fun Int.invertIdx(baseIdx: Int): Int {
            return (2 * baseIdx - this) - 1
        }
    }

    fun part1(input: List<String>): Long {
        val inputProcess = input.toMutableList()
        val images = mutableListOf<Image>()
        while (inputProcess.isNotEmpty()) {
            val block = inputProcess.takeWhile { it.isNotBlank() }

            // Remove entries
            for (i in 0 until block.size) {
                inputProcess.removeFirst()
            }
            // Remove following separator
            inputProcess.removeFirstOrNull()

            images.add(Image(block))
        }

        return images.sumOf { it.findMirrorValue() }
    }

    fun part2(input: List<String>): Long {
        val inputProcess = input.toMutableList()
        val images = mutableListOf<Image>()
        while (inputProcess.isNotEmpty()) {
            val block = inputProcess.takeWhile { it.isNotBlank() }

            // Remove entries
            for (i in 0 until block.size) {
                inputProcess.removeFirst()
            }
            // Remove following separator
            inputProcess.removeFirstOrNull()

            images.add(Image(block))
        }

        return images.sumOf { it.findSmudgeMirrorValue() }
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput), 405L)
    check(part2(testInput), 400L)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}