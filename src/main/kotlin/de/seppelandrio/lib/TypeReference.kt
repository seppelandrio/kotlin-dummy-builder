package de.seppelandrio.lib

import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.kotlinFunction

abstract class TypeReference<T> {
    // val type: KType = this::class.supertypes.single().arguments.single().type!!
    // workaround needed until closed: https://youtrack.jetbrains.com/projects/KT/issues/KT-47030/Type-argument-from-outer-context-not-correctly-captured-by-local-classes-or-local-anonymous-objects
    val type: KType = (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments.single().toKType()

    private fun Type.toKType() = when (this) {
        is Class<*> -> {
            when {
                isArray -> kotlin.createType(listOfNotNull(componentType?.takeUnless { it.isPrimitive }?.toKTypeProjection()))
                else -> kotlin.createType(List(typeParameters.size) { KTypeProjection.STAR })
            }
        }

        is ParameterizedType -> {
            (rawType as Class<*>).kotlin.createType(actualTypeArguments.map { it.toKTypeProjection() })
        }

        is GenericArrayType -> {
            Array::class.createType(listOf(genericComponentType.toKTypeProjection()))
        }

        is TypeVariable<*> -> {
            toKType()
        }

        else -> {
            throw NotImplementedError()
        }
    }

    private fun Type.toKTypeProjection(): KTypeProjection = when (this) {
        is WildcardType -> {
            when {
                lowerBounds.isNotEmpty() -> KTypeProjection(KVariance.IN, lowerBounds[0].toKType())
                upperBounds.isNotEmpty() -> KTypeProjection(KVariance.OUT, upperBounds[0].toKType())
                else -> KTypeProjection(KVariance.INVARIANT, toKType())
            }
        }

        else -> {
            KTypeProjection(KVariance.INVARIANT, toKType())
        }
    }

    private fun TypeVariable<*>.toKType() = when (val decl = this.genericDeclaration) {
        is Class<*> -> {
            decl.kotlin.typeParameters
                .first { it.name == this.name }
                .createType()
        }

        is Method -> {
            checkNotNull(decl.kotlinFunction)
                .typeParameters
                .first { it.name == this.name }
                .createType()
        }

        else -> {
            throw NotImplementedError()
        }
    }
}
