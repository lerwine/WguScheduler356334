package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringLineIterator implements Iterator<String> {
    private final String source;
    private int startIndex;
    private int endIndex;
    private int nextIndex;

    public StringLineIterator(String source) {
        this.source = source;
        reset();
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
                        int i = endIndex - 1;
                        if (Character.isWhitespace(source.charAt(i))) {
                            do {
                                i--;
                                if (i == startIndex) {
                                    return "";
                                }
                            } while (Character.isWhitespace(source.charAt(i)));
                            endIndex = i + 1;
                        }
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
                    int i = endIndex - 1;
                    if (Character.isWhitespace(source.charAt(i))) {
                        do {
                            i--;
                            if (i == startIndex) {
                                return "";
                            }
                        } while (Character.isWhitespace(source.charAt(i)));
                        endIndex = i + 1;
                    }
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
        char c = source.charAt(endIndex);
        while (Character.getType(c) != Character.LINE_SEPARATOR) {
            if (++endIndex == source.length()) {
                nextIndex = -1;
                break;
            }
            c = source.charAt(endIndex);
        }
        if (nextIndex > -1 && (++nextIndex == source.length() || (c == '\r' && source.charAt(nextIndex) == '\n' && ++nextIndex == source.length()))) {
            nextIndex = -1;
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
