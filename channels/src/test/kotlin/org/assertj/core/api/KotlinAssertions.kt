/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.

 * Copyright 2012-2016 the original author or authors.
 */

package org.assertj.core.api

import org.assertj.core.api.filter.Filters
import org.assertj.core.api.filter.InFilter
import org.assertj.core.api.filter.NotFilter
import org.assertj.core.api.filter.NotInFilter
import org.assertj.core.condition.AllOf
import org.assertj.core.condition.AnyOf
import org.assertj.core.condition.DoesNotHave
import org.assertj.core.condition.Not
import org.assertj.core.data.Index
import org.assertj.core.data.MapEntry
import org.assertj.core.data.Offset
import org.assertj.core.data.Percentage
import org.assertj.core.groups.Properties
import org.assertj.core.groups.Tuple
import org.assertj.core.util.Files
import org.assertj.core.util.GroupFormatUtil
import org.assertj.core.util.URLs
import org.assertj.core.util.introspection.FieldSupport
import java.io.File
import java.io.InputStream
import java.math.BigDecimal
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.text.DateFormat
import java.util.*

/**
 * Entry point for assertion methods for different data types. Each method in this class is a static factory for the
 * type-specific assertion objects. The purpose of this class is to make test code more readable.
 *
 *
 * For example:
 * ` int removed = employees.removeFired();
 * [assertThat][Assertions.assertThat](removed).[isZero][IntegerAssert.isZero]();

 * List&lt;Employee&gt; newEmployees = employees.hired(TODAY);
 * [assertThat][Assertions.assertThat](newEmployees).[hasSize][IterableAssert.hasSize](6);`
 *
 *
 *

 * @author Alex Ruiz
 * *
 * @author Yvonne Wang
 * *
 * @author David DIDIER
 * *
 * @author Ted Young
 * *
 * @author Joel Costigliola
 * *
 * @author Matthieu Baechler
 * *
 * @author Mikhail Mazursky
 * *
 * @author Nicolas François
 * *
 * @author Julien Meddah
 * *
 * @author William Delanoue
 * *
 * @author Turbo87
 * *
 * @author dorzey
 */
object KotlinAssertions {
}

/**
 * Creates a new instance of `[BigDecimalAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: BigDecimal?): AbstractBigDecimalAssert<*> {
    return BigDecimalAssert(actual)
}

/**
 * Creates a new instance of `[UriAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: URI?): AbstractUriAssert<*> {
    return UriAssert(actual)
}

/**
 * Creates a new instance of `[UrlAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: URL?): AbstractUrlAssert<*> {
    return UrlAssert(actual)
}

/**
 * Creates a new instance of `[BooleanAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Boolean?): AbstractBooleanAssert<*> {
    return BooleanAssert(actual)
}

/**
 * Creates a new instance of `[BooleanArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: BooleanArray): AbstractBooleanArrayAssert<*> {
    return BooleanArrayAssert(actual)
}

/**
 * Creates a new instance of `[ByteAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Byte?): AbstractByteAssert<*> {
    return ByteAssert(actual)
}

/**
 * Creates a new instance of `[ByteArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: ByteArray): AbstractByteArrayAssert<*> {
    return ByteArrayAssert(actual)
}

/**
 * Creates a new instance of `[CharacterAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Char?): AbstractCharacterAssert<*> {
    return CharacterAssert(actual)
}

/**
 * Creates a new instance of `[CharArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: CharArray): AbstractCharArrayAssert<*> {
    return CharArrayAssert(actual)
}

/**
 * Creates a new instance of `[ClassAssert]`

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Class<*>?): AbstractClassAssert<*> {
    return ClassAssert(actual)
}

/**
 * Creates a new instance of `[GenericComparableAssert]` with
 * standard comparison semantics.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T : Comparable<T>> assertThat(actual: T): AbstractComparableAssert<*, T> {
    return GenericComparableAssert(actual)
}

/**
 * Creates a new instance of `[IterableAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T> assertThat(actual: Iterable<T>): AbstractIterableAssert<*, out Iterable<T>, T> {
    return IterableAssert<T>(actual)
}

/**
 * Creates a new instance of `[IterableAssert]`.
 *
 *
 * **Be aware that calls to most methods on returned IterableAssert will consume Iterator so it won't be possible to
 * iterate over it again.** Calling multiple methods on returned IterableAssert is safe as Iterator's elements are
 * cached by IterableAssert first time Iterator is consumed.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T> assertThat(actual: Iterator<T>): AbstractIterableAssert<*, out Iterable<T>, T> {
    return IterableAssert<T>(actual)
}

/**
 * Creates a new instance of `[DoubleAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Double?): AbstractDoubleAssert<*> {
    return DoubleAssert(actual)
}

/**
 * Creates a new instance of `[DoubleArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: DoubleArray): AbstractDoubleArrayAssert<*> {
    return DoubleArrayAssert(actual)
}

/**
 * Creates a new instance of `[FileAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: File?): AbstractFileAssert<*> {
    return FileAssert(actual)
}

/**
 * Creates a new instance of `[InputStreamAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: InputStream): AbstractInputStreamAssert<*, out InputStream> {
    return InputStreamAssert(actual)
}

/**
 * Creates a new instance of `[FloatAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Float?): AbstractFloatAssert<*> {
    return FloatAssert(actual)
}

/**
 * Creates a new instance of `[FloatArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: FloatArray): AbstractFloatArrayAssert<*> {
    return FloatArrayAssert(actual)
}

/**
 * Creates a new instance of `[IntegerAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Int?): AbstractIntegerAssert<*> {
    return IntegerAssert(actual)
}

/**
 * Creates a new instance of `[IntArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: IntArray): AbstractIntArrayAssert<*> {
    return IntArrayAssert(actual)
}

/**
 * Creates a new instance of `[ListAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T> assertThat(actual: List<T>?): AbstractListAssert<*, out List<T>, T> {
    return ListAssert(actual)
}

/**
 * Creates a new instance of `[LongAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Long?): AbstractLongAssert<*> {
    return LongAssert(actual)
}

/**
 * Creates a new instance of `[LongArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: LongArray): AbstractLongArrayAssert<*> {
    return LongArrayAssert(actual)
}

/**
 * Creates a new instance of `[ObjectAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T : Any?> assertThat(actual: T): AbstractObjectAssert<*, T> {
    return ObjectAssert(actual)
}

/**
 * Returns the given assertion. This method improves code readability by surrounding the given assertion with
 * `assertThat`.
 *
 *
 * Consider for example the following MyButton and MyButtonAssert classes:
 * ` public class MyButton extends JButton {

 * private boolean blinking;

 * public boolean isBlinking() { return this.blinking; }

 * public void setBlinking(boolean blink) { this.blinking = blink; }

 * }

 * private static class MyButtonAssert implements AssertDelegateTarget {

 * private MyButton button;
 * MyButtonAssert(MyButton button) { this.button = button; }

 * void isBlinking() {
 * // standard assertion from core Assertions.assertThat
 * assertThat(button.isBlinking()).isTrue();
 * }

 * void isNotBlinking() {
 * // standard assertion from core Assertions.assertThat
 * assertThat(button.isBlinking()).isFalse();
 * }
 * }`

 * As MyButtonAssert implements AssertDelegateTarget, you can use `assertThat(buttonAssert).isBlinking();`
 * instead of `buttonAssert.isBlinking();` to have easier to read assertions:
 * ` @Test
 * public void AssertDelegateTarget_example() {

 * MyButton button = new MyButton();
 * MyButtonAssert buttonAssert = new MyButtonAssert(button);

 * // you can encapsulate MyButtonAssert assertions methods within assertThat
 * assertThat(buttonAssert).isNotBlinking(); // same as : buttonAssert.isNotBlinking();

 * button.setBlinking(true);

 * assertThat(buttonAssert).isBlinking(); // same as : buttonAssert.isBlinking();
 * }`

 * @param  the generic type of the user-defined assert.
 * *
 * @param assertion the assertion to return.
 * *
 * @return the given assertion.
 */
