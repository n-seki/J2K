import generate.ClassGenerator
import org.junit.Test
import parser.JsonParser
import java.io.File

internal class JsonParserTest {

    @Test
    fun test() {
        val file = File("./src/main/resources/sample/sample.json")
        val parts = JsonParser("MyClass").execute(file)
        ClassGenerator(parts).execute()
    }
}