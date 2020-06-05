package com.nseki.parser

import com.squareup.moshi.JsonReader
import okio.Buffer
import com.nseki.parts.KClassType
import com.nseki.parts.KProperty
import java.io.File
import java.io.FileInputStream
import java.util.*

class JsonParser(private val className: String) {

    private val classList = ArrayDeque<KClassType.KClass>()

    fun execute(json: String): List<KClassType.KClass> {
        val jsonReader = JsonReader.of(Buffer().writeUtf8(json)).apply {
            isLenient = true
        }
        readObject(jsonReader, className)
        return classList.toList()
    }

    fun execute(file: File): List<KClassType.KClass> {
        val jsonReader = JsonReader.of(Buffer().readFrom(FileInputStream(file))).apply {
            isLenient = true
        }
        readObject(jsonReader, className)
        return classList.toList()
    }

    private fun readObject(
        jsonReader: JsonReader,
        className: String,
        inner: Boolean = false
    ) {
        val classMembers = mutableListOf<KProperty>()
        jsonReader.beginObject()
        while (jsonReader.hasNext() && jsonReader.peek() != JsonReader.Token.END_OBJECT) {
            if (jsonReader.peek() != JsonReader.Token.NAME) {
                break
            }
            val name = jsonReader.nextName()
            val property = when (jsonReader.peek()) {
                JsonReader.Token.BEGIN_OBJECT -> {
                    readObject(jsonReader, name.toClassName(), inner = true)
                    KProperty(
                        name.toCamelCase(),
                        KClassType.KClass(name.toClassName())
                    )
                }
                JsonReader.Token.BEGIN_ARRAY -> {
                    val listType = readArray(name, jsonReader)
                    KProperty(
                        name.toCamelCase(),
                        KClassType.KList(listType)
                    )
                }
                else -> {
                    val kType = jsonReader.readPrimitiveType()
                    KProperty(name.toCamelCase(), kType)
                }
            }
            classMembers += property
        }
        if (inner) {
            jsonReader.endObject()
        }
        if (hasClass(className)) {
            return
        }
        classList.addFirst(KClassType.KClass(className, classMembers))
    }

    private fun readArray(name: String, jsonReader: JsonReader): KClassType {
        jsonReader.beginArray()
        val listType = when (jsonReader.peek()) {
            JsonReader.Token.BEGIN_OBJECT -> {
                val className = name.toClassName(ignoreLastS = true)
                readObject(jsonReader, className, inner = true)
                KClassType.KClass(className)
            }
            else -> {
                jsonReader.readPrimitiveType()
            }
        }
        jsonReader.skipTo(JsonReader.Token.END_ARRAY)
        jsonReader.endArray()
        return listType
    }

    private fun JsonReader.readPrimitiveType(): KClassType {
        return when (peek()) {
            JsonReader.Token.STRING -> {
                nextString() // ignore
                KClassType.KString
            }
            JsonReader.Token.NUMBER -> {
                val strNum = nextString()
                if (strNum.contains(".")) {
                    KClassType.KDouble
                } else {
                    KClassType.KLong
                }
            }
            JsonReader.Token.BOOLEAN -> {
                nextBoolean() // ignore
                KClassType.KBoolean
            }
            JsonReader.Token.NULL -> {
                nextNull<String>() // ignore
                KClassType.KString // treat as String
            }
            JsonReader.Token.END_ARRAY -> {  // consider empty array
                KClassType.KString // treat as String
            }
            else -> throw IllegalStateException("Value is not primitive")
        }
    }

    private fun JsonReader.skipTo(token: JsonReader.Token) {
        when (peek()) {
            token -> return
            JsonReader.Token.NAME -> skipName()
            else -> skipValue()
        }
        skipTo(token)
    }

    private fun hasClass(className: String): Boolean {
        return classList.any { c -> c.className == className }
    }
}