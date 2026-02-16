package io.github.seppelandrio.kotlindummybuilder.lib

import io.github.seppelandrio.kotlindummybuilder.TypeOverwrite
import java.lang.reflect.Array
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.Currency
import java.util.Locale
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSuperclassOf
import kotlin.time.Duration.Companion.nanoseconds

@Suppress("UNCHECKED_CAST")
internal fun <T> buildDummy(
    type: KType,
    randomize: Boolean,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>>,
): T {
    if (type.isMarkedNullable && (!randomize || Random.nextBoolean())) return null as T

    val kClass = when (val classifier = type.classifier) {
        is KClass<*> -> classifier
        else -> throw IllegalArgumentException("Cannot create dummy for type $type as type is not a KClass")
    } as KClass<T & Any>

    fun <S> buildDummy(type: KType): S = buildDummy(type, randomize, packageNameForChildClassLookup, emptyMap(), typeOverwrites)

    fun <S : Any> buildDummy(clazz: KClass<S>): S = buildDummy(clazz.createType())

    fun <S : Any> buildDummy(typeReference: TypeReference<S>): S = buildDummy(typeReference.type)

    fun nextCollectionSizeOr0(): Int = if (randomize) Random.nextInt(0, 100) else 0

    fun <S> Collection<S>.randomOrFirst(): S = if (randomize) random() else first()

    fun buildArray(): Any? {
        val size = nextCollectionSizeOr0()
        val type = type.argumentType(0)
        val array = Array.newInstance((type.classifier as KClass<*>).java, size)
        for (i in 0 until size) {
            Array.set(array, i, buildDummy(type))
        }
        return array
    }

    val typeOverwrite = typeOverwrites.find { it.type == kClass }
    @Suppress("USELESS_CAST")
    return when {
        typeOverwrite != null -> typeOverwrite.getValue()
        kClass == Boolean::class -> if (randomize) Random.nextBoolean() else false
        kClass == Byte::class -> if (randomize) Random.nextBytes(1)[0] else 0.toByte()
        kClass == Short::class -> if (randomize) Random.nextInt().toShort() else 0.toShort()
        kClass == Int::class -> if (randomize) Random.nextInt() else 0
        kClass == Long::class -> if (randomize) Random.nextLong() else 0L
        kClass == Float::class -> if (randomize) Random.nextFloat() else 0.0f
        kClass == Double::class -> if (randomize) Random.nextDouble() else 0.0
        kClass == Char::class -> if (randomize) supportedChars.random() else 'a'
        kClass == String::class -> buildDummy(object : TypeReference<List<Char>>() {}).joinToString("")
        kClass == BigInteger::class -> BigInteger.valueOf(buildDummy(Long::class))
        kClass == BigDecimal::class -> BigDecimal.valueOf(buildDummy(Long::class))
        kClass == LocalDate::class -> if (randomize) LocalDate.ofEpochDay(nextLong(ChronoField.EPOCH_DAY)) else LocalDate.ofEpochDay(0)
        kClass == LocalTime::class -> if (randomize) LocalTime.ofNanoOfDay(nextLong(ChronoField.NANO_OF_DAY)) else LocalTime.ofSecondOfDay(0)
        kClass == ZoneId::class -> if (randomize) ZoneId.of(ZoneId.getAvailableZoneIds().random()) else ZoneId.of("UTC")
        kClass == ZoneOffset::class -> if (randomize) ZoneOffset.ofTotalSeconds(nextLong(ChronoField.OFFSET_SECONDS).toInt()) else ZoneOffset.UTC
        kClass == Instant::class -> if (randomize) Instant.ofEpochSecond(Random.nextLong(Instant.MIN.epochSecond..Instant.MAX.epochSecond)) else Instant.ofEpochSecond(0)
        kClass == LocalDateTime::class -> LocalDateTime.of(buildDummy(LocalDate::class), buildDummy(LocalTime::class))
        kClass == OffsetTime::class -> OffsetTime.of(buildDummy(LocalTime::class), buildDummy(ZoneOffset::class))
        kClass == OffsetDateTime::class -> OffsetDateTime.of(buildDummy(LocalDateTime::class), buildDummy(ZoneOffset::class))
        kClass == ZonedDateTime::class -> ZonedDateTime.of(buildDummy(LocalDateTime::class), buildDummy(ZoneOffset::class))
        kClass == Duration::class -> Duration.ofNanos(if (randomize) Random.nextLong(0..Long.MAX_VALUE) else 0)
        kClass == kotlin.time.Duration::class -> if (randomize) Random.nextLong(0..Long.MAX_VALUE).nanoseconds else 0.nanoseconds
        kClass == Currency::class -> if (randomize) Currency.getAvailableCurrencies().random() else Currency.getInstance("USD")
        kClass == Locale::class -> if (randomize) Locale.getAvailableLocales().random() else Locale.US
        kClass == KClass::class -> type.argumentTypeClassifierOfConcreteSubclassIfOut(0, packageNameForChildClassLookup, randomize)
        kClass == Class::class -> type.argumentTypeClassifierOfConcreteSubclassIfOut(0, packageNameForChildClassLookup, randomize).java
        kClass.java.isEnum -> kClass.java.enumConstants.toList().randomOrFirst()
        kClass.objectInstance != null -> kClass.objectInstance
        kClass == ByteArray::class -> ByteArray(nextCollectionSizeOr0()) { buildDummy(Byte::class) }
        kClass == CharArray::class -> CharArray(nextCollectionSizeOr0()) { buildDummy(Char::class) }
        kClass == ShortArray::class -> ShortArray(nextCollectionSizeOr0()) { buildDummy(Short::class) }
        kClass == IntArray::class -> IntArray(nextCollectionSizeOr0()) { buildDummy(Int::class) }
        kClass == LongArray::class -> LongArray(nextCollectionSizeOr0()) { buildDummy(Long::class) }
        kClass == FloatArray::class -> FloatArray(nextCollectionSizeOr0()) { buildDummy(Float::class) }
        kClass == DoubleArray::class -> DoubleArray(nextCollectionSizeOr0()) { buildDummy(Double::class) }
        kClass == BooleanArray::class -> BooleanArray(nextCollectionSizeOr0()) { buildDummy(Boolean::class) }
        kClass.java.isArray -> buildArray()
        kClass.isSuperclassOf(MutableList::class) -> List(nextCollectionSizeOr0()) { buildDummy<Any?>(type.argumentType(0)) }
        kClass.isSuperclassOf(MutableSet::class) -> HashSet<Any?>().apply { repeat(nextCollectionSizeOr0()) { add(buildDummy(type.argumentType(0))) } }
        kClass.isSuperclassOf(MutableMap::class) -> HashMap<Any?, Any?>().apply { repeat(nextCollectionSizeOr0()) { put(buildDummy(type.argumentType(0)), buildDummy(type.argumentType(1))) } }
        kClass.isSuperclassOf(Stream::class) -> Stream.builder<Any?>().apply { repeat(nextCollectionSizeOr0()) { add(buildDummy(type.argumentType(0))) } }.build()
        kClass == Function::class || kClass == Function0::class -> ({ buildDummy<Any?>(type.argumentType(0)) })
        kClass == Function1::class -> ({ _: Any? -> buildDummy<Any?>(type.argumentType(1)) }) as (Any?) -> Any?
        kClass == Function2::class -> ({ _: Any?, _: Any? -> buildDummy<Any?>(type.argumentType(2)) }) as (Any?, Any?) -> Any?
        Function::class.isSuperclassOf(kClass) -> throw IllegalArgumentException("Cannot create dummy for function type as kotlin does not capture the generic type information: $type.")
        kClass.isSealed -> buildDummy(kClass.sealedSubclasses.randomOrFirst())
        kClass.isAbstract -> buildDummy(kClass.concreteSubclass(packageNameForChildClassLookup, randomize))
        else -> kClass.createObject(type, randomize, packageNameForChildClassLookup, argumentOverwrites, typeOverwrites)
    } as T
}

private val supportedChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf(' ', '-', '_')

private fun KType.argumentType(index: Int): KType = arguments[index].type ?: Any::class.createType()

private fun KType.argumentTypeClassifierOfConcreteSubclassIfOut(
    index: Int,
    packageNameForChildClassLookup: String,
    randomize: Boolean,
): KClass<*> {
    val classifier = argumentType(index).classifier as KClass<*>
    return when (arguments[index].variance) {
        KVariance.OUT -> classifier.concreteSubclass(packageNameForChildClassLookup, randomize)
        else -> classifier
    }
}

private fun nextLong(chronoField: ChronoField): Long = Random.nextLong(chronoField.range().largestMinimum..chronoField.range().smallestMaximum)
