package com.nseki.parts

sealed class KClassType {
    object KString : KClassType()
    object KLong : KClassType()
    object KDouble : KClassType()
    object KBoolean : KClassType()
    data class KClass(
        val className: String,
        val member: List<KProperty> = emptyList()
    ) : KClassType()
    data class KList(
        val kClassType: KClassType
    ): KClassType()
}