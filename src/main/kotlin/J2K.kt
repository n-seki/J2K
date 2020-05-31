import generate.ClassGenerator
import parser.JsonParser
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("""
           |You need pass args.
           |    1. path to json file
           |    2. Kotlin class name you want
           |    3. path to out file (optional)
           """.trimMargin())
        exitProcess(1)
    }
    val jsonFile = File(args[0])
    val className = args[1]
    val kotlinParts = JsonParser(className).execute(jsonFile)
    if (args.size >= 3) {
        val outFile = File(args[2])
        ClassGenerator(kotlinParts, outFile).execute()
    } else {
        ClassGenerator(kotlinParts).execute()
    }
}