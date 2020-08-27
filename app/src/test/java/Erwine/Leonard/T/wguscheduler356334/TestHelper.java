package Erwine.Leonard.T.wguscheduler356334;

public class TestHelper {
    private TestHelper() {
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

    private static void appendStringDescriptionImpl(StringBuilder target, String value) {
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
                    target.append("[LS]");
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
