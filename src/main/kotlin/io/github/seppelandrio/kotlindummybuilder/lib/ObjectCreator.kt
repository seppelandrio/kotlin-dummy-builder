package io.github.seppelandrio.kotlindummybuilder.lib

import io.github.seppelandrio.kotlindummybuilder.TypeOverwrite
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

/**
 * Calls either a matching constructor or a companion object function of this class with dummy values for all parameters,
 * applying [argumentOverwrites] and [typeOverwrites] as needed.
 *
 * The function prioritizes by:
 * 1. public over non-public functions
 * 2. constructors over companion object functions
 * 3. functions with fewer parameters over functions with more parameters
 */
internal fun <T : Any> KClass<T>.createObject(
    type: KType,
    randomize: Boolean,
    packageNameForChildClassLookup: String,
    argumentOverwrites: Map<String, Any?>,
    typeOverwrites: Set<TypeOverwrite<*>>,
): T {
    val prioritizedConstructors = constructors
        .filter { it.hasAllArgumentOverwrites(argumentOverwrites) }
        .sortedBy { it.parameters.size }
    val prioritizedCompanionCreators by lazy {
        @Suppress("UNCHECKED_CAST")
        companionObject
            ?.memberFunctions
            ?.filter { !it.returnType.isMarkedNullable && it.returnType.classifier == this && it.hasAllArgumentOverwrites(argumentOverwrites) }
            ?.sortedBy { it.parameters.size }
            .orEmpty() as Collection<KFunction<T>>
    }
    val creatorFunction = prioritizedConstructors.firstOrNull { it.visibility == KVisibility.PUBLIC }
        ?: prioritizedCompanionCreators.firstOrNull { it.visibility == KVisibility.PUBLIC }
        ?: prioritizedConstructors.firstOrNull()
        ?: prioritizedCompanionCreators.firstOrNull()
        ?: throw IllegalArgumentException("Cannot construct test instance for type $type as no constructor or companion method matches the provided overwrites: ${argumentOverwrites.keys}")
    creatorFunction.isAccessible = true

    val typeParameters = resolveTypeParameters(this, type.arguments)
    val parameters = creatorFunction.parameters.map { parameter ->
        argumentOverwrites[parameter.name] ?: buildDummy(parameter.type.resolvedType(typeParameters), randomize, packageNameForChildClassLookup, emptyMap(), typeOverwrites)
    }
    return try {
        creatorFunction.call(*parameters.toTypedArray())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Failed to create test instance for type $this with function $creatorFunction and parameters $parameters - looks like a framework bug",
            e,
        )
    }
}

private fun KFunction<*>.hasAllArgumentOverwrites(argumentOverwrites: Map<String, Any?>): Boolean = argumentOverwrites.keys.all { propertyName -> propertyName in parameters.map { it.name } }

/**
 * Resolves the type of this parameter using the [typeParameters] of the enclosing class.
 * This is needed to resolve generic type parameters to their actual types.
 */
private fun KType.resolvedType(typeParameters: Map<String, KType?>): KType = when (val classifier = classifier) {
    is KClass<*> -> classifier.createType(arguments.map { it.copy(type = it.type?.resolvedType(typeParameters)) }, isMarkedNullable, annotations)
    is KTypeParameter -> checkNotNull(typeParameters[classifier.name]) { "Cannot resolve type parameter ${classifier.name} for parameter $this" }
    else -> throw IllegalArgumentException("Cannot resolve type for parameter $this with type parameters $typeParameters")
}

/**
 * Resolves the type parameters of [kClass] to their actual types based on the provided [arguments].
 *
 * @return a map of type parameter names to their resolved types
 */
private fun resolveTypeParameters(
    kClass: KClass<*>,
    arguments: List<KTypeProjection>,
): Map<String, KType?> = kClass.typeParameters
    .zip(arguments)
    .associate { (typeParameter, argument) -> typeParameter.name to argument.type }
