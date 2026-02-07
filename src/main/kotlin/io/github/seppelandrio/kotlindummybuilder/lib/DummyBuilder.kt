package io.github.seppelandrio.kotlindummybuilder.lib

import java.lang.reflect.Array
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSuperclassOf

@Suppress("UNCHECKED_CAST")
internal fun <T> buildDummy(
    type: KType,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Map<KClass<*>, Any>,
): T {
    if (type.isMarkedNullable) return null as T

    val kClass = when (val classifier = type.classifier) {
        is KClass<*> -> classifier
        else -> throw IllegalArgumentException("Cannot create dummy for type $type as type is not a KClass")
    } as KClass<T & Any>

    fun <S> buildDummy(type: KType): S = buildDummy(type, packageNameForChildClassLookup, emptyMap(), typeOverwrites)

    @Suppress("USELESS_CAST")
    return when {
        kClass in typeOverwrites -> typeOverwrites[kClass]
        kClass == Boolean::class -> false
        kClass == Byte::class -> 0.toByte()
        kClass == Short::class -> 0.toShort()
        kClass == Int::class -> 0
        kClass == Long::class -> 0L
        kClass == BigDecimal::class -> BigDecimal.ZERO
        kClass == Double::class -> 0.0
        kClass == Float::class -> 0.0f
        kClass == Char::class -> 'a'
        kClass == String::class -> ""
        kClass == LocalDateTime::class -> LocalDateTime.of(1970, 1, 1, 0, 0)
        kClass == Duration::class -> Duration.ZERO
        kClass == LocalDate::class -> LocalDate.of(1970, 1, 1)
        kClass == Instant::class -> Instant.ofEpochSecond(0L)
        kClass == ByteArray::class -> ByteArray(0)
        kClass == CharArray::class -> CharArray(0)
        kClass == ShortArray::class -> ShortArray(0)
        kClass == IntArray::class -> IntArray(0)
        kClass == LongArray::class -> LongArray(0)
        kClass == FloatArray::class -> FloatArray(0)
        kClass == DoubleArray::class -> DoubleArray(0)
        kClass == BooleanArray::class -> BooleanArray(0)
        kClass == KClass::class -> checkNotNull(type.arguments.first().type?.classifier) { "Cannot create dummy for $type with unspecified type argument." }
        kClass.java.isEnum -> kClass.java.enumConstants.first()
        kClass.objectInstance != null -> kClass.objectInstance
        kClass.java.isArray -> Array.newInstance((type.arguments.first().type?.classifier as KClass<*>).java, 0)
        kClass.isSuperclassOf(MutableList::class) -> mutableListOf<Any?>()
        kClass.isSuperclassOf(MutableSet::class) -> mutableSetOf<Any?>()
        kClass.isSuperclassOf(MutableMap::class) -> mutableMapOf<Any?, Any?>()
        kClass == Function::class || kClass == Function0::class -> ({ buildDummy<Any?>(type.arguments.first().type!!) } as () -> Any?)
        kClass == Function1::class -> ({ _: Any? -> buildDummy<Any?>(type.arguments[1].type!!) } as (Any?) -> Any?)
        kClass == Function2::class -> ({ _: Any?, _: Any? -> buildDummy<Any?>(type.arguments[2].type!!) } as (Any?, Any?) -> Any?)
        Function::class.isSuperclassOf(kClass) -> throw IllegalArgumentException("Cannot create dummy for function type as kotlin does not capture the generic type information: $type.")
        kClass.isSealed -> buildDummy(kClass.sealedSubclasses.first().createType())
        kClass.isAbstract -> buildDummy(kClass.firstConcreteSubclass(packageNameForChildClassLookup).createType())
        else -> kClass.callConstructor(type, packageNameForChildClassLookup, argumentOverwrites, typeOverwrites)
    } as T
}
