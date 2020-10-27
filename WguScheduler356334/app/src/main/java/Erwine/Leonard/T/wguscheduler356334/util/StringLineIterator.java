package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class StringLineIterator implements Iterator<String> {
    private final String source;
    private int startIndex;
    private int endIndex;
    private int nextIndex;

    public StringLineIterator(String source) {
        this.source = source;
        reset();
    }

    public static Stream<String> getLines(String source) {
        StringLineIterator iterator = new StringLineIterator(source);
        Stream.Builder<String> builder = Stream.builder();
        while (iterator.hasNext())
            builder.accept(iterator.next());
        return builder.build();
    }

    public static StringLineIterator create(String source, boolean trimLineStart, boolean trimLineEnd) {
        if (trimLineStart) {
            if (trimLineEnd) {
                return new StringLineIterator(source) {
                    @Override
                    protected String getResult() {
                        int startIndex = getStartIndex();
                        int endIndex = getEndIndex();
                        if (startIndex == endIndex) {
                            return "";
                        }
                        String source = getSource();
                        while (Character.isWhitespace(source.charAt(startIndex))) {
                            if (++startIndex == endIndex) {
                                return "";
                            }
                        }
                        int i = endIndex;
                        while (Character.isWhitespace(source.charAt(--i))) {
                            if (i == startIndex) {
                                return "";
                            }
                        }
                        endIndex = i + 1;
                        return (endIndex == source.length()) ? ((startIndex > 0) ? source.substring(startIndex) : source) : source.substring(startIndex, endIndex);
                    }
                };
            }
            return new StringLineIterator(source) {
                @Override
                protected String getResult() {
                    int startIndex = getStartIndex();
                    int endIndex = getEndIndex();
                    if (startIndex == endIndex) {
                        return "";
                    }
                    String source = getSource();
                    while (Character.isWhitespace(source.charAt(startIndex))) {
                        if (++startIndex == endIndex) {
                            return "";
                        }
                    }
                    return (endIndex == source.length()) ? ((startIndex > 0) ? source.substring(startIndex) : source) : source.substring(startIndex, endIndex);
                }
            };
        }
        if (trimLineEnd) {
            return new StringLineIterator(source) {
                @Override
                protected String getResult() {
                    int startIndex = getStartIndex();
                    int endIndex = getEndIndex();
                    if (startIndex == endIndex) {
                        return "";
                    }
                    String source = getSource();
                    int i = endIndex;
                    while (Character.isWhitespace(source.charAt(--i))) {
                        if (i == startIndex) {
                            return "";
                        }
                    }
                    endIndex = i + 1;
                    return (endIndex == source.length()) ? ((startIndex > 0) ? source.substring(startIndex) : source) : source.substring(startIndex, endIndex);
                }
            };
        }
        return new StringLineIterator(source);
    }

    public final int getStartIndex() {
        return startIndex;
    }

    public final int getEndIndex() {
        return endIndex;
    }

    public final int getNextIndex() {
        return nextIndex;
    }

    public final String getSource() {
        return source;
    }

    @Override
    public final boolean hasNext() {
        return nextIndex > -1;
    }

    @Override
    public final synchronized String next() {
        if (nextIndex < 0) {
            throw new NoSuchElementException();
        }
        startIndex = endIndex = nextIndex;
        if (startIndex == source.length()) {
            nextIndex = -1;
        } else {
            char c = source.charAt(endIndex);
            while (!StringHelper.isLineSeparator(c)) {
                if (++endIndex == source.length()) {
                    nextIndex = -1;
                    break;
                }
                c = source.charAt(endIndex);
            }
            if (nextIndex > -1 && (nextIndex = endIndex + 1) < source.length() && c == '\r' && source.charAt(nextIndex) == '\n') {
                nextIndex++;
            }
        }
        return getResult();
    }

    protected String getResult() {
        return (endIndex == startIndex) ? "" : ((endIndex == source.length()) ? ((startIndex > 0) ? source.substring(startIndex) : source) : source.substring(startIndex, endIndex));
    }

    public final synchronized void reset() {
        startIndex = endIndex = -1;
        nextIndex = (null == source) ? -1 : 0;
    }

}