fun <T : AssertDelegateTarget> assertThat(assertion: T): T {
    return assertion
}

/**
 * Delegates the creation of the [Assert] to the [AssertProvider.assertThat] of the given component.

 *
 *
 * Read the comments on [AssertProvider] for an example of its usage.
 *

 * @param component
 * *          the component that creates its own assert
 * *
 * @return the associated [Assert] of the given component
 */
fun <T> assertThat(component: AssertProvider<T>): T {
    return component.assertThat()
}

/**
 * Creates a new instance of `[ObjectArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <T> assertThat(actual: Array<T>): AbstractObjectArrayAssert<*, T> {
    return ObjectArrayAssert(actual)
}

/**
 * Creates a new instance of `[MapAssert]`.
 *
 *
 * Returned type is [MapAssert] as it overrides method to annotate them with [SafeVarargs] avoiding
 * annoying warnings.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun <K, V> assertThat(actual: Map<K, V>): MapAssert<K, V> {
    return MapAssert(actual)
}

/**
 * Creates a new instance of `[ShortAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Short): AbstractShortAssert<*> {
    return ShortAssert(actual)
}

/**
 * Creates a new instance of `[ShortAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Short?): AbstractShortAssert<*> {
    return ShortAssert(actual)
}

/**
 * Creates a new instance of `[ShortArrayAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: ShortArray): AbstractShortArrayAssert<*> {
    return ShortArrayAssert(actual)
}

/**
 * Creates a new instance of `[CharSequenceAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: CharSequence?): AbstractCharSequenceAssert<*, out CharSequence?> {
    return CharSequenceAssert(actual)
}

/**
 * Creates a new instance of `[StringAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: String?): AbstractCharSequenceAssert<*, String?> {
    return StringAssert(actual)
}

/**
 * Creates a new instance of `[DateAssert]`.

 * @param actual the actual value.
 * *
 * @return the created assertion object.
 */
fun assertThat(actual: Date): AbstractDateAssert<*> {
    return DateAssert(actual)
}

/**
 * Creates a new instance of `[ThrowableAssert]`.

 * @param actual the actual value.
 * *
 * @return the created [ThrowableAssert].
 */
fun assertThat(actual: Throwable): AbstractThrowableAssert<*, out Throwable> {
    return ThrowableAssert(actual)
}

/**
 * Allows to capture and then assert on a [Throwable] more easily when used with Java 8 lambdas.

 *
 *
 * Java 8 example :
 * `  @Test
 * public void testException() {
 * assertThatThrownBy(() -> { throw new Exception("boom!") }).isInstanceOf(Exception.class)
 * .hasMessageContaining("boom");
 * }`

 *
 *
 * Java 7 example :
 * ` assertThatThrownBy(new ThrowingCallable() {

 * @Override
 * public void call() throws Exception {
 * throw new Exception("boom!");
 * }

 * }).isInstanceOf(Exception.class)
 * .hasMessageContaining("boom");`

 * If the provided [ThrowingCallable] does not raise an exception, an error is immediately raised,
 * in that case the test description provided with [as(String, Object...)][AbstractAssert.as] is not honored.
 * To use a test description, use [catchThrowable][.catchThrowable] as shown below.
 * ` // assertion will fail but "display me" won't appear in the error
 * assertThatThrownBy(() -> { // do nothing }).as("display me").isInstanceOf(Exception.class);

 * // assertion will fail AND "display me" will appear in the error
 * Throwable thrown = catchThrowable(() -> { // do nothing });
 * assertThat(thrown).as("display me").isInstanceOf(Exception.class); `

 * @param shouldRaiseThrowable The [ThrowingCallable] or lambda with the code that should raise the throwable.
 * *
 * @return The captured exception or `null` if none was raised by the callable.
 */
