package Erwine.Leonard.T.wguscheduler356334.util;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.TestHelper;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CollectionHelperTest {

    private static final Predicate<String> INTEGER_PREDICATE = Pattern.compile("^\\d+$").asPredicate();
    private static final Predicate<String> DATE_PREDICATE = Pattern.compile("^\\d{4}-(0\\d|1[012])-([012]\\d|3[01])$").asPredicate();

    private static <T> Stream<T> deviate(Supplier<T> source, Function<Supplier<T>, Stream<T>> mapper) {
        Stream.Builder<T> builder = Stream.builder();
        mapper.apply(source).forEach(builder);
        return builder.build();
    }

    private Stream<Long> getNumberValues(NullSpec nullSpec) {
        switch (nullSpec) {
            case BEFORE:
                return Stream.of(null, 0L, 1L, 20L, 333L, 4040L);
            case MIDDLE:
                return Stream.of(0L, 1L, 20L, null, 333L, 4040L);
            case AFTER:
                return Stream.of(0L, 1L, 20L, 333L, 4040L, null);
            default:
                return Stream.of(0L, 1L, 20L, 333L, 4040L);
        }
    }

    private Stream<String> getNumericStringValues(NullSpec nullSpec, boolean includeEmpty) {
        switch (nullSpec) {
            case BEFORE:
                if (includeEmpty)
                    return Stream.concat(Stream.of(null, ""), getNumberValues(NullSpec.NONE).map(Object::toString));
                break;
            case MIDDLE:
                if (includeEmpty)
                    return Stream.concat(Stream.of(""), getNumberValues(NullSpec.MIDDLE).map(t -> (null == t) ? null : t.toString()));
                break;
            case AFTER:
                if (includeEmpty)
                    return Stream.concat(Stream.of(""), getNumberValues(NullSpec.AFTER).map(t -> (null == t) ? null : t.toString()));
                break;
            default:
                if (includeEmpty)
                    return Stream.concat(Stream.of(""), getNumberValues(NullSpec.NONE).map(Object::toString));
                return getNumberValues(NullSpec.NONE).map(Object::toString);
        }
        return getNumberValues(nullSpec).map(t -> (null == t) ? null : t.toString());
    }

    private Stream<String> getStringValues(NullSpec nullSpec, boolean includeEmpty) {
        switch (nullSpec) {
            case BEFORE:
                if (includeEmpty)
                    return Stream.of(null, "", "Test", "Test Data", " ", "_", "\n", "\u00A0");
                return Stream.of(null, "Test", "Test Data", " ", "_", "\n", "\u00A0");
            case MIDDLE:
                if (includeEmpty)
                    return Stream.of("", "Test", "Test Data", null, " ", "_", "\n", "\u00A0");
                return Stream.of("Test", "Test Data", null, " ", "_", "\n", "\u00A0");
            case AFTER:
                if (includeEmpty)
                    return Stream.of("", "Test", "Test Data", " ", "_", "\n", "\u00A0", null);
                return Stream.of("Test", "Test Data", " ", "_", "\n", "\u00A0", null);
            default:
                if (includeEmpty)
                    return Stream.of("", "Test", "Test Data", " ", "_", "\n", "\u00A0");
                return Stream.of("Test", "Test Data", " ", "_", "\n", "\u00A0");
        }
    }

    private Stream<LocalDate> getLocalDateValues(NullSpec nullSpec) {
        switch (nullSpec) {
            case BEFORE:
                return Stream.of(null, LocalDate.now().minusMonths(1).minusWeeks(2).minusDays(3), LocalDate.now(), LocalDate.now().plusYears(2).plusMonths(3).plusWeeks(1).plusDays(5));
            case MIDDLE:
                return Stream.of(LocalDate.now().minusMonths(1).minusWeeks(2).minusDays(3), LocalDate.now(), null, LocalDate.now().plusYears(2).plusMonths(3).plusWeeks(1).plusDays(5));
            case AFTER:
                return Stream.of(LocalDate.now().minusMonths(1).minusWeeks(2).minusDays(3), LocalDate.now(), LocalDate.now().plusYears(2).plusMonths(3).plusWeeks(1).plusDays(5), null);
            default:
                return Stream.of(LocalDate.now().minusMonths(1).minusWeeks(2).minusDays(3), LocalDate.now(), LocalDate.now().plusYears(2).plusMonths(3).plusWeeks(1).plusDays(5));
        }
    }

    private Stream<DescribedData<Function<String, Long>>> getStringNumberMappers() {
        return Stream.of(
                new DescribedData<>(t -> (null == t) ? null : ((INTEGER_PREDICATE.test(t)) ? Long.parseLong(t) : t.length()), "(null) ? null : ((\\d+) ? value : length)"),
                new DescribedData<>(t -> (null == t) ? -1L : ((INTEGER_PREDICATE.test(t)) ? Long.parseLong(t) : t.length()), "(null) ? -1 : ((\\d+) ? value : length)"),
                new DescribedData<>(t -> null, "null"),
                new DescribedData<>(t -> 0L, "0")
        );
    }

    private Stream<DescribedData<Function<Long, String>>> getNumberStringMappers() {
        return Stream.of(
                new DescribedData<>(t -> (null == t) ? null : t.toString(), "(null) ? null : toString"),
                new DescribedData<>(t -> (null == t) ? "" : t.toString(), "(null) ? empty : toString"),
                new DescribedData<>(t -> null, "null"),
                new DescribedData<>(t -> "", "empty")
        );
    }

    private Stream<DescribedData<Function<LocalDate, String>>> getLocalDateStringMappers() {
        return Stream.of(
                new DescribedData<>(t -> (null == t) ? null : t.toString(), "(null) ? null : toString"),
                new DescribedData<>(t -> (null == t) ? "" : t.toString(), "(null) ? empty : toString"),
                new DescribedData<>(t -> null, "null"),
                new DescribedData<>(t -> "", "empty")
        );
    }

    private Stream<DescribedData<Function<LocalDate, Long>>> getLocalDateNumberMappers() {
        return Stream.of(
                new DescribedData<>(LocalDateConverter::fromLocalDate, "(null) ? null : epochDay"),
                new DescribedData<>(t -> (null == t) ? 0 : LocalDateConverter.fromLocalDate(t), "(null) ? 0 : epochDay"),
                new DescribedData<>(t -> null, "null"),
                new DescribedData<>(t -> -1L, "-1")
        );
    }

    private Stream<DescribedData<Function<Long, LocalDate>>> getNumberLocalDateMappers() {
        return Stream.of(
                new DescribedData<>(LocalDateConverter::toLocalDate, "(null) ? null : dayOfYear"),
                new DescribedData<>(t -> (null == t) ? LocalDate.MAX : LocalDateConverter.toLocalDate(t), "(null) ? MAX : dayOfYear"),
                new DescribedData<>(t -> null, "null"),
                new DescribedData<>(t -> LocalDate.MIN, "MIN")
        );
    }

    private Stream<DescribedData<Predicate<Long>>> getNumberFilters() {
        return Stream.of(
                new DescribedData<>(t -> true, "true"),
                new DescribedData<>(t -> false, "false"),
                new DescribedData<>(t -> null != t && t != 0, "not null or zero"),
                new DescribedData<>(t -> null == t || t == 0, "if null or zero")
        );
    }

    private Stream<DescribedData<Predicate<LocalDate>>> getLocalDateFilters() {
        return Stream.of(
                new DescribedData<>(t -> true, "true"),
                new DescribedData<>(t -> false, "false"),
                new DescribedData<>(t -> null != t && t.compareTo(LocalDate.now()) < 0, "not null or future"),
                new DescribedData<>(t -> null == t || t.compareTo(LocalDate.now()) > 0, "if null or future")
        );
    }

    private Stream<DescribedData<Predicate<String>>> getStringFilters() {
        return Stream.of(
                new DescribedData<>(t -> true, "true"),
                new DescribedData<>(t -> false, "false"),
                new DescribedData<>(t -> !(null == t || t.trim().isEmpty()), "not null or whitespace"),
                new DescribedData<>(t -> null == t || t.trim().isEmpty(), "if null or whitespace")
        );
    }

    private Stream<DescribedData<Predicate<String>>> getNumberStringFilters() {
        return Stream.of(
                new DescribedData<>(t -> !(null != t && INTEGER_PREDICATE.test(t)), "not numeric"),
                new DescribedData<>(t -> null != t && INTEGER_PREDICATE.test(t), "if numeric")
        );
    }

    private Stream<DescribedData<Predicate<String>>> getLocalDateStringFilters() {
        return Stream.of(
                new DescribedData<>(t -> !(null != t && DATE_PREDICATE.test(t)), "not date"),
                new DescribedData<>(t -> null != t && DATE_PREDICATE.test(t), "if date")
        );
    }

    @Test
    public void testToStreamIterable() {
        //noinspection ConstantConditions
        Stream<Long> target = CollectionHelper.toStream((Iterable<Long>) null);
        assertNull(target);
        Iterator<List<Long>> intIterator = Stream.concat(
                Stream.<List<Long>>concat(
                        Stream.of(Collections.emptyList()),
                        getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                ),
                Stream.of(
                        getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                        Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                        Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                        getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                )
        ).iterator();
        do {
            List<Long> source = intIterator.next();
            String description = TestHelper.toIterableNumberDescription(source);
            target = CollectionHelper.toStream(source);
            assertNotNull(description, target);
            Iterator<Long> srcIterator = source.iterator();
            Iterator<Long> tgtIterator = target.iterator();
            int index = -1;
            while (srcIterator.hasNext()) {
                String d = String.format("%s[%d]", description, ++index);
                assertTrue(d, tgtIterator.hasNext());
                Long expected = srcIterator.next();
                Long actual = tgtIterator.next();
                if (null == expected)
                    assertNull(d, actual);
                else {
                    assertNotNull(d, actual);
                    assertEquals(d, expected, actual);
                }
            }
            assertFalse(description, tgtIterator.hasNext());
        } while (intIterator.hasNext());

        Iterator<List<String>> stringIterator = Stream.concat(
                Stream.<List<String>>concat(
                        Stream.of(Collections.emptyList()),
                        getStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                ),
                Stream.of(
                        getStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                        Stream.concat(Stream.<String>builder().add(null).build(), getStringValues(NullSpec.NONE, true).skip(1).limit(1)).collect(Collectors.toList()),
                        Stream.concat(getStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                        getStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                )
        ).iterator();
        do {
            List<String> source = stringIterator.next();
            StringBuilder sb = new StringBuilder("String(");
            if (!source.isEmpty()) {
                sb.append(TestHelper.toStringDescription(source.get(0)));
                source.stream().skip(1).forEach(t -> sb.append(", ").append(TestHelper.toStringDescription(t)));
            }
            String description = sb.append(")").toString();
            Stream<String> sTarget = CollectionHelper.toStream(source);
            assertNotNull(description, sTarget);
            Iterator<String> srcIterator = source.iterator();
            Iterator<String> tgtIterator = sTarget.iterator();
            int index = -1;
            while (srcIterator.hasNext()) {
                String d = String.format("%s[%d]", description, ++index);
                assertTrue(d, tgtIterator.hasNext());
                String expected = srcIterator.next();
                String actual = tgtIterator.next();
                if (null == expected)
                    assertNull(d, actual);
                else {
                    assertNotNull(d, actual);
                    assertEquals(d, expected, actual);
                }
            }
            assertFalse(description, tgtIterator.hasNext());
        } while (stringIterator.hasNext());
    }

    @Test
    public void testToStreamIterator() {
        //noinspection ConstantConditions
        Stream<Long> target = CollectionHelper.toStream((Iterator<Long>) null);
        assertNull(target);
        Iterator<List<Long>> intIterator = Stream.concat(
                Stream.<List<Long>>concat(
                        Stream.of(Collections.emptyList()),
                        getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                ),
                Stream.of(
                        getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                        Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                        Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                        getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                        getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                )
        ).iterator();
        do {
            List<Long> source = intIterator.next();
            String description = TestHelper.toIterableNumberDescription(source);
            target = CollectionHelper.toStream(source.iterator());
            assertNotNull(description, target);
            Iterator<Long> srcIterator = source.iterator();
            Iterator<Long> tgtIterator = target.iterator();
            int index = -1;
            while (srcIterator.hasNext()) {
                String d = String.format("%s[%d]", description, ++index);
                assertTrue(d, tgtIterator.hasNext());
                Long expected = srcIterator.next();
                Long actual = tgtIterator.next();
                if (null == expected)
                    assertNull(d, actual);
                else {
                    assertNotNull(d, actual);
                    assertEquals(d, expected, actual);
                }
            }
            assertFalse(description, tgtIterator.hasNext());
        } while (intIterator.hasNext());

        Iterator<List<String>> stringIterator = Stream.concat(
                Stream.<List<String>>concat(
                        Stream.of(Collections.emptyList()),
                        getStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                ),
                Stream.of(
                        getStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                        Stream.concat(Stream.<String>builder().add(null).build(), getStringValues(NullSpec.NONE, true).skip(1).limit(1)).collect(Collectors.toList()),
                        Stream.concat(getStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                        getStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                        getStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                )
        ).iterator();
        do {
            List<String> source = stringIterator.next();
            StringBuilder sb = new StringBuilder("String(");
            if (!source.isEmpty()) {
                sb.append(TestHelper.toStringDescription(source.get(0)));
                source.stream().skip(1).forEach(t -> sb.append(", ").append(TestHelper.toStringDescription(t)));
            }
            String description = sb.append(")").toString();
            Stream<String> sTarget = CollectionHelper.toStream(source.iterator());
            assertNotNull(description, sTarget);
            Iterator<String> srcIterator = source.iterator();
            Iterator<String> tgtIterator = sTarget.iterator();
            int index = -1;
            while (srcIterator.hasNext()) {
                String d = String.format("%s[%d]", description, ++index);
                assertTrue(d, tgtIterator.hasNext());
                String expected = srcIterator.next();
                String actual = tgtIterator.next();
                if (null == expected)
                    assertNull(d, actual);
                else {
                    assertNotNull(d, actual);
                    assertEquals(d, expected, actual);
                }
            }
            assertFalse(description, tgtIterator.hasNext());
        } while (stringIterator.hasNext());
    }

    @Test
    public void testToStreamIterableFilter() {
        Iterator<DescribedData<Predicate<Long>>> intFilterIterator = getNumberFilters().iterator();
        do {
            DescribedData<Predicate<Long>> filter = intFilterIterator.next();
            //noinspection ConstantConditions
            Stream<Long> target = CollectionHelper.toStream((Iterable<Long>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<Long>> intValuesIterator = Stream.concat(
                    Stream.<List<Long>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                            getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<Long> source = intValuesIterator.next();
                String description = TestHelper.appendIterableNumberDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<Long> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<Long> actual = CollectionHelper.toStream(source, filter.value);
                assertNotNull(description, actual);
                Iterator<Long> expectedIterator = expected.iterator();
                Iterator<Long> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    Long e = expectedIterator.next();
                    Long a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (intValuesIterator.hasNext());
        } while (intFilterIterator.hasNext());

        Iterator<DescribedData<Predicate<String>>> stringFilterIterator = getStringFilters().iterator();
        do {
            DescribedData<Predicate<String>> filter = stringFilterIterator.next();
            //noinspection ConstantConditions
            Stream<String> target = CollectionHelper.toStream((Iterable<String>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<String>> stringValuesIterator = Stream.concat(
                    Stream.<List<String>>concat(
                            Stream.of(Collections.emptyList()),
                            getStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                    ),
                    Stream.of(
                            getStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<String>builder().add(null).build(), getStringValues(NullSpec.NONE, false).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                            getStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<String> source = stringValuesIterator.next();
                String description = TestHelper.appendIterableStringDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<String> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<String> actual = CollectionHelper.toStream(source, filter.value);
                assertNotNull(description, actual);
                Iterator<String> expectedIterator = expected.iterator();
                Iterator<String> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    String e = expectedIterator.next();
                    String a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (stringValuesIterator.hasNext());
        } while (stringFilterIterator.hasNext());

        Iterator<DescribedData<Predicate<LocalDate>>> dateFilterIterator = getLocalDateFilters().iterator();
        do {
            DescribedData<Predicate<LocalDate>> filter = dateFilterIterator.next();
            //noinspection ConstantConditions
            Stream<LocalDate> target = CollectionHelper.toStream((Iterable<LocalDate>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<LocalDate>> localDateValuesIterator = Stream.concat(
                    Stream.<List<LocalDate>>concat(
                            Stream.of(Collections.emptyList()),
                            getLocalDateValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getLocalDateValues(NullSpec.NONE).skip(1).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<LocalDate>builder().add(null).build(), getLocalDateValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getLocalDateValues(NullSpec.NONE).limit(1), Stream.<LocalDate>builder().add(null).build()).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.NONE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<LocalDate> source = localDateValuesIterator.next();
                String description = TestHelper.appendIterableLocalDateDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<LocalDate> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<LocalDate> actual = CollectionHelper.toStream(source, filter.value);
                assertNotNull(description, actual);
                Iterator<LocalDate> expectedIterator = expected.iterator();
                Iterator<LocalDate> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    LocalDate e = expectedIterator.next();
                    LocalDate a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (localDateValuesIterator.hasNext());
        } while (dateFilterIterator.hasNext());
    }

    @Test
    public void testToStreamIteratorFilter() {
        Iterator<DescribedData<Predicate<Long>>> intFilterIterator = getNumberFilters().iterator();
        do {
            DescribedData<Predicate<Long>> filter = intFilterIterator.next();
            //noinspection ConstantConditions
            Stream<Long> target = CollectionHelper.toStream((Iterator<Long>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<Long>> intValuesIterator = Stream.concat(
                    Stream.<List<Long>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                            getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<Long> source = intValuesIterator.next();
                String description = TestHelper.appendIterableNumberDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<Long> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<Long> actual = CollectionHelper.toStream(source.iterator(), filter.value);
                assertNotNull(description, actual);
                Iterator<Long> expectedIterator = expected.iterator();
                Iterator<Long> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    Long e = expectedIterator.next();
                    Long a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (intValuesIterator.hasNext());
        } while (intFilterIterator.hasNext());

        Iterator<DescribedData<Predicate<String>>> stringFilterIterator = getStringFilters().iterator();
        do {
            DescribedData<Predicate<String>> filter = stringFilterIterator.next();
            //noinspection ConstantConditions
            Stream<String> target = CollectionHelper.toStream((Iterator<String>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<String>> stringValuesIterator = Stream.concat(
                    Stream.<List<String>>concat(
                            Stream.of(Collections.emptyList()),
                            getStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                    ),
                    Stream.of(
                            getStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<String>builder().add(null).build(), getStringValues(NullSpec.NONE, false).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                            getStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                            getStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<String> source = stringValuesIterator.next();
                String description = TestHelper.appendIterableStringDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<String> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<String> actual = CollectionHelper.toStream(source.iterator(), filter.value);
                assertNotNull(description, actual);
                Iterator<String> expectedIterator = expected.iterator();
                Iterator<String> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    String e = expectedIterator.next();
                    String a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (stringValuesIterator.hasNext());
        } while (stringFilterIterator.hasNext());

        Iterator<DescribedData<Predicate<LocalDate>>> dateFilterIterator = getLocalDateFilters().iterator();
        do {
            DescribedData<Predicate<LocalDate>> filter = dateFilterIterator.next();
            //noinspection ConstantConditions
            Stream<LocalDate> target = CollectionHelper.toStream((Iterator<LocalDate>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<LocalDate>> localDateValuesIterator = Stream.concat(
                    Stream.<List<LocalDate>>concat(
                            Stream.of(Collections.emptyList()),
                            getLocalDateValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getLocalDateValues(NullSpec.NONE).skip(1).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<LocalDate>builder().add(null).build(), getLocalDateValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getLocalDateValues(NullSpec.NONE).limit(1), Stream.<LocalDate>builder().add(null).build()).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.NONE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<LocalDate> source = localDateValuesIterator.next();
                String description = TestHelper.appendIterableLocalDateDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<LocalDate> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<LocalDate> actual = CollectionHelper.toStream(source.iterator(), filter.value);
                assertNotNull(description, actual);
                Iterator<LocalDate> expectedIterator = expected.iterator();
                Iterator<LocalDate> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    LocalDate e = expectedIterator.next();
                    LocalDate a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (localDateValuesIterator.hasNext());
        } while (dateFilterIterator.hasNext());
    }

    @Test
    public void testToStreamIterableMapper() {
        Iterator<DescribedData<Function<Long, String>>> intStringMapperIterator = getNumberStringMappers().iterator();
        do {
            DescribedData<Function<Long, String>> mapper = intStringMapperIterator.next();
            Stream<String> target = CollectionHelper.toStream((Iterable<Long>) null, mapper.value);
            assertNull(String.format("null -> %s", mapper.description), target);
            List<Long> ss = Stream.<Long>builder().add(null).build().collect(Collectors.toList());
            Iterator<List<Long>> intValuesIterator = Stream.concat(
                    Stream.<List<Long>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                            getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<Long> source = intValuesIterator.next();
                String description = TestHelper.appendIterableNumberDescription(new StringBuilder(), source).append(" -> ").append(mapper.description).toString();
                List<String> expected = source.stream().map(mapper.value).collect(Collectors.toList());
                Stream<String> actual = CollectionHelper.toStream(source, mapper.value);
                assertNotNull(description, actual);
                Iterator<String> expectedIterator = expected.iterator();
                Iterator<String> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    String e = expectedIterator.next();
                    String a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (intValuesIterator.hasNext());
        } while (intStringMapperIterator.hasNext());

        Iterator<DescribedData<Function<String, Long>>> stringIntMapperIterator = getStringNumberMappers().iterator();
        do {
            Iterator<List<String>> stringValuesIterator = Stream.concat(
                    Stream.<List<String>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumericStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumericStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<String>builder().add(null).build(), getNumericStringValues(NullSpec.NONE, false).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumericStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                    )
            ).iterator();
            DescribedData<Function<String, Long>> mapper = stringIntMapperIterator.next();
            //noinspection ConstantConditions
            Stream<Long> target = CollectionHelper.toStream((Iterator<String>) null, mapper.value);
            assertNull(String.format("null -> %s", mapper.description), target);
            do {
                List<String> source = stringValuesIterator.next();
                String description = TestHelper.appendIterableStringDescription(new StringBuilder(), source).append(" -> ").append(mapper.description).toString();
                List<Long> expected = source.stream().map(mapper.value).collect(Collectors.toList());
                Stream<Long> actual = CollectionHelper.toStream(source, mapper.value);
                assertNotNull(description, actual);
                Iterator<Long> expectedIterator = expected.iterator();
                Iterator<Long> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    Long e = expectedIterator.next();
                    Long a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (stringValuesIterator.hasNext());
        } while (stringIntMapperIterator.hasNext());

        Iterator<DescribedData<Function<LocalDate, String>>> localDateStringMapperIterator = getLocalDateStringMappers().iterator();
        do {
            DescribedData<Function<LocalDate, String>> mapper = localDateStringMapperIterator.next();
            //noinspection ConstantConditions
            Stream<String> target = CollectionHelper.toStream((Iterator<LocalDate>) null, mapper.value);
            assertNull(String.format("null -> %s", mapper.description), target);
            Iterator<List<LocalDate>> localDateValuesIterator = Stream.concat(
                    Stream.<List<LocalDate>>concat(
                            Stream.of(Collections.emptyList()),
                            getLocalDateValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getLocalDateValues(NullSpec.NONE).skip(1).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<LocalDate>builder().add(null).build(), getLocalDateValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getLocalDateValues(NullSpec.NONE).limit(1), Stream.<LocalDate>builder().add(null).build()).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.NONE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<LocalDate> source = localDateValuesIterator.next();
                String description = TestHelper.appendIterableLocalDateDescription(new StringBuilder(), source).append(" -> ").append(mapper.description).toString();
                List<String> expected = source.stream().map(mapper.value).collect(Collectors.toList());
                Stream<String> actual = CollectionHelper.toStream(source.iterator(), mapper.value);
                assertNotNull(description, actual);
                Iterator<String> expectedIterator = expected.iterator();
                Iterator<String> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    String e = expectedIterator.next();
                    String a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (localDateValuesIterator.hasNext());
        } while (localDateStringMapperIterator.hasNext());
    }

    @Test
    public void testToStreamIteratorMapper() {
        Iterator<DescribedData<Function<Long, String>>> intStringMapperIterator = getNumberStringMappers().iterator();
        do {
            DescribedData<Function<Long, String>> mapper = intStringMapperIterator.next();
            //noinspection ConstantConditions
            Stream<String> target = CollectionHelper.toStream((Iterator<Long>) null, mapper.value);
            assertNull(String.format("null -> %s", mapper.description), target);
            Iterator<List<Long>> intValuesIterator = Stream.concat(
                    Stream.<List<Long>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                            getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<Long> source = intValuesIterator.next();
                String description = TestHelper.appendIterableNumberDescription(new StringBuilder(), source).append(" -> ").append(mapper.description).toString();
                List<String> expected = source.stream().map(mapper.value).collect(Collectors.toList());
                Stream<String> actual = CollectionHelper.toStream(source.iterator(), mapper.value);
                assertNotNull(description, actual);
                Iterator<String> expectedIterator = expected.iterator();
                Iterator<String> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    String e = expectedIterator.next();
                    String a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (intValuesIterator.hasNext());
        } while (intStringMapperIterator.hasNext());

        Iterator<DescribedData<Function<String, Long>>> stringIntMapperIterator = getStringNumberMappers().iterator();
        do {
            DescribedData<Function<String, Long>> mapper = stringIntMapperIterator.next();
            //noinspection ConstantConditions
            Stream<Long> target = CollectionHelper.toStream((Iterator<String>) null, mapper.value);
            assertNull(String.format("null -> %s", mapper.description), target);
            Iterator<List<String>> stringValuesIterator = Stream.concat(
                    Stream.<List<String>>concat(
                            Stream.of(Collections.emptyList()),
                            getNumericStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                    ),
                    Stream.of(
                            getNumericStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<String>builder().add(null).build(), getNumericStringValues(NullSpec.NONE, false).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getNumericStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                            getNumericStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<String> source = stringValuesIterator.next();
                String description = TestHelper.appendIterableStringDescription(new StringBuilder(), source).append(" -> ").append(mapper.description).toString();
                List<Long> expected = source.stream().map(mapper.value).collect(Collectors.toList());
                Stream<Long> actual = CollectionHelper.toStream(source.iterator(), mapper.value);
                assertNotNull(description, actual);
                Iterator<Long> expectedIterator = expected.iterator();
                Iterator<Long> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    Long e = expectedIterator.next();
                    Long a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (stringValuesIterator.hasNext());
        } while (stringIntMapperIterator.hasNext());

        Iterator<DescribedData<Predicate<LocalDate>>> dateFilterIterator = getLocalDateFilters().iterator();
        do {
            DescribedData<Predicate<LocalDate>> filter = dateFilterIterator.next();
            //noinspection ConstantConditions
            Stream<LocalDate> target = CollectionHelper.toStream((Iterator<LocalDate>) null, filter.value);
            assertNull(String.format("null -> %s", filter.description), target);
            Iterator<List<LocalDate>> localDateValuesIterator = Stream.concat(
                    Stream.<List<LocalDate>>concat(
                            Stream.of(Collections.emptyList()),
                            getLocalDateValues(NullSpec.BEFORE).map(Arrays::asList)
                    ),
                    Stream.of(
                            getLocalDateValues(NullSpec.NONE).skip(1).limit(2).collect(Collectors.toList()),
                            Stream.concat(Stream.<LocalDate>builder().add(null).build(), getLocalDateValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                            Stream.concat(getLocalDateValues(NullSpec.NONE).limit(1), Stream.<LocalDate>builder().add(null).build()).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.NONE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.BEFORE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                            getLocalDateValues(NullSpec.AFTER).collect(Collectors.toList())
                    )
            ).iterator();
            do {
                List<LocalDate> source = localDateValuesIterator.next();
                String description = TestHelper.appendIterableLocalDateDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                List<LocalDate> expected = source.stream().filter(filter.value).collect(Collectors.toList());
                Stream<LocalDate> actual = CollectionHelper.toStream(source.iterator(), filter.value);
                assertNotNull(description, actual);
                Iterator<LocalDate> expectedIterator = expected.iterator();
                Iterator<LocalDate> actualIterator = actual.iterator();
                int count = 0;
                while (expectedIterator.hasNext()) {
                    String d = String.format("(%s)[%d]", description, count++);
                    assertTrue(d, actualIterator.hasNext());
                    LocalDate e = expectedIterator.next();
                    LocalDate a = actualIterator.next();
                    if (null == e)
                        assertNull(d, a);
                    else {
                        assertNotNull(d, a);
                        assertEquals(d, e, a);
                    }
                }
                assertFalse(description, actualIterator.hasNext());
            } while (localDateValuesIterator.hasNext());
        } while (dateFilterIterator.hasNext());
    }

    @Test
    public void testToStreamIterableMapperFilter() {
        Iterator<DescribedData<Function<Long, String>>> intStringMapperIterator = getNumberStringMappers().iterator();
        do {
            DescribedData<Function<Long, String>> mapper = intStringMapperIterator.next();
            Iterator<DescribedData<Predicate<String>>> stringFilterIterator = Stream.concat(getStringFilters(), getNumberStringFilters()).iterator();
            do {
                DescribedData<Predicate<String>> filter = stringFilterIterator.next();
                Stream<String> target = CollectionHelper.toStream((Iterable<Long>) null, mapper.value, filter.value);
                assertNull(String.format("(null -> %s) -> %s", mapper.description, filter.description), target);
                Iterator<List<Long>> intValuesIterator = Stream.concat(
                        Stream.<List<Long>>concat(
                                Stream.of(Collections.emptyList()),
                                getNumberValues(NullSpec.BEFORE).map(Arrays::asList)
                        ),
                        Stream.of(
                                getNumberValues(NullSpec.NONE).skip(2).limit(2).collect(Collectors.toList()),
                                Stream.concat(Stream.<Long>builder().add(null).build(), getNumberValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                                Stream.concat(getNumberValues(NullSpec.NONE).skip(4).limit(1), Stream.<Long>builder().add(null).build()).collect(Collectors.toList()),
                                getNumberValues(NullSpec.NONE).collect(Collectors.toList()),
                                getNumberValues(NullSpec.BEFORE).collect(Collectors.toList()),
                                getNumberValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                                getNumberValues(NullSpec.AFTER).collect(Collectors.toList())
                        )
                ).iterator();
                do {
                    List<Long> source = intValuesIterator.next();
                    String description = TestHelper.appendIterableNumberDescription(new StringBuilder("("), source).append(" -> ").append(mapper.description).append(") -> ").append(filter.description).toString();
                    List<String> expected = source.stream().map(mapper.value).filter(filter.value).collect(Collectors.toList());
                    Stream<String> actual = CollectionHelper.toStream(source, mapper.value, filter.value);
                    assertNotNull(description, actual);
                    Iterator<String> expectedIterator = expected.iterator();
                    Iterator<String> actualIterator = actual.iterator();
                    int count = 0;
                    while (expectedIterator.hasNext()) {
                        String d = String.format("(%s)[%d]", description, count++);
                        assertTrue(d, actualIterator.hasNext());
                        String e = expectedIterator.next();
                        String a = actualIterator.next();
                        if (null == e)
                            assertNull(d, a);
                        else {
                            assertNotNull(d, a);
                            assertEquals(d, e, a);
                        }
                    }
                    assertFalse(description, actualIterator.hasNext());
                } while (intValuesIterator.hasNext());
            } while (stringFilterIterator.hasNext());
        } while (intStringMapperIterator.hasNext());

        Iterator<DescribedData<Function<String, Long>>> stringIntMapperIterator = getStringNumberMappers().iterator();
        do {
            DescribedData<Function<String, Long>> mapper = stringIntMapperIterator.next();
            Iterator<DescribedData<Predicate<Long>>> intFilterIterator = getNumberFilters().iterator();
            do {
                DescribedData<Predicate<Long>> filter = intFilterIterator.next();
                Stream<Long> target = CollectionHelper.toStream((Iterable<String>) null, mapper.value, filter.value);
                assertNull(String.format("(null -> %s) -> %s", mapper.description, filter.description), target);
                Iterator<List<String>> stringValuesIterator = Stream.concat(
                        Stream.<List<String>>concat(
                                Stream.of(Collections.emptyList()),
                                getNumericStringValues(NullSpec.BEFORE, true).map(Arrays::asList)
                        ),
                        Stream.of(
                                getNumericStringValues(NullSpec.NONE, false).skip(2).limit(2).collect(Collectors.toList()),
                                Stream.concat(Stream.<String>builder().add(null).build(), getNumericStringValues(NullSpec.NONE, false).skip(1).limit(1)).collect(Collectors.toList()),
                                Stream.concat(getNumericStringValues(NullSpec.NONE, false).skip(4).limit(1), Stream.<String>builder().add(null).build()).collect(Collectors.toList()),
                                getNumericStringValues(NullSpec.NONE, true).collect(Collectors.toList()),
                                getNumericStringValues(NullSpec.BEFORE, true).collect(Collectors.toList()),
                                getNumericStringValues(NullSpec.MIDDLE, true).collect(Collectors.toList()),
                                getNumericStringValues(NullSpec.AFTER, true).collect(Collectors.toList())
                        )
                ).iterator();
                do {
                    List<String> source = stringValuesIterator.next();
                    String description = TestHelper.appendIterableStringDescription(new StringBuilder("("), source).append(" -> ").append(mapper.description).append(") -> ").append(filter.description).toString();
                    List<Long> expected = source.stream().map(mapper.value).filter(filter.value).collect(Collectors.toList());
                    Stream<Long> actual = CollectionHelper.toStream(source, mapper.value, filter.value);
                    assertNotNull(description, actual);
                    Iterator<Long> expectedIterator = expected.iterator();
                    Iterator<Long> actualIterator = actual.iterator();
                    int count = 0;
                    while (expectedIterator.hasNext()) {
                        String d = String.format("(%s)[%d]", description, count++);
                        assertTrue(d, actualIterator.hasNext());
                        Long e = expectedIterator.next();
                        Long a = actualIterator.next();
                        if (null == e)
                            assertNull(d, a);
                        else {
                            assertNotNull(d, a);
                            assertEquals(d, e, a);
                        }
                    }
                    assertFalse(description, actualIterator.hasNext());
                } while (stringValuesIterator.hasNext());
            } while (intFilterIterator.hasNext());
        } while (stringIntMapperIterator.hasNext());

        Iterator<DescribedData<Function<LocalDate, String>>> localDateStringMapperIterator = getLocalDateStringMappers().iterator();
        do {
            DescribedData<Function<LocalDate, String>> mapper = localDateStringMapperIterator.next();
            Iterator<DescribedData<Predicate<String>>> stringFilterIterator = Stream.concat(getStringFilters(), getLocalDateStringFilters()).iterator();
            do {
                DescribedData<Predicate<String>> filter = stringFilterIterator.next();
                Stream<String> target = CollectionHelper.toStream((Iterable<LocalDate>) null, mapper.value, filter.value);
                assertNull(String.format("(null -> %s) -> %s", mapper.description, filter.description), target);
                Iterator<List<LocalDate>> localDateValuesIterator = Stream.concat(
                        Stream.<List<LocalDate>>concat(
                                Stream.of(Collections.emptyList()),
                                getLocalDateValues(NullSpec.BEFORE).map(Arrays::asList)
                        ),
                        Stream.of(
                                getLocalDateValues(NullSpec.NONE).skip(1).limit(2).collect(Collectors.toList()),
                                Stream.concat(Stream.<LocalDate>builder().add(null).build(), getLocalDateValues(NullSpec.NONE).skip(1).limit(1)).collect(Collectors.toList()),
                                Stream.concat(getLocalDateValues(NullSpec.NONE).limit(1), Stream.<LocalDate>builder().add(null).build()).collect(Collectors.toList()),
                                getLocalDateValues(NullSpec.NONE).collect(Collectors.toList()),
                                getLocalDateValues(NullSpec.BEFORE).collect(Collectors.toList()),
                                getLocalDateValues(NullSpec.MIDDLE).collect(Collectors.toList()),
                                getLocalDateValues(NullSpec.AFTER).collect(Collectors.toList())
                        )
                ).iterator();
                do {
                    List<LocalDate> source = localDateValuesIterator.next();
                    String description = TestHelper.appendIterableLocalDateDescription(new StringBuilder(), source).append(" -> ").append(filter.description).toString();
                    List<String> expected = source.stream().map(mapper.value).filter(filter.value).collect(Collectors.toList());
                    Stream<String> actual = CollectionHelper.toStream(source.iterator(), mapper.value, filter.value);
                    assertNotNull(description, actual);
                    Iterator<String> expectedIterator = expected.iterator();
                    Iterator<String> actualIterator = actual.iterator();
                    int count = 0;
                    while (expectedIterator.hasNext()) {
                        String d = String.format("(%s)[%d]", description, count++);
                        assertTrue(d, actualIterator.hasNext());
                        String e = expectedIterator.next();
                        String a = actualIterator.next();
                        if (null == e)
                            assertNull(d, a);
                        else {
                            assertNotNull(d, a);
                            assertEquals(d, e, a);
                        }
                    }
                    assertFalse(description, actualIterator.hasNext());
                } while (localDateValuesIterator.hasNext());
            } while (stringFilterIterator.hasNext());
        } while (localDateStringMapperIterator.hasNext());
    }

    @Test
    public void testToStreamIteratorMapperFilter() {
        Iterator<String> source = Arrays.asList("1", "2", "3", "4").iterator();
    }

    @Test
    public void testIfAllElementsMatchIterablePredicate() {
    }

    @Test
    public void testIfAllElementsMatchStreamPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIteratorPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIterableFilterPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIteratorFilterPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIterableMapperPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIteratorMapperPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIterableMapperFilterPredicate() {
    }

    @Test
    public void testIfAllElementsMatchIteratorMapperFilterPredicate() {
    }

    @Test
    public void testAllElementsMatchIterable() {
    }

    @Test
    public void testAllElementsMatchStream() {
    }

    @Test
    public void testAllElementsMatchIterator() {
    }

    private enum NullSpec {
        NONE,
        BEFORE,
        MIDDLE,
        AFTER
    }

    private static class DescribedData<T> {
        private T value;
        private String description;

        private DescribedData(T value, String description) {
            this.value = value;
            this.description = description;
        }
    }

}