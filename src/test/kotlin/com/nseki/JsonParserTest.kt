package com.nseki

import com.nseki.generate.ClassGenerator
import org.junit.Test
import com.nseki.parser.JsonParser
import java.io.File

internal class JsonParserTest {

    @Test
    fun test() {
        val file = File("./src/main/resources/sample/sample.json")
        println(file.absolutePath)
        val parts = JsonParser("MyClass").execute(file)
        ClassGenerator(parts, "MyClass").execute()
    }
}