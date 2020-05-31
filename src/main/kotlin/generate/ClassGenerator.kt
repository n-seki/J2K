package generate

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import parts.KClassType
import parts.KProperty
import java.io.File

class ClassGenerator(
    private val classList: List<KClassType.KClass>,
    private val out: File? = null
) {

    fun execute() {
        for (clazz in classList) {
            generateClassFile(clazz)
        }
    }

    private fun generateClassFile(kClass: KClassType.KClass) {
        val parameters = kClass.member.map { it.toParameterSpec() }
        val constructor = FunSpec.constructorBuilder()
            .addParameters(parameters)
            .build()
        val properties = kClass.member.map { it.toDataClassPropertySpec() }
        val dataClass = TypeSpec.classBuilder(kClass.className)
            .primaryConstructor(constructor)
            .addProperties(properties)
            .addModifiers(KModifier.DATA).build()
        val file = FileSpec.builder("", kClass.className)
            .addType(dataClass)
            .build()

        if (out != null) {
            file.writeTo(out)
        } else {
            file.writeTo(System.out)
        }
    }

    private fun KProperty.toParameterSpec(): ParameterSpec {
        return ParameterSpec.builder(name, type.toTypeName()).build()
    }

    private fun KProperty.toDataClassPropertySpec(): PropertySpec {
        return PropertySpec.builder(name, type.toTypeName()).initializer(name).build()
    }

    private fun KClassType.toTypeName(): TypeName {
        return when (this) {
            KClassType.KString -> STRING
            KClassType.KLong -> LONG
            KClassType.KDouble -> DOUBLE
            KClassType.KBoolean -> BOOLEAN
            is KClassType.KClass -> ClassName("", className)
            is KClassType.KList -> {
                val className = kClassType.toTypeName()
                LIST.parameterizedBy(className)
            }
        }
    }
}