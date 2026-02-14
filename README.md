# Kotlin Dummy Builder
[![version](https://img.shields.io/github/v/release/seppelandrio/kotlin-dummy-builder?color=informational&include_prereleases&label=latest%20release)](https://github.com/seppelandrio/kotlin-dummy-builder/releases)
[![license](https://img.shields.io/github/license/seppelandrio/kotlin-dummy-builder?color=yellow)](https://www.apache.org/licenses/LICENSE-2.0)
[![CI](https://github.com/seppelandrio/kotlin-dummy-builder/actions/workflows/test.yml/badge.svg)](https://github.com/seppelandrio/kotlin-dummy-builder/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.seppelandrio.kotlindummybuilder/kotlin-dummy-builder.svg)](https://search.maven.org/artifact/io.github.seppelandrio.kotlindummybuilder/kotlin-dummy-builder/)

A lightweight Kotlin/ Java library for generating dummy objects based on reflection for testing and prototyping purposes.

This library recursively creates dummy instances of classes by using reflection to call their constructors with default or dummy values for their parameters.
It supports various types, including primitives, collections, and custom classes.
You can also customize the generated dummy objects by providing specific values for certain properties or types.
For a complete list of supported types and features, please refer to the [features section](#features).

## Installation
All you need to get started is to add a dependency to `Kotlin Dummy Builder` in your project:

| Approach   | Instruction                                                                                                                                                                                |
|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Gradle     | `testImplementation "io.github.seppelandrio:kotlin-dummy-builder:x.y.z"`                                                                                                                   |
| Gradle Kts | `testImplementation("io.github.seppelandrio:kotlin-dummy-builder:x.y.z")`                                                                                                                  |
| Maven      | `<dependency>`<br>`<groupId>io.github.seppelandrio</groupId>`<br>`<artifactId>kotlin-dummy-builder</artifactId>`<br>`<version>x.y.z</version>`<br>`<scope>test</scope>`<br>`</dependency>` |

## Usage
> ℹ️ You can either generate dummies with fixed/ default values by calling the `fixedDummy` function or with random values by calling the `randomDummy` function.
> Both functions have the same parameters and behavior, except for the values they generate.
> 
> For the following section we will use `fixedDummy` for demonstration purposes, but the same applies to `randomDummy` as well.

Default usage
```kotlin
val myDummy = fixedDummy<MyClass>()
```

To customize constructor arguments you can either use the copy operator of data classes (preferred)
```kotlin
val myDummy = fixedDummy<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
```

or use `argumentOverwrites` to overwrite constructor arguments either by property reference or by name
```kotlin
val myDummy = fixedDummy<MyClass>(
    argumentOverwrites = setOf(
        ArgumentOverwrite(MyClass::someProperty, "CustomValue"),
        ArgumentOverwrite(MyClass::anotherProperty, 42),
    ),
)

val myDummy2 = fixedDummy<MyClass>(
    argumentOverwrites = mapOf(
        "someProperty" to "CustomValue",
        "anotherProperty" to 42,
    ),
)
```

To provide specific implementations for certain types across every (nested) property in this dummy, use `typeOverwrites`
```kotlin
val myDummy = fixedDummy<MyClass>(
  typeOverwrites = setOf(
    TypeOverwrite(String::class, "OverwrittenString"),
  ),
)
```

When using argument and type overwrites together, argument overwrites take precedence.

If your type or its nested properties are abstract classes or interfaces,
you may need to specify `packageNameForChildClassLookup` to help the dummy builder find concrete implementations.
By default, it uses the package of the provided class.

## Features

This library generates dummy by calling constructors based on reflection and so it is capable of generating dummy for all nested classes and properties as long as they have a constructor that can be called with dummy values.

### Simple Types
The library supports the following types out of the box

| Type                 | Fixed/ Default Value                                  |
|----------------------|-------------------------------------------------------|
| Boolean              | `false`                                               |
| Byte                 | `0`                                                   |
| Short                | `0`                                                   |
| Int                  | `0`                                                   |
| Long                 | `0L`                                                  |
| Float                | `0.0f`                                                |
| Double               | `0.0`                                                 |
| Char                 | `'a'`                                                 |
| String               | `""`                                                  |
| BigInteger           | `BigInteger.ZERO`                                     |
| BigDecimal           | `BigDecimal.ZERO`                                     |
| LocalDate            | `LocalDate.MIN`                                       |
| LocalTime            | `LocalTime.MIN`                                       |
| ZoneId               | `ZoneId.of("UTC")`                                    |
| ZoneOffset           | `ZoneOffset.MAX`                                      |
| Instant              | `Instant.MIN`                                         |
| LocalDateTime        | `LocalDateTime.MIN`                                   |
| OffsetTime           | `OffsetTime.MIN`                                      |
| OffsetDateTime       | `OffsetDateTime.MIN`                                  |
| ZonedDateTime        | `ZonedDateTime.of(LocalDateTime.MIN, ZoneOffset.MAX)` |
| java.time.Duration   | `Duration.ZERO`                                       |
| kotlin.time.Duration | `Duration.ZERO`                                       |
| KClass\<T>           | `T::class`                                            |
| Class\<T>            | `T::class.java`                                       |
| Enum                 | `first value of the enum`                             |

## Collection Types
The library supports the following collection types out of the box

| Type                | Default Value          |
|---------------------|------------------------|
| ByteArray           | `ByteArray(0)`         |
| CharArray           | `CharArray(0)`         |
| ShortArray          | `ShortArray(0)`        |
| IntArray            | `IntArray(0)`          |
| LongArray           | `LongArray(0)`         |
| FloatArray          | `FloatArray(0)`        |
| DoubleArray         | `DoubleArray(0)`       |
| BooleanArray        | `BooleanArray(0)`      |
| Array\<T>           | `emptyArray<T>()`      |
| Iterable\<T>        | `mutableListOf<T>()`   |
| Collection\<T>      | `mutableListOf<T>()`   |
| (Mutable)List\<T>   | `mutableListOf<T>()`   |
| (Mutable)Set\<T>    | `mutableSetOf<T>()`    |
| Stream<T>           | `Stream.empty<T>()`    |
| (Mutable)Map\<K, V> | `mutableMapOf<K, V>()` |


## Function Types
The library supports function types by generating a lambda that returns a dummy value of the return type.

> **Limitation**
> 
> Kotlin reflection looses type parameters for functions with more than 2 parameters, so the library will throw an exception when generating dummies for them.

## Custom Types
For custom types, the library will try to find a constructor and call it with dummy values for its parameters.

Additionally, it supports the following language features
- **Abstract Classes and Interfaces:** if the type is an abstract class or an interface, it will try to find a concrete implementation and use it to generate the dummy
- **Sealed Classes and Interfaces:** if the type is a sealed class, it will use a non-abstract subclass and use it to generate the dummy
- **Generics:** if the type has generic parameters, it will infer the type arguments from the context and use them to generate the dummy
- **Value Classes:** if the type is a value class, it will generate a dummy for its underlying type and use it to create an instance of the value class
- **Objects:** if the type is an object, it will return the object instance
- **Private Constructors:** if the type has a private constructor, it will try to make it accessible and use it to generate the dummy