fun assertThatThrownBy(shouldRaiseThrowable: ThrowableAssert.ThrowingCallable): AbstractThrowableAssert<*, out Throwable> {
    return ThrowableAssert(catchThrowable(shouldRaiseThrowable)).hasBeenThrown()
}

/**
 * Allows to catch an [Throwable] more easily when used with Java 8 lambdas.

 *
 *
 * This caught [Throwable] can then be asserted.
 *

 *
 *
 * Java 8 example:
 * ` @Test
 * public void testException() {
 * // when
 * Throwable thrown = catchThrowable(() -> { throw new Exception("boom!") });

 * // then
 * assertThat(thrown).isInstanceOf(Exception.class)
 * .hasMessageContaining("boom");
 * }`

 *
 *
 * Java 7 example:
 * ` @Test
 * public void testException() {
 * // when
 * Throwable thrown = catchThrowable(new ThrowingCallable() {

 * @Override
 * public void call() throws Exception {
 * throw new Exception("boom!");
 * }

 * })
 * // then
 * assertThat(thrown).isInstanceOf(Exception.class)
 * .hasMessageContaining("boom");
 * }`

 * @param shouldRaiseThrowable The lambda with the code that should raise the exception.
 * *
 * @return The captured exception or `null` if none was raised by the callable.
 */
fun catchThrowable(shouldRaiseThrowable: ThrowableAssert.ThrowingCallable): Throwable {
    return ThrowableAssert.catchThrowable(shouldRaiseThrowable)
}

// -------------------------------------------------------------------------------------------------
// fail methods : not assertions but here to have a single entry point to all AssertJ features.
// -------------------------------------------------------------------------------------------------

/**
 * Only delegate to [Fail.setRemoveAssertJRelatedElementsFromStackTrace] so that Assertions offers a
 * full feature entry point to all AssertJ Assert features (but you can use [Fail] if you prefer).
 */
fun setRemoveAssertJRelatedElementsFromStackTrace(removeAssertJRelatedElementsFromStackTrace: Boolean) {
    Fail.setRemoveAssertJRelatedElementsFromStackTrace(removeAssertJRelatedElementsFromStackTrace)
}

/**
 * Only delegate to [Fail.fail] so that Assertions offers a full feature entry point to all Assertj
 * Assert features (but you can use Fail if you prefer).
 */
fun fail(failureMessage: String) {
    Fail.fail(failureMessage)
}

/**
 * Only delegate to [Fail.fail] so that Assertions offers a full feature entry point to all
 * AssertJ features (but you can use Fail if you prefer).
 */
fun fail(failureMessage: String, realCause: Throwable) {
    Fail.fail(failureMessage, realCause)
}

/**
 * Only delegate to [Fail.failBecauseExceptionWasNotThrown] so that Assertions offers a full feature
 * entry point to all AssertJ features (but you can use Fail if you prefer).

 * [KotlinAssertions.shouldHaveThrown] can be used as a replacement.
 */
fun failBecauseExceptionWasNotThrown(exceptionClass: Class<out Throwable>) {
    Fail.shouldHaveThrown(exceptionClass)
}

/**
 * Only delegate to [Fail.shouldHaveThrown] so that Assertions offers a full feature
 * entry point to all AssertJ features (but you can use Fail if you prefer).
 */
fun shouldHaveThrown(exceptionClass: Class<out Throwable>) {
    Fail.shouldHaveThrown(exceptionClass)
}

/**
 * In error messages, sets the threshold when iterable/array formatting will on one line (if their String description
 * is less than this parameter) or it will be formatted with one element per line.
 *
 *
 * The following array will be formatted on one line as its length < 80:
 * ` String[] greatBooks = array("A Game of Thrones", "The Lord of the Rings", "Assassin's Apprentice");

 * // formatted as:

 * ["A Game of Thrones", "The Lord of the Rings", "Assassin's Apprentice"]`

 * whereas this array is formatted on multiple lines (one element per line):
 * ` String[] greatBooks = array("A Game of Thrones", "The Lord of the Rings", "Assassin's Apprentice", "Guards! Guards! (Discworld)");

 * // formatted as:

 * ["A Game of Thrones",
 * "The Lord of the Rings",
 * "Assassin's Apprentice",
 * "Guards! Guards! (Discworld)"]`

 * @param maxLengthForSingleLineDescription the maximum lenght for an iterable/array to be displayed on one line
 */
fun setMaxLengthForSingleLineDescription(maxLengthForSingleLineDescription: Int) {
    GroupFormatUtil.setMaxLengthForSingleLineDescription(maxLengthForSingleLineDescription)
}

// ------------------------------------------------------------------------------------------------------
// properties methods : not assertions but here to have a single entry point to all AssertJ features.
// ------------------------------------------------------------------------------------------------------

