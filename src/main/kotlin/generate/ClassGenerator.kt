package generate

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import parts.KClassType
import parts.KProperty
import java.io.File

class ClassGenerator(
    private val classList: List<KClassType.KClass>,
    private val fileName: String,
    private val out: File? = null
) {

    fun execute() {
        val fileSpec = FileSpec.builder("", fileName)
        for (clazz in classList) {
            fileSpec.addType(generateClassFile(clazz))
        }
        val file = fileSpec.build()
        if (out != null) {
            file.writeTo(out)
        } else {
            file.writeTo(System.out)
        }
    }

    private fun generateClassFile(kClass: KClassType.KClass): TypeSpec {
        val parameters = kClass.member.map { it.toParameterSpec() }
        val constructor = FunSpec.constructorBuilder()
            .addParameters(parameters)
            .build()
        val properties = kClass.member.map { it.toDataClassPropertySpec() }
        return TypeSpec.classBuilder(kClass.className)
            .primaryConstructor(constructor)
            .addProperties(properties)
            .addModifiers(KModifier.DATA).build()
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