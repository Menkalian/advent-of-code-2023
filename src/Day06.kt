import kotlin.math.*

fun main() {
    fun solvePart1(record: Long, fullTime: Long): Long {
        // d = (t - x) * x = x^2 - tx
        // 2 Nst -> 0, t
        // max ist in der Mitte
        // Finde Schnittpunkte mit y = r
        // - x^2 + tx - r = 0
        // x1/2 = [-t +/- sqrt(t^2 + 4 r)] / 2

        val discriminant = sqrt(((fullTime * fullTime) - 4 * record).toDouble())
        val x1 = ((-fullTime + discriminant) / -2).toLong().absoluteValue + 1
        var x2float = (-fullTime - discriminant) / -2
        if (x2float % 1.0 == 0.0) {
            // Only exclude upper bound if we do not win with it.
            x2float -= 1
        }
        val x2 = x2float.toLong().absoluteValue // upper bound exclusive

        val winPossibilities = (x1 - x2).absoluteValue + 1
        println("Time: $fullTime ; Record: $record ; Possibilities: $winPossibilities")
        return winPossibilities
    }

    fun part1(input: List<String>): Long {
        val times = input[0]
            .substringAfter(':')
            .trim()
            .split("\\s+".toRegex())
            .map { it.toLong() }
        val records = input[1]
            .substringAfter(':')
            .trim()
            .split("\\s+".toRegex())
            .map { it.toLong() }
        return times.zip(records)
            .map { solvePart1(it.second, it.first) }
            .reduceRight { l, acc -> l * acc }
    }
    
    fun part2(input: List<String>): Long {
        val time = input[0]
            .replace(" ", "")
            .substringAfter(":")
            .toLong()
        val record = input[1]
            .replace(" ", "")
            .substringAfter(":")
            .toLong()
        return solvePart1(record, time)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput), 288L)
    check(part2(testInput), 71503L)
    
    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}