/**
 * Only delegate to [Properties.extractProperty] so that Assertions offers a full feature entry point
 * to
 * all AssertJ features (but you can use [Properties] if you prefer).
 *
 *
 * Typical usage is to chain `extractProperty` with `from` method, see examples below :
 * ` // extract simple property values having a java standard type (here String)
 * assertThat(extractProperty(&quot;name&quot;, String.class).from(fellowshipOfTheRing)).contains(&quot;
 * Boromir&quot;, &quot;Gandalf&quot;, &quot;Frodo&quot;,
 * &quot;Legolas&quot;).doesNotContain(&quot;Sauron&quot;, &quot;Elrond&quot;);

 * // extracting property works also with user's types (here Race)
 * assertThat(extractProperty(&quot;race&quot;, String.class).from(fellowshipOfTheRing)).contains(HOBBIT,
 * ELF).doesNotContain(ORC);

 * // extract nested property on Race
 * assertThat(extractProperty(&quot;race.name&quot;, String.class).from(fellowshipOfTheRing)).contains(&quot;
 * Hobbit&quot;, &quot;Elf&quot;)
 * .doesNotContain(&quot;Orc&quot;);`
 */
fun <T> extractProperty(propertyName: String, propertyType: Class<T>): Properties<T> {
    return Properties.extractProperty(propertyName, propertyType)
}

/**
 * Only delegate to [Properties.extractProperty] so that Assertions offers a full feature entry point
 * to
 * all AssertJ features (but you can use [Properties] if you prefer).
 *
 *
 * Typical usage is to chain `extractProperty` with `from` method, see examples below :
 * ` // extract simple property values, as no type has been defined the extracted property will be considered as Object
 * // to define the real property type (here String) use extractProperty(&quot;name&quot;, String.class) instead.
 * assertThat(extractProperty(&quot;name&quot;).from(fellowshipOfTheRing)).contains(&quot;Boromir&quot;,
 * &quot;Gandalf&quot;, &quot;Frodo&quot;, &quot;Legolas&quot;)
 * .doesNotContain(&quot;Sauron&quot;, &quot;Elrond&quot;);

 * // extracting property works also with user's types (here Race), even though it will be considered as Object
 * // to define the real property type (here String) use extractProperty(&quot;name&quot;, Race.class) instead.
 * assertThat(extractProperty(&quot;race&quot;).from(fellowshipOfTheRing)).contains(HOBBIT, ELF).doesNotContain(ORC);

 * // extract nested property on Race
 * assertThat(extractProperty(&quot;race.name&quot;).from(fellowshipOfTheRing)).contains(&quot;Hobbit&quot;,
 * &quot;Elf&quot;).doesNotContain(&quot;Orc&quot;);`
 */
fun extractProperty(propertyName: String): Properties<Any> {
    return Properties.extractProperty(propertyName)
}

/**
 * Utility method to build nicely a [Tuple] when working with [IterableAssert.extracting] or
 * [ObjectArrayAssert.extracting]

 * @param values the values stored in the [Tuple]
 * *
 * @return the built [Tuple]
 */
fun tuple(vararg values: Any): Tuple {
    return Tuple.tuple(*values)
}

/**
 * Globally sets whether
 * `[IterableAssert#extracting(String)][org.assertj.core.api.AbstractIterableAssert.extracting]`
 * and
 * `[ObjectArrayAssert#extracting(String)][org.assertj.core.api.AbstractObjectArrayAssert.extracting]`
 * should be allowed to extract private fields, if not and they try it fails with exception.

 * @param allowExtractingPrivateFields allow private fields extraction. Default `true`.
 */
fun setAllowExtractingPrivateFields(allowExtractingPrivateFields: Boolean) {
    FieldSupport.extraction().setAllowUsingPrivateFields(allowExtractingPrivateFields)
}

/**
 * Globally sets whether the use of private fields is allowed for comparison.
 * The following (incomplete) list of methods will be impacted by this change :
 *
 *  *
 * ``[org.assertj.core.api.AbstractIterableAssert.usingElementComparatorOnFields]`
` *
 *  * `[org.assertj.core.api.AbstractObjectAssert.isEqualToComparingFieldByField]`
 *

 * If the value is `false` and these methods try to compare private fields, it will fail with an exception.

 * @param allowComparingPrivateFields allow private fields comparison. Default `true`.
 */
fun setAllowComparingPrivateFields(allowComparingPrivateFields: Boolean) {
    FieldSupport.comparison().setAllowUsingPrivateFields(allowComparingPrivateFields)
}

// ------------------------------------------------------------------------------------------------------
// Data utility methods : not assertions but here to have a single entry point to all AssertJ features.
// ------------------------------------------------------------------------------------------------------

/**
 * Only delegate to [MapEntry.entry] so that Assertions offers a full feature entry point to
 * all
 * AssertJ features (but you can use [MapEntry] if you prefer).
 *
 *
 * Typical usage is to call `entry` in MapAssert `contains` assertion, see examples below :
 * ` Map, TolkienCharacter> ringBearers = ... // init omitted

 * assertThat(ringBearers).contains(entry(oneRing, frodo), entry(nenya, galadriel));`
 */
fun <K, V> entry(key: K, value: V): MapEntry<K, V> {
    return MapEntry.entry(key, value)
}

/**
 * Only delegate to [Index.atIndex] so that Assertions offers a full feature entry point to all AssertJ
 * features (but you can use [Index] if you prefer).
 *
 *
 * Typical usage :
 * ` List&lt;Ring&gt; elvesRings = newArrayList(vilya, nenya, narya);
 * assertThat(elvesRings).contains(vilya, atIndex(0)).contains(nenya, atIndex(1)).contains(narya, atIndex(2));`
 */
