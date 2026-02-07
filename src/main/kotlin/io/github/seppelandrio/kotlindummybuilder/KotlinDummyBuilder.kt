package io.github.seppelandrio.kotlindummybuilder

import io.github.seppelandrio.kotlindummybuilder.lib.TypeReference
import io.github.seppelandrio.kotlindummybuilder.lib.buildDummy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Creates a dummy instance of the specified type [T].
 *
 * Default usage:
 * ```kotlin
 * val myDummy = dummy<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = dummy<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = dummy<MyClass>(
 *   argumentOverwrites = setOf(
 *     ArgumentOverwrite(MyClass::someProperty, "CustomValue"),
 *     ArgumentOverwrite(MyClass::anotherProperty, 42),
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = dummy<MyClass>(
 *   typeOverwrites = mapOf(
 *     String::class to "OverwrittenString",
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> dummy(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Set<ArgumentOverwrite<T, *>> = emptySet(),
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = dummy<T>(
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites.associate { it.argument.name to it.value },
    typeOverwrites = typeOverwrites,
)

/**
 * Creates a dummy instance of the specified type [T].
 * If you want to specify argument or type overwrites and want to be type safe as possible use the typed version of [dummy].
 * This method is more flexible as you can overwrite arguments based on their name even though their property is not public.
 *
 * Default usage:
 * ```kotlin
 * val myDummy = dummy<MyClass>()
 * ```
 *
 * To customize constructor arguments you can either use the copy operator of data classes (preferred):
 * ```kotlin
 * val myDummy = dummy<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
 * ```
 *
 * or use [argumentOverwrites]:
 * ```kotlin
 * val myDummy = dummy<MyClass>(
 *   argumentOverwrites = mapOf(
 *     "someProperty" to "CustomValue",
 *     "anotherProperty" to 42,
 *   ),
 * )
 * ```
 *
 * To provide specific implementations for certain types across every (nested) property in this dummy, use [typeOverwrites]:
 * ```kotlin
 * val myDummy = dummy<MyClass>(
 *   typeOverwrites = setOf(
 *     TypeOverwrite(String::class, "OverwrittenString"),
 *   ),
 * )
 * ```
 *
 * When using argument and type overwrites together, argument overwrites take precedence.
 *
 * If your type or its nested properties are abstract classes or interfaces, you may need to specify
 * [packageNameForChildClassLookup] to help the dummy builder find concrete implementations. By default, it uses the package of [T].
 */
inline fun <reified T : Any> dummy(
    packageNameForChildClassLookup: String = T::class.java.packageName,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>> = emptySet(),
): T = dummy(
    typeReference = object : TypeReference<T>() {},
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites,
    typeOverwrites = typeOverwrites.associate { it.type to it.value },
)

/**
 * Should not be used as the reified dummy method is preferred.
 */
fun <T : Any> dummy(
    typeReference: TypeReference<T>,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Map<KClass<*>, Any>,
): T = buildDummy(
    type = typeReference.type,
    packageNameForChildClassLookup = packageNameForChildClassLookup,
    argumentOverwrites = argumentOverwrites,
    typeOverwrites = typeOverwrites,
)

data class ArgumentOverwrite<T, V>(
    val argument: KProperty1<T, V>,
    val value: V,
)

data class TypeOverwrite<T : Any>(
    val type: KClass<T>,
    val value: T,
)
