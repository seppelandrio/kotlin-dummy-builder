package io.github.seppelandrio.kotlindummybuilder

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class KotlinDummyBuilderTest {
    @Nested
    inner class SimpleTypes {
        @TestFactory
        fun `should provide dummy for simple types`() = listOf(
            false to dummy<Boolean>(),
            0.toByte() to dummy<Byte>(),
            0.toShort() to dummy<Short>(),
            0 to dummy<Int>(),
            0L to dummy<Long>(),
            BigDecimal.ZERO to dummy<BigDecimal>(),
            0.0 to dummy<Double>(),
            0.0f to dummy<Float>(),
            'a' to dummy<Char>(),
            "" to dummy<String>(),
            LocalDateTime.of(1970, 1, 1, 0, 0) to dummy<LocalDateTime>(),
            Duration.ZERO to dummy<Duration>(),
            kotlin.time.Duration.ZERO to dummy<kotlin.time.Duration>(),
            LocalDate.of(1970, 1, 1) to dummy<LocalDate>(),
            Instant.ofEpochSecond(0L) to dummy<Instant>(),
            Int::class to dummy<KClass<Int>>(),
        ).map { (expected, actual) ->
            DynamicTest.dynamicTest("${expected::class.simpleName}") {
                assertEquals(expected, actual)
            }
        }

        @TestFactory
        fun `should provide dummy for ranges`() = listOf(
            CharRange::class to dummy<CharRange>(),
            IntRange::class to dummy<IntRange>(),
            LongRange::class to dummy<LongRange>(),
        ).map { (clazz, actual) ->
            DynamicTest.dynamicTest("${clazz.simpleName}") {
                assertTrue(clazz.isInstance(actual))
            }
        }

        @Test
        fun `should provide dummy for enum`() {
            val d = dummy<ExampleEnum>()

            assertEquals(ExampleEnum.A, d)
        }
    }

    @Nested
    inner class CollectionTypes {
        @TestFactory
        fun `should provide dummy for primitive arrays`() = listOf(
            ByteArray::class to dummy<ByteArray>(),
            CharArray::class to dummy<CharArray>(),
            ShortArray::class to dummy<ShortArray>(),
            IntArray::class to dummy<IntArray>(),
            LongArray::class to dummy<LongArray>(),
            FloatArray::class to dummy<FloatArray>(),
            DoubleArray::class to dummy<DoubleArray>(),
            BooleanArray::class to dummy<BooleanArray>(),
        ).map { (clazz, actual) ->
            DynamicTest.dynamicTest("${clazz.simpleName}") {
                assertTrue(clazz.isInstance(actual))
            }
        }

        @Test
        fun `should provide dummy for normal arrays`() {
            val d = dummy<Array<String>>()

            assertEquals(0, d.size)
        }

        @Test
        fun `should provide dummy for iterables`() {
            val iterable = dummy<Iterable<String>>()

            assertEquals(0, iterable.toList().size)
        }

        @Test
        fun `should provide dummy for collections`() {
            val collection = dummy<Collection<String>>()

            assertEquals(0, collection.toList().size)
        }

        @TestFactory
        fun `should provide dummy for lists`() = listOf(
            dummy<List<String>>(),
            dummy<MutableList<String>>(),
        ).map { d ->
            assertEquals(0, d.size)
        }

        @TestFactory
        fun `should provide dummy for sets`() = listOf(
            dummy<Set<String>>(),
            dummy<MutableSet<String>>(),
        ).map { d ->
            assertEquals(0, d.size)
        }

        @Test
        fun `should provide dummy for streams`() {
            val stream = dummy<Stream<String>>()

            assertEquals(0, stream.toList().size)
        }

        @TestFactory
        fun `should provide dummy for maps`() = listOf(
            dummy<Map<String, Int>>(),
            dummy<MutableMap<String, Int>>(),
        ).map { d ->
            assertEquals(0, d.size)
        }
    }

    @Nested
    inner class ObjectTypes {
        @Test
        fun `should provide dummy for value class`() {
            val d = dummy<ExampleValueClass>()

            assertEquals(ExampleValueClass(""), d)
        }

        @Test
        fun `should provide dummy for regular class`() {
            val d = dummy<ExampleClass>()

            assertEquals("", d.s)
            assertEquals(null, d.i)
            assertEquals("", d.n.s)
        }

        @Test
        fun `should provide dummy for data class`() {
            val d = dummy<ExampleDataClass>()

            assertEquals(ExampleDataClass("", null, ExampleDataClass.Nested(false)), d)
        }

        @Test
        fun `should provide dummy for class with private constructor`() {
            val d = dummy<ExampleClassWithPrivateConstructor>()

            assertEquals("", d.s)
        }

        @Test
        fun `should provide dummy for object`() {
            val d = dummy<ExampleObject>()

            assertEquals(ExampleObject, d)
        }

        @Test
        fun `should provide dummy for generic class`() {
            val d = dummy<ExampleGenericDataClass<ExampleGenericDataClass.Nested<String>>>()

            assertEquals(
                expected = ExampleGenericDataClass(
                    t = ExampleGenericDataClass.Nested(v = ""),
                    n = ExampleGenericDataClass.Nested(v = 0),
                ),
                actual = d,
            )
        }
    }

    @Nested
    inner class FunctionTypes {
        @TestFactory
        fun `should be able to generate dummy for function without arguments`() = listOf(
            Triple("lambda without return type", { dummy<() -> Unit>() }, Unit),
            Triple("function without return type", { dummy<Function0<Unit>>() }, Unit),
            Triple("lambda with non null return type", { dummy<() -> String>() }, ""),
            Triple("function with non null return type", { dummy<Function0<String>>() }, ""),
            Triple("lambda with nullable return type", { dummy<() -> String?>() }, ""),
            Triple("function with nullable return type", { dummy<Function0<String?>>() }, ""),
        ).map { (description, generateDummy, expectedReturnValue) ->
            DynamicTest.dynamicTest(description) {
                val d = generateDummy()
                assertEquals(expectedReturnValue, d())
            }
        }

        @TestFactory
        fun `should be able to generate dummy for function with single argument`() = listOf(
            Triple("lambda without return type", { dummy<(String) -> Unit>() }, Unit),
            Triple("function without return type", { dummy<Function1<String, Unit>>() }, Unit),
            Triple("lambda with non null return type", { dummy<(String) -> String>() }, ""),
            Triple("function with non null return type", { dummy<Function1<String, String>>() }, ""),
            Triple("lambda with nullable return type", { dummy<(String) -> String?>() }, ""),
            Triple("function with nullable return type", { dummy<Function1<String, String?>>() }, ""),
        ).map { (description, generateDummy, expectedReturnValue) ->
            DynamicTest.dynamicTest(description) {
                val d = generateDummy()
                assertEquals(expectedReturnValue, d("input"))
            }
        }

        @TestFactory
        fun `should be able to generate dummy for function with two arguments`() = listOf(
            Triple("lambda without return type", { dummy<(String, Int) -> Unit>() }, Unit),
            Triple("function without return type", { dummy<Function2<String, Int, Unit>>() }, Unit),
            Triple("lambda with non null return type", { dummy<(String, Int) -> String>() }, ""),
            Triple("function with non null return type", { dummy<Function2<String, Int, String>>() }, ""),
            Triple("lambda with nullable return type", { dummy<(String, Int) -> String?>() }, ""),
            Triple("function with nullable return type", { dummy<Function2<String, Int, String?>>() }, ""),
        ).map { (description, generateDummy, expectedReturnValue) ->
            DynamicTest.dynamicTest(description) {
                val d = generateDummy()
                assertEquals(expectedReturnValue, d("input", 0))
            }
        }

        @Test
        fun `should not be able to generate dummy for functions with three or more arguments as reflections are not able to provide type arguments anymore for the return value`() {
            val exception = assertFails { dummy<(Nothing?, Nothing?, Nothing?) -> String>() }

            assertTrue(exception is IllegalArgumentException)
            assertEquals("Cannot create dummy for function type as kotlin does not capture the generic type information: kotlin.Function3<*, *, *, *>.", exception.message)
        }
    }

    @Nested
    inner class AbstractTypes {
        @Test
        fun `should provide dummy for interface`() {
            val d = dummy<ExampleInterface>()

            assertEquals(ExampleInterface.Impl(s = ""), d)
        }

        @Test
        fun `should provide dummy for sealed interface`() {
            val d = dummy<ExampleSealedInterface>()

            assertEquals(ExampleSealedInterface.Impl(s = ""), d)
        }

        @Test
        fun `should provide dummy for abstract class`() {
            val d = dummy<ExampleAbstractClass>()

            assertEquals(ExampleAbstractClass.Impl(s = ""), d)
        }

        @Test
        fun `should provide dummy for sealed class`() {
            val d = dummy<ExampleSealedClass>()

            assertEquals(ExampleSealedClass.Impl(s = ""), d)
        }
    }

    @Nested
    inner class Overwrites {
        @Test
        fun `should apply argument overwrite`() {
            val d = dummy<ExampleClass>(
                argumentOverwrites = setOf(
                    ArgumentOverwrite(ExampleClass::s, "overwritten"),
                ),
            )

            assertEquals("overwritten", d.s)
            assertEquals("", d.n.s)
        }

        @Test
        fun `should apply type overwrite`() {
            val d = dummy<ExampleClass>(
                typeOverwrites = setOf(
                    TypeOverwrite(String::class, "overwritten"),
                ),
            )

            assertEquals("overwritten", d.s)
            assertEquals("overwritten", d.n.s)
        }
    }

    // region helper classes
    enum class ExampleEnum { A, B }

    @JvmInline
    value class ExampleValueClass(val a: String)

    class ExampleClass(
        val s: String,
        val i: Int?,
        val n: ExampleClass.Nested,
    ) {
        class Nested(val s: String)
    }

    data class ExampleDataClass(
        val s: String,
        val i: Int?,
        val n: ExampleDataClass.Nested,
    ) {
        data class Nested(val b: Boolean)
    }

    class ExampleClassWithPrivateConstructor private constructor(val s: String)

    object ExampleObject

    data class ExampleGenericDataClass<T>(
        val t: T,
        val n: Nested<Int>,
    ) {
        data class Nested<V>(val v: V)
    }

    interface ExampleInterface {
        val s: String

        data class Impl(override val s: String) : ExampleInterface
    }

    sealed interface ExampleSealedInterface {
        val s: String

        data class Impl(override val s: String) : ExampleSealedInterface
    }

    abstract class ExampleAbstractClass {
        abstract val s: String

        data class Impl(override val s: String) : ExampleAbstractClass()
    }

    sealed class ExampleSealedClass {
        abstract val s: String

        data class Impl(override val s: String) : ExampleSealedClass()
    }
    // endregion
}