fun atIndex(index: Int): Index {
    return Index.atIndex(index)
}

/**
 * Assertions entry point for double [Offset].
 *
 *
 * Typical usage :
 * ` assertThat(8.1).isEqualTo(8.0, offset(0.1));`
 */
fun offset(value: Double?): Offset<Double> {
    return Offset.offset<Double>(value)
}

/**
 * Assertions entry point for float [Offset].
 *
 *
 * Typical usage :
 * ` assertThat(8.2f).isCloseTo(8.0f, offset(0.2f));`
 */
fun offset(value: Float?): Offset<Float> {
    return Offset.offset<Float>(value)
}

/**
 * Alias for [.offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(8.1).isCloseTo(8.0, within(0.1));`
 */
fun within(value: Double?): Offset<Double> {
    return Offset.offset<Double>(value)
}

/**
 * Alias for [.offset] to use with real number assertions.
 *
 *
 * Typical usage :
 * ` assertThat(8.1).isEqualTo(8.0, withPrecision(0.1));`
 */
fun withPrecision(value: Double?): Offset<Double> {
    return Offset.offset<Double>(value)
}

/**
 * Alias for [.offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(8.2f).isCloseTo(8.0f, within(0.2f));`
 */
fun within(value: Float?): Offset<Float> {
    return Offset.offset<Float>(value)
}

/**
 * Alias for [.offset] to use with real number assertions.
 *
 *
 * Typical usage :
 * ` assertThat(8.2f).isEqualTo(8.0f, withPrecision(0.2f));`
 */
fun withPrecision(value: Float?): Offset<Float> {
    return Offset.offset<Float>(value)
}

/**
 * Assertions entry point for BigDecimal [Offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(BigDecimal.TEN).isCloseTo(new BigDecimal("10.5"), within(BigDecimal.ONE));`
 */
fun within(value: BigDecimal): Offset<BigDecimal> {
    return Offset.offset(value)
}

/**
 * Assertions entry point for Byte [Offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat((byte)10).isCloseTo((byte)11, within((byte)1));`
 */
fun within(value: Byte?): Offset<Byte> {
    return Offset.offset<Byte>(value)
}

/**
 * Assertions entry point for Integer [Offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(10).isCloseTo(11, within(1));`
 */
fun within(value: Int?): Offset<Int> {
    return Offset.offset<Int>(value)
}

/**
 * Assertions entry point for Short [Offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(10).isCloseTo(11, within(1));`
 */
fun within(value: Short?): Offset<Short> {
    return Offset.offset<Short>(value)
}

/**
 * Assertions entry point for Long [Offset] to use with isCloseTo assertions.
 *
 *
 * Typical usage :
 * ` assertThat(5l).isCloseTo(7l, within(2l));`
 */
fun within(value: Long?): Offset<Long> {
    return Offset.offset<Long>(value)
}

/**
 * Assertions entry point for Double [org.assertj.core.data.Percentage] to use with isCloseTo assertions for
 * percentages.
 *
 *
 * Typical usage :
 * ` assertThat(11.0).isCloseTo(10.0, withinPercentage(10.0));`
 */
fun withinPercentage(value: Double?): Percentage {
    return Percentage.withPercentage(value!!)
}

/**
 * Assertions entry point for Integer [org.assertj.core.data.Percentage] to use with isCloseTo assertions for
 * percentages.
 *
 *
 * Typical usage :
 * ` assertThat(11).isCloseTo(10, withinPercentage(10));`
 */
fun withinPercentage(value: Int?): Percentage {
    return withinPercentage(value?.toDouble())
}

/**
 * Assertions entry point for Long [org.assertj.core.data.Percentage] to use with isCloseTo assertions for
 * percentages.
 *
 *
 * Typical usage :
 * ` assertThat(11L).isCloseTo(10L, withinPercentage(10L));`
 */
fun withinPercentage(value: Long?): Percentage {
    return withinPercentage(value?.toDouble())
}

// ------------------------------------------------------------------------------------------------------
// Condition methods : not assertions but here to have a single entry point to all AssertJ features.
// ------------------------------------------------------------------------------------------------------

/**
 * Creates a new `[AllOf]`

 * @param  the type of object the given condition accept.
 * *
 * @param conditions the conditions to evaluate.
 * *
 * @return the created `AnyOf`.
 * *
 * @throws NullPointerException if the given array is `null`.
 * *
 * @throws NullPointerException if any of the elements in the given array is `null`.
 */
@SafeVarargs
fun <T> allOf(vararg conditions: Condition<in T>): Condition<T> {
    return AllOf.allOf(*conditions)
}

/**
 * Creates a new `[AllOf]`

 * @param  the type of object the given condition accept.
 * *
 * @param conditions the conditions to evaluate.
 * *
 * @return the created `AnyOf`.
 * *
 * @throws NullPointerException if the given iterable is `null`.
 * *
 * @throws NullPointerException if any of the elements in the given iterable is `null`.
 */
fun <T> allOf(conditions: Iterable<Condition<in T>>): Condition<T> {
    return AllOf.allOf(conditions)
}

/**
 * Only delegate to [AnyOf.anyOf] so that Assertions offers a full feature entry point to all
 * AssertJ features (but you can use [AnyOf] if you prefer).
 *
 *
 * Typical usage (`jedi` and `sith` are [Condition]) :
 * ` assertThat(&quot;Vader&quot;).is(anyOf(jedi, sith));`
 */
@SafeVarargs
fun <T> anyOf(vararg conditions: Condition<in T>): Condition<T> {
    return AnyOf.anyOf(*conditions)
}

