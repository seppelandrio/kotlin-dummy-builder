package io.github.seppelandrio.kotlindummybuilder.lib

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.superclasses

/**
 * Finds a concrete subclass of this [KClass] within the specified package.
 *
 * @param packageNameForChildClassLookup The package name to search for subclasses.
 * @param randomize Whether to randomize the selection of the concrete subclass if multiple are found. If false, the first found subclass will be returned.
 * @return If randomize is false, the first concrete subclass sorted by qualified name of this [KClass] found in the specified package. If randomize is true, a random concrete subclass of this [KClass] found in the specified package. If no concrete subclass is found, an [IllegalArgumentException] is thrown.
 * @throws IllegalArgumentException If no concrete subclass is found.
 */
internal fun KClass<*>.concreteSubclass(packageNameForChildClassLookup: String, randomize: Boolean): KClass<*> = getSubclassRelations(packageNameForChildClassLookup)[this]
    ?.let { concreteSubclasses -> if(randomize) concreteSubclasses.randomOrNull() else concreteSubclasses.sortedBy { it.qualifiedName }.firstOrNull() }
    ?: throw IllegalArgumentException(
        "Cannot create dummy for abstract type $this as no concrete subclass " +
            "could be found in package $packageNameForChildClassLookup. " +
            "Please consider providing a custom packageNameForChildClassLookup " +
            "or a property or class overwrite to help the dummy framework.",
    )

private val subtypesCachePerPackage: MutableMap<String, Map<KClass<*>, Set<KClass<*>>>> = mutableMapOf()

private fun getSubclassRelations(packageName: String): Map<KClass<*>, Set<KClass<*>>> = subtypesCachePerPackage.getOrPut(packageName) {
    val path = packageName.replace('.', '/')
    Thread
        .currentThread()
        .contextClassLoader!!
        .getResources(path)
        .asSequence()
        .flatMap { resource -> findClasses(File(resource.file), packageName) }
        .filter { clazz -> clazz.visibility != KVisibility.PRIVATE && !clazz.isSealed && !clazz.isAbstract }
        .flatMap { clazz -> runCatching { clazz.superclasses.map { it to clazz } }.getOrDefault(emptyList()) }
        .groupBy { (superclass, _) -> superclass }
        .mapValues { (_, pairs) -> pairs.mapTo(mutableSetOf()) { (_, subclass) -> subclass } }
}

private fun findClasses(
    directory: File,
    packageName: String,
): Sequence<KClass<*>> {
    if (!directory.exists()) return emptySequence()

    return directory
        .listFiles()
        ?.asSequence()
        ?.flatMap { file ->
            when {
                file.isDirectory -> {
                    findClasses(file, packageName + "." + file.name)
                }

                file.name.endsWith(".class") -> {
                    try {
                        sequenceOf(Class.forName(packageName + '.' + file.name.substring(0, file.name.length - ".class".length)).kotlin)
                    } catch (_: ExceptionInInitializerError) {
                        emptySequence()
                    }
                }

                else -> {
                    emptySequence()
                }
            }
        }.orEmpty()
}
