package Erwine.Leonard.T.wguscheduler356334;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

public class TestHelper {
    private TestHelper() {
    }

    public static <T> Supplier<T> createReIterator(Collection<T> source) {
        return new Supplier<T>() {
            private Iterator<T> iterator = source.iterator();

            @Override
            public T get() {
                T result = iterator.next();
                if (!iterator.hasNext())
                    iterator = source.iterator();
                return result;
            }
        };
    }

    public static <T> ArrayList<T> combineCollections(Collection<T> a, Collection<T> b) {
        ArrayList<T> result = new ArrayList<T>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static <T> ArrayList<T> combineCollections(Collection<T> a, T[] b) {
        ArrayList<T> result = new ArrayList<T>();
        result.addAll(a);
        Collections.addAll(result, b);
        return result;
    }

    public static <T> ArrayList<T> combineCollections(T[] a, Collection<T> b) {
        return combineCollections(Arrays.asList(a), b);
    }

    public static <T> ArrayList<T> combineCollections(T[] a, T[] b) {
        return combineCollections(Arrays.asList(a), b);
    }


    public static StringBuilder appendStringDescription(StringBuilder target, String value) {
        if (null == value) {
            target.append("[null]");
        } else if (value.isEmpty()) {
            target.append("[empty]");
        } else {
            appendStringDescriptionImpl(target, value);
        }
        return target;
    }

    public static String toStringDescription(String value) {
        if (null == value) {
            return "[null]";
        }
        if (value.isEmpty()) {
            return "[empty]";
        }
        StringBuilder sb = new StringBuilder();
        appendStringDescriptionImpl(sb, value);
        return sb.toString();
    }

    public static String toStringDescription(StringBuilder value) {
        if (null == value) {
            return "[null]";
        }
        if (value.length() == 0) {
            return "[empty]";
        }
        StringBuilder sb = new StringBuilder();
        appendStringDescriptionImpl(sb, value);
        return sb.toString();
    }

    private static void appendStringDescriptionImpl(StringBuilder target, CharSequence value) {
        boolean cr = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (cr) {
                cr = false;
                if (c == '\n') {
                    target.append("[CRLF]");
                    continue;
                }
                target.append("[CR]");
            }
            switch (c) {
                case ' ':
                    target.append("[SPACE]");
                    break;
                case '\t':
                    target.append("[TAB]");
                    break;
                case '\r':
                    cr = true;
                    break;
                case '\n':
                    target.append("[LF]");
                    break;
                case '\f':
                    target.append("[FF]");
                    break;
                case '\u000B':
                    target.append("[VT]");
                    break;
                case '\u00A0':
                    target.append("[NBSP]");
                    break;
                case '\u0085':
                    target.append("[NEL]");
                    break;
                case '\u1680':
                    target.append("[OGHAM]");
                    break;
                case '\u2028':
                    target.append("[LS]");
                    break;
                case '\u2029':
                    target.append("[PS]");
                    break;
                default:
                    target.append(c);
                    break;
            }
        }
        if (cr) {
            target.append("[CR]");
        }
    }
}
