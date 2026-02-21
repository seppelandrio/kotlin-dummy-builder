package io.github.seppelandrio.kotlindummybuilder

import io.github.seppelandrio.kotlindummybuilder.lib.TypeReference
import io.github.seppelandrio.kotlindummybuilder.lib.buildDummy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Creates a dummy instance of the specified type [T] with default values.
 *
 * Default usage:
 * ```kotlin
 * val myDummy = default<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = default<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = default<MyClass>(
 *   argumentOverwrites = setOf(
 *     ArgumentOverwrite(MyClass::someProperty, "CustomValue"),
 *     ArgumentOverwrite(MyClass::anotherProperty, 42),
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = default<MyClass>(
 *   typeOverwrites = setOf(
 *     TypeOverwrite(String::class) { "OverwrittenString" },
 *     TypeOverwrite(Int::class) { Random.nextInt(0, 5) },
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> default(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Set<ArgumentOverwrite<T, *>> = emptySet(),
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = default<T>(
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites.associate { it.argument.name to it.value },
    typeOverwrites = typeOverwrites,
)

/**
 * Creates a dummy instance of the specified type [T] with default values.
 * If you want to specify argument or type overwrites and want to be type safe as possible use the typed version of [default].
 * This method is more flexible as you can overwrite arguments based on their name even though their property is not public.
 *
 * Default usage:
 * ```kotlin
 * val myDummy = default<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = default<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = default<MyClass>(
 *   argumentOverwrites = mapOf(
 *     "someProperty" to "CustomValue",
 *     "anotherProperty" to 42,
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = default<MyClass>(
 *   typeOverwrites = setOf(
 *     TypeOverwrite(String::class) { "OverwrittenString" },
 *     TypeOverwrite(Int::class) { Random.nextInt(0, 5) },
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> default(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = buildDummy(
    typeReference = object : TypeReference<T>() {},
    randomize = false,
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites,
    typeOverwrites = typeOverwrites,
)

/**
 * Creates a dummy instance of the specified type [T] with random values.
 *
 * Default usage:
 * ```kotlin
 * val myDummy = random<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = random<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = random<MyClass>(
 *   argumentOverwrites = setOf(
 *     ArgumentOverwrite(MyClass::someProperty, "CustomValue"),
 *     ArgumentOverwrite(MyClass::anotherProperty, 42),
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = random<MyClass>(
 *   typeOverwrites = setOf(
 *     TypeOverwrite(String::class) { "OverwrittenString" },
 *     TypeOverwrite(Int::class) { Random.nextInt(0, 5) },
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> random(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Set<ArgumentOverwrite<T, *>> = emptySet(),
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = random<T>(
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites.associate { it.argument.name to it.value },
    typeOverwrites = typeOverwrites,
)

/**
 * Creates a dummy instance of the specified type [T] with random values.
 * If you want to specify argument or type overwrites and want to be type safe as possible use the typed version of [default].
 * This method is more flexible as you can overwrite arguments based on their name even though their property is not public.
 *
 * Default usage:
 * ```kotlin
 * val myDummy = random<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = random<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = random<MyClass>(
 *   argumentOverwrites = mapOf(
 *     "someProperty" to "CustomValue",
 *     "anotherProperty" to 42,
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = random<MyClass>(
 *   typeOverwrites = setOf(
 *     TypeOverwrite(String::class) { "OverwrittenString" },
 *     TypeOverwrite(Int::class) { Random.nextInt(0, 5) },
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> random(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = buildDummy(
    typeReference = object : TypeReference<T>() {},
    randomize = true,
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites,
    typeOverwrites = typeOverwrites,
)

/**
 * Should not be used as the reified dummy methods are preferred.
 */
fun <T : Any> buildDummy(
    typeReference: TypeReference<T>,
    randomize: Boolean,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>>,
): T = buildDummy(
    type = typeReference.type,
    randomize = randomize,
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    typeOverwrites = typeOverwrites,
    argumentOverwrites = argumentOverwrites,
)

class ArgumentOverwrite<T, V>(
    val argument: KProperty1<T, V>,
    val value: V,
)

class TypeOverwrite<T : Any>(
    val type: KClass<T>,
    val getValue: () -> T,
) {
    override fun equals(other: Any?): Boolean = other is TypeOverwrite<*> && type == other.type

    override fun hashCode(): Int = type.hashCode()
}