/**
 * Creates a new `[AnyOf]`

 * @param  the type of object the given condition accept.
 * *
 * @param conditions the conditions to evaluate.
 * *
 * @return the created `AnyOf`.
 * *
 * @throws NullPointerException if the given iterable is `null`.
 * *
 * @throws NullPointerException if any of the elements in the given iterable is `null`.
 */
fun <T> anyOf(conditions: Iterable<Condition<in T>>): Condition<T> {
    return AnyOf.anyOf(conditions)
}

/**
 * Creates a new [DoesNotHave].

 * @param condition the condition to inverse.
 * *
 * @return The Not condition created.
 */
fun <T> doesNotHave(condition: Condition<in T>): DoesNotHave<T> {
    return DoesNotHave.doesNotHave(condition)
}

/**
 * Creates a new [Not].

 * @param condition the condition to inverse.
 * *
 * @return The Not condition created.
 */
fun <T> not(condition: Condition<in T>): Not<T> {
    return Not.not(condition)
}

// --------------------------------------------------------------------------------------------------
// Filter methods : not assertions but here to have a single entry point to all AssertJ features.
// --------------------------------------------------------------------------------------------------

/**
 * Only delegate to [Filters.filter] so that Assertions offers a full feature entry point to all
 * AssertJ features (but you can use [Filters] if you prefer).
 *
 *
 * Note that the given array is not modified, the filters are performed on an [Iterable] copy of the array.
 *
 *
 * Typical usage with [Condition] :
 * ` assertThat(filter(players).being(potentialMVP).get()).containsOnly(james, rose);`
 *
 *
 * and with filter language based on java bean property :
 * ` assertThat(filter(players).with(&quot;pointsPerGame&quot;).greaterThan(20).and(&quot;assistsPerGame&quot;)
 * .greaterThan(7).get()).containsOnly(james, rose);`
 */
fun <E> filter(array: Array<E>): Filters<E> {
    return Filters.filter(array)
}

/**
 * Only delegate to [Filters.filter] so that Assertions offers a full feature entry point to all
 * AssertJ features (but you can use [Filters] if you prefer).
 *
 *
 * Note that the given [Iterable] is not modified, the filters are performed on a copy.
 *
 *
 * Typical usage with [Condition] :
 * ` assertThat(filter(players).being(potentialMVP).get()).containsOnly(james, rose);`
 *
 *
 * and with filter language based on java bean property :
 * ` assertThat(filter(players).with(&quot;pointsPerGame&quot;).greaterThan(20).and(&quot;assistsPerGame&quot;)
 * .greaterThan(7).get()).containsOnly(james, rose);`
 */
fun <E> filter(iterableToFilter: Iterable<E>): Filters<E> {
    return Filters.filter(iterableToFilter)
}

/**
 * Create a [FilterOperator] to use in [ filteredOn(String, FilterOperation)][AbstractIterableAssert.filteredOn] to express a filter keeping all Iterable elements whose property/field
 * value matches one of the given values.
 *
 *
 * As often, an example helps:
 * ` Employee yoda   = new Employee(1L, new Name("Yoda"), 800);
 * Employee obiwan = new Employee(2L, new Name("Obiwan"), 800);
 * Employee luke   = new Employee(3L, new Name("Luke", "Skywalker"), 26);
 * Employee noname = new Employee(4L, null, 50);

 * List&lt;Employee&gt; employees = newArrayList(yoda, luke, obiwan, noname);

 * assertThat(employees).filteredOn("age", in(800, 26))
 * .containsOnly(yoda, obiwan, luke);`

 * @param values values to match (one match is sufficient)
 * *
 * @return the created "in" filter
 */
fun `in`(vararg values: Any): InFilter {
    return InFilter.`in`(*values)
}

/**
 * Create a [FilterOperator] to use in [ filteredOn(String, FilterOperation)][AbstractIterableAssert.filteredOn] to express a filter keeping all Iterable elements whose property/field
 * value matches does not match any of the given values.
 *
 *
 * As often, an example helps:
 * ` Employee yoda   = new Employee(1L, new Name("Yoda"), 800);
 * Employee obiwan = new Employee(2L, new Name("Obiwan"), 800);
 * Employee luke   = new Employee(3L, new Name("Luke", "Skywalker"), 26);
 * Employee noname = new Employee(4L, null, 50);

 * List&lt;Employee&gt; employees = newArrayList(yoda, luke, obiwan, noname);

 * assertThat(employees).filteredOn("age", notIn(800, 50))
 * .containsOnly(luke);`

 * @param valuesNotToMatch values not to match (none of the values must match)
 * *
 * @return the created "not in" filter
 */
fun notIn(vararg valuesNotToMatch: Any): NotInFilter {
    return NotInFilter.notIn(*valuesNotToMatch)
}

/**
 * Create a [FilterOperator] to use in [ filteredOn(String, FilterOperation)][AbstractIterableAssert.filteredOn] to express a filter keeping all Iterable elements whose property/field
 * value matches does not match the given value.
 *
 *
 * As often, an example helps:
 * ` Employee yoda   = new Employee(1L, new Name("Yoda"), 800);
 * Employee obiwan = new Employee(2L, new Name("Obiwan"), 800);
 * Employee luke   = new Employee(3L, new Name("Luke", "Skywalker"), 26);
 * Employee noname = new Employee(4L, null, 50);

 * List&lt;Employee&gt; employees = newArrayList(yoda, luke, obiwan, noname);

 * assertThat(employees).filteredOn("age", not(800))
 * .containsOnly(luke, noname);`

 * @param valueNotToMatch the value not to match
 * *
 * @return the created "not" filter
 */
