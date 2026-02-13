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
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFails
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
        *TestCases.defaultAndRandom("LocalDate", LocalDate.MIN),
        *TestCases.defaultAndRandom("LocalTime", LocalTime.MIN),
        *TestCases.defaultAndRandom("ZoneId", ZoneId.of("UTC")),
        *TestCases.defaultAndRandom("ZoneOffset", ZoneOffset.MAX),
        *TestCases.defaultAndRandom("Instant", Instant.MIN),
        *TestCases.defaultAndRandom("LocalDateTime", LocalDateTime.MIN),
        *TestCases.defaultAndRandom("OffsetTime", OffsetTime.MIN),
        *TestCases.defaultAndRandom("OffsetDateTime", OffsetDateTime.MIN),
        *TestCases.defaultAndRandom("ZonedDateTime", ZonedDateTime.of(LocalDateTime.MIN, ZoneOffset.MAX)),
        *TestCases.defaultAndRandom("Duration", Duration.ZERO),
        *TestCases.defaultAndRandom("kotlin.time.Duration", kotlin.time.Duration.ZERO),
        // endregion
        // region classes
        *TestCases.alwaysDefault("KClass", Int::class),
        *TestCases.alwaysDefault("JavaClass", String::class.java),
        // endregion
        // region enums
        *TestCases.defaultAndRandom("Enum", ExampleEnum.A),
        // endregion
        // region collections
        *TestCases.defaultAndRandom("ByteArray", emptyList(), { defaultDummy<ByteArray>().toList() }, { randomDummy<ByteArray>().toList() }),
        *TestCases.defaultAndRandom("CharArray", emptyList(), { defaultDummy<CharArray>().toList() }, { randomDummy<CharArray>().toList() }),
        *TestCases.defaultAndRandom("ShortArray", emptyList(), { defaultDummy<ShortArray>().toList() }, { randomDummy<ShortArray>().toList() }),
        *TestCases.defaultAndRandom("IntArray", emptyList(), { defaultDummy<IntArray>().toList() }, { randomDummy<IntArray>().toList() }),
        *TestCases.defaultAndRandom("LongArray", emptyList(), { defaultDummy<LongArray>().toList() }, { randomDummy<LongArray>().toList() }),
        *TestCases.defaultAndRandom("FloatArray", emptyList(), { defaultDummy<FloatArray>().toList() }, { randomDummy<FloatArray>().toList() }),
        *TestCases.defaultAndRandom("DoubleArray", emptyList(), { defaultDummy<DoubleArray>().toList() }, { randomDummy<DoubleArray>().toList() }),
        *TestCases.defaultAndRandom("BooleanArray", emptyList(), { defaultDummy<BooleanArray>().toList() }, { randomDummy<BooleanArray>().toList() }),
        *TestCases.defaultAndRandom("Array<String>", emptyList(), { defaultDummy<Array<String>>().toList() }, { randomDummy<Array<String>>().toList() }),
        *TestCases.defaultAndRandom("Iterable<String>", emptyList(), { defaultDummy<Iterable<String>>().toList() }, { randomDummy<Iterable<String>>().toList() }),
        *TestCases.defaultAndRandom("Collection<String>", emptyList(), { defaultDummy<Collection<String>>().toList() }, { randomDummy<Collection<String>>().toList() }),
        *TestCases.defaultAndRandom("List<String>", emptyList(), { defaultDummy<List<String>>() }, { randomDummy<List<String>>() }),
        *TestCases.defaultAndRandom("MutableList<String>", emptyList(), { defaultDummy<MutableList<String>>() }, { randomDummy<MutableList<String>>() }),
        *TestCases.defaultAndRandom("Set<String>", emptySet(), { defaultDummy<Set<String>>() }, { randomDummy<Set<String>>() }),
        *TestCases.defaultAndRandom("MutableSet<String>", emptySet(), { defaultDummy<MutableSet<String>>() }, { randomDummy<MutableSet<String>>() }),
        *TestCases.defaultAndRandom("Stream<String>", emptyList(), { defaultDummy<Stream<String>>().toList() }, { randomDummy<Stream<String>>().toList() }),
        *TestCases.defaultAndRandom("Map<String, Int>", emptyList(), { defaultDummy<Map<String, Int>>().entries.toList() }, { randomDummy<Map<String, Int>>().entries.toList() }),
        *TestCases.defaultAndRandom("MutableMap<String, Int>", emptyList(), { defaultDummy<MutableMap<String, Int>>().entries.toList() }, { randomDummy<MutableMap<String, Int>>().entries.toList() }),
        // endregion
        // region functions
        *TestCases.alwaysDefault("lambda () -> Unit", Unit, { defaultDummy<() -> Unit>()() }, { randomDummy<() -> Unit>()() }),
        *TestCases.alwaysDefault("function () -> Unit", Unit, { defaultDummy<Function0<Unit>>()() }, { randomDummy<Function0<Unit>>()() }),
        *TestCases.defaultAndRandom("lambda () -> String", "", { defaultDummy<() -> String>()() }, { randomDummy<() -> String>()() }),
        *TestCases.defaultAndRandom("function () -> String", "", { defaultDummy<Function0<String>>()() }, { randomDummy<Function0<String>>()() }),
        *TestCases.defaultAndRandom("lambda () -> String?", "", { defaultDummy<() -> String?>()()!! }, { randomDummy<() -> String?>()()!! }),
        *TestCases.defaultAndRandom("function () -> String?", "", { defaultDummy<Function0<String?>>()()!! }, { randomDummy<Function0<String?>>()()!! }),
        *TestCases.alwaysDefault("lambda (String) -> Unit", Unit, { defaultDummy<(String) -> Unit>()("input") }, { randomDummy<(String) -> Unit>()("input") }),
        *TestCases.alwaysDefault("function (String) -> Unit", Unit, { defaultDummy<Function1<String, Unit>>()("input") }, { randomDummy<Function1<String, Unit>>()("input") }),
        *TestCases.defaultAndRandom("lambda (String) -> String", "", { defaultDummy<(String) -> String>()("input") }, { randomDummy<(String) -> String>()("input") }),
        *TestCases.defaultAndRandom("function (String) -> String", "", { defaultDummy<Function1<String, String>>()("input") }, { randomDummy<Function1<String, String>>()("input") }),
        *TestCases.defaultAndRandom("lambda (String) -> String?", "", { defaultDummy<(String) -> String?>()("input")!! }, { randomDummy<(String) -> String?>()("input")!! }),
        *TestCases.defaultAndRandom("function (String) -> String?", "", { defaultDummy<Function1<String, String?>>()("input")!! }, { randomDummy<Function1<String, String?>>()("input")!! }),
        *TestCases.alwaysDefault("lambda (String, Int?) -> Unit", Unit, { defaultDummy<(String, Int?) -> Unit>()("input", null) }, { randomDummy<(String, Int?) -> Unit>()("input", null) }),
        *TestCases.alwaysDefault(
            typeDescription = "function (String, Int?) -> Unit",
            expectedDefaultValue = Unit,
            buildDefaultDummy = { defaultDummy<Function2<String, Int?, Unit>>()("input", null) },
            buildRandomDummy = { randomDummy<Function2<String, Int?, Unit>>()("input", null) },
        ),
        *TestCases.defaultAndRandom("lambda (String, Int?) -> String", "", { defaultDummy<(String, Int?) -> String>()("input", null) }, { randomDummy<(String, Int?) -> String>()("input", null) }),
        *TestCases.defaultAndRandom(
            typeDescription = "function (String, Int?) -> String",
            expectedDefaultValue = "",
            buildDefaultDummy = { defaultDummy<Function2<String, Int?, String>>()("input", null) },
            buildRandomDummy = { randomDummy<Function2<String, Int?, String>>()("input", null) },
        ),
        *TestCases.defaultAndRandom("lambda (String, Int?) -> String?", "", {
            defaultDummy<
                (
                    String,
                    Int?,
                ) -> String?,
            >()("input", null)!!
        }, { randomDummy<(String, Int?) -> String?>()("input", null)!! }),
        *TestCases.defaultAndRandom(
            typeDescription = "function (String, Int?) -> String?",
            expectedDefaultValue = "",
            buildDefaultDummy = { defaultDummy<Function2<String, Int?, String?>>()("input", null)!! },
            buildRandomDummy = { randomDummy<Function2<String, Int?, String?>>()("input", null)!! },
        ),
        // endregion
        // region complex objects
        *TestCases.defaultAndRandom("ValueClass", ValueClass("")),
        *TestCases.defaultAndRandom("Clazz", Clazz("", null, Clazz.Nested(""))),
        *TestCases.defaultAndRandom("DataClass", DataClass("", null, DataClass.Nested(false))),
        *TestCases.defaultAndRandom("GenericClass", GenericClass(GenericClass.Nested(""), GenericClass.Nested(0))),
        *TestCases.defaultAndRandom("ClassWithPrivateConstructor", "", { defaultDummy<ClassWithPrivateConstructor>().s }, { randomDummy<ClassWithPrivateConstructor>().s }),
        *TestCases.alwaysDefault("Object", Object),
        // endregion
        // region abstract types
        *TestCases.defaultAndRandom("Interface", Interface.Impl1(s = "")),
        *TestCases.defaultAndRandom("SealedInterface", SealedInterface.Impl1(s = "")),
        *TestCases.defaultAndRandom("AbstractClass", AbstractClass.Impl1(s = "")),
        *TestCases.defaultAndRandom("SealedClass", SealedClass.Impl1(s = "")),
        // endregion
    )

    @Nested
    inner class FunctionsWithMoreThanTwoArguments {
        @Test
        fun `should not be able to generate default dummy as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { defaultDummy<(Nothing?, Nothing?, Nothing?) -> String>() }

            assertTrue(exception is IllegalArgumentException)
            assertEquals("Cannot create dummy for function type as kotlin does not capture the generic type information: kotlin.Function3<*, *, *, *>.", exception.message)
        }

        @Test
        fun `should not be able to generate random dummy as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { randomDummy<(Nothing?, Nothing?, Nothing?) -> String>() }

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
                val d = defaultDummy<Clazz>(
                    argumentOverwrites = setOf(
                        ArgumentOverwrite(Clazz::s, "overwritten"),
                    ),
                )

                assertEquals("overwritten", d.s)
                assertEquals("", d.n.s)
            }

            @Test
            fun `should apply overwrite to random dummy`() {
                val d = randomDummy<Clazz>(
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
                val d = defaultDummy<Clazz>(
                    typeOverwrites = setOf(
                        TypeOverwrite(String::class, "overwritten"),
                    ),
                )

                assertEquals("overwritten", d.s)
                assertEquals("overwritten", d.n.s)
            }

            @Test
            fun `should apply overwrite to random dummy`() {
                val d = randomDummy<Clazz>(
                    typeOverwrites = setOf(
                        TypeOverwrite(String::class, "overwritten"),
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
            noinline buildDefaultDummy: () -> T = { defaultDummy() },
            noinline buildRandomDummy: () -> T = { randomDummy() },
        ): Array<DynamicTest> = listOf(
            TestCase.DefaultValue("$typeDescription: defaultDummy() should return default value", buildDefaultDummy, expectedDefaultValue),
            TestCase.RandomValue("$typeDescription: randomDummy() should return return random value", buildRandomDummy),
        ).map {
            DynamicTest.dynamicTest(it.description) {
                it.execute()
            }
        }.toTypedArray()

        inline fun <reified T : Any> alwaysDefault(
            typeDescription: String,
            expectedDefaultValue: T,
            noinline buildDefaultDummy: () -> T = { defaultDummy() },
            noinline buildRandomDummy: () -> T = { randomDummy() },
        ): Array<DynamicTest> = listOf(
            TestCase.DefaultValue("$typeDescription: defaultDummy() should return default value", buildDefaultDummy, expectedDefaultValue),
            TestCase.DefaultValue("$typeDescription: randomDummy() should return default value", buildRandomDummy, expectedDefaultValue),
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

    object Object

    data class GenericClass<T>(
        val t: T,
        val n: Nested<Int>,
    ) {
        data class Nested<V>(val v: V)
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
