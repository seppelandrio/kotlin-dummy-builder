# Kotlin Dummy Builder

[![version](https://img.shields.io/github/v/release/seppelandrio/kotlin-dummy-builder)](https://github.com/seppelandrio/kotlin-dummy-builder/releases)
[![CI](https://github.com/seppelandrio/kotlin-dummy-builder/actions/workflows/test.yml/badge.svg)](https://github.com/seppelandrio/kotlin-dummy-builder/actions/workflows/test.yml)
[![license](https://img.shields.io/github/license/seppelandrio/kotlin-dummy-builder?color=yellow)](https://www.apache.org/licenses/LICENSE-2.0)

A lightweight Kotlin library for generating dummy objects based on reflection for testing and prototyping purposes.

This library recursively creates dummy instances of classes by using reflection to call their constructors with default or random arguments.
It supports various types, including primitives, collections, generics and custom classes.
You can also customize the generated dummy objects by providing specific values for certain properties or types.
For a complete list of supported types and features, please refer to the [features section](#features).

## Installation

All you need to get started is to add a dependency to `Kotlin Dummy Builder` in your project:

| Approach   | Instruction                                                                                                                                                                                |
|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Gradle     | `testImplementation "io.github.seppelandrio:kotlin-dummy-builder:x.y.z"`                                                                                                                   |
| Gradle Kts | `testImplementation("io.github.seppelandrio:kotlin-dummy-builder:x.y.z")`                                                                                                                  |
| Maven      | `<dependency>`<br>`<groupId>io.github.seppelandrio</groupId>`<br>`<artifactId>kotlin-dummy-builder</artifactId>`<br>`<version>x.y.z</version>`<br>`<scope>test</scope>`<br>`</dependency>` |

> [!IMPORTANT]
> Since this library makes extensive use of `kotlin-reflect`, and many related issues are resolved with each new Kotlin language release,
> it targets the latest Kotlin language version by default (currently 2.3).
>
> As a result, your project must use at least the same Kotlin language version, because libraries compiled with newer Kotlin language versions are not compatible with projects with older Kotlin language version.
> For the best compatibility and stability, we recommend upgrading your Kotlin version so you can use the default library release: `x.y.z`.
>
> If upgrading is not possible, a backwards-compatible variant is available for Kotlin 1.9+ (`x.y.z-kotlin1.9`).
> It provides the same feature set, but compatibility might be slightly reduced due to missing `kotlin-reflect` features or unresolved issues in older Kotlin versions.

## Usage

You can either generate dummies with default values by calling the `default` function or with random values by calling the `random` function.
Both functions have the same parameters and behavior, except for the values they generate.

> [!NOTE]
> For the following section we will use `default` for demonstration purposes, but the same applies to `random` as well.

Default usage

```kotlin
val myDummy = default<MyClass>()
```

To customize the dummy you can either use the copy operator of data classes (preferred)

```kotlin
val myDummy = default<MyClass>().copy(someProperty = "CustomValue", anotherProperty = 42)
```

or use `argumentOverwrites` to overwrite constructor arguments either by property reference or by name

```kotlin
val myDummy = default<MyClass>(
    argumentOverwrites = setOf(
        ArgumentOverwrite(MyClass::someProperty, "CustomValue"),
        ArgumentOverwrite(MyClass::anotherProperty, 42),
    ),
)

val myDummy2 = default<MyClass>(
    argumentOverwrites = mapOf(
        "someProperty" to "CustomValue",
        "anotherProperty" to 42,
    ),
)
```

To provide specific values for certain types across every (nested) property in this dummy, use `typeOverwrites`

```kotlin
val myDummy = default<MyClass>(
    typeOverwrites = setOf(
        TypeOverwrite(String::class) { "OverwrittenString" },
        TypeOverwrite(Int::class) { Random.nextInt(0, 6) },
    ),
)
```

When using argument and type overwrites together, argument overwrites take precedence.

If your type or its nested property is an abstract class or interface,
you may need to specify `packageNameForChildClassLookup` to help the dummy builder find concrete implementations.
By default, it uses the package of the provided class.

## Features

This library generates dummy by calling constructors based on reflection and so it is capable of generating dummy for all nested classes and properties as long as they have a constructor that can be called with dummy values.

### Simple Types

The library supports the following types out of the box

| Type                 | Default Value                     |
|----------------------|-----------------------------------|
| Boolean              | `false`                           |
| Byte                 | `0`                               |
| Short                | `0`                               |
| Int                  | `0`                               |
| Long                 | `0L`                              |
| Float                | `0.0f`                            |
| Double               | `0.0`                             |
| Char                 | `'a'`                             |
| String               | `""`                              |
| BigInteger           | `BigInteger.ZERO`                 |
| BigDecimal           | `BigDecimal.ZERO`                 |
| LocalDate            | `1970-01-01`                      |
| LocalTime            | `00:00`                           |
| ZoneId               | `ZoneId.of("UTC")`                |
| ZoneOffset           | `ZoneOffset.UTC`                  |
| Instant              | `1970-01-01T00:00:00Z`            |
| LocalDateTime        | `1970-01-01T00:00:00`             |
| OffsetTime           | `00:00Z`                          |
| OffsetDateTime       | `1970-01-01T00:00:00Z`            |
| ZonedDateTime        | `1970-01-01T00:00:00Z`            |
| java.time.Duration   | `Duration.ZERO`                   |
| kotlin.time.Duration | `Duration.ZERO`                   |
| Currency             | `USD`                             |
| KClass\<T>           | `T::class`                        |
| KClass\<out T>       | `ConcreteSubclass(T)::class`      |
| Class\<T>            | `T::class.java`                   |
| Class\<out T>        | `ConcreteSubclass(T)::class.java` |
| Enum                 | `first value of the enum`         |

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

> [!WARNING]
> Kotlin reflection looses type parameters for functions with more than 2 arguments, so the library will throw an exception when generating dummies for them.

## Custom Types

For custom types, the library will try to find a constructor and call it with dummy values for its parameters.

Additionally, it supports the following features

| Feature                            | Behavior                                                                                                                                                                                                                                                                   |
|------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Abstract Classes/ Interfaces       | it will try to find a concrete implementation in the package of the generated dummy object or if specified in `packageNameForChildClassLookup`. If nothing found it will search in the parent packages. If still nothing found it will throw an `IllegalArgumentException` |
| Sealed Classes and Interfaces      | it will use a non-abstract subclass and use it to generate the dummy                                                                                                                                                                                                       |
| Generics                           | it will infer the type arguments from the context and use them to generate the dummy                                                                                                                                                                                       |
| Value Classes                      | it will generate a dummy for its underlying type and use it to create an instance of the value class                                                                                                                                                                       |
| Objects                            | it will return the object instance                                                                                                                                                                                                                                         |
| Companion Object Creator Functions | it will use the companion object creator function to generate the dummy if no public constructor found                                                                                                                                                                     |
| Private Constructors               | it will try to make it accessible and use it to generate the dummy if no public constructor or companion object creator function found                                                                                                                                     |
