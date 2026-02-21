package io.github.seppelandrio.kotlindummybuilder

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
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
import java.util.Currency
import java.util.Locale
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class KotlinDummyBuilderTest {
    @TestFactory
    fun `should support`(): List<DynamicTest> = listOf(
        // region simple types
        *TestCases.defaultAndRandom("Boolean", false),
        *TestCases.defaultAndRandom("Byte", 0.toByte()),
        *TestCases.defaultAndRandom("Short", 0.toShort()),
        *TestCases.defaultAndRandom("Int", 0),
        *TestCases.defaultAndRandom("Long", 0L),
        *TestCases.defaultAndRandom("Float", 0.0f),
        *TestCases.defaultAndRandom("Double", 0.0),
        *TestCases.defaultAndRandom("Char", 'a'),
        *TestCases.defaultAndRandom("String", ""),
        *TestCases.defaultAndRandom("BigInteger", BigInteger.ZERO),
        *TestCases.defaultAndRandom("BigDecimal", BigDecimal.ZERO),
        *TestCases.defaultAndRandom("LocalDate", LocalDate.parse("1970-01-01")),
        *TestCases.defaultAndRandom("LocalTime", LocalTime.parse("00:00")),
        *TestCases.defaultAndRandom("ZoneId", ZoneId.of("UTC")),
        *TestCases.defaultAndRandom("ZoneOffset", ZoneOffset.UTC),
        *TestCases.defaultAndRandom("Instant", Instant.parse("1970-01-01T00:00:00Z")),
        *TestCases.defaultAndRandom("LocalDateTime", LocalDateTime.parse("1970-01-01T00:00:00")),
        *TestCases.defaultAndRandom("OffsetTime", OffsetTime.parse("00:00Z")),
        *TestCases.defaultAndRandom("OffsetDateTime", OffsetDateTime.parse("1970-01-01T00:00:00Z")),
        *TestCases.defaultAndRandom("ZonedDateTime", ZonedDateTime.parse("1970-01-01T00:00:00Z")),
        *TestCases.defaultAndRandom("Duration", Duration.ZERO),
        *TestCases.defaultAndRandom("kotlin.time.Duration", kotlin.time.Duration.ZERO),
        *TestCases.defaultAndRandom("Currency", Currency.getInstance("USD")),
        *TestCases.defaultAndRandom("Locale", Locale.US),
        // endregion
        // region classes
        *TestCases.alwaysDefault("KClass", Int::class),
        *TestCases.defaultAndRandom<KClass<out Interface>>(
            typeDescription = "KClass<out Interface>",
            expectedDefaultValue = Interface.Impl1::class,
            buildDefaultDummy = { default(packageNameForChildClassLookup = Interface::class.java.packageName) },
            buildRandomDummy = { random(packageNameForChildClassLookup = Interface::class.java.packageName) },
        ),
        *TestCases.defaultAndRandom<KClass<out SealedInterface>>(
            typeDescription = "KClass<out SealedInterface>",
            expectedDefaultValue = SealedInterface.Impl1::class,
            buildDefaultDummy = { default(packageNameForChildClassLookup = SealedInterface::class.java.packageName) },
            buildRandomDummy = { random(packageNameForChildClassLookup = SealedInterface::class.java.packageName) },
        ),
        *TestCases.alwaysDefault("Class", String::class.java),
        *TestCases.defaultAndRandom<Class<out Interface>>(
            typeDescription = "Class<out Interface>",
            expectedDefaultValue = Interface.Impl1::class.java,
            buildDefaultDummy = { default(packageNameForChildClassLookup = Interface::class.java.packageName) },
            buildRandomDummy = { random(packageNameForChildClassLookup = Interface::class.java.packageName) },
        ),
        *TestCases.defaultAndRandom<Class<out SealedInterface>>(
            typeDescription = "Class<out SealedInterface>",
            expectedDefaultValue = SealedInterface.Impl1::class.java,
            buildDefaultDummy = { default(packageNameForChildClassLookup = SealedInterface::class.java.packageName) },
            buildRandomDummy = { random(packageNameForChildClassLookup = SealedInterface::class.java.packageName) },
        ),
        // endregion
        // region enums
        *TestCases.defaultAndRandom("Enum", ExampleEnum.A),
        // endregion
        // region collections
        *TestCases.defaultAndRandom("ByteArray", emptyList(), { default<ByteArray>().toList() }, { random<ByteArray>().toList() }),
        *TestCases.defaultAndRandom("CharArray", emptyList(), { default<CharArray>().toList() }, { random<CharArray>().toList() }),
        *TestCases.defaultAndRandom("ShortArray", emptyList(), { default<ShortArray>().toList() }, { random<ShortArray>().toList() }),
        *TestCases.defaultAndRandom("IntArray", emptyList(), { default<IntArray>().toList() }, { random<IntArray>().toList() }),
        *TestCases.defaultAndRandom("LongArray", emptyList(), { default<LongArray>().toList() }, { random<LongArray>().toList() }),
        *TestCases.defaultAndRandom("FloatArray", emptyList(), { default<FloatArray>().toList() }, { random<FloatArray>().toList() }),
        *TestCases.defaultAndRandom("DoubleArray", emptyList(), { default<DoubleArray>().toList() }, { random<DoubleArray>().toList() }),
        *TestCases.defaultAndRandom("BooleanArray", emptyList(), { default<BooleanArray>().toList() }, { random<BooleanArray>().toList() }),
        *TestCases.defaultAndRandom("Array<String>", emptyList(), { default<Array<String>>().toList() }, { random<Array<String>>().toList() }),
        *TestCases.defaultAndRandom("Iterable<String>", emptyList(), { default<Iterable<String>>().toList() }, { random<Iterable<String>>().toList() }),
        *TestCases.defaultAndRandom("Collection<String>", emptyList(), { default<Collection<String>>().toList() }, { random<Collection<String>>().toList() }),
        *TestCases.defaultAndRandom("List<String>", emptyList(), { default<List<String>>() }, { random<List<String>>() }),
        *TestCases.defaultAndRandom("MutableList<String>", emptyList(), { default<MutableList<String>>() }, { random<MutableList<String>>() }),
        *TestCases.defaultAndRandom("Set<String>", emptySet(), { default<Set<String>>() }, { random<Set<String>>() }),
        *TestCases.defaultAndRandom("MutableSet<String>", emptySet(), { default<MutableSet<String>>() }, { random<MutableSet<String>>() }),
        *TestCases.defaultAndRandom("Stream<String>", emptyList(), { default<Stream<String>>().toList() }, { random<Stream<String>>().toList() }),
        *TestCases.defaultAndRandom("Map<String, Int>", emptyList(), { default<Map<String, Int>>().entries.toList() }, { random<Map<String, Int>>().entries.toList() }),
        *TestCases.defaultAndRandom("MutableMap<String, Int>", emptyList(), { default<MutableMap<String, Int>>().entries.toList() }, { random<MutableMap<String, Int>>().entries.toList() }),
        // endregion
        // region functions
        *TestCases.alwaysDefault("lambda () -> Unit", Unit, { default<() -> Unit>()() }, { random<() -> Unit>()() }),
        *TestCases.alwaysDefault("function () -> Unit", Unit, { default<Function0<Unit>>()() }, { random<Function0<Unit>>()() }),
        *TestCases.defaultAndRandom("lambda () -> String", "", { default<() -> String>()() }, { random<() -> String>()() }),
        *TestCases.defaultAndRandom("function () -> String", "", { default<Function0<String>>()() }, { random<Function0<String>>()() }),
        *TestCases.defaultAndRandom("lambda () -> String?", "", { default<() -> String?>()()!! }, { random<() -> String?>()()!! }),
        *TestCases.defaultAndRandom("function () -> String?", "", { default<Function0<String?>>()()!! }, { random<Function0<String?>>()()!! }),
        *TestCases.alwaysDefault("lambda (String) -> Unit", Unit, { default<(String) -> Unit>()("input") }, { random<(String) -> Unit>()("input") }),
        *TestCases.alwaysDefault("function (String) -> Unit", Unit, { default<Function1<String, Unit>>()("input") }, { random<Function1<String, Unit>>()("input") }),
        *TestCases.defaultAndRandom("lambda (String) -> String", "", { default<(String) -> String>()("input") }, { random<(String) -> String>()("input") }),
        *TestCases.defaultAndRandom("function (String) -> String", "", { default<Function1<String, String>>()("input") }, { random<Function1<String, String>>()("input") }),
        *TestCases.defaultAndRandom("lambda (String) -> String?", "", { default<(String) -> String?>()("input")!! }, { random<(String) -> String?>()("input")!! }),
        *TestCases.defaultAndRandom("function (String) -> String?", "", { default<Function1<String, String?>>()("input")!! }, { random<Function1<String, String?>>()("input")!! }),
        *TestCases.alwaysDefault("lambda (String, Int?) -> Unit", Unit, { default<(String, Int?) -> Unit>()("input", null) }, { random<(String, Int?) -> Unit>()("input", null) }),
        *TestCases.alwaysDefault(
            typeDescription = "function (String, Int?) -> Unit",
            expectedDefaultValue = Unit,
            buildDefaultDummy = { default<Function2<String, Int?, Unit>>()("input", null) },
            buildRandomDummy = { random<Function2<String, Int?, Unit>>()("input", null) },
        ),
        *TestCases.defaultAndRandom("lambda (String, Int?) -> String", "", { default<(String, Int?) -> String>()("input", null) }, { random<(String, Int?) -> String>()("input", null) }),
        *TestCases.defaultAndRandom(
            typeDescription = "function (String, Int?) -> String",
            expectedDefaultValue = "",
            buildDefaultDummy = { default<Function2<String, Int?, String>>()("input", null) },
            buildRandomDummy = { random<Function2<String, Int?, String>>()("input", null) },
        ),
        *TestCases.defaultAndRandom(
            typeDescription = "lambda (String, Int?) -> String?",
            expectedDefaultValue = "",
            buildDefaultDummy = {
                default<
                    (
                        String,
                        Int?,
                    ) -> String?,
                >()("input", null)!!
            },
            buildRandomDummy = { random<(String, Int?) -> String?>()("input", null)!! },
        ),
        *TestCases.defaultAndRandom(
            typeDescription = "function (String, Int?) -> String?",
            expectedDefaultValue = "",
            buildDefaultDummy = { default<Function2<String, Int?, String?>>()("input", null)!! },
            buildRandomDummy = { random<Function2<String, Int?, String?>>()("input", null)!! },
        ),
        // endregion
        // region complex objects
        *TestCases.defaultAndRandom("ValueClass", ValueClass("")),
        *TestCases.defaultAndRandom("Clazz", Clazz("", null, Clazz.Nested(""))),
        *TestCases.defaultAndRandom("DataClass", DataClass("", null, DataClass.Nested(false))),
        *TestCases.defaultAndRandom("GenericClass", GenericClass(0, GenericClass.Nested(0))),
        *TestCases.defaultAndRandom("ClassWithPrivateConstructor", "", { default<ClassWithPrivateConstructor>().s }, { random<ClassWithPrivateConstructor>().s }),
        *TestCases.defaultAndRandom("ClassWithPrivateConstructorAndCompanion", "_companion", { default<ClassWithPrivateConstructorAndCompanion>().s }, { random<ClassWithPrivateConstructor>().s }),
        *TestCases.defaultAndRandom("ClassWithInvariantThatCanBeConstructed", "0", { default<ClassWithInvariantThatCanBeConstructed>().s }, { random<ClassWithInvariantThatCanBeConstructed>().s }),
        *TestCases.alwaysDefault("Object", Object),
        // endregion
        // region abstract types
        *TestCases.defaultAndRandom("Interface", Interface.Impl1(s = "")),
        *TestCases.defaultAndRandom("SealedInterface", SealedInterface.Impl1(s = "")),
        *TestCases.defaultAndRandom("AbstractClass", AbstractClass.Impl1(s = "")),
        *TestCases.defaultAndRandom("SealedClass", SealedClass.Impl1(s = "")),
        // endregion
    )

    @Test
    fun `should throw meaningful exception when no suitable creator function could be found`() {
        val exception = assertFailsWith<IllegalArgumentException> { default<ClassWithInvariantThatCannotBeConstructed>() }

        assertEquals(
            $$"""
                Failed to create test instance for type class io.github.seppelandrio.kotlindummybuilder.KotlinDummyBuilderTest$ClassWithInvariantThatCannotBeConstructed.

                The following functions have been tried:
                - fun `<init>`(kotlin.String): io.github.seppelandrio.kotlindummybuilder.KotlinDummyBuilderTest.ClassWithInvariantThatCannotBeConstructed with parameters [""]: java.lang.IllegalArgumentException("String should have more than 0 characters")
                - fun io.github.seppelandrio.kotlindummybuilder.KotlinDummyBuilderTest.ClassWithInvariantThatCannotBeConstructed.Companion.of(kotlin.String): io.github.seppelandrio.kotlindummybuilder.KotlinDummyBuilderTest.ClassWithInvariantThatCannotBeConstructed with parameters [io.github.seppelandrio.kotlindummybuilder.KotlinDummyBuilderTest$ClassWithInvariantThatCannotBeConstructed$Companion@23382f76, ""]: java.lang.IllegalArgumentException("String should have more than 0 characters")
            """.trimIndent().replaceObjectReferences(),
            exception.message?.replaceObjectReferences(),
        )
    }

    @Nested
    inner class FunctionsWithMoreThanTwoArguments {
        @Test
        fun `should not be able to generate default dummy as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { default<(Nothing?, Nothing?, Nothing?) -> String>() }

            assertTrue(exception is IllegalArgumentException)
            assertEquals("Cannot create dummy for function type as kotlin does not capture the generic type information: kotlin.Function3<*, *, *, *>.", exception.message)
        }

        @Test
        fun `should not be able to generate random dummy as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { random<(Nothing?, Nothing?, Nothing?) -> String>() }

            assertTrue(exception is IllegalArgumentException)
            assertEquals("Cannot create dummy for function type as kotlin does not capture the generic type information: kotlin.Function3<*, *, *, *>.", exception.message)
        }
    }

    @Nested
    inner class Overwrites {
        @Nested
        inner class Arguments {
            @Test
            fun `should apply overwrite to default dummy`() {
                val d = default<Clazz>(
                    argumentOverwrites = setOf(
                        ArgumentOverwrite(Clazz::s, "overwritten"),
                    ),
                )

                assertEquals("overwritten", d.s)
                assertEquals("", d.n.s)
            }

            @Test
            fun `should apply overwrite to random dummy`() {
                val d = random<Clazz>(
                    argumentOverwrites = setOf(
                        ArgumentOverwrite(Clazz::s, "overwritten"),
                    ),
                )

                assertEquals("overwritten", d.s)
                assertNotEquals("overwritten", d.n.s)
            }
        }

        @Nested
        inner class Types {
            @Test
            fun `should apply overwrite to default dummy`() {
                val d = default<Clazz>(
                    typeOverwrites = setOf(
                        TypeOverwrite(String::class) { "overwritten" },
                    ),
                )

                assertEquals("overwritten", d.s)
                assertEquals("overwritten", d.n.s)
            }

            @Test
            fun `should apply overwrite to random dummy`() {
                val d = random<Clazz>(
                    typeOverwrites = setOf(
                        TypeOverwrite(String::class, { "overwritten" }),
                    ),
                )

                assertEquals("overwritten", d.s)
                assertEquals("overwritten", d.n.s)
            }
        }
    }

    private object TestCases {
        inline fun <reified T : Any> defaultAndRandom(
            typeDescription: String,
            expectedDefaultValue: T,
            noinline buildDefaultDummy: () -> T = ::default,
            noinline buildRandomDummy: () -> T = ::random,
        ): Array<DynamicTest> = listOf(
            TestCase.DefaultValue("$typeDescription: default() should return default value", buildDefaultDummy, expectedDefaultValue),
            TestCase.RandomValue("$typeDescription: random() should return return random value", buildRandomDummy),
        ).map {
            DynamicTest.dynamicTest(it.description) {
                it.execute()
            }
        }.toTypedArray()

        inline fun <reified T : Any> alwaysDefault(
            typeDescription: String,
            expectedDefaultValue: T,
            noinline buildDefaultDummy: () -> T = ::default,
            noinline buildRandomDummy: () -> T = ::random,
        ): Array<DynamicTest> = listOf(
            TestCase.DefaultValue("$typeDescription: default() should return default value", buildDefaultDummy, expectedDefaultValue),
            TestCase.DefaultValue("$typeDescription: random() should return default value", buildRandomDummy, expectedDefaultValue),
        ).map {
            DynamicTest.dynamicTest(it.description) {
                it.execute()
            }
        }.toTypedArray()

        private sealed interface TestCase<T : Any> {
            val description: String

            fun execute()

            class DefaultValue<T : Any>(
                override val description: String,
                private val buildDummy: () -> T,
                private val expected: T,
            ) : TestCase<T> {
                override fun execute() {
                    val d = buildDummy()
                    assertEquals(expected, d)
                }
            }

            class RandomValue<T : Any>(
                override val description: String,
                private val buildDummy: () -> T,
            ) : TestCase<T> {
                override fun execute() {
                    val dummies = mutableSetOf<T>()
                    @Suppress("unused")
                    for (i in 0 until 10000) {
                        dummies += buildDummy()
                        if (dummies.size > 1) break
                    }

                    assertEquals(2, dummies.size, "Expected to get different values when generating random dummy, but got $dummies")
                }
            }
        }
    }

    // region helper classes
    enum class ExampleEnum { A, B }

    @JvmInline
    value class ValueClass(val a: String)

    class Clazz(
        val s: String,
        val i: Int?,
        val n: Clazz.Nested,
    ) {
        override fun equals(other: Any?) = other is Clazz && s == other.s && i == other.i && n == other.n

        override fun hashCode() = 31 * s.hashCode() + 31 * (i?.hashCode() ?: 0) + n.hashCode()

        class Nested(val s: String) {
            override fun equals(other: Any?) = other is Nested && s == other.s

            override fun hashCode() = s.hashCode()
        }
    }

    data class DataClass(
        val s: String,
        val i: Int?,
        val n: DataClass.Nested,
    ) {
        data class Nested(val b: Boolean)
    }

    class ClassWithPrivateConstructor private constructor(val s: String) {
        override fun equals(other: Any?) = other is ClassWithPrivateConstructor && s == other.s

        override fun hashCode() = s.hashCode()
    }

    class ClassWithPrivateConstructorAndCompanion private constructor(val s: String) {
        override fun equals(other: Any?) = other is ClassWithPrivateConstructor && s == other.s

        override fun hashCode() = s.hashCode()

        companion object {
            fun of(s: String) = ClassWithPrivateConstructorAndCompanion("${s}_companion")
        }
    }

    object Object

    data class GenericClass<T>(
        val t: T,
        val n: Nested<T>,
    ) {
        data class Nested<V>(val v: V)
    }

    class ClassWithInvariantThatCanBeConstructed(val s: String) {
        init {
            require(s.toIntOrNull() != null) { "String should be an Int" }
        }

        companion object {
            fun of(i: Int) = ClassWithInvariantThatCanBeConstructed(i.toString())
        }
    }

    class ClassWithInvariantThatCannotBeConstructed(s: String) {
        init {
            require(s.isNotEmpty()) { "String should have more than 0 characters" }
        }

        companion object {
            fun of(s: String) = ClassWithInvariantThatCannotBeConstructed(s)
        }
    }

    interface Interface {
        val s: String

        data class Impl1(override val s: String) : Interface

        @Suppress("unused")
        data class Impl2(override val s: String) : Interface
    }

    sealed interface SealedInterface {
        val s: String

        data class Impl1(override val s: String) : SealedInterface

        @Suppress("unused")
        data class Impl2(override val s: String) : SealedInterface
    }

    abstract class AbstractClass {
        abstract val s: String

        data class Impl1(override val s: String) : AbstractClass()

        @Suppress("unused")
        data class Impl2(override val s: String) : AbstractClass()
    }

    sealed class SealedClass {
        abstract val s: String

        data class Impl1(override val s: String) : SealedClass()

        @Suppress("unused")
        data class Impl2(override val s: String) : SealedClass()
    }
    // endregion
}

private fun String.replaceObjectReferences(): String = replace(Regex("@\\w{8}"), "@00000000")
