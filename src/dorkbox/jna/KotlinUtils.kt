package dorkbox.jna

import java.io.File
import java.util.concurrent.*

internal object KotlinUtils {
    /**
     * Reads the contents of the supplied input stream into a list of lines.
     *
     * @return Always returns a list, even if the file does not exist, or there are errors reading it.
     */
    fun readLines(file: File): List<String> {
        return file.readLines()
    }

    fun toInteger(string: String?): Int? {
        return string?.toIntOrNull()
    }

    /**
     * Executes the given command and returns its output.
     *
     * This is based on an aggregate of the answers provided here: [https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code]
     */
    fun execute(vararg args: String): String {
        return ProcessBuilder(args.toList())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .apply { waitFor(60, TimeUnit.SECONDS) }
            .inputStream.bufferedReader().readText().trim()
    }
}
