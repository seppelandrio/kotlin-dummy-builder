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
        *TestCases.fixedAndRandom("Boolean", false),
        *TestCases.fixedAndRandom("Byte", 0.toByte()),
        *TestCases.fixedAndRandom("Short", 0.toShort()),
        *TestCases.fixedAndRandom("Int", 0),
        *TestCases.fixedAndRandom("Long", 0L),
        *TestCases.fixedAndRandom("Float", 0.0f),
        *TestCases.fixedAndRandom("Double", 0.0),
        *TestCases.fixedAndRandom("Char", 'a'),
        *TestCases.fixedAndRandom("String", ""),
        *TestCases.fixedAndRandom("BigInteger", BigInteger.ZERO),
        *TestCases.fixedAndRandom("BigDecimal", BigDecimal.ZERO),
        *TestCases.fixedAndRandom("LocalDate", LocalDate.MIN),
        *TestCases.fixedAndRandom("LocalTime", LocalTime.MIN),
        *TestCases.fixedAndRandom("ZoneId", ZoneId.of("UTC")),
        *TestCases.fixedAndRandom("ZoneOffset", ZoneOffset.MAX),
        *TestCases.fixedAndRandom("Instant", Instant.MIN),
        *TestCases.fixedAndRandom("LocalDateTime", LocalDateTime.MIN),
        *TestCases.fixedAndRandom("OffsetTime", OffsetTime.MIN),
        *TestCases.fixedAndRandom("OffsetDateTime", OffsetDateTime.MIN),
        *TestCases.fixedAndRandom("ZonedDateTime", ZonedDateTime.of(LocalDateTime.MIN, ZoneOffset.MAX)),
        *TestCases.fixedAndRandom("Duration", Duration.ZERO),
        *TestCases.fixedAndRandom("kotlin.time.Duration", kotlin.time.Duration.ZERO),
        // endregion
        // region classes
        *TestCases.alwaysFixed("KClass", Int::class),
        *TestCases.alwaysFixed("JavaClass", String::class.java),
        // endregion
        // region enums
        *TestCases.fixedAndRandom("Enum", ExampleEnum.A),
        // endregion
        // region collections
        *TestCases.fixedAndRandom("ByteArray", emptyList(), { fixedDummy<ByteArray>().toList() }, { randomDummy<ByteArray>().toList() }),
        *TestCases.fixedAndRandom("CharArray", emptyList(), { fixedDummy<CharArray>().toList() }, { randomDummy<CharArray>().toList() }),
        *TestCases.fixedAndRandom("ShortArray", emptyList(), { fixedDummy<ShortArray>().toList() }, { randomDummy<ShortArray>().toList() }),
        *TestCases.fixedAndRandom("IntArray", emptyList(), { fixedDummy<IntArray>().toList() }, { randomDummy<IntArray>().toList() }),
        *TestCases.fixedAndRandom("LongArray", emptyList(), { fixedDummy<LongArray>().toList() }, { randomDummy<LongArray>().toList() }),
        *TestCases.fixedAndRandom("FloatArray", emptyList(), { fixedDummy<FloatArray>().toList() }, { randomDummy<FloatArray>().toList() }),
        *TestCases.fixedAndRandom("DoubleArray", emptyList(), { fixedDummy<DoubleArray>().toList() }, { randomDummy<DoubleArray>().toList() }),
        *TestCases.fixedAndRandom("BooleanArray", emptyList(), { fixedDummy<BooleanArray>().toList() }, { randomDummy<BooleanArray>().toList() }),
        *TestCases.fixedAndRandom("Array<String>", emptyList(), { fixedDummy<Array<String>>().toList() }, { randomDummy<Array<String>>().toList() }),
        *TestCases.fixedAndRandom("Iterable<String>", emptyList(), { fixedDummy<Iterable<String>>().toList() }, { randomDummy<Iterable<String>>().toList() }),
        *TestCases.fixedAndRandom("Collection<String>", emptyList(), { fixedDummy<Collection<String>>().toList() }, { randomDummy<Collection<String>>().toList() }),
        *TestCases.fixedAndRandom("List<String>", emptyList(), { fixedDummy<List<String>>() }, { randomDummy<List<String>>() }),
        *TestCases.fixedAndRandom("MutableList<String>", emptyList(), { fixedDummy<MutableList<String>>() }, { randomDummy<MutableList<String>>() }),
        *TestCases.fixedAndRandom("Set<String>", emptySet(), { fixedDummy<Set<String>>() }, { randomDummy<Set<String>>() }),
        *TestCases.fixedAndRandom("MutableSet<String>", emptySet(), { fixedDummy<MutableSet<String>>() }, { randomDummy<MutableSet<String>>() }),
        *TestCases.fixedAndRandom("Stream<String>", emptyList(), { fixedDummy<Stream<String>>().toList() }, { randomDummy<Stream<String>>().toList() }),
        *TestCases.fixedAndRandom("Map<String, Int>", emptyList(), { fixedDummy<Map<String, Int>>().entries.toList() }, { randomDummy<Map<String, Int>>().entries.toList() }),
        *TestCases.fixedAndRandom("MutableMap<String, Int>", emptyList(), { fixedDummy<MutableMap<String, Int>>().entries.toList() }, { randomDummy<MutableMap<String, Int>>().entries.toList() }),
        // endregion
        // region functions
        *TestCases.alwaysFixed("lambda () -> Unit", Unit, { fixedDummy<() -> Unit>()() }, { randomDummy<() -> Unit>()() }),
        *TestCases.alwaysFixed("function () -> Unit", Unit, { fixedDummy<Function0<Unit>>()() }, { randomDummy<Function0<Unit>>()() }),
        *TestCases.fixedAndRandom("lambda () -> String", "", { fixedDummy<() -> String>()() }, { randomDummy<() -> String>()() }),
        *TestCases.fixedAndRandom("function () -> String", "", { fixedDummy<Function0<String>>()() }, { randomDummy<Function0<String>>()() }),
        *TestCases.fixedAndRandom("lambda () -> String?", "", { fixedDummy<() -> String?>()()!! }, { randomDummy<() -> String?>()()!! }),
        *TestCases.fixedAndRandom("function () -> String?", "", { fixedDummy<Function0<String?>>()()!! }, { randomDummy<Function0<String?>>()()!! }),
        *TestCases.alwaysFixed("lambda (String) -> Unit", Unit, { fixedDummy<(String) -> Unit>()("input") }, { randomDummy<(String) -> Unit>()("input") }),
        *TestCases.alwaysFixed("function (String) -> Unit", Unit, { fixedDummy<Function1<String, Unit>>()("input") }, { randomDummy<Function1<String, Unit>>()("input") }),
        *TestCases.fixedAndRandom("lambda (String) -> String", "", { fixedDummy<(String) -> String>()("input") }, { randomDummy<(String) -> String>()("input") }),
        *TestCases.fixedAndRandom("function (String) -> String", "", { fixedDummy<Function1<String, String>>()("input") }, { randomDummy<Function1<String, String>>()("input") }),
        *TestCases.fixedAndRandom("lambda (String) -> String?", "", { fixedDummy<(String) -> String?>()("input")!! }, { randomDummy<(String) -> String?>()("input")!! }),
        *TestCases.fixedAndRandom("function (String) -> String?", "", { fixedDummy<Function1<String, String?>>()("input")!! }, { randomDummy<Function1<String, String?>>()("input")!! }),
        *TestCases.alwaysFixed("lambda (String, Int?) -> Unit", Unit, { fixedDummy<(String, Int?) -> Unit>()("input", null) }, { randomDummy<(String, Int?) -> Unit>()("input", null) }),
        *TestCases.alwaysFixed(
            typeDescription = "function (String, Int?) -> Unit",
            expectedFixedValue = Unit,
            buildFixedDummy = { fixedDummy<Function2<String, Int?, Unit>>()("input", null) },
            buildRandomDummy = { randomDummy<Function2<String, Int?, Unit>>()("input", null) },
        ),
        *TestCases.fixedAndRandom("lambda (String, Int?) -> String", "", { fixedDummy<(String, Int?) -> String>()("input", null) }, { randomDummy<(String, Int?) -> String>()("input", null) }),
        *TestCases.fixedAndRandom(
            typeDescription = "function (String, Int?) -> String",
            expectedFixedValue = "",
            buildFixedDummy = { fixedDummy<Function2<String, Int?, String>>()("input", null) },
            buildRandomDummy = { randomDummy<Function2<String, Int?, String>>()("input", null) },
        ),
        *TestCases.fixedAndRandom("lambda (String, Int?) -> String?", "", { fixedDummy<(String, Int?) -> String?>()("input", null)!! }, { randomDummy<(String, Int?) -> String?>()("input", null)!! }),
        *TestCases.fixedAndRandom(
            typeDescription = "function (String, Int?) -> String?",
            expectedFixedValue = "",
            buildFixedDummy = { fixedDummy<Function2<String, Int?, String?>>()("input", null)!! },
            buildRandomDummy = { randomDummy<Function2<String, Int?, String?>>()("input", null)!! },
        ),
        // endregion
        // region complex objects
        *TestCases.fixedAndRandom("ValueClass", ValueClass("")),
        *TestCases.fixedAndRandom("Clazz", Clazz("", null, Clazz.Nested(""))),
        *TestCases.fixedAndRandom("DataClass", DataClass("", null, DataClass.Nested(false))),
        *TestCases.fixedAndRandom("GenericClass", GenericClass(GenericClass.Nested(""), GenericClass.Nested(0))),
        *TestCases.fixedAndRandom("ClassWithPrivateConstructor", "", { fixedDummy<ClassWithPrivateConstructor>().s }, { randomDummy<ClassWithPrivateConstructor>().s }),
        *TestCases.alwaysFixed("Object", Object),
        // endregion
        // region abstract types
        *TestCases.fixedAndRandom("Interface", Interface.Impl1(s = "")),
        *TestCases.fixedAndRandom("SealedInterface", SealedInterface.Impl1(s = "")),
        *TestCases.fixedAndRandom("AbstractClass", AbstractClass.Impl1(s = "")),
        *TestCases.fixedAndRandom("SealedClass", SealedClass.Impl1(s = "")),
        // endregion
    )

    @Nested
    inner class FunctionsWithMoreThanTwoArguments {
        @Test
        fun `should not be able to generate fixed dummy as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { fixedDummy<(Nothing?, Nothing?, Nothing?) -> String>() }

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
            fun `should apply overwrite to fixed dummy`() {
                val d = fixedDummy<Clazz>(
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
            fun `should apply overwrite to fixed dummy`() {
                val d = fixedDummy<Clazz>(
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
        inline fun <reified T : Any> fixedAndRandom(
            typeDescription: String,
            expectedFixedValue: T,
            noinline buildFixedDummy: () -> T = ::fixedDummy,
            noinline buildRandomDummy: () -> T = ::randomDummy,
        ): Array<DynamicTest> = listOf(
            TestCase.FixedValue("${typeDescription}: fixedDummy() should return fixed value", buildFixedDummy, expectedFixedValue),
            TestCase.RandomValue("${typeDescription}: randomDummy() should return return random value", buildRandomDummy),
        ).map {
            DynamicTest.dynamicTest(it.description) {
                it.execute()
            }
        }.toTypedArray()

        inline fun <reified T : Any> alwaysFixed(
            typeDescription: String,
            expectedFixedValue: T,
            noinline buildFixedDummy: () -> T = ::fixedDummy,
            noinline buildRandomDummy: () -> T = ::randomDummy,
        ): Array<DynamicTest> = listOf(
            TestCase.FixedValue("${typeDescription}: fixedDummy() should return fixed value", buildFixedDummy, expectedFixedValue),
            TestCase.FixedValue("${typeDescription}: randomDummy() should return fixed value", buildRandomDummy, expectedFixedValue),
        ).map {
            DynamicTest.dynamicTest(it.description) {
                it.execute()
            }
        }.toTypedArray()

        private sealed interface TestCase<T : Any> {
            val description: String

            fun execute()

            class FixedValue<T : Any>(
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
