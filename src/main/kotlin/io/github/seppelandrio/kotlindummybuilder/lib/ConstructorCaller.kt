package io.github.seppelandrio.kotlindummybuilder.lib

import kotlin.collections.get
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.jvm.isAccessible

/**
 * Calls a matching constructor of this class with dummy values for all parameters,
 * applying [argumentOverwrites] and [typeOverwrites] as needed.
 */
fun <T : Any> KClass<T>.callConstructor(
    type: KType,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Map<KClass<*>, Any>,
): T {
    val constructor = constructors
        .sortedBy { it.parameters.size }
        .firstOrNull { argumentOverwrites.keys.all { propertyName -> propertyName in it.parameters.map { it.name } } }
        ?: throw IllegalArgumentException("Cannot construct test instance for type $type as no constructor matches the provided overwrites: ${argumentOverwrites.keys}")
    constructor.isAccessible = true
    val parameters = constructor.parameters.map { parameter ->
        argumentOverwrites[parameter.name] ?: buildDummy(parameter.resolvedType(this, type.arguments), packageNameForChildClassLookup, emptyMap(), typeOverwrites)
    }
    return try {
        constructor.call(*parameters.toTypedArray())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Failed to create test instance for type $this with constructor $constructor and parameters $parameters - looks like a framework bug",
            e,
        )
    }
}

/**
 * Resolves the type of this parameter for the given [kClass] and its [typeArguments].
 * This is needed to resolve generic type parameters to their actual types.
 */
private fun KParameter.resolvedType(
    kClass: KClass<*>,
    typeArguments: List<KTypeProjection>,
): KType = when (val classifier = type.classifier) {
    is KClass<*> -> type
    is KTypeParameter -> checkNotNull(resolveTypeParameter(classifier.name, kClass, typeArguments)) { "Cannot resolve type parameter ${classifier.name} for class $kClass" }
    else -> throw IllegalArgumentException("Cannot resolve type for parameter $this of class $kClass with arguments $typeArguments")
}

private fun resolveTypeParameter(
    name: String,
    kClass: KClass<*>,
    arguments: List<KTypeProjection>,
): KType? = kClass.typeParameters
    .zip(arguments)
    .find { (typeParameter, _) -> typeParameter.name == name }
    ?.let { (_, argument) -> argument.type }
