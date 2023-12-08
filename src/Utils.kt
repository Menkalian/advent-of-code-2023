import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> check(actual: T, expected: T) {
    if (actual != expected) {
        throw RuntimeException("Check failed: actual was $actual while $expected was expected.")
    }
}

// kleinstes gemeinsames Vielfaches
fun lcm(ints: List<Long>): Long {
    val divisor = gcd(ints)
    return divisor * ints
        .map {
            it / divisor
        }
        .reduce { acc, l ->
            acc * l
        }
}

fun gcd(ints: List<Long>): Long {
    return ints.reduce { acc, l -> gcd(acc, l) }
}

fun gcd(a: Long, b: Long): Long {
    if (b == 0L) {
        return a
    } else {
        return gcd(b, a % b)
    }
}