fun not(valueNotToMatch: Any): NotFilter {
    return NotFilter.not(valueNotToMatch)
}

// --------------------------------------------------------------------------------------------------
// File methods : not assertions but here to have a single entry point to all AssertJ features.
// --------------------------------------------------------------------------------------------------

/**
 * Loads the text content of a file, so that it can be passed to [.assertThat].
 *
 *
 * Note that this will load the entire file in memory; for larger files, there might be a more efficient alternative
 * with [.assertThat].
 *

 * @param file the file.
 * *
 * @param charset the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(file: File, charset: Charset): String {
    return Files.contentOf(file, charset)
}

/**
 * Loads the text content of a file, so that it can be passed to [.assertThat].
 *
 *
 * Note that this will load the entire file in memory; for larger files, there might be a more efficient alternative
 * with [.assertThat].
 *

 * @param file the file.
 * *
 * @param charsetName the name of the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws IllegalArgumentException if the given character set is not supported on this platform.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(file: File, charsetName: String): String {
    return Files.contentOf(file, charsetName)
}

/**
 * Loads the text content of a file with the default character set, so that it can be passed to
 * [.assertThat].
 *
 *
 * Note that this will load the entire file in memory; for larger files, there might be a more efficient alternative
 * with [.assertThat].
 *

 * @param file the file.
 * *
 * @return the content of the file.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(file: File): String {
    return Files.contentOf(file, Charset.defaultCharset())
}

/**
 * Loads the text content of a file into a list of strings with the default charset, each string corresponding to a
 * line.
 * The line endings are either \n, \r or \r\n.

 * @param file the file.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(file: File): List<String> {
    return Files.linesOf(file, Charset.defaultCharset())
}

/**
 * Loads the text content of a file into a list of strings, each string corresponding to a line.
 * The line endings are either \n, \r or \r\n.

 * @param file the file.
 * *
 * @param charset the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(file: File, charset: Charset): List<String> {
    return Files.linesOf(file, charset)
}

/**
 * Loads the text content of a file into a list of strings, each string corresponding to a line. The line endings are
 * either \n, \r or \r\n.

 * @param file the file.
 * *
 * @param charsetName the name of the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(file: File, charsetName: String): List<String> {
    return Files.linesOf(file, charsetName)
}

// --------------------------------------------------------------------------------------------------
// URL/Resource methods : not assertions but here to have a single entry point to all AssertJ features.
// --------------------------------------------------------------------------------------------------

/**
 * Loads the text content of a URL, so that it can be passed to [.assertThat].
 *
 *
 * Note that this will load the entire contents in memory.
 *

 * @param url the URL.
 * *
 * @param charset the character set to use.
 * *
 * @return the content of the URL.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(url: URL, charset: Charset): String {
    return URLs.contentOf(url, charset)
}

/**
 * Loads the text content of a URL, so that it can be passed to [.assertThat].
 *
 *
 * Note that this will load the entire contents in memory.
 *

 * @param url the URL.
 * *
 * @param charsetName the name of the character set to use.
 * *
 * @return the content of the URL.
 * *
 * @throws IllegalArgumentException if the given character set is not supported on this platform.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(url: URL, charsetName: String): String {
    return URLs.contentOf(url, charsetName)
}

/**
 * Loads the text content of a URL with the default character set, so that it can be passed to
 * [.assertThat].
 *
 *
 * Note that this will load the entire file in memory; for larger files.
 *

 * @param url the URL.
 * *
 * @return the content of the file.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun contentOf(url: URL): String {
    return URLs.contentOf(url, Charset.defaultCharset())
}

/**
 * Loads the text content of a URL into a list of strings with the default charset, each string corresponding to a
 * line.
 * The line endings are either \n, \r or \r\n.

 * @param url the URL.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(url: URL): List<String> {
    return URLs.linesOf(url, Charset.defaultCharset())
}

/**
 * Loads the text content of a URL into a list of strings, each string corresponding to a line.
 * The line endings are either \n, \r or \r\n.

 * @param url the URL.
 * *
 * @param charset the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(url: URL, charset: Charset): List<String> {
    return URLs.linesOf(url, charset)
}

/**
 * Loads the text content of a URL into a list of strings, each string corresponding to a line. The line endings are
 * either \n, \r or \r\n.

 * @param url the URL.
 * *
 * @param charsetName the name of the character set to use.
 * *
 * @return the content of the file.
 * *
 * @throws NullPointerException if the given charset is `null`.
 * *
 * @throws RuntimeIOException if an I/O exception occurs.
 */
fun linesOf(url: URL, charsetName: String): List<String> {
    return URLs.linesOf(url, charsetName)
}

// --------------------------------------------------------------------------------------------------
// Date formatting methods : not assertions but here to have a single entry point to all AssertJ features.
// --------------------------------------------------------------------------------------------------

/**
 * Instead of using default strict date/time parsing, it is possible to use lenient parsing mode for default date
 * formats parser to interpret inputs that do not precisely match supported date formats (lenient parsing).
 *
 *
 * With strict parsing, inputs must match exactly date/time format.

 *
 *
 * Example:
 * ` final Date date = Dates.parse("2001-02-03");
 * final Date dateTime = parseDatetime("2001-02-03T04:05:06");
 * final Date dateTimeWithMs = parseDatetimeWithMs("2001-02-03T04:05:06.700");

 * Assertions.setLenientDateParsing(true);

 * // assertions will pass
 * assertThat(date).isEqualTo("2001-01-34");
 * assertThat(date).isEqualTo("2001-02-02T24:00:00");
 * assertThat(date).isEqualTo("2001-02-04T-24:00:00.000");
 * assertThat(dateTime).isEqualTo("2001-02-03T04:05:05.1000");
 * assertThat(dateTime).isEqualTo("2001-02-03T04:04:66");
 * assertThat(dateTimeWithMs).isEqualTo("2001-02-03T04:05:07.-300");

 * // assertions will fail
 * assertThat(date).hasSameTimeAs("2001-02-04"); // different date
 * assertThat(dateTime).hasSameTimeAs("2001-02-03 04:05:06"); // leniency does not help here`

 * To revert to default strict date parsing, call `setLenientDateParsing(false)`.

 * @param value whether lenient parsing mode should be enabled or not
 */
