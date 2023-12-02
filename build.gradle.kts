import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers
import java.time.*
import java.util.Properties

plugins {
    kotlin("jvm") version "1.9.20"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

try {
    val localProperties = Properties()
    localProperties.load(rootProject.file("local.properties").inputStream())
    localProperties.forEach {
        rootProject.ext.set(it.key.toString(), it.value.toString())
    }
} catch (ex: Exception) {
    logger.warn("local.properties could not be found. Please specify your AoC session to allow automatic loading of the problems")
}

createTemplates()

// I do not want to run any explicit tasks to create the templates and download the input.
// Therefor I just do it in the gradle initialization, so it will be executed while syncing.
fun createTemplates() {
    val date = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
    val downloadedUntil = findLatestDayExisting()
    println("Files exist until day $downloadedUntil")

    if (date.monthValue == Month.DECEMBER.value
        && date.dayOfMonth > downloadedUntil
    ) {
        println("Downloading until ${date.dayOfMonth}")

        for (n in (downloadedUntil + 1)..date.dayOfMonth) {
            if (n > 25) {
                continue
            }
            println("Creating Day $n")
            createFiles(n)
            downloadInput(n, date.year)
        }
    }
}

fun findLatestDayExisting(): Int {
    return rootProject.file("src")
        .listFiles()
        ?.filter { it.isFile && it.nameWithoutExtension.startsWith("Day") && it.extension == "kt" }
        ?.map { it.nameWithoutExtension.removePrefix("Day").toIntOrNull() ?: 0 }
        ?.max() ?: 0
}

fun createFiles(n: Int) {
    val dayNumberString = n.toString().padStart(2, '0')
    val ktFile = File("src/Day$dayNumberString.kt")
    listOf(
        File("src/Day$dayNumberString.txt"),
        File("src/Day${dayNumberString}_test.txt"),
        ktFile,
    ).forEach {
        if (it.exists().not()) {
            it.parentFile.mkdirs()
            it.createNewFile()
        }
    }

    if (ktFile.readText().isBlank()) {
        ktFile.writeText("""
            fun main() {
                fun part1(input: List<String>): Long {
                    return 0L
                }
                
                fun part2(input: List<String>): Long {
                    return 0L
                }
                
                val testInput = readInput("Day${dayNumberString}_test")
                check(part1(testInput), 0L)
                check(part2(testInput), 0L)
                
                val input = readInput("Day${dayNumberString}")
                part1(input).println()
                part2(input).println()
            }
        """.trimIndent())
    }
}

fun downloadInput(n: Int, year: Int) {
    if (rootProject.ext.has("aoc-session")) {
        println("Querying input for $year::$n")
        val httpclient = HttpClient.newHttpClient()
        val response = httpclient.send<String>(
            HttpRequest.newBuilder(URI("https://adventofcode.com/$year/day/$n/input"))
                .header("Cookie", "session=${rootProject.ext["aoc-session"]}")
                .build()
        ) {
            println("Response: ${it.statusCode()}")
            BodySubscribers.ofString(Charsets.UTF_8)
        }

        File("src/Day${n.toString().padStart(2, '0')}.txt")
            .writeText(response.body())
    }
}