fun setLenientDateParsing(value: Boolean) {
    AbstractDateAssert.setLenientDateParsing(value)
}

/**
 * Add the given date format to the ones used to parse date String in String based Date assertions like
 * [org.assertj.core.api.AbstractDateAssert.isEqualTo].
 *
 *
 * User date formats are used before default ones in the order they have been registered (first registered, first
 * used).
 *
 *
 * AssertJ is gonna use any date formats registered with one of these methods :
 *
 *  * [org.assertj.core.api.AbstractDateAssert.withDateFormat]
 *  * [org.assertj.core.api.AbstractDateAssert.withDateFormat]
 *  * [.registerCustomDateFormat]
 *  * [.registerCustomDateFormat]
 *
 *
 *
 * Beware that AssertJ will use the newly registered format for **all remaining Date assertions in the test suite**
 *
 *
 * To revert to default formats only, call [.useDefaultDateFormatsOnly] or
 * [org.assertj.core.api.AbstractDateAssert.withDefaultDateFormatsOnly].
 *
 *
 * Code examples:
 * ` Date date = ... // set to 2003 April the 26th
 * assertThat(date).isEqualTo("2003-04-26");

 * try {
 * // date with a custom format : failure since the default formats don't match.
 * assertThat(date).isEqualTo("2003/04/26");
 * } catch (AssertionError e) {
 * assertThat(e).hasMessage("Failed to parse 2003/04/26 with any of these date formats: " +
 * "[yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd]");
 * }

 * // registering a custom date format to make the assertion pass
 * registerCustomDateFormat(new SimpleDateFormat("yyyy/MM/dd")); // registerCustomDateFormat("yyyy/MM/dd") would work to.
 * assertThat(date).isEqualTo("2003/04/26");

 * // the default formats are still available and should work
 * assertThat(date).isEqualTo("2003-04-26");`

 * @param userCustomDateFormat the new Date format used for String based Date assertions.
 */
fun registerCustomDateFormat(userCustomDateFormat: DateFormat) {
    AbstractDateAssert.registerCustomDateFormat(userCustomDateFormat)
}

/**
 * Add the given date format to the ones used to parse date String in String based Date assertions like
 * [org.assertj.core.api.AbstractDateAssert.isEqualTo].
 *
 *
 * User date formats are used before default ones in the order they have been registered (first registered, first
 * used).
 *
 *
 * AssertJ is gonna use any date formats registered with one of these methods :
 *
 *  * [org.assertj.core.api.AbstractDateAssert.withDateFormat]
 *  * [org.assertj.core.api.AbstractDateAssert.withDateFormat]
 *  * [.registerCustomDateFormat]
 *  * [.registerCustomDateFormat]
 *
 *
 *
 * Beware that AssertJ will use the newly registered format for **all remaining Date assertions in the test suite**
 *
 *
 * To revert to default formats only, call [.useDefaultDateFormatsOnly] or
 * [org.assertj.core.api.AbstractDateAssert.withDefaultDateFormatsOnly].
 *
 *
 * Code examples:
 * ` Date date = ... // set to 2003 April the 26th
 * assertThat(date).isEqualTo("2003-04-26");

 * try {
 * // date with a custom format : failure since the default formats don't match.
 * assertThat(date).isEqualTo("2003/04/26");
 * } catch (AssertionError e) {
 * assertThat(e).hasMessage("Failed to parse 2003/04/26 with any of these date formats: " +
 * "[yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd]");
 * }

 * // registering a custom date format to make the assertion pass
 * registerCustomDateFormat("yyyy/MM/dd");
 * assertThat(date).isEqualTo("2003/04/26");

 * // the default formats are still available and should work
 * assertThat(date).isEqualTo("2003-04-26");`

 * @param userCustomDateFormatPattern the new Date format pattern used for String based Date assertions.
 */
fun registerCustomDateFormat(userCustomDateFormatPattern: String) {
    AbstractDateAssert.registerCustomDateFormat(userCustomDateFormatPattern)
}

/**
 * Remove all registered custom date formats => use only the defaults date formats to parse string as date.
 *
 *
 * Beware that the default formats are expressed in the current local timezone.
 *
 *
 * Defaults date format are:
 *
 *  * `yyyy-MM-dd'T'HH:mm:ss.SSS`
 *  * `yyyy-MM-dd HH:mm:ss.SSS` (for [Timestamp] String representation support)
 *  * `yyyy-MM-dd'T'HH:mm:ss`
 *  * `yyyy-MM-dd`
 *
 *
 *
 * Example of valid string date representations:
 *
 *  * `2003-04-26T03:01:02.999`
 *  * `2003-04-26 03:01:02.999`
 *  * `2003-04-26T13:01:02`
 *  * `2003-04-26`
 *
 */
fun useDefaultDateFormatsOnly() {
    AbstractDateAssert.useDefaultDateFormatsOnly()
}