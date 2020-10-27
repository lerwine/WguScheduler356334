package Erwine.Leonard.T.wguscheduler356334.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings({"ConstantConditions", "StreamToLoop"})
public class StringHelperTest {

    private static final List<Character> WHITESPACE_CHARACTERS = Collections.unmodifiableList(Arrays.asList(' ', '\t', '\u000B', '\u1680'));
    private static final List<Character> LINE_SEPARATOR_CHARACTERS = Collections.unmodifiableList(Arrays.asList('\r', '\n', '\f', '\u0085', '\u2028', '\u2029'));
    private static final List<String> SINGLE_LINE_SEPARATOR_SEQ = Collections.unmodifiableList(Arrays.asList("\r\n", "\r", "\n", "\f", "\u0085", "\u2028", "\u2029"));
    private static final List<String> DOUBLE_LINE_SEPARATOR_SEQ = Collections.unmodifiableList(Arrays.asList(
            "\r\n\r\n", "\r\n\r", "\r\n\n", "\r\n\f", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029",
            "\r\r\n", "\r\r", "\r\f", "\r\u0085", "\r\u2028", "\r\u2029",
            "\n\r\n", "\n\r", "\n\n", "\n\f", "\n\u0085", "\n\u2028", "\n\u2029",
            "\f\r\n", "\f\r", "\f\n", "\f\f", "\f\u0085", "\f\u2028", "\f\u2029",
            "\u0085\r\n", "\u0085\r", "\u0085\n", "\u0085\f", "\u0085\u0085", "\u0085\u2028", "\u0085\u2029",
            "\u2028\r\n", "\u2028\r", "\u2028\n", "\u2028\f", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029",
            "\u2029\r\n", "\u2029\r", "\u2029\n", "\u2029\f", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029"
    ));

    @Test
    public void trimStart() {
        String actual = StringHelper.trimStart(null);
        assertNull(actual);
        TestData[] testData = new TestData[]{
                new TestData("", "", "\n", "\r\n", "\r", "\f", "\u0085", "\u2028", "\u2029", "\n\n", "\n\r\n", "\n\r", "\n\f", "\n\u0085", "\n\u2028", "\n\u2029",
                        "\r\n\n", "\r\n\r\n", "\r\n\r", "\r\n\f", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\r\r\n", "\r\r", "\r\f", "\r\u0085", "\r\u2028", "\r\u2029",
                        "\f\n", "\f\r\n", "\f\r", "\f\f", "\f\u0085", "\f\u2028", "\f\u2029", "\u0085\n", "\u0085\r\n", "\u0085\r", "\u0085\f", "\u0085\u0085",
                        "\u0085\u2028", "\u0085\u2029", "\u2028\n", "\u2028\r\n", "\u2028\r", "\u2028\f", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\n",
                        "\u2029\r\n", "\u2029\r", "\u2029\f", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029", " ", "  ", " \t", " \u1680", " \n ", "\n ", "\n \n", " \r\n ",
                        "\r\n ", "\r\n \r\n", " \r ", "\r ", "\r \r", " \f ", "\f ", "\f \f", " \u0085 ", "\u0085 ", "\u0085 \u0085", " \u2028 ", "\u2028 ", "\u2028 \u2028",
                        " \u2029 ", "\u2029 ", "\u2029 \u2029", " \n\n ", "\n\n ", "\n\n \n\n", " \n\r\n ", "\n\r\n ", "\n\r\n \n\r\n", " \n\r ", "\n\r ", "\n\r \n\r",
                        " \n\f ", "\n\f ", "\n\f \n\f", " \n\u0085 ", "\n\u0085 ", "\n\u0085 \n\u0085", " \n\u2028 ", "\n\u2028 ", "\n\u2028 \n\u2028", " \n\u2029 ",
                        "\n\u2029 ", "\n\u2029 \n\u2029", " \r\n\n ", "\r\n\n ", "\r\n\n \r\n\n", " \r\n\r\n ", "\r\n\r\n ", "\r\n\r\n \r\n\r\n", " \r\n\r ", "\r\n\r ",
                        "\r\n\r \r\n\r", " \r\n\f ", "\r\n\f ", "\r\n\f \r\n\f", " \r\n\u0085 ", "\r\n\u0085 ", "\r\n\u0085 \r\n\u0085", " \r\n\u2028 ", "\r\n\u2028 ",
                        "\r\n\u2028 \r\n\u2028", " \r\n\u2029 ", "\r\n\u2029 ", "\r\n\u2029 \r\n\u2029", " \r\r\n ", "\r\r\n ", "\r\r\n \r\r\n", " \r\r ", "\r\r ",
                        "\r\r \r\r", " \r\f ", "\r\f ", "\r\f \r\f", " \r\u0085 ", "\r\u0085 ", "\r\u0085 \r\u0085", " \r\u2028 ", "\r\u2028 ", "\r\u2028 \r\u2028",
                        " \r\u2029 ", "\r\u2029 ", "\r\u2029 \r\u2029", " \f\n ", "\f\n ", "\f\n \f\n", " \f\r\n ", "\f\r\n ", "\f\r\n \f\r\n", " \f\r ", "\f\r ",
                        "\f\r \f\r", " \f\f ", "\f\f ", "\f\f \f\f", " \f\u0085 ", "\f\u0085 ", "\f\u0085 \f\u0085", " \f\u2028 ", "\f\u2028 ", "\f\u2028 \f\u2028",
                        " \f\u2029 ", "\f\u2029 ", "\f\u2029 \f\u2029", " \u0085\n ", "\u0085\n ", "\u0085\n \u0085\n", " \u0085\r\n ", "\u0085\r\n ",
                        "\u0085\r\n \u0085\r\n", " \u0085\r ", "\u0085\r ", "\u0085\r \u0085\r", " \u0085\f ", "\u0085\f ", "\u0085\f \u0085\f", " \u0085\u0085 ",
                        "\u0085\u0085 ", "\u0085\u0085 \u0085\u0085", " \u0085\u2028 ", "\u0085\u2028 ", "\u0085\u2028 \u0085\u2028", " \u0085\u2029 ", "\u0085\u2029 ",
                        "\u0085\u2029 \u0085\u2029", " \u2028\n ", "\u2028\n ", "\u2028\n \u2028\n", " \u2028\r\n ", "\u2028\r\n ", "\u2028\r\n \u2028\r\n", " \u2028\r ",
                        "\u2028\r ", "\u2028\r \u2028\r", " \u2028\f ", "\u2028\f ", "\u2028\f \u2028\f", " \u2028\u0085 ", "\u2028\u0085 ", "\u2028\u0085 \u2028\u0085",
                        " \u2028\u2028 ", "\u2028\u2028 ", "\u2028\u2028 \u2028\u2028", " \u2028\u2029 ", "\u2028\u2029 ", "\u2028\u2029 \u2028\u2029", " \u2029\n ",
                        "\u2029\n ", "\u2029\n \u2029\n", " \u2029\r\n ", "\u2029\r\n ", "\u2029\r\n \u2029\r\n", " \u2029\r ", "\u2029\r ", "\u2029\r \u2029\r",
                        " \u2029\f ", "\u2029\f ", "\u2029\f \u2029\f", " \u2029\u0085 ", "\u2029\u0085 ", "\u2029\u0085 \u2029\u0085", " \u2029\u2028 ", "\u2029\u2028 ",
                        "\u2029\u2028 \u2029\u2028", " \u2029\u2029 ", "\u2029\u2029 ", "\u2029\u2029 \u2029\u2029", "\t", "\t ", "\t\t", "\t\u1680", "\t\n\t", "\n\t",
                        "\n\t\n", "\t\r\n\t", "\r\n\t", "\r\n\t\r\n", "\t\r\t", "\r\t", "\r\t\r", "\t\f\t", "\f\t", "\f\t\f", "\t\u0085\t", "\u0085\t", "\u0085\t\u0085",
                        "\t\u2028\t", "\u2028\t", "\u2028\t\u2028", "\t\u2029\t", "\u2029\t", "\u2029\t\u2029", "\t\n\n\t", "\n\n\t", "\n\n\t\n\n", "\t\n\r\n\t", "\n\r\n\t",
                        "\n\r\n\t\n\r\n", "\t\n\r\t", "\n\r\t", "\n\r\t\n\r", "\t\n\f\t", "\n\f\t", "\n\f\t\n\f", "\t\n\u0085\t", "\n\u0085\t", "\n\u0085\t\n\u0085",
                        "\t\n\u2028\t", "\n\u2028\t", "\n\u2028\t\n\u2028", "\t\n\u2029\t", "\n\u2029\t", "\n\u2029\t\n\u2029", "\t\r\n\n\t", "\r\n\n\t", "\r\n\n\t\r\n\n",
                        "\t\r\n\r\n\t", "\r\n\r\n\t", "\r\n\r\n\t\r\n\r\n", "\t\r\n\r\t", "\r\n\r\t", "\r\n\r\t\r\n\r", "\t\r\n\f\t", "\r\n\f\t", "\r\n\f\t\r\n\f",
                        "\t\r\n\u0085\t", "\r\n\u0085\t", "\r\n\u0085\t\r\n\u0085", "\t\r\n\u2028\t", "\r\n\u2028\t", "\r\n\u2028\t\r\n\u2028", "\t\r\n\u2029\t",
                        "\r\n\u2029\t", "\r\n\u2029\t\r\n\u2029", "\t\r\r\n\t", "\r\r\n\t", "\r\r\n\t\r\r\n", "\t\r\r\t", "\r\r\t", "\r\r\t\r\r", "\t\r\f\t", "\r\f\t",
                        "\r\f\t\r\f", "\t\r\u0085\t", "\r\u0085\t", "\r\u0085\t\r\u0085", "\t\r\u2028\t", "\r\u2028\t", "\r\u2028\t\r\u2028", "\t\r\u2029\t", "\r\u2029\t",
                        "\r\u2029\t\r\u2029", "\t\f\n\t", "\f\n\t", "\f\n\t\f\n", "\t\f\r\n\t", "\f\r\n\t", "\f\r\n\t\f\r\n", "\t\f\r\t", "\f\r\t", "\f\r\t\f\r", "\t\f\f\t",
                        "\f\f\t", "\f\f\t\f\f", "\t\f\u0085\t", "\f\u0085\t", "\f\u0085\t\f\u0085", "\t\f\u2028\t", "\f\u2028\t", "\f\u2028\t\f\u2028", "\t\f\u2029\t",
                        "\f\u2029\t", "\f\u2029\t\f\u2029", "\t\u0085\n\t", "\u0085\n\t", "\u0085\n\t\u0085\n", "\t\u0085\r\n\t", "\u0085\r\n\t", "\u0085\r\n\t\u0085\r\n",
                        "\t\u0085\r\t", "\u0085\r\t", "\u0085\r\t\u0085\r", "\t\u0085\f\t", "\u0085\f\t", "\u0085\f\t\u0085\f", "\t\u0085\u0085\t", "\u0085\u0085\t",
                        "\u0085\u0085\t\u0085\u0085", "\t\u0085\u2028\t", "\u0085\u2028\t", "\u0085\u2028\t\u0085\u2028", "\t\u0085\u2029\t", "\u0085\u2029\t",
                        "\u0085\u2029\t\u0085\u2029", "\t\u2028\n\t", "\u2028\n\t", "\u2028\n\t\u2028\n", "\t\u2028\r\n\t", "\u2028\r\n\t", "\u2028\r\n\t\u2028\r\n",
                        "\t\u2028\r\t", "\u2028\r\t", "\u2028\r\t\u2028\r", "\t\u2028\f\t", "\u2028\f\t", "\u2028\f\t\u2028\f", "\t\u2028\u0085\t", "\u2028\u0085\t",
                        "\u2028\u0085\t\u2028\u0085", "\t\u2028\u2028\t", "\u2028\u2028\t", "\u2028\u2028\t\u2028\u2028", "\t\u2028\u2029\t", "\u2028\u2029\t",
                        "\u2028\u2029\t\u2028\u2029", "\t\u2029\n\t", "\u2029\n\t", "\u2029\n\t\u2029\n", "\t\u2029\r\n\t", "\u2029\r\n\t", "\u2029\r\n\t\u2029\r\n",
                        "\t\u2029\r\t", "\u2029\r\t", "\u2029\r\t\u2029\r", "\t\u2029\f\t", "\u2029\f\t", "\u2029\f\t\u2029\f", "\t\u2029\u0085\t", "\u2029\u0085\t",
                        "\u2029\u0085\t\u2029\u0085", "\t\u2029\u2028\t", "\u2029\u2028\t", "\u2029\u2028\t\u2029\u2028", "\t\u2029\u2029\t", "\u2029\u2029\t",
                        "\u2029\u2029\t\u2029\u2029", "\u1680", "\u1680 ", "\u1680\t", "\u1680\u1680", "\u1680\n\u1680", "\n\u1680", "\n\u1680\n", "\u1680\r\n\u1680",
                        "\r\n\u1680", "\r\n\u1680\r\n", "\u1680\r\u1680", "\r\u1680", "\r\u1680\r", "\u1680\f\u1680", "\f\u1680", "\f\u1680\f", "\u1680\u0085\u1680",
                        "\u0085\u1680", "\u0085\u1680\u0085", "\u1680\u2028\u1680", "\u2028\u1680", "\u2028\u1680\u2028", "\u1680\u2029\u1680", "\u2029\u1680",
                        "\u2029\u1680\u2029", "\u1680\n\n\u1680", "\n\n\u1680", "\n\n\u1680\n\n", "\u1680\n\r\n\u1680", "\n\r\n\u1680", "\n\r\n\u1680\n\r\n",
                        "\u1680\n\r\u1680", "\n\r\u1680", "\n\r\u1680\n\r", "\u1680\n\f\u1680", "\n\f\u1680", "\n\f\u1680\n\f", "\u1680\n\u0085\u1680", "\n\u0085\u1680",
                        "\n\u0085\u1680\n\u0085", "\u1680\n\u2028\u1680", "\n\u2028\u1680", "\n\u2028\u1680\n\u2028", "\u1680\n\u2029\u1680", "\n\u2029\u1680",
                        "\n\u2029\u1680\n\u2029", "\u1680\r\n\n\u1680", "\r\n\n\u1680", "\r\n\n\u1680\r\n\n", "\u1680\r\n\r\n\u1680", "\r\n\r\n\u1680",
                        "\r\n\r\n\u1680\r\n\r\n", "\u1680\r\n\r\u1680", "\r\n\r\u1680", "\r\n\r\u1680\r\n\r", "\u1680\r\n\f\u1680", "\r\n\f\u1680", "\r\n\f\u1680\r\n\f",
                        "\u1680\r\n\u0085\u1680", "\r\n\u0085\u1680", "\r\n\u0085\u1680\r\n\u0085", "\u1680\r\n\u2028\u1680", "\r\n\u2028\u1680",
                        "\r\n\u2028\u1680\r\n\u2028", "\u1680\r\n\u2029\u1680", "\r\n\u2029\u1680", "\r\n\u2029\u1680\r\n\u2029", "\u1680\r\r\n\u1680", "\r\r\n\u1680",
                        "\r\r\n\u1680\r\r\n", "\u1680\r\r\u1680", "\r\r\u1680", "\r\r\u1680\r\r", "\u1680\r\f\u1680", "\r\f\u1680", "\r\f\u1680\r\f", "\u1680\r\u0085\u1680",
                        "\r\u0085\u1680", "\r\u0085\u1680\r\u0085", "\u1680\r\u2028\u1680", "\r\u2028\u1680", "\r\u2028\u1680\r\u2028", "\u1680\r\u2029\u1680",
                        "\r\u2029\u1680", "\r\u2029\u1680\r\u2029", "\u1680\f\n\u1680", "\f\n\u1680", "\f\n\u1680\f\n", "\u1680\f\r\n\u1680", "\f\r\n\u1680",
                        "\f\r\n\u1680\f\r\n", "\u1680\f\r\u1680", "\f\r\u1680", "\f\r\u1680\f\r", "\u1680\f\f\u1680", "\f\f\u1680", "\f\f\u1680\f\f", "\u1680\f\u0085\u1680",
                        "\f\u0085\u1680", "\f\u0085\u1680\f\u0085", "\u1680\f\u2028\u1680", "\f\u2028\u1680", "\f\u2028\u1680\f\u2028", "\u1680\f\u2029\u1680",
                        "\f\u2029\u1680", "\f\u2029\u1680\f\u2029", "\u1680\u0085\n\u1680", "\u0085\n\u1680", "\u0085\n\u1680\u0085\n", "\u1680\u0085\r\n\u1680",
                        "\u0085\r\n\u1680", "\u0085\r\n\u1680\u0085\r\n", "\u1680\u0085\r\u1680", "\u0085\r\u1680", "\u0085\r\u1680\u0085\r", "\u1680\u0085\f\u1680",
                        "\u0085\f\u1680", "\u0085\f\u1680\u0085\f", "\u1680\u0085\u0085\u1680", "\u0085\u0085\u1680", "\u0085\u0085\u1680\u0085\u0085",
                        "\u1680\u0085\u2028\u1680", "\u0085\u2028\u1680", "\u0085\u2028\u1680\u0085\u2028", "\u1680\u0085\u2029\u1680", "\u0085\u2029\u1680",
                        "\u0085\u2029\u1680\u0085\u2029", "\u1680\u2028\n\u1680", "\u2028\n\u1680", "\u2028\n\u1680\u2028\n", "\u1680\u2028\r\n\u1680", "\u2028\r\n\u1680",
                        "\u2028\r\n\u1680\u2028\r\n", "\u1680\u2028\r\u1680", "\u2028\r\u1680", "\u2028\r\u1680\u2028\r", "\u1680\u2028\f\u1680", "\u2028\f\u1680",
                        "\u2028\f\u1680\u2028\f", "\u1680\u2028\u0085\u1680", "\u2028\u0085\u1680", "\u2028\u0085\u1680\u2028\u0085", "\u1680\u2028\u2028\u1680",
                        "\u2028\u2028\u1680", "\u2028\u2028\u1680\u2028\u2028", "\u1680\u2028\u2029\u1680", "\u2028\u2029\u1680", "\u2028\u2029\u1680\u2028\u2029",
                        "\u1680\u2029\n\u1680", "\u2029\n\u1680", "\u2029\n\u1680\u2029\n", "\u1680\u2029\r\n\u1680", "\u2029\r\n\u1680", "\u2029\r\n\u1680\u2029\r\n",
                        "\u1680\u2029\r\u1680", "\u2029\r\u1680", "\u2029\r\u1680\u2029\r", "\u1680\u2029\f\u1680", "\u2029\f\u1680", "\u2029\f\u1680\u2029\f",
                        "\u1680\u2029\u0085\u1680", "\u2029\u0085\u1680", "\u2029\u0085\u1680\u2029\u0085", "\u1680\u2029\u2028\u1680", "\u2029\u2028\u1680",
                        "\u2029\u2028\u1680\u2029\u2028", "\u1680\u2029\u2029\u1680", "\u2029\u2029\u1680", "\u2029\u2029\u1680\u2029\u2029"),
                new TestData("Test", "Test", " Test", " \n Test", " \r\n Test", " \r Test", " \f Test", " \u0085 Test", " \u2028 Test", " \u2029 Test", "\tTest",
                        "\t\n\tTest", "\t\r\n\tTest", "\t\r\tTest", "\t\f\tTest", "\t\u0085\tTest", "\t\u2028\tTest", "\t\u2029\tTest", "\u1680Test", "\u1680\n\u1680Test",
                        "\u1680\r\n\u1680Test", "\u1680\r\u1680Test", "\u1680\f\u1680Test", "\u1680\u0085\u1680Test", "\u1680\u2028\u1680Test", "\u1680\u2029\u1680Test",
                        "\nTest", "\r\nTest", "\rTest", "\fTest", "\u0085Test", "\u2028Test", "\u2029Test", "\n\nTest", "\n\r\nTest", "\n\rTest", "\n\fTest", "\n\u0085Test",
                        "\n\u2028Test", "\n\u2029Test", "\r\n\nTest", "\r\n\r\nTest", "\r\n\rTest", "\r\n\fTest", "\r\n\u0085Test", "\r\n\u2028Test", "\r\n\u2029Test",
                        "\r\r\nTest", "\r\rTest", "\r\fTest", "\r\u0085Test", "\r\u2028Test", "\r\u2029Test", "\f\nTest", "\f\r\nTest", "\f\rTest", "\f\fTest",
                        "\f\u0085Test", "\f\u2028Test", "\f\u2029Test", "\u0085\nTest", "\u0085\r\nTest", "\u0085\rTest", "\u0085\fTest", "\u0085\u0085Test",
                        "\u0085\u2028Test", "\u0085\u2029Test", "\u2028\nTest", "\u2028\r\nTest", "\u2028\rTest", "\u2028\fTest", "\u2028\u0085Test", "\u2028\u2028Test",
                        "\u2028\u2029Test", "\u2029\nTest", "\u2029\r\nTest", "\u2029\rTest", "\u2029\fTest", "\u2029\u0085Test", "\u2029\u2028Test", "\u2029\u2029Test"),
                new TestData("_", "_"),
                new TestData("\u00A0", "\u00A0", " \u00A0", " \n \u00A0", " \r\n \u00A0", " \r \u00A0", " \f \u00A0", " \u0085 \u00A0", " \u2028 \u00A0",
                        " \u2029 \u00A0", "\t\u00A0", "\t\n\t\u00A0", "\t\r\n\t\u00A0", "\t\r\t\u00A0", "\t\f\t\u00A0", "\t\u0085\t\u00A0", "\t\u2028\t\u00A0",
                        "\t\u2029\t\u00A0", "\u1680\u00A0", "\u1680\n\u1680\u00A0", "\u1680\r\n\u1680\u00A0", "\u1680\r\u1680\u00A0", "\u1680\f\u1680\u00A0",
                        "\u1680\u0085\u1680\u00A0", "\u1680\u2028\u1680\u00A0", "\u1680\u2029\u1680\u00A0", "\n\u00A0", "\r\n\u00A0", "\r\u00A0", "\f\u00A0", "\u0085\u00A0",
                        "\u2028\u00A0", "\u2029\u00A0", "\n\n\u00A0", "\n\r\n\u00A0", "\n\r\u00A0", "\n\f\u00A0", "\n\u0085\u00A0", "\n\u2028\u00A0", "\n\u2029\u00A0",
                        "\r\n\n\u00A0", "\r\n\r\n\u00A0", "\r\n\r\u00A0", "\r\n\f\u00A0", "\r\n\u0085\u00A0", "\r\n\u2028\u00A0", "\r\n\u2029\u00A0", "\r\r\n\u00A0",
                        "\r\r\u00A0", "\r\f\u00A0", "\r\u0085\u00A0", "\r\u2028\u00A0", "\r\u2029\u00A0", "\f\n\u00A0", "\f\r\n\u00A0", "\f\r\u00A0", "\f\f\u00A0",
                        "\f\u0085\u00A0", "\f\u2028\u00A0", "\f\u2029\u00A0", "\u0085\n\u00A0", "\u0085\r\n\u00A0", "\u0085\r\u00A0", "\u0085\f\u00A0", "\u0085\u0085\u00A0",
                        "\u0085\u2028\u00A0", "\u0085\u2029\u00A0", "\u2028\n\u00A0", "\u2028\r\n\u00A0", "\u2028\r\u00A0", "\u2028\f\u00A0", "\u2028\u0085\u00A0",
                        "\u2028\u2028\u00A0", "\u2028\u2029\u00A0", "\u2029\n\u00A0", "\u2029\r\n\u00A0", "\u2029\r\u00A0", "\u2029\f\u00A0", "\u2029\u0085\u00A0",
                        "\u2029\u2028\u00A0", "\u2029\u2029\u00A0"),
                new TestData("Test Data", "Test Data"),
                new TestData("Test  Data", "Test  Data"),
                new TestData("Test ", "Test ", " Test ", "\n Test ", "\r\n Test ", "\r Test ", "\f Test ", "\u0085 Test ", "\u2028 Test ", "\u2029 Test "),
                new TestData("Test  ", "  Test  "),
                new TestData("Test \n Data", " Test \n Data"),
                new TestData("Test \n Data ", "Test \n Data "),
                new TestData("Test \n", "\n Test \n"),
                new TestData("Test \n ", " \n Test \n "),
                new TestData("Test \r\n Data", " Test \r\n Data"),
                new TestData("Test \r\n Data ", "Test \r\n Data "),
                new TestData("Test \r\n", "\r\n Test \r\n"),
                new TestData("Test \r\n ", " \r\n Test \r\n "),
                new TestData("Test \r Data", " Test \r Data"),
                new TestData("Test \r Data ", "Test \r Data "),
                new TestData("Test \r", "\r Test \r"),
                new TestData("Test \r ", " \r Test \r "),
                new TestData("Test \f Data", " Test \f Data"),
                new TestData("Test \f Data ", "Test \f Data "),
                new TestData("Test \f", "\f Test \f"),
                new TestData("Test \f ", " \f Test \f "),
                new TestData("Test \u0085 Data", " Test \u0085 Data"),
                new TestData("Test \u0085 Data ", "Test \u0085 Data "),
                new TestData("Test \u0085", "\u0085 Test \u0085"),
                new TestData("Test \u0085 ", " \u0085 Test \u0085 "),
                new TestData("Test \u2028 Data", " Test \u2028 Data"),
                new TestData("Test \u2028 Data ", "Test \u2028 Data "),
                new TestData("Test \u2028", "\u2028 Test \u2028"),
                new TestData("Test \u2028 ", " \u2028 Test \u2028 "),
                new TestData("Test \u2029 Data", " Test \u2029 Data"),
                new TestData("Test \u2029 Data ", "Test \u2029 Data "),
                new TestData("Test \u2029", "\u2029 Test \u2029"),
                new TestData("Test \u2029 ", " \u2029 Test \u2029 "),
                new TestData("Test\tData", "Test\tData"),
                new TestData("Test\t\tData", "Test\t\tData"),
                new TestData("Test\t", "Test\t", "\tTest\t", "\n\tTest\t", "\r\n\tTest\t", "\r\tTest\t", "\f\tTest\t", "\u0085\tTest\t", "\u2028\tTest\t",
                        "\u2029\tTest\t"),
                new TestData("Test\t\t", "\t\tTest\t\t"),
                new TestData("Test\t\n\tData", "\tTest\t\n\tData"),
                new TestData("Test\t\n\tData\t", "Test\t\n\tData\t"),
                new TestData("Test\t\n", "\n\tTest\t\n"),
                new TestData("Test\t\n\t", "\t\n\tTest\t\n\t"),
                new TestData("Test\t\r\n\tData", "\tTest\t\r\n\tData"),
                new TestData("Test\t\r\n\tData\t", "Test\t\r\n\tData\t"),
                new TestData("Test\t\r\n", "\r\n\tTest\t\r\n"),
                new TestData("Test\t\r\n\t", "\t\r\n\tTest\t\r\n\t"),
                new TestData("Test\t\r\tData", "\tTest\t\r\tData"),
                new TestData("Test\t\r\tData\t", "Test\t\r\tData\t"),
                new TestData("Test\t\r", "\r\tTest\t\r"),
                new TestData("Test\t\r\t", "\t\r\tTest\t\r\t"),
                new TestData("Test\t\f\tData", "\tTest\t\f\tData"),
                new TestData("Test\t\f\tData\t", "Test\t\f\tData\t"),
                new TestData("Test\t\f", "\f\tTest\t\f"),
                new TestData("Test\t\f\t", "\t\f\tTest\t\f\t"),
                new TestData("Test\t\u0085\tData", "\tTest\t\u0085\tData"),
                new TestData("Test\t\u0085\tData\t", "Test\t\u0085\tData\t"),
                new TestData("Test\t\u0085", "\u0085\tTest\t\u0085"),
                new TestData("Test\t\u0085\t", "\t\u0085\tTest\t\u0085\t"),
                new TestData("Test\t\u2028\tData", "\tTest\t\u2028\tData"),
                new TestData("Test\t\u2028\tData\t", "Test\t\u2028\tData\t"),
                new TestData("Test\t\u2028", "\u2028\tTest\t\u2028"),
                new TestData("Test\t\u2028\t", "\t\u2028\tTest\t\u2028\t"),
                new TestData("Test\t\u2029\tData", "\tTest\t\u2029\tData"),
                new TestData("Test\t\u2029\tData\t", "Test\t\u2029\tData\t"),
                new TestData("Test\t\u2029", "\u2029\tTest\t\u2029"),
                new TestData("Test\t\u2029\t", "\t\u2029\tTest\t\u2029\t"),
                new TestData("Test\u1680Data", "Test\u1680Data"),
                new TestData("Test\u1680\u1680Data", "Test\u1680\u1680Data"),
                new TestData("Test\u1680", "Test\u1680", "\u1680Test\u1680", "\n\u1680Test\u1680", "\r\n\u1680Test\u1680", "\r\u1680Test\u1680", "\f\u1680Test\u1680",
                        "\u0085\u1680Test\u1680", "\u2028\u1680Test\u1680", "\u2029\u1680Test\u1680"),
                new TestData("Test\u1680\u1680", "\u1680\u1680Test\u1680\u1680"),
                new TestData("Test\u1680\n\u1680Data", "\u1680Test\u1680\n\u1680Data"),
                new TestData("Test\u1680\n\u1680Data\u1680", "Test\u1680\n\u1680Data\u1680"),
                new TestData("Test\u1680\n", "\n\u1680Test\u1680\n"),
                new TestData("Test\u1680\n\u1680", "\u1680\n\u1680Test\u1680\n\u1680"),
                new TestData("Test\u1680\r\n\u1680Data", "\u1680Test\u1680\r\n\u1680Data"),
                new TestData("Test\u1680\r\n\u1680Data\u1680", "Test\u1680\r\n\u1680Data\u1680"),
                new TestData("Test\u1680\r\n", "\r\n\u1680Test\u1680\r\n"),
                new TestData("Test\u1680\r\n\u1680", "\u1680\r\n\u1680Test\u1680\r\n\u1680"),
                new TestData("Test\u1680\r\u1680Data", "\u1680Test\u1680\r\u1680Data"),
                new TestData("Test\u1680\r\u1680Data\u1680", "Test\u1680\r\u1680Data\u1680"),
                new TestData("Test\u1680\r", "\r\u1680Test\u1680\r"),
                new TestData("Test\u1680\r\u1680", "\u1680\r\u1680Test\u1680\r\u1680"),
                new TestData("Test\u1680\f\u1680Data", "\u1680Test\u1680\f\u1680Data"),
                new TestData("Test\u1680\f\u1680Data\u1680", "Test\u1680\f\u1680Data\u1680"),
                new TestData("Test\u1680\f", "\f\u1680Test\u1680\f"),
                new TestData("Test\u1680\f\u1680", "\u1680\f\u1680Test\u1680\f\u1680"),
                new TestData("Test\u1680\u0085\u1680Data", "\u1680Test\u1680\u0085\u1680Data"),
                new TestData("Test\u1680\u0085\u1680Data\u1680", "Test\u1680\u0085\u1680Data\u1680"),
                new TestData("Test\u1680\u0085", "\u0085\u1680Test\u1680\u0085"),
                new TestData("Test\u1680\u0085\u1680", "\u1680\u0085\u1680Test\u1680\u0085\u1680"),
                new TestData("Test\u1680\u2028\u1680Data", "\u1680Test\u1680\u2028\u1680Data"),
                new TestData("Test\u1680\u2028\u1680Data\u1680", "Test\u1680\u2028\u1680Data\u1680"),
                new TestData("Test\u1680\u2028", "\u2028\u1680Test\u1680\u2028"),
                new TestData("Test\u1680\u2028\u1680", "\u1680\u2028\u1680Test\u1680\u2028\u1680"),
                new TestData("Test\u1680\u2029\u1680Data", "\u1680Test\u1680\u2029\u1680Data"),
                new TestData("Test\u1680\u2029\u1680Data\u1680", "Test\u1680\u2029\u1680Data\u1680"),
                new TestData("Test\u1680\u2029", "\u2029\u1680Test\u1680\u2029"),
                new TestData("Test\u1680\u2029\u1680", "\u1680\u2029\u1680Test\u1680\u2029\u1680"),
                new TestData("Test\nData", "Test\nData"),
                new TestData("Test\n", "\nTest\n"),
                new TestData("Test\r\nData", "Test\r\nData"),
                new TestData("Test\r\n", "\r\nTest\r\n"),
                new TestData("Test\rData", "Test\rData"),
                new TestData("Test\r", "\rTest\r"),
                new TestData("Test\fData", "Test\fData"),
                new TestData("Test\f", "\fTest\f"),
                new TestData("Test\u0085Data", "Test\u0085Data"),
                new TestData("Test\u0085", "\u0085Test\u0085"),
                new TestData("Test\u2028Data", "Test\u2028Data"),
                new TestData("Test\u2028", "\u2028Test\u2028"),
                new TestData("Test\u2029Data", "Test\u2029Data"),
                new TestData("Test\u2029", "\u2029Test\u2029"),
                new TestData("Test\n\nData", "Test\n\nData"),
                new TestData("Test\n\n", "Test\n\n", "\n\nTest\n\n"),
                new TestData("Test\n\r\nData", "Test\n\r\nData"),
                new TestData("Test\n\r\n", "Test\n\r\n", "\n\r\nTest\n\r\n"),
                new TestData("Test\n\rData", "Test\n\rData"),
                new TestData("Test\n\r", "Test\n\r", "\n\rTest\n\r"),
                new TestData("Test\n\fData", "Test\n\fData"),
                new TestData("Test\n\f", "Test\n\f", "\n\fTest\n\f"),
                new TestData("Test\n\u0085Data", "Test\n\u0085Data"),
                new TestData("Test\n\u0085", "Test\n\u0085", "\n\u0085Test\n\u0085"),
                new TestData("Test\n\u2028Data", "Test\n\u2028Data"),
                new TestData("Test\n\u2028", "Test\n\u2028", "\n\u2028Test\n\u2028"),
                new TestData("Test\n\u2029Data", "Test\n\u2029Data"),
                new TestData("Test\n\u2029", "Test\n\u2029", "\n\u2029Test\n\u2029"),
                new TestData("Test\r\n\nData", "Test\r\n\nData"),
                new TestData("Test\r\n\n", "Test\r\n\n", "\r\n\nTest\r\n\n"),
                new TestData("Test\r\n\r\nData", "Test\r\n\r\nData"),
                new TestData("Test\r\n\r\n", "Test\r\n\r\n", "\r\n\r\nTest\r\n\r\n"),
                new TestData("Test\r\n\rData", "Test\r\n\rData"),
                new TestData("Test\r\n\r", "Test\r\n\r", "\r\n\rTest\r\n\r"),
                new TestData("Test\r\n\fData", "Test\r\n\fData"),
                new TestData("Test\r\n\f", "Test\r\n\f", "\r\n\fTest\r\n\f"),
                new TestData("Test\r\n\u0085Data", "Test\r\n\u0085Data"),
                new TestData("Test\r\n\u0085", "Test\r\n\u0085", "\r\n\u0085Test\r\n\u0085"),
                new TestData("Test\r\n\u2028Data", "Test\r\n\u2028Data"),
                new TestData("Test\r\n\u2028", "Test\r\n\u2028", "\r\n\u2028Test\r\n\u2028"),
                new TestData("Test\r\n\u2029Data", "Test\r\n\u2029Data"),
                new TestData("Test\r\n\u2029", "Test\r\n\u2029", "\r\n\u2029Test\r\n\u2029"),
                new TestData("Test\r\r\nData", "Test\r\r\nData"),
                new TestData("Test\r\r\n", "Test\r\r\n", "\r\r\nTest\r\r\n"),
                new TestData("Test\r\rData", "Test\r\rData"),
                new TestData("Test\r\r", "Test\r\r", "\r\rTest\r\r"),
                new TestData("Test\r\fData", "Test\r\fData"),
                new TestData("Test\r\f", "Test\r\f", "\r\fTest\r\f"),
                new TestData("Test\r\u0085Data", "Test\r\u0085Data"),
                new TestData("Test\r\u0085", "Test\r\u0085", "\r\u0085Test\r\u0085"),
                new TestData("Test\r\u2028Data", "Test\r\u2028Data"),
                new TestData("Test\r\u2028", "Test\r\u2028", "\r\u2028Test\r\u2028"),
                new TestData("Test\r\u2029Data", "Test\r\u2029Data"),
                new TestData("Test\r\u2029", "Test\r\u2029", "\r\u2029Test\r\u2029"),
                new TestData("Test\f\nData", "Test\f\nData"),
                new TestData("Test\f\n", "Test\f\n", "\f\nTest\f\n"),
                new TestData("Test\f\r\nData", "Test\f\r\nData"),
                new TestData("Test\f\r\n", "Test\f\r\n", "\f\r\nTest\f\r\n"),
                new TestData("Test\f\rData", "Test\f\rData"),
                new TestData("Test\f\r", "Test\f\r", "\f\rTest\f\r"),
                new TestData("Test\f\fData", "Test\f\fData"),
                new TestData("Test\f\f", "Test\f\f", "\f\fTest\f\f"),
                new TestData("Test\f\u0085Data", "Test\f\u0085Data"),
                new TestData("Test\f\u0085", "Test\f\u0085", "\f\u0085Test\f\u0085"),
                new TestData("Test\f\u2028Data", "Test\f\u2028Data"),
                new TestData("Test\f\u2028", "Test\f\u2028", "\f\u2028Test\f\u2028"),
                new TestData("Test\f\u2029Data", "Test\f\u2029Data"),
                new TestData("Test\f\u2029", "Test\f\u2029", "\f\u2029Test\f\u2029"),
                new TestData("Test\u0085\nData", "Test\u0085\nData"),
                new TestData("Test\u0085\n", "Test\u0085\n", "\u0085\nTest\u0085\n"),
                new TestData("Test\u0085\r\nData", "Test\u0085\r\nData"),
                new TestData("Test\u0085\r\n", "Test\u0085\r\n", "\u0085\r\nTest\u0085\r\n"),
                new TestData("Test\u0085\rData", "Test\u0085\rData"),
                new TestData("Test\u0085\r", "Test\u0085\r", "\u0085\rTest\u0085\r"),
                new TestData("Test\u0085\fData", "Test\u0085\fData"),
                new TestData("Test\u0085\f", "Test\u0085\f", "\u0085\fTest\u0085\f"),
                new TestData("Test\u0085\u0085Data", "Test\u0085\u0085Data"),
                new TestData("Test\u0085\u0085", "Test\u0085\u0085", "\u0085\u0085Test\u0085\u0085"),
                new TestData("Test\u0085\u2028Data", "Test\u0085\u2028Data"),
                new TestData("Test\u0085\u2028", "Test\u0085\u2028", "\u0085\u2028Test\u0085\u2028"),
                new TestData("Test\u0085\u2029Data", "Test\u0085\u2029Data"),
                new TestData("Test\u0085\u2029", "Test\u0085\u2029", "\u0085\u2029Test\u0085\u2029"),
                new TestData("Test\u2028\nData", "Test\u2028\nData"),
                new TestData("Test\u2028\n", "Test\u2028\n", "\u2028\nTest\u2028\n"),
                new TestData("Test\u2028\r\nData", "Test\u2028\r\nData"),
                new TestData("Test\u2028\r\n", "Test\u2028\r\n", "\u2028\r\nTest\u2028\r\n"),
                new TestData("Test\u2028\rData", "Test\u2028\rData"),
                new TestData("Test\u2028\r", "Test\u2028\r", "\u2028\rTest\u2028\r"),
                new TestData("Test\u2028\fData", "Test\u2028\fData"),
                new TestData("Test\u2028\f", "Test\u2028\f", "\u2028\fTest\u2028\f"),
                new TestData("Test\u2028\u0085Data", "Test\u2028\u0085Data"),
                new TestData("Test\u2028\u0085", "Test\u2028\u0085", "\u2028\u0085Test\u2028\u0085"),
                new TestData("Test\u2028\u2028Data", "Test\u2028\u2028Data"),
                new TestData("Test\u2028\u2028", "Test\u2028\u2028", "\u2028\u2028Test\u2028\u2028"),
                new TestData("Test\u2028\u2029Data", "Test\u2028\u2029Data"),
                new TestData("Test\u2028\u2029", "Test\u2028\u2029", "\u2028\u2029Test\u2028\u2029"),
                new TestData("Test\u2029\nData", "Test\u2029\nData"),
                new TestData("Test\u2029\n", "Test\u2029\n", "\u2029\nTest\u2029\n"),
                new TestData("Test\u2029\r\nData", "Test\u2029\r\nData"),
                new TestData("Test\u2029\r\n", "Test\u2029\r\n", "\u2029\r\nTest\u2029\r\n"),
                new TestData("Test\u2029\rData", "Test\u2029\rData"),
                new TestData("Test\u2029\r", "Test\u2029\r", "\u2029\rTest\u2029\r"),
                new TestData("Test\u2029\fData", "Test\u2029\fData"),
                new TestData("Test\u2029\f", "Test\u2029\f", "\u2029\fTest\u2029\f"),
                new TestData("Test\u2029\u0085Data", "Test\u2029\u0085Data"),
                new TestData("Test\u2029\u0085", "Test\u2029\u0085", "\u2029\u0085Test\u2029\u0085"),
                new TestData("Test\u2029\u2028Data", "Test\u2029\u2028Data"),
                new TestData("Test\u2029\u2028", "Test\u2029\u2028", "\u2029\u2028Test\u2029\u2028"),
                new TestData("Test\u2029\u2029Data", "Test\u2029\u2029Data"),
                new TestData("Test\u2029\u2029", "Test\u2029\u2029", "\u2029\u2029Test\u2029\u2029"),
                new TestData("\u00A0 _", "\u00A0 _"),
                new TestData("\u00A0  _", "\u00A0  _"),
                new TestData("\u00A0 ", "\u00A0 ", " \u00A0 ", "\n \u00A0 ", "\r\n \u00A0 ", "\r \u00A0 ", "\f \u00A0 ", "\u0085 \u00A0 ", "\u2028 \u00A0 ",
                        "\u2029 \u00A0 "),
                new TestData("\u00A0  ", "  \u00A0  "),
                new TestData("\u00A0 \n _", " \u00A0 \n _"),
                new TestData("\u00A0 \n _ ", "\u00A0 \n _ "),
                new TestData("\u00A0 \n", "\n \u00A0 \n"),
                new TestData("\u00A0 \n ", " \n \u00A0 \n "),
                new TestData("\u00A0 \r\n _", " \u00A0 \r\n _"),
                new TestData("\u00A0 \r\n _ ", "\u00A0 \r\n _ "),
                new TestData("\u00A0 \r\n", "\r\n \u00A0 \r\n"),
                new TestData("\u00A0 \r\n ", " \r\n \u00A0 \r\n "),
                new TestData("\u00A0 \r _", " \u00A0 \r _"),
                new TestData("\u00A0 \r _ ", "\u00A0 \r _ "),
                new TestData("\u00A0 \r", "\r \u00A0 \r"),
                new TestData("\u00A0 \r ", " \r \u00A0 \r "),
                new TestData("\u00A0 \f _", " \u00A0 \f _"),
                new TestData("\u00A0 \f _ ", "\u00A0 \f _ "),
                new TestData("\u00A0 \f", "\f \u00A0 \f"),
                new TestData("\u00A0 \f ", " \f \u00A0 \f "),
                new TestData("\u00A0 \u0085 _", " \u00A0 \u0085 _"),
                new TestData("\u00A0 \u0085 _ ", "\u00A0 \u0085 _ "),
                new TestData("\u00A0 \u0085", "\u0085 \u00A0 \u0085"),
                new TestData("\u00A0 \u0085 ", " \u0085 \u00A0 \u0085 "),
                new TestData("\u00A0 \u2028 _", " \u00A0 \u2028 _"),
                new TestData("\u00A0 \u2028 _ ", "\u00A0 \u2028 _ "),
                new TestData("\u00A0 \u2028", "\u2028 \u00A0 \u2028"),
                new TestData("\u00A0 \u2028 ", " \u2028 \u00A0 \u2028 "),
                new TestData("\u00A0 \u2029 _", " \u00A0 \u2029 _"),
                new TestData("\u00A0 \u2029 _ ", "\u00A0 \u2029 _ "),
                new TestData("\u00A0 \u2029", "\u2029 \u00A0 \u2029"),
                new TestData("\u00A0 \u2029 ", " \u2029 \u00A0 \u2029 "),
                new TestData("\u00A0\t_", "\u00A0\t_"),
                new TestData("\u00A0\t\t_", "\u00A0\t\t_"),
                new TestData("\u00A0\t", "\u00A0\t", "\t\u00A0\t", "\n\t\u00A0\t", "\r\n\t\u00A0\t", "\r\t\u00A0\t", "\f\t\u00A0\t", "\u0085\t\u00A0\t",
                        "\u2028\t\u00A0\t", "\u2029\t\u00A0\t"),
                new TestData("\u00A0\t\t", "\t\t\u00A0\t\t"),
                new TestData("\u00A0\t\n\t_", "\t\u00A0\t\n\t_"),
                new TestData("\u00A0\t\n\t_\t", "\u00A0\t\n\t_\t"),
                new TestData("\u00A0\t\n", "\n\t\u00A0\t\n"),
                new TestData("\u00A0\t\n\t", "\t\n\t\u00A0\t\n\t"),
                new TestData("\u00A0\t\r\n\t_", "\t\u00A0\t\r\n\t_"),
                new TestData("\u00A0\t\r\n\t_\t", "\u00A0\t\r\n\t_\t"),
                new TestData("\u00A0\t\r\n", "\r\n\t\u00A0\t\r\n"),
                new TestData("\u00A0\t\r\n\t", "\t\r\n\t\u00A0\t\r\n\t"),
                new TestData("\u00A0\t\r\t_", "\t\u00A0\t\r\t_"),
                new TestData("\u00A0\t\r\t_\t", "\u00A0\t\r\t_\t"),
                new TestData("\u00A0\t\r", "\r\t\u00A0\t\r"),
                new TestData("\u00A0\t\r\t", "\t\r\t\u00A0\t\r\t"),
                new TestData("\u00A0\t\f\t_", "\t\u00A0\t\f\t_"),
                new TestData("\u00A0\t\f\t_\t", "\u00A0\t\f\t_\t"),
                new TestData("\u00A0\t\f", "\f\t\u00A0\t\f"),
                new TestData("\u00A0\t\f\t", "\t\f\t\u00A0\t\f\t"),
                new TestData("\u00A0\t\u0085\t_", "\t\u00A0\t\u0085\t_"),
                new TestData("\u00A0\t\u0085\t_\t", "\u00A0\t\u0085\t_\t"),
                new TestData("\u00A0\t\u0085", "\u0085\t\u00A0\t\u0085"),
                new TestData("\u00A0\t\u0085\t", "\t\u0085\t\u00A0\t\u0085\t"),
                new TestData("\u00A0\t\u2028\t_", "\t\u00A0\t\u2028\t_"),
                new TestData("\u00A0\t\u2028\t_\t", "\u00A0\t\u2028\t_\t"),
                new TestData("\u00A0\t\u2028", "\u2028\t\u00A0\t\u2028"),
                new TestData("\u00A0\t\u2028\t", "\t\u2028\t\u00A0\t\u2028\t"),
                new TestData("\u00A0\t\u2029\t_", "\t\u00A0\t\u2029\t_"),
                new TestData("\u00A0\t\u2029\t_\t", "\u00A0\t\u2029\t_\t"),
                new TestData("\u00A0\t\u2029", "\u2029\t\u00A0\t\u2029"),
                new TestData("\u00A0\t\u2029\t", "\t\u2029\t\u00A0\t\u2029\t"),
                new TestData("\u00A0\u1680_", "\u00A0\u1680_"),
                new TestData("\u00A0\u1680\u1680_", "\u00A0\u1680\u1680_"),
                new TestData("\u00A0\u1680", "\u00A0\u1680", "\u1680\u00A0\u1680", "\n\u1680\u00A0\u1680", "\r\n\u1680\u00A0\u1680", "\r\u1680\u00A0\u1680",
                        "\f\u1680\u00A0\u1680", "\u0085\u1680\u00A0\u1680", "\u2028\u1680\u00A0\u1680", "\u2029\u1680\u00A0\u1680"),
                new TestData("\u00A0\u1680\u1680", "\u1680\u1680\u00A0\u1680\u1680"),
                new TestData("\u00A0\u1680\n\u1680_", "\u1680\u00A0\u1680\n\u1680_"),
                new TestData("\u00A0\u1680\n\u1680_\u1680", "\u00A0\u1680\n\u1680_\u1680"),
                new TestData("\u00A0\u1680\n", "\n\u1680\u00A0\u1680\n"),
                new TestData("\u00A0\u1680\n\u1680", "\u1680\n\u1680\u00A0\u1680\n\u1680"),
                new TestData("\u00A0\u1680\r\n\u1680_", "\u1680\u00A0\u1680\r\n\u1680_"),
                new TestData("\u00A0\u1680\r\n\u1680_\u1680", "\u00A0\u1680\r\n\u1680_\u1680"),
                new TestData("\u00A0\u1680\r\n", "\r\n\u1680\u00A0\u1680\r\n"),
                new TestData("\u00A0\u1680\r\n\u1680", "\u1680\r\n\u1680\u00A0\u1680\r\n\u1680"),
                new TestData("\u00A0\u1680\r\u1680_", "\u1680\u00A0\u1680\r\u1680_"),
                new TestData("\u00A0\u1680\r\u1680_\u1680", "\u00A0\u1680\r\u1680_\u1680"),
                new TestData("\u00A0\u1680\r", "\r\u1680\u00A0\u1680\r"),
                new TestData("\u00A0\u1680\r\u1680", "\u1680\r\u1680\u00A0\u1680\r\u1680"),
                new TestData("\u00A0\u1680\f\u1680_", "\u1680\u00A0\u1680\f\u1680_"),
                new TestData("\u00A0\u1680\f\u1680_\u1680", "\u00A0\u1680\f\u1680_\u1680"),
                new TestData("\u00A0\u1680\f", "\f\u1680\u00A0\u1680\f"),
                new TestData("\u00A0\u1680\f\u1680", "\u1680\f\u1680\u00A0\u1680\f\u1680"),
                new TestData("\u00A0\u1680\u0085\u1680_", "\u1680\u00A0\u1680\u0085\u1680_"),
                new TestData("\u00A0\u1680\u0085\u1680_\u1680", "\u00A0\u1680\u0085\u1680_\u1680"),
                new TestData("\u00A0\u1680\u0085", "\u0085\u1680\u00A0\u1680\u0085"),
                new TestData("\u00A0\u1680\u0085\u1680", "\u1680\u0085\u1680\u00A0\u1680\u0085\u1680"),
                new TestData("\u00A0\u1680\u2028\u1680_", "\u1680\u00A0\u1680\u2028\u1680_"),
                new TestData("\u00A0\u1680\u2028\u1680_\u1680", "\u00A0\u1680\u2028\u1680_\u1680"),
                new TestData("\u00A0\u1680\u2028", "\u2028\u1680\u00A0\u1680\u2028"),
                new TestData("\u00A0\u1680\u2028\u1680", "\u1680\u2028\u1680\u00A0\u1680\u2028\u1680"),
                new TestData("\u00A0\u1680\u2029\u1680_", "\u1680\u00A0\u1680\u2029\u1680_"),
                new TestData("\u00A0\u1680\u2029\u1680_\u1680", "\u00A0\u1680\u2029\u1680_\u1680"),
                new TestData("\u00A0\u1680\u2029", "\u2029\u1680\u00A0\u1680\u2029"),
                new TestData("\u00A0\u1680\u2029\u1680", "\u1680\u2029\u1680\u00A0\u1680\u2029\u1680"),
                new TestData("\u00A0\n_", "\u00A0\n_"),
                new TestData("\u00A0\n", "\n\u00A0\n"),
                new TestData("\u00A0\r\n_", "\u00A0\r\n_"),
                new TestData("\u00A0\r\n", "\r\n\u00A0\r\n"),
                new TestData("\u00A0\r_", "\u00A0\r_"),
                new TestData("\u00A0\r", "\r\u00A0\r"),
                new TestData("\u00A0\f_", "\u00A0\f_"),
                new TestData("\u00A0\f", "\f\u00A0\f"),
                new TestData("\u00A0\u0085_", "\u00A0\u0085_"),
                new TestData("\u00A0\u0085", "\u0085\u00A0\u0085"),
                new TestData("\u00A0\u2028_", "\u00A0\u2028_"),
                new TestData("\u00A0\u2028", "\u2028\u00A0\u2028"),
                new TestData("\u00A0\u2029_", "\u00A0\u2029_"),
                new TestData("\u00A0\u2029", "\u2029\u00A0\u2029"),
                new TestData("\u00A0\n\n_", "\u00A0\n\n_"),
                new TestData("\u00A0\n\n", "\u00A0\n\n", "\n\n\u00A0\n\n"),
                new TestData("\u00A0\n\r\n_", "\u00A0\n\r\n_"),
                new TestData("\u00A0\n\r\n", "\u00A0\n\r\n", "\n\r\n\u00A0\n\r\n"),
                new TestData("\u00A0\n\r_", "\u00A0\n\r_"),
                new TestData("\u00A0\n\r", "\u00A0\n\r", "\n\r\u00A0\n\r"),
                new TestData("\u00A0\n\f_", "\u00A0\n\f_"),
                new TestData("\u00A0\n\f", "\u00A0\n\f", "\n\f\u00A0\n\f"),
                new TestData("\u00A0\n\u0085_", "\u00A0\n\u0085_"),
                new TestData("\u00A0\n\u0085", "\u00A0\n\u0085", "\n\u0085\u00A0\n\u0085"),
                new TestData("\u00A0\n\u2028_", "\u00A0\n\u2028_"),
                new TestData("\u00A0\n\u2028", "\u00A0\n\u2028", "\n\u2028\u00A0\n\u2028"),
                new TestData("\u00A0\n\u2029_", "\u00A0\n\u2029_"),
                new TestData("\u00A0\n\u2029", "\u00A0\n\u2029", "\n\u2029\u00A0\n\u2029"),
                new TestData("\u00A0\r\n\n_", "\u00A0\r\n\n_"),
                new TestData("\u00A0\r\n\n", "\u00A0\r\n\n", "\r\n\n\u00A0\r\n\n"),
                new TestData("\u00A0\r\n\r\n_", "\u00A0\r\n\r\n_"),
                new TestData("\u00A0\r\n\r\n", "\u00A0\r\n\r\n", "\r\n\r\n\u00A0\r\n\r\n"),
                new TestData("\u00A0\r\n\r_", "\u00A0\r\n\r_"),
                new TestData("\u00A0\r\n\r", "\u00A0\r\n\r", "\r\n\r\u00A0\r\n\r"),
                new TestData("\u00A0\r\n\f_", "\u00A0\r\n\f_"),
                new TestData("\u00A0\r\n\f", "\u00A0\r\n\f", "\r\n\f\u00A0\r\n\f"),
                new TestData("\u00A0\r\n\u0085_", "\u00A0\r\n\u0085_"),
                new TestData("\u00A0\r\n\u0085", "\u00A0\r\n\u0085", "\r\n\u0085\u00A0\r\n\u0085"),
                new TestData("\u00A0\r\n\u2028_", "\u00A0\r\n\u2028_"),
                new TestData("\u00A0\r\n\u2028", "\u00A0\r\n\u2028", "\r\n\u2028\u00A0\r\n\u2028"),
                new TestData("\u00A0\r\n\u2029_", "\u00A0\r\n\u2029_"),
                new TestData("\u00A0\r\n\u2029", "\u00A0\r\n\u2029", "\r\n\u2029\u00A0\r\n\u2029"),
                new TestData("\u00A0\r\r\n_", "\u00A0\r\r\n_"),
                new TestData("\u00A0\r\r\n", "\u00A0\r\r\n", "\r\r\n\u00A0\r\r\n"),
                new TestData("\u00A0\r\r_", "\u00A0\r\r_"),
                new TestData("\u00A0\r\r", "\u00A0\r\r", "\r\r\u00A0\r\r"),
                new TestData("\u00A0\r\f_", "\u00A0\r\f_"),
                new TestData("\u00A0\r\f", "\u00A0\r\f", "\r\f\u00A0\r\f"),
                new TestData("\u00A0\r\u0085_", "\u00A0\r\u0085_"),
                new TestData("\u00A0\r\u0085", "\u00A0\r\u0085", "\r\u0085\u00A0\r\u0085"),
                new TestData("\u00A0\r\u2028_", "\u00A0\r\u2028_"),
                new TestData("\u00A0\r\u2028", "\u00A0\r\u2028", "\r\u2028\u00A0\r\u2028"),
                new TestData("\u00A0\r\u2029_", "\u00A0\r\u2029_"),
                new TestData("\u00A0\r\u2029", "\u00A0\r\u2029", "\r\u2029\u00A0\r\u2029"),
                new TestData("\u00A0\f\n_", "\u00A0\f\n_"),
                new TestData("\u00A0\f\n", "\u00A0\f\n", "\f\n\u00A0\f\n"),
                new TestData("\u00A0\f\r\n_", "\u00A0\f\r\n_"),
                new TestData("\u00A0\f\r\n", "\u00A0\f\r\n", "\f\r\n\u00A0\f\r\n"),
                new TestData("\u00A0\f\r_", "\u00A0\f\r_"),
                new TestData("\u00A0\f\r", "\u00A0\f\r", "\f\r\u00A0\f\r"),
                new TestData("\u00A0\f\f_", "\u00A0\f\f_"),
                new TestData("\u00A0\f\f", "\u00A0\f\f", "\f\f\u00A0\f\f"),
                new TestData("\u00A0\f\u0085_", "\u00A0\f\u0085_"),
                new TestData("\u00A0\f\u0085", "\u00A0\f\u0085", "\f\u0085\u00A0\f\u0085"),
                new TestData("\u00A0\f\u2028_", "\u00A0\f\u2028_"),
                new TestData("\u00A0\f\u2028", "\u00A0\f\u2028", "\f\u2028\u00A0\f\u2028"),
                new TestData("\u00A0\f\u2029_", "\u00A0\f\u2029_"),
                new TestData("\u00A0\f\u2029", "\u00A0\f\u2029", "\f\u2029\u00A0\f\u2029"),
                new TestData("\u00A0\u0085\n_", "\u00A0\u0085\n_"),
                new TestData("\u00A0\u0085\n", "\u00A0\u0085\n", "\u0085\n\u00A0\u0085\n"),
                new TestData("\u00A0\u0085\r\n_", "\u00A0\u0085\r\n_"),
                new TestData("\u00A0\u0085\r\n", "\u00A0\u0085\r\n", "\u0085\r\n\u00A0\u0085\r\n"),
                new TestData("\u00A0\u0085\r_", "\u00A0\u0085\r_"),
                new TestData("\u00A0\u0085\r", "\u00A0\u0085\r", "\u0085\r\u00A0\u0085\r"),
                new TestData("\u00A0\u0085\f_", "\u00A0\u0085\f_"),
                new TestData("\u00A0\u0085\f", "\u00A0\u0085\f", "\u0085\f\u00A0\u0085\f"),
                new TestData("\u00A0\u0085\u0085_", "\u00A0\u0085\u0085_"),
                new TestData("\u00A0\u0085\u0085", "\u00A0\u0085\u0085", "\u0085\u0085\u00A0\u0085\u0085"),
                new TestData("\u00A0\u0085\u2028_", "\u00A0\u0085\u2028_"),
                new TestData("\u00A0\u0085\u2028", "\u00A0\u0085\u2028", "\u0085\u2028\u00A0\u0085\u2028"),
                new TestData("\u00A0\u0085\u2029_", "\u00A0\u0085\u2029_"),
                new TestData("\u00A0\u0085\u2029", "\u00A0\u0085\u2029", "\u0085\u2029\u00A0\u0085\u2029"),
                new TestData("\u00A0\u2028\n_", "\u00A0\u2028\n_"),
                new TestData("\u00A0\u2028\n", "\u00A0\u2028\n", "\u2028\n\u00A0\u2028\n"),
                new TestData("\u00A0\u2028\r\n_", "\u00A0\u2028\r\n_"),
                new TestData("\u00A0\u2028\r\n", "\u00A0\u2028\r\n", "\u2028\r\n\u00A0\u2028\r\n"),
                new TestData("\u00A0\u2028\r_", "\u00A0\u2028\r_"),
                new TestData("\u00A0\u2028\r", "\u00A0\u2028\r", "\u2028\r\u00A0\u2028\r"),
                new TestData("\u00A0\u2028\f_", "\u00A0\u2028\f_"),
                new TestData("\u00A0\u2028\f", "\u00A0\u2028\f", "\u2028\f\u00A0\u2028\f"),
                new TestData("\u00A0\u2028\u0085_", "\u00A0\u2028\u0085_"),
                new TestData("\u00A0\u2028\u0085", "\u00A0\u2028\u0085", "\u2028\u0085\u00A0\u2028\u0085"),
                new TestData("\u00A0\u2028\u2028_", "\u00A0\u2028\u2028_"),
                new TestData("\u00A0\u2028\u2028", "\u00A0\u2028\u2028", "\u2028\u2028\u00A0\u2028\u2028"),
                new TestData("\u00A0\u2028\u2029_", "\u00A0\u2028\u2029_"),
                new TestData("\u00A0\u2028\u2029", "\u00A0\u2028\u2029", "\u2028\u2029\u00A0\u2028\u2029"),
                new TestData("\u00A0\u2029\n_", "\u00A0\u2029\n_"),
                new TestData("\u00A0\u2029\n", "\u00A0\u2029\n", "\u2029\n\u00A0\u2029\n"),
                new TestData("\u00A0\u2029\r\n_", "\u00A0\u2029\r\n_"),
                new TestData("\u00A0\u2029\r\n", "\u00A0\u2029\r\n", "\u2029\r\n\u00A0\u2029\r\n"),
                new TestData("\u00A0\u2029\r_", "\u00A0\u2029\r_"),
                new TestData("\u00A0\u2029\r", "\u00A0\u2029\r", "\u2029\r\u00A0\u2029\r"),
                new TestData("\u00A0\u2029\f_", "\u00A0\u2029\f_"),
                new TestData("\u00A0\u2029\f", "\u00A0\u2029\f", "\u2029\f\u00A0\u2029\f"),
                new TestData("\u00A0\u2029\u0085_", "\u00A0\u2029\u0085_"),
                new TestData("\u00A0\u2029\u0085", "\u00A0\u2029\u0085", "\u2029\u0085\u00A0\u2029\u0085"),
                new TestData("\u00A0\u2029\u2028_", "\u00A0\u2029\u2028_"),
                new TestData("\u00A0\u2029\u2028", "\u00A0\u2029\u2028", "\u2029\u2028\u00A0\u2029\u2028"),
                new TestData("\u00A0\u2029\u2029_", "\u00A0\u2029\u2029_"),
                new TestData("\u00A0\u2029\u2029", "\u00A0\u2029\u2029", "\u2029\u2029\u00A0\u2029\u2029"),

        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(s -> {
                String d = TestHelper.toStringDescription(s);
                String a = StringHelper.trimStart(s);
                assertEquals(d, expected, a);
            });
        }
    }

    @Test
    public void trimEnd() {
        String actual = StringHelper.trimEnd(null);
        assertNull(actual);
        TestData[] testData = new TestData[]{
                new TestData("", "", "\n", "\r\n", "\r", "\f", "\u0085", "\u2028", "\u2029", "\n\n", "\n\r\n", "\n\r", "\n\f", "\n\u0085", "\n\u2028", "\n\u2029",
                        "\r\n\n", "\r\n\r\n", "\r\n\r", "\r\n\f", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\r\r\n", "\r\r", "\r\f", "\r\u0085", "\r\u2028", "\r\u2029",
                        "\f\n", "\f\r\n", "\f\r", "\f\f", "\f\u0085", "\f\u2028", "\f\u2029", "\u0085\n", "\u0085\r\n", "\u0085\r", "\u0085\f", "\u0085\u0085",
                        "\u0085\u2028", "\u0085\u2029", "\u2028\n", "\u2028\r\n", "\u2028\r", "\u2028\f", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\n",
                        "\u2029\r\n", "\u2029\r", "\u2029\f", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029", " ", "  ", " \t", " \u1680", " \n ", "\n ", "\n \n", " \r\n ",
                        "\r\n ", "\r\n \r\n", " \r ", "\r ", "\r \r", " \f ", "\f ", "\f \f", " \u0085 ", "\u0085 ", "\u0085 \u0085", " \u2028 ", "\u2028 ", "\u2028 \u2028",
                        " \u2029 ", "\u2029 ", "\u2029 \u2029", " \n\n ", "\n\n ", "\n\n \n\n", " \n\r\n ", "\n\r\n ", "\n\r\n \n\r\n", " \n\r ", "\n\r ", "\n\r \n\r",
                        " \n\f ", "\n\f ", "\n\f \n\f", " \n\u0085 ", "\n\u0085 ", "\n\u0085 \n\u0085", " \n\u2028 ", "\n\u2028 ", "\n\u2028 \n\u2028", " \n\u2029 ",
                        "\n\u2029 ", "\n\u2029 \n\u2029", " \r\n\n ", "\r\n\n ", "\r\n\n \r\n\n", " \r\n\r\n ", "\r\n\r\n ", "\r\n\r\n \r\n\r\n", " \r\n\r ", "\r\n\r ",
                        "\r\n\r \r\n\r", " \r\n\f ", "\r\n\f ", "\r\n\f \r\n\f", " \r\n\u0085 ", "\r\n\u0085 ", "\r\n\u0085 \r\n\u0085", " \r\n\u2028 ", "\r\n\u2028 ",
                        "\r\n\u2028 \r\n\u2028", " \r\n\u2029 ", "\r\n\u2029 ", "\r\n\u2029 \r\n\u2029", " \r\r\n ", "\r\r\n ", "\r\r\n \r\r\n", " \r\r ", "\r\r ",
                        "\r\r \r\r", " \r\f ", "\r\f ", "\r\f \r\f", " \r\u0085 ", "\r\u0085 ", "\r\u0085 \r\u0085", " \r\u2028 ", "\r\u2028 ", "\r\u2028 \r\u2028",
                        " \r\u2029 ", "\r\u2029 ", "\r\u2029 \r\u2029", " \f\n ", "\f\n ", "\f\n \f\n", " \f\r\n ", "\f\r\n ", "\f\r\n \f\r\n", " \f\r ", "\f\r ",
                        "\f\r \f\r", " \f\f ", "\f\f ", "\f\f \f\f", " \f\u0085 ", "\f\u0085 ", "\f\u0085 \f\u0085", " \f\u2028 ", "\f\u2028 ", "\f\u2028 \f\u2028",
                        " \f\u2029 ", "\f\u2029 ", "\f\u2029 \f\u2029", " \u0085\n ", "\u0085\n ", "\u0085\n \u0085\n", " \u0085\r\n ", "\u0085\r\n ",
                        "\u0085\r\n \u0085\r\n", " \u0085\r ", "\u0085\r ", "\u0085\r \u0085\r", " \u0085\f ", "\u0085\f ", "\u0085\f \u0085\f", " \u0085\u0085 ",
                        "\u0085\u0085 ", "\u0085\u0085 \u0085\u0085", " \u0085\u2028 ", "\u0085\u2028 ", "\u0085\u2028 \u0085\u2028", " \u0085\u2029 ", "\u0085\u2029 ",
                        "\u0085\u2029 \u0085\u2029", " \u2028\n ", "\u2028\n ", "\u2028\n \u2028\n", " \u2028\r\n ", "\u2028\r\n ", "\u2028\r\n \u2028\r\n", " \u2028\r ",
                        "\u2028\r ", "\u2028\r \u2028\r", " \u2028\f ", "\u2028\f ", "\u2028\f \u2028\f", " \u2028\u0085 ", "\u2028\u0085 ", "\u2028\u0085 \u2028\u0085",
                        " \u2028\u2028 ", "\u2028\u2028 ", "\u2028\u2028 \u2028\u2028", " \u2028\u2029 ", "\u2028\u2029 ", "\u2028\u2029 \u2028\u2029", " \u2029\n ",
                        "\u2029\n ", "\u2029\n \u2029\n", " \u2029\r\n ", "\u2029\r\n ", "\u2029\r\n \u2029\r\n", " \u2029\r ", "\u2029\r ", "\u2029\r \u2029\r",
                        " \u2029\f ", "\u2029\f ", "\u2029\f \u2029\f", " \u2029\u0085 ", "\u2029\u0085 ", "\u2029\u0085 \u2029\u0085", " \u2029\u2028 ", "\u2029\u2028 ",
                        "\u2029\u2028 \u2029\u2028", " \u2029\u2029 ", "\u2029\u2029 ", "\u2029\u2029 \u2029\u2029", "\t", "\t ", "\t\t", "\t\u1680", "\t\n\t", "\n\t",
                        "\n\t\n", "\t\r\n\t", "\r\n\t", "\r\n\t\r\n", "\t\r\t", "\r\t", "\r\t\r", "\t\f\t", "\f\t", "\f\t\f", "\t\u0085\t", "\u0085\t", "\u0085\t\u0085",
                        "\t\u2028\t", "\u2028\t", "\u2028\t\u2028", "\t\u2029\t", "\u2029\t", "\u2029\t\u2029", "\t\n\n\t", "\n\n\t", "\n\n\t\n\n", "\t\n\r\n\t", "\n\r\n\t",
                        "\n\r\n\t\n\r\n", "\t\n\r\t", "\n\r\t", "\n\r\t\n\r", "\t\n\f\t", "\n\f\t", "\n\f\t\n\f", "\t\n\u0085\t", "\n\u0085\t", "\n\u0085\t\n\u0085",
                        "\t\n\u2028\t", "\n\u2028\t", "\n\u2028\t\n\u2028", "\t\n\u2029\t", "\n\u2029\t", "\n\u2029\t\n\u2029", "\t\r\n\n\t", "\r\n\n\t", "\r\n\n\t\r\n\n",
                        "\t\r\n\r\n\t", "\r\n\r\n\t", "\r\n\r\n\t\r\n\r\n", "\t\r\n\r\t", "\r\n\r\t", "\r\n\r\t\r\n\r", "\t\r\n\f\t", "\r\n\f\t", "\r\n\f\t\r\n\f",
                        "\t\r\n\u0085\t", "\r\n\u0085\t", "\r\n\u0085\t\r\n\u0085", "\t\r\n\u2028\t", "\r\n\u2028\t", "\r\n\u2028\t\r\n\u2028", "\t\r\n\u2029\t",
                        "\r\n\u2029\t", "\r\n\u2029\t\r\n\u2029", "\t\r\r\n\t", "\r\r\n\t", "\r\r\n\t\r\r\n", "\t\r\r\t", "\r\r\t", "\r\r\t\r\r", "\t\r\f\t", "\r\f\t",
                        "\r\f\t\r\f", "\t\r\u0085\t", "\r\u0085\t", "\r\u0085\t\r\u0085", "\t\r\u2028\t", "\r\u2028\t", "\r\u2028\t\r\u2028", "\t\r\u2029\t", "\r\u2029\t",
                        "\r\u2029\t\r\u2029", "\t\f\n\t", "\f\n\t", "\f\n\t\f\n", "\t\f\r\n\t", "\f\r\n\t", "\f\r\n\t\f\r\n", "\t\f\r\t", "\f\r\t", "\f\r\t\f\r", "\t\f\f\t",
                        "\f\f\t", "\f\f\t\f\f", "\t\f\u0085\t", "\f\u0085\t", "\f\u0085\t\f\u0085", "\t\f\u2028\t", "\f\u2028\t", "\f\u2028\t\f\u2028", "\t\f\u2029\t",
                        "\f\u2029\t", "\f\u2029\t\f\u2029", "\t\u0085\n\t", "\u0085\n\t", "\u0085\n\t\u0085\n", "\t\u0085\r\n\t", "\u0085\r\n\t", "\u0085\r\n\t\u0085\r\n",
                        "\t\u0085\r\t", "\u0085\r\t", "\u0085\r\t\u0085\r", "\t\u0085\f\t", "\u0085\f\t", "\u0085\f\t\u0085\f", "\t\u0085\u0085\t", "\u0085\u0085\t",
                        "\u0085\u0085\t\u0085\u0085", "\t\u0085\u2028\t", "\u0085\u2028\t", "\u0085\u2028\t\u0085\u2028", "\t\u0085\u2029\t", "\u0085\u2029\t",
                        "\u0085\u2029\t\u0085\u2029", "\t\u2028\n\t", "\u2028\n\t", "\u2028\n\t\u2028\n", "\t\u2028\r\n\t", "\u2028\r\n\t", "\u2028\r\n\t\u2028\r\n",
                        "\t\u2028\r\t", "\u2028\r\t", "\u2028\r\t\u2028\r", "\t\u2028\f\t", "\u2028\f\t", "\u2028\f\t\u2028\f", "\t\u2028\u0085\t", "\u2028\u0085\t",
                        "\u2028\u0085\t\u2028\u0085", "\t\u2028\u2028\t", "\u2028\u2028\t", "\u2028\u2028\t\u2028\u2028", "\t\u2028\u2029\t", "\u2028\u2029\t",
                        "\u2028\u2029\t\u2028\u2029", "\t\u2029\n\t", "\u2029\n\t", "\u2029\n\t\u2029\n", "\t\u2029\r\n\t", "\u2029\r\n\t", "\u2029\r\n\t\u2029\r\n",
                        "\t\u2029\r\t", "\u2029\r\t", "\u2029\r\t\u2029\r", "\t\u2029\f\t", "\u2029\f\t", "\u2029\f\t\u2029\f", "\t\u2029\u0085\t", "\u2029\u0085\t",
                        "\u2029\u0085\t\u2029\u0085", "\t\u2029\u2028\t", "\u2029\u2028\t", "\u2029\u2028\t\u2029\u2028", "\t\u2029\u2029\t", "\u2029\u2029\t",
                        "\u2029\u2029\t\u2029\u2029", "\u1680", "\u1680 ", "\u1680\t", "\u1680\u1680", "\u1680\n\u1680", "\n\u1680", "\n\u1680\n", "\u1680\r\n\u1680",
                        "\r\n\u1680", "\r\n\u1680\r\n", "\u1680\r\u1680", "\r\u1680", "\r\u1680\r", "\u1680\f\u1680", "\f\u1680", "\f\u1680\f", "\u1680\u0085\u1680",
                        "\u0085\u1680", "\u0085\u1680\u0085", "\u1680\u2028\u1680", "\u2028\u1680", "\u2028\u1680\u2028", "\u1680\u2029\u1680", "\u2029\u1680",
                        "\u2029\u1680\u2029", "\u1680\n\n\u1680", "\n\n\u1680", "\n\n\u1680\n\n", "\u1680\n\r\n\u1680", "\n\r\n\u1680", "\n\r\n\u1680\n\r\n",
                        "\u1680\n\r\u1680", "\n\r\u1680", "\n\r\u1680\n\r", "\u1680\n\f\u1680", "\n\f\u1680", "\n\f\u1680\n\f", "\u1680\n\u0085\u1680", "\n\u0085\u1680",
                        "\n\u0085\u1680\n\u0085", "\u1680\n\u2028\u1680", "\n\u2028\u1680", "\n\u2028\u1680\n\u2028", "\u1680\n\u2029\u1680", "\n\u2029\u1680",
                        "\n\u2029\u1680\n\u2029", "\u1680\r\n\n\u1680", "\r\n\n\u1680", "\r\n\n\u1680\r\n\n", "\u1680\r\n\r\n\u1680", "\r\n\r\n\u1680",
                        "\r\n\r\n\u1680\r\n\r\n", "\u1680\r\n\r\u1680", "\r\n\r\u1680", "\r\n\r\u1680\r\n\r", "\u1680\r\n\f\u1680", "\r\n\f\u1680", "\r\n\f\u1680\r\n\f",
                        "\u1680\r\n\u0085\u1680", "\r\n\u0085\u1680", "\r\n\u0085\u1680\r\n\u0085", "\u1680\r\n\u2028\u1680", "\r\n\u2028\u1680",
                        "\r\n\u2028\u1680\r\n\u2028", "\u1680\r\n\u2029\u1680", "\r\n\u2029\u1680", "\r\n\u2029\u1680\r\n\u2029", "\u1680\r\r\n\u1680", "\r\r\n\u1680",
                        "\r\r\n\u1680\r\r\n", "\u1680\r\r\u1680", "\r\r\u1680", "\r\r\u1680\r\r", "\u1680\r\f\u1680", "\r\f\u1680", "\r\f\u1680\r\f", "\u1680\r\u0085\u1680",
                        "\r\u0085\u1680", "\r\u0085\u1680\r\u0085", "\u1680\r\u2028\u1680", "\r\u2028\u1680", "\r\u2028\u1680\r\u2028", "\u1680\r\u2029\u1680",
                        "\r\u2029\u1680", "\r\u2029\u1680\r\u2029", "\u1680\f\n\u1680", "\f\n\u1680", "\f\n\u1680\f\n", "\u1680\f\r\n\u1680", "\f\r\n\u1680",
                        "\f\r\n\u1680\f\r\n", "\u1680\f\r\u1680", "\f\r\u1680", "\f\r\u1680\f\r", "\u1680\f\f\u1680", "\f\f\u1680", "\f\f\u1680\f\f", "\u1680\f\u0085\u1680",
                        "\f\u0085\u1680", "\f\u0085\u1680\f\u0085", "\u1680\f\u2028\u1680", "\f\u2028\u1680", "\f\u2028\u1680\f\u2028", "\u1680\f\u2029\u1680",
                        "\f\u2029\u1680", "\f\u2029\u1680\f\u2029", "\u1680\u0085\n\u1680", "\u0085\n\u1680", "\u0085\n\u1680\u0085\n", "\u1680\u0085\r\n\u1680",
                        "\u0085\r\n\u1680", "\u0085\r\n\u1680\u0085\r\n", "\u1680\u0085\r\u1680", "\u0085\r\u1680", "\u0085\r\u1680\u0085\r", "\u1680\u0085\f\u1680",
                        "\u0085\f\u1680", "\u0085\f\u1680\u0085\f", "\u1680\u0085\u0085\u1680", "\u0085\u0085\u1680", "\u0085\u0085\u1680\u0085\u0085",
                        "\u1680\u0085\u2028\u1680", "\u0085\u2028\u1680", "\u0085\u2028\u1680\u0085\u2028", "\u1680\u0085\u2029\u1680", "\u0085\u2029\u1680",
                        "\u0085\u2029\u1680\u0085\u2029", "\u1680\u2028\n\u1680", "\u2028\n\u1680", "\u2028\n\u1680\u2028\n", "\u1680\u2028\r\n\u1680", "\u2028\r\n\u1680",
                        "\u2028\r\n\u1680\u2028\r\n", "\u1680\u2028\r\u1680", "\u2028\r\u1680", "\u2028\r\u1680\u2028\r", "\u1680\u2028\f\u1680", "\u2028\f\u1680",
                        "\u2028\f\u1680\u2028\f", "\u1680\u2028\u0085\u1680", "\u2028\u0085\u1680", "\u2028\u0085\u1680\u2028\u0085", "\u1680\u2028\u2028\u1680",
                        "\u2028\u2028\u1680", "\u2028\u2028\u1680\u2028\u2028", "\u1680\u2028\u2029\u1680", "\u2028\u2029\u1680", "\u2028\u2029\u1680\u2028\u2029",
                        "\u1680\u2029\n\u1680", "\u2029\n\u1680", "\u2029\n\u1680\u2029\n", "\u1680\u2029\r\n\u1680", "\u2029\r\n\u1680", "\u2029\r\n\u1680\u2029\r\n",
                        "\u1680\u2029\r\u1680", "\u2029\r\u1680", "\u2029\r\u1680\u2029\r", "\u1680\u2029\f\u1680", "\u2029\f\u1680", "\u2029\f\u1680\u2029\f",
                        "\u1680\u2029\u0085\u1680", "\u2029\u0085\u1680", "\u2029\u0085\u1680\u2029\u0085", "\u1680\u2029\u2028\u1680", "\u2029\u2028\u1680",
                        "\u2029\u2028\u1680\u2029\u2028", "\u1680\u2029\u2029\u1680", "\u2029\u2029\u1680", "\u2029\u2029\u1680\u2029\u2029"),
                new TestData("Test", "Test", "Test ", "Test\t", "Test\u1680", "Test\n\n", "Test\n\r\n", "Test\n\r", "Test\n\f", "Test\n\u0085", "Test\n\u2028",
                        "Test\n\u2029", "Test\r\n\n", "Test\r\n\r\n", "Test\r\n\r", "Test\r\n\f", "Test\r\n\u0085", "Test\r\n\u2028", "Test\r\n\u2029", "Test\r\r\n",
                        "Test\r\r", "Test\r\f", "Test\r\u0085", "Test\r\u2028", "Test\r\u2029", "Test\f\n", "Test\f\r\n", "Test\f\r", "Test\f\f", "Test\f\u0085",
                        "Test\f\u2028", "Test\f\u2029", "Test\u0085\n", "Test\u0085\r\n", "Test\u0085\r", "Test\u0085\f", "Test\u0085\u0085", "Test\u0085\u2028",
                        "Test\u0085\u2029", "Test\u2028\n", "Test\u2028\r\n", "Test\u2028\r", "Test\u2028\f", "Test\u2028\u0085", "Test\u2028\u2028", "Test\u2028\u2029",
                        "Test\u2029\n", "Test\u2029\r\n", "Test\u2029\r", "Test\u2029\f", "Test\u2029\u0085", "Test\u2029\u2028", "Test\u2029\u2029"),
                new TestData("_", "_"),
                new TestData("\u00A0", "\u00A0", "\u00A0 ", "\u00A0\t", "\u00A0\u1680", "\u00A0\n\n", "\u00A0\n\r\n", "\u00A0\n\r", "\u00A0\n\f", "\u00A0\n\u0085",
                        "\u00A0\n\u2028", "\u00A0\n\u2029", "\u00A0\r\n\n", "\u00A0\r\n\r\n", "\u00A0\r\n\r", "\u00A0\r\n\f", "\u00A0\r\n\u0085", "\u00A0\r\n\u2028",
                        "\u00A0\r\n\u2029", "\u00A0\r\r\n", "\u00A0\r\r", "\u00A0\r\f", "\u00A0\r\u0085", "\u00A0\r\u2028", "\u00A0\r\u2029", "\u00A0\f\n", "\u00A0\f\r\n",
                        "\u00A0\f\r", "\u00A0\f\f", "\u00A0\f\u0085", "\u00A0\f\u2028", "\u00A0\f\u2029", "\u00A0\u0085\n", "\u00A0\u0085\r\n", "\u00A0\u0085\r",
                        "\u00A0\u0085\f", "\u00A0\u0085\u0085", "\u00A0\u0085\u2028", "\u00A0\u0085\u2029", "\u00A0\u2028\n", "\u00A0\u2028\r\n", "\u00A0\u2028\r",
                        "\u00A0\u2028\f", "\u00A0\u2028\u0085", "\u00A0\u2028\u2028", "\u00A0\u2028\u2029", "\u00A0\u2029\n", "\u00A0\u2029\r\n", "\u00A0\u2029\r",
                        "\u00A0\u2029\f", "\u00A0\u2029\u0085", "\u00A0\u2029\u2028", "\u00A0\u2029\u2029"),
                new TestData("Test Data", "Test Data"),
                new TestData("Test  Data", "Test  Data"),
                new TestData(" Test", " Test", " Test "),
                new TestData("  Test", "  Test  "),
                new TestData(" Test \n Data", " Test \n Data"),
                new TestData("Test \n Data", "Test \n Data "),
                new TestData(" \n Test", " \n Test", " \n Test \n "),
                new TestData("\n Test", "\n Test ", "\n Test \n"),
                new TestData(" Test \r\n Data", " Test \r\n Data"),
                new TestData("Test \r\n Data", "Test \r\n Data "),
                new TestData(" \r\n Test", " \r\n Test", " \r\n Test \r\n "),
                new TestData("\r\n Test", "\r\n Test ", "\r\n Test \r\n"),
                new TestData(" Test \r Data", " Test \r Data"),
                new TestData("Test \r Data", "Test \r Data "),
                new TestData(" \r Test", " \r Test", " \r Test \r "),
                new TestData("\r Test", "\r Test ", "\r Test \r"),
                new TestData(" Test \f Data", " Test \f Data"),
                new TestData("Test \f Data", "Test \f Data "),
                new TestData(" \f Test", " \f Test", " \f Test \f "),
                new TestData("\f Test", "\f Test ", "\f Test \f"),
                new TestData(" Test \u0085 Data", " Test \u0085 Data"),
                new TestData("Test \u0085 Data", "Test \u0085 Data "),
                new TestData(" \u0085 Test", " \u0085 Test", " \u0085 Test \u0085 "),
                new TestData("\u0085 Test", "\u0085 Test ", "\u0085 Test \u0085"),
                new TestData(" Test \u2028 Data", " Test \u2028 Data"),
                new TestData("Test \u2028 Data", "Test \u2028 Data "),
                new TestData(" \u2028 Test", " \u2028 Test", " \u2028 Test \u2028 "),
                new TestData("\u2028 Test", "\u2028 Test ", "\u2028 Test \u2028"),
                new TestData(" Test \u2029 Data", " Test \u2029 Data"),
                new TestData("Test \u2029 Data", "Test \u2029 Data "),
                new TestData(" \u2029 Test", " \u2029 Test", " \u2029 Test \u2029 "),
                new TestData("\u2029 Test", "\u2029 Test ", "\u2029 Test \u2029"),
                new TestData("Test\tData", "Test\tData"),
                new TestData("Test\t\tData", "Test\t\tData"),
                new TestData("\tTest", "\tTest", "\tTest\t"),
                new TestData("\t\tTest", "\t\tTest\t\t"),
                new TestData("\tTest\t\n\tData", "\tTest\t\n\tData"),
                new TestData("Test\t\n\tData", "Test\t\n\tData\t"),
                new TestData("\t\n\tTest", "\t\n\tTest", "\t\n\tTest\t\n\t"),
                new TestData("\n\tTest", "\n\tTest\t", "\n\tTest\t\n"),
                new TestData("\tTest\t\r\n\tData", "\tTest\t\r\n\tData"),
                new TestData("Test\t\r\n\tData", "Test\t\r\n\tData\t"),
                new TestData("\t\r\n\tTest", "\t\r\n\tTest", "\t\r\n\tTest\t\r\n\t"),
                new TestData("\r\n\tTest", "\r\n\tTest\t", "\r\n\tTest\t\r\n"),
                new TestData("\tTest\t\r\tData", "\tTest\t\r\tData"),
                new TestData("Test\t\r\tData", "Test\t\r\tData\t"),
                new TestData("\t\r\tTest", "\t\r\tTest", "\t\r\tTest\t\r\t"),
                new TestData("\r\tTest", "\r\tTest\t", "\r\tTest\t\r"),
                new TestData("\tTest\t\f\tData", "\tTest\t\f\tData"),
                new TestData("Test\t\f\tData", "Test\t\f\tData\t"),
                new TestData("\t\f\tTest", "\t\f\tTest", "\t\f\tTest\t\f\t"),
                new TestData("\f\tTest", "\f\tTest\t", "\f\tTest\t\f"),
                new TestData("\tTest\t\u0085\tData", "\tTest\t\u0085\tData"),
                new TestData("Test\t\u0085\tData", "Test\t\u0085\tData\t"),
                new TestData("\t\u0085\tTest", "\t\u0085\tTest", "\t\u0085\tTest\t\u0085\t"),
                new TestData("\u0085\tTest", "\u0085\tTest\t", "\u0085\tTest\t\u0085"),
                new TestData("\tTest\t\u2028\tData", "\tTest\t\u2028\tData"),
                new TestData("Test\t\u2028\tData", "Test\t\u2028\tData\t"),
                new TestData("\t\u2028\tTest", "\t\u2028\tTest", "\t\u2028\tTest\t\u2028\t"),
                new TestData("\u2028\tTest", "\u2028\tTest\t", "\u2028\tTest\t\u2028"),
                new TestData("\tTest\t\u2029\tData", "\tTest\t\u2029\tData"),
                new TestData("Test\t\u2029\tData", "Test\t\u2029\tData\t"),
                new TestData("\t\u2029\tTest", "\t\u2029\tTest", "\t\u2029\tTest\t\u2029\t"),
                new TestData("\u2029\tTest", "\u2029\tTest\t", "\u2029\tTest\t\u2029"),
                new TestData("Test\u1680Data", "Test\u1680Data"),
                new TestData("Test\u1680\u1680Data", "Test\u1680\u1680Data"),
                new TestData("\u1680Test", "\u1680Test", "\u1680Test\u1680"),
                new TestData("\u1680\u1680Test", "\u1680\u1680Test\u1680\u1680"),
                new TestData("\u1680Test\u1680\n\u1680Data", "\u1680Test\u1680\n\u1680Data"),
                new TestData("Test\u1680\n\u1680Data", "Test\u1680\n\u1680Data\u1680"),
                new TestData("\u1680\n\u1680Test", "\u1680\n\u1680Test", "\u1680\n\u1680Test\u1680\n\u1680"),
                new TestData("\n\u1680Test", "\n\u1680Test\u1680", "\n\u1680Test\u1680\n"),
                new TestData("\u1680Test\u1680\r\n\u1680Data", "\u1680Test\u1680\r\n\u1680Data"),
                new TestData("Test\u1680\r\n\u1680Data", "Test\u1680\r\n\u1680Data\u1680"),
                new TestData("\u1680\r\n\u1680Test", "\u1680\r\n\u1680Test", "\u1680\r\n\u1680Test\u1680\r\n\u1680"),
                new TestData("\r\n\u1680Test", "\r\n\u1680Test\u1680", "\r\n\u1680Test\u1680\r\n"),
                new TestData("\u1680Test\u1680\r\u1680Data", "\u1680Test\u1680\r\u1680Data"),
                new TestData("Test\u1680\r\u1680Data", "Test\u1680\r\u1680Data\u1680"),
                new TestData("\u1680\r\u1680Test", "\u1680\r\u1680Test", "\u1680\r\u1680Test\u1680\r\u1680"),
                new TestData("\r\u1680Test", "\r\u1680Test\u1680", "\r\u1680Test\u1680\r"),
                new TestData("\u1680Test\u1680\f\u1680Data", "\u1680Test\u1680\f\u1680Data"),
                new TestData("Test\u1680\f\u1680Data", "Test\u1680\f\u1680Data\u1680"),
                new TestData("\u1680\f\u1680Test", "\u1680\f\u1680Test", "\u1680\f\u1680Test\u1680\f\u1680"),
                new TestData("\f\u1680Test", "\f\u1680Test\u1680", "\f\u1680Test\u1680\f"),
                new TestData("\u1680Test\u1680\u0085\u1680Data", "\u1680Test\u1680\u0085\u1680Data"),
                new TestData("Test\u1680\u0085\u1680Data", "Test\u1680\u0085\u1680Data\u1680"),
                new TestData("\u1680\u0085\u1680Test", "\u1680\u0085\u1680Test", "\u1680\u0085\u1680Test\u1680\u0085\u1680"),
                new TestData("\u0085\u1680Test", "\u0085\u1680Test\u1680", "\u0085\u1680Test\u1680\u0085"),
                new TestData("\u1680Test\u1680\u2028\u1680Data", "\u1680Test\u1680\u2028\u1680Data"),
                new TestData("Test\u1680\u2028\u1680Data", "Test\u1680\u2028\u1680Data\u1680"),
                new TestData("\u1680\u2028\u1680Test", "\u1680\u2028\u1680Test", "\u1680\u2028\u1680Test\u1680\u2028\u1680"),
                new TestData("\u2028\u1680Test", "\u2028\u1680Test\u1680", "\u2028\u1680Test\u1680\u2028"),
                new TestData("\u1680Test\u1680\u2029\u1680Data", "\u1680Test\u1680\u2029\u1680Data"),
                new TestData("Test\u1680\u2029\u1680Data", "Test\u1680\u2029\u1680Data\u1680"),
                new TestData("\u1680\u2029\u1680Test", "\u1680\u2029\u1680Test", "\u1680\u2029\u1680Test\u1680\u2029\u1680"),
                new TestData("\u2029\u1680Test", "\u2029\u1680Test\u1680", "\u2029\u1680Test\u1680\u2029"),
                new TestData("Test\nData", "Test\nData"),
                new TestData("\nTest", "\nTest", "\nTest\n"),
                new TestData("Test\r\nData", "Test\r\nData"),
                new TestData("\r\nTest", "\r\nTest", "\r\nTest\r\n"),
                new TestData("Test\rData", "Test\rData"),
                new TestData("\rTest", "\rTest", "\rTest\r"),
                new TestData("Test\fData", "Test\fData"),
                new TestData("\fTest", "\fTest", "\fTest\f"),
                new TestData("Test\u0085Data", "Test\u0085Data"),
                new TestData("\u0085Test", "\u0085Test", "\u0085Test\u0085"),
                new TestData("Test\u2028Data", "Test\u2028Data"),
                new TestData("\u2028Test", "\u2028Test", "\u2028Test\u2028"),
                new TestData("Test\u2029Data", "Test\u2029Data"),
                new TestData("\u2029Test", "\u2029Test", "\u2029Test\u2029"),
                new TestData("Test\n\nData", "Test\n\nData"),
                new TestData("\n\nTest", "\n\nTest", "\n\nTest\n\n"),
                new TestData("Test\n\r\nData", "Test\n\r\nData"),
                new TestData("\n\r\nTest", "\n\r\nTest", "\n\r\nTest\n\r\n"),
                new TestData("Test\n\rData", "Test\n\rData"),
                new TestData("\n\rTest", "\n\rTest", "\n\rTest\n\r"),
                new TestData("Test\n\fData", "Test\n\fData"),
                new TestData("\n\fTest", "\n\fTest", "\n\fTest\n\f"),
                new TestData("Test\n\u0085Data", "Test\n\u0085Data"),
                new TestData("\n\u0085Test", "\n\u0085Test", "\n\u0085Test\n\u0085"),
                new TestData("Test\n\u2028Data", "Test\n\u2028Data"),
                new TestData("\n\u2028Test", "\n\u2028Test", "\n\u2028Test\n\u2028"),
                new TestData("Test\n\u2029Data", "Test\n\u2029Data"),
                new TestData("\n\u2029Test", "\n\u2029Test", "\n\u2029Test\n\u2029"),
                new TestData("Test\r\n\nData", "Test\r\n\nData"),
                new TestData("\r\n\nTest", "\r\n\nTest", "\r\n\nTest\r\n\n"),
                new TestData("Test\r\n\r\nData", "Test\r\n\r\nData"),
                new TestData("\r\n\r\nTest", "\r\n\r\nTest", "\r\n\r\nTest\r\n\r\n"),
                new TestData("Test\r\n\rData", "Test\r\n\rData"),
                new TestData("\r\n\rTest", "\r\n\rTest", "\r\n\rTest\r\n\r"),
                new TestData("Test\r\n\fData", "Test\r\n\fData"),
                new TestData("\r\n\fTest", "\r\n\fTest", "\r\n\fTest\r\n\f"),
                new TestData("Test\r\n\u0085Data", "Test\r\n\u0085Data"),
                new TestData("\r\n\u0085Test", "\r\n\u0085Test", "\r\n\u0085Test\r\n\u0085"),
                new TestData("Test\r\n\u2028Data", "Test\r\n\u2028Data"),
                new TestData("\r\n\u2028Test", "\r\n\u2028Test", "\r\n\u2028Test\r\n\u2028"),
                new TestData("Test\r\n\u2029Data", "Test\r\n\u2029Data"),
                new TestData("\r\n\u2029Test", "\r\n\u2029Test", "\r\n\u2029Test\r\n\u2029"),
                new TestData("Test\r\r\nData", "Test\r\r\nData"),
                new TestData("\r\r\nTest", "\r\r\nTest", "\r\r\nTest\r\r\n"),
                new TestData("Test\r\rData", "Test\r\rData"),
                new TestData("\r\rTest", "\r\rTest", "\r\rTest\r\r"),
                new TestData("Test\r\fData", "Test\r\fData"),
                new TestData("\r\fTest", "\r\fTest", "\r\fTest\r\f"),
                new TestData("Test\r\u0085Data", "Test\r\u0085Data"),
                new TestData("\r\u0085Test", "\r\u0085Test", "\r\u0085Test\r\u0085"),
                new TestData("Test\r\u2028Data", "Test\r\u2028Data"),
                new TestData("\r\u2028Test", "\r\u2028Test", "\r\u2028Test\r\u2028"),
                new TestData("Test\r\u2029Data", "Test\r\u2029Data"),
                new TestData("\r\u2029Test", "\r\u2029Test", "\r\u2029Test\r\u2029"),
                new TestData("Test\f\nData", "Test\f\nData"),
                new TestData("\f\nTest", "\f\nTest", "\f\nTest\f\n"),
                new TestData("Test\f\r\nData", "Test\f\r\nData"),
                new TestData("\f\r\nTest", "\f\r\nTest", "\f\r\nTest\f\r\n"),
                new TestData("Test\f\rData", "Test\f\rData"),
                new TestData("\f\rTest", "\f\rTest", "\f\rTest\f\r"),
                new TestData("Test\f\fData", "Test\f\fData"),
                new TestData("\f\fTest", "\f\fTest", "\f\fTest\f\f"),
                new TestData("Test\f\u0085Data", "Test\f\u0085Data"),
                new TestData("\f\u0085Test", "\f\u0085Test", "\f\u0085Test\f\u0085"),
                new TestData("Test\f\u2028Data", "Test\f\u2028Data"),
                new TestData("\f\u2028Test", "\f\u2028Test", "\f\u2028Test\f\u2028"),
                new TestData("Test\f\u2029Data", "Test\f\u2029Data"),
                new TestData("\f\u2029Test", "\f\u2029Test", "\f\u2029Test\f\u2029"),
                new TestData("Test\u0085\nData", "Test\u0085\nData"),
                new TestData("\u0085\nTest", "\u0085\nTest", "\u0085\nTest\u0085\n"),
                new TestData("Test\u0085\r\nData", "Test\u0085\r\nData"),
                new TestData("\u0085\r\nTest", "\u0085\r\nTest", "\u0085\r\nTest\u0085\r\n"),
                new TestData("Test\u0085\rData", "Test\u0085\rData"),
                new TestData("\u0085\rTest", "\u0085\rTest", "\u0085\rTest\u0085\r"),
                new TestData("Test\u0085\fData", "Test\u0085\fData"),
                new TestData("\u0085\fTest", "\u0085\fTest", "\u0085\fTest\u0085\f"),
                new TestData("Test\u0085\u0085Data", "Test\u0085\u0085Data"),
                new TestData("\u0085\u0085Test", "\u0085\u0085Test", "\u0085\u0085Test\u0085\u0085"),
                new TestData("Test\u0085\u2028Data", "Test\u0085\u2028Data"),
                new TestData("\u0085\u2028Test", "\u0085\u2028Test", "\u0085\u2028Test\u0085\u2028"),
                new TestData("Test\u0085\u2029Data", "Test\u0085\u2029Data"),
                new TestData("\u0085\u2029Test", "\u0085\u2029Test", "\u0085\u2029Test\u0085\u2029"),
                new TestData("Test\u2028\nData", "Test\u2028\nData"),
                new TestData("\u2028\nTest", "\u2028\nTest", "\u2028\nTest\u2028\n"),
                new TestData("Test\u2028\r\nData", "Test\u2028\r\nData"),
                new TestData("\u2028\r\nTest", "\u2028\r\nTest", "\u2028\r\nTest\u2028\r\n"),
                new TestData("Test\u2028\rData", "Test\u2028\rData"),
                new TestData("\u2028\rTest", "\u2028\rTest", "\u2028\rTest\u2028\r"),
                new TestData("Test\u2028\fData", "Test\u2028\fData"),
                new TestData("\u2028\fTest", "\u2028\fTest", "\u2028\fTest\u2028\f"),
                new TestData("Test\u2028\u0085Data", "Test\u2028\u0085Data"),
                new TestData("\u2028\u0085Test", "\u2028\u0085Test", "\u2028\u0085Test\u2028\u0085"),
                new TestData("Test\u2028\u2028Data", "Test\u2028\u2028Data"),
                new TestData("\u2028\u2028Test", "\u2028\u2028Test", "\u2028\u2028Test\u2028\u2028"),
                new TestData("Test\u2028\u2029Data", "Test\u2028\u2029Data"),
                new TestData("\u2028\u2029Test", "\u2028\u2029Test", "\u2028\u2029Test\u2028\u2029"),
                new TestData("Test\u2029\nData", "Test\u2029\nData"),
                new TestData("\u2029\nTest", "\u2029\nTest", "\u2029\nTest\u2029\n"),
                new TestData("Test\u2029\r\nData", "Test\u2029\r\nData"),
                new TestData("\u2029\r\nTest", "\u2029\r\nTest", "\u2029\r\nTest\u2029\r\n"),
                new TestData("Test\u2029\rData", "Test\u2029\rData"),
                new TestData("\u2029\rTest", "\u2029\rTest", "\u2029\rTest\u2029\r"),
                new TestData("Test\u2029\fData", "Test\u2029\fData"),
                new TestData("\u2029\fTest", "\u2029\fTest", "\u2029\fTest\u2029\f"),
                new TestData("Test\u2029\u0085Data", "Test\u2029\u0085Data"),
                new TestData("\u2029\u0085Test", "\u2029\u0085Test", "\u2029\u0085Test\u2029\u0085"),
                new TestData("Test\u2029\u2028Data", "Test\u2029\u2028Data"),
                new TestData("\u2029\u2028Test", "\u2029\u2028Test", "\u2029\u2028Test\u2029\u2028"),
                new TestData("Test\u2029\u2029Data", "Test\u2029\u2029Data"),
                new TestData("\u2029\u2029Test", "\u2029\u2029Test", "\u2029\u2029Test\u2029\u2029"),
                new TestData("\u00A0 _", "\u00A0 _"),
                new TestData("\u00A0  _", "\u00A0  _"),
                new TestData(" \u00A0", " \u00A0", " \u00A0 "),
                new TestData("  \u00A0", "  \u00A0  "),
                new TestData(" \u00A0 \n _", " \u00A0 \n _"),
                new TestData("\u00A0 \n _", "\u00A0 \n _ "),
                new TestData(" \n \u00A0", " \n \u00A0", " \n \u00A0 \n "),
                new TestData("\n \u00A0", "\n \u00A0 ", "\n \u00A0 \n"),
                new TestData(" \u00A0 \r\n _", " \u00A0 \r\n _"),
                new TestData("\u00A0 \r\n _", "\u00A0 \r\n _ "),
                new TestData(" \r\n \u00A0", " \r\n \u00A0", " \r\n \u00A0 \r\n "),
                new TestData("\r\n \u00A0", "\r\n \u00A0 ", "\r\n \u00A0 \r\n"),
                new TestData(" \u00A0 \r _", " \u00A0 \r _"),
                new TestData("\u00A0 \r _", "\u00A0 \r _ "),
                new TestData(" \r \u00A0", " \r \u00A0", " \r \u00A0 \r "),
                new TestData("\r \u00A0", "\r \u00A0 ", "\r \u00A0 \r"),
                new TestData(" \u00A0 \f _", " \u00A0 \f _"),
                new TestData("\u00A0 \f _", "\u00A0 \f _ "),
                new TestData(" \f \u00A0", " \f \u00A0", " \f \u00A0 \f "),
                new TestData("\f \u00A0", "\f \u00A0 ", "\f \u00A0 \f"),
                new TestData(" \u00A0 \u0085 _", " \u00A0 \u0085 _"),
                new TestData("\u00A0 \u0085 _", "\u00A0 \u0085 _ "),
                new TestData(" \u0085 \u00A0", " \u0085 \u00A0", " \u0085 \u00A0 \u0085 "),
                new TestData("\u0085 \u00A0", "\u0085 \u00A0 ", "\u0085 \u00A0 \u0085"),
                new TestData(" \u00A0 \u2028 _", " \u00A0 \u2028 _"),
                new TestData("\u00A0 \u2028 _", "\u00A0 \u2028 _ "),
                new TestData(" \u2028 \u00A0", " \u2028 \u00A0", " \u2028 \u00A0 \u2028 "),
                new TestData("\u2028 \u00A0", "\u2028 \u00A0 ", "\u2028 \u00A0 \u2028"),
                new TestData(" \u00A0 \u2029 _", " \u00A0 \u2029 _"),
                new TestData("\u00A0 \u2029 _", "\u00A0 \u2029 _ "),
                new TestData(" \u2029 \u00A0", " \u2029 \u00A0", " \u2029 \u00A0 \u2029 "),
                new TestData("\u2029 \u00A0", "\u2029 \u00A0 ", "\u2029 \u00A0 \u2029"),
                new TestData("\u00A0\t_", "\u00A0\t_"),
                new TestData("\u00A0\t\t_", "\u00A0\t\t_"),
                new TestData("\t\u00A0", "\t\u00A0", "\t\u00A0\t"),
                new TestData("\t\t\u00A0", "\t\t\u00A0\t\t"),
                new TestData("\t\u00A0\t\n\t_", "\t\u00A0\t\n\t_"),
                new TestData("\u00A0\t\n\t_", "\u00A0\t\n\t_\t"),
                new TestData("\t\n\t\u00A0", "\t\n\t\u00A0", "\t\n\t\u00A0\t\n\t"),
                new TestData("\n\t\u00A0", "\n\t\u00A0\t", "\n\t\u00A0\t\n"),
                new TestData("\t\u00A0\t\r\n\t_", "\t\u00A0\t\r\n\t_"),
                new TestData("\u00A0\t\r\n\t_", "\u00A0\t\r\n\t_\t"),
                new TestData("\t\r\n\t\u00A0", "\t\r\n\t\u00A0", "\t\r\n\t\u00A0\t\r\n\t"),
                new TestData("\r\n\t\u00A0", "\r\n\t\u00A0\t", "\r\n\t\u00A0\t\r\n"),
                new TestData("\t\u00A0\t\r\t_", "\t\u00A0\t\r\t_"),
                new TestData("\u00A0\t\r\t_", "\u00A0\t\r\t_\t"),
                new TestData("\t\r\t\u00A0", "\t\r\t\u00A0", "\t\r\t\u00A0\t\r\t"),
                new TestData("\r\t\u00A0", "\r\t\u00A0\t", "\r\t\u00A0\t\r"),
                new TestData("\t\u00A0\t\f\t_", "\t\u00A0\t\f\t_"),
                new TestData("\u00A0\t\f\t_", "\u00A0\t\f\t_\t"),
                new TestData("\t\f\t\u00A0", "\t\f\t\u00A0", "\t\f\t\u00A0\t\f\t"),
                new TestData("\f\t\u00A0", "\f\t\u00A0\t", "\f\t\u00A0\t\f"),
                new TestData("\t\u00A0\t\u0085\t_", "\t\u00A0\t\u0085\t_"),
                new TestData("\u00A0\t\u0085\t_", "\u00A0\t\u0085\t_\t"),
                new TestData("\t\u0085\t\u00A0", "\t\u0085\t\u00A0", "\t\u0085\t\u00A0\t\u0085\t"),
                new TestData("\u0085\t\u00A0", "\u0085\t\u00A0\t", "\u0085\t\u00A0\t\u0085"),
                new TestData("\t\u00A0\t\u2028\t_", "\t\u00A0\t\u2028\t_"),
                new TestData("\u00A0\t\u2028\t_", "\u00A0\t\u2028\t_\t"),
                new TestData("\t\u2028\t\u00A0", "\t\u2028\t\u00A0", "\t\u2028\t\u00A0\t\u2028\t"),
                new TestData("\u2028\t\u00A0", "\u2028\t\u00A0\t", "\u2028\t\u00A0\t\u2028"),
                new TestData("\t\u00A0\t\u2029\t_", "\t\u00A0\t\u2029\t_"),
                new TestData("\u00A0\t\u2029\t_", "\u00A0\t\u2029\t_\t"),
                new TestData("\t\u2029\t\u00A0", "\t\u2029\t\u00A0", "\t\u2029\t\u00A0\t\u2029\t"),
                new TestData("\u2029\t\u00A0", "\u2029\t\u00A0\t", "\u2029\t\u00A0\t\u2029"),
                new TestData("\u00A0\u1680_", "\u00A0\u1680_"),
                new TestData("\u00A0\u1680\u1680_", "\u00A0\u1680\u1680_"),
                new TestData("\u1680\u00A0", "\u1680\u00A0", "\u1680\u00A0\u1680"),
                new TestData("\u1680\u1680\u00A0", "\u1680\u1680\u00A0\u1680\u1680"),
                new TestData("\u1680\u00A0\u1680\n\u1680_", "\u1680\u00A0\u1680\n\u1680_"),
                new TestData("\u00A0\u1680\n\u1680_", "\u00A0\u1680\n\u1680_\u1680"),
                new TestData("\u1680\n\u1680\u00A0", "\u1680\n\u1680\u00A0", "\u1680\n\u1680\u00A0\u1680\n\u1680"),
                new TestData("\n\u1680\u00A0", "\n\u1680\u00A0\u1680", "\n\u1680\u00A0\u1680\n"),
                new TestData("\u1680\u00A0\u1680\r\n\u1680_", "\u1680\u00A0\u1680\r\n\u1680_"),
                new TestData("\u00A0\u1680\r\n\u1680_", "\u00A0\u1680\r\n\u1680_\u1680"),
                new TestData("\u1680\r\n\u1680\u00A0", "\u1680\r\n\u1680\u00A0", "\u1680\r\n\u1680\u00A0\u1680\r\n\u1680"),
                new TestData("\r\n\u1680\u00A0", "\r\n\u1680\u00A0\u1680", "\r\n\u1680\u00A0\u1680\r\n"),
                new TestData("\u1680\u00A0\u1680\r\u1680_", "\u1680\u00A0\u1680\r\u1680_"),
                new TestData("\u00A0\u1680\r\u1680_", "\u00A0\u1680\r\u1680_\u1680"),
                new TestData("\u1680\r\u1680\u00A0", "\u1680\r\u1680\u00A0", "\u1680\r\u1680\u00A0\u1680\r\u1680"),
                new TestData("\r\u1680\u00A0", "\r\u1680\u00A0\u1680", "\r\u1680\u00A0\u1680\r"),
                new TestData("\u1680\u00A0\u1680\f\u1680_", "\u1680\u00A0\u1680\f\u1680_"),
                new TestData("\u00A0\u1680\f\u1680_", "\u00A0\u1680\f\u1680_\u1680"),
                new TestData("\u1680\f\u1680\u00A0", "\u1680\f\u1680\u00A0", "\u1680\f\u1680\u00A0\u1680\f\u1680"),
                new TestData("\f\u1680\u00A0", "\f\u1680\u00A0\u1680", "\f\u1680\u00A0\u1680\f"),
                new TestData("\u1680\u00A0\u1680\u0085\u1680_", "\u1680\u00A0\u1680\u0085\u1680_"),
                new TestData("\u00A0\u1680\u0085\u1680_", "\u00A0\u1680\u0085\u1680_\u1680"),
                new TestData("\u1680\u0085\u1680\u00A0", "\u1680\u0085\u1680\u00A0", "\u1680\u0085\u1680\u00A0\u1680\u0085\u1680"),
                new TestData("\u0085\u1680\u00A0", "\u0085\u1680\u00A0\u1680", "\u0085\u1680\u00A0\u1680\u0085"),
                new TestData("\u1680\u00A0\u1680\u2028\u1680_", "\u1680\u00A0\u1680\u2028\u1680_"),
                new TestData("\u00A0\u1680\u2028\u1680_", "\u00A0\u1680\u2028\u1680_\u1680"),
                new TestData("\u1680\u2028\u1680\u00A0", "\u1680\u2028\u1680\u00A0", "\u1680\u2028\u1680\u00A0\u1680\u2028\u1680"),
                new TestData("\u2028\u1680\u00A0", "\u2028\u1680\u00A0\u1680", "\u2028\u1680\u00A0\u1680\u2028"),
                new TestData("\u1680\u00A0\u1680\u2029\u1680_", "\u1680\u00A0\u1680\u2029\u1680_"),
                new TestData("\u00A0\u1680\u2029\u1680_", "\u00A0\u1680\u2029\u1680_\u1680"),
                new TestData("\u1680\u2029\u1680\u00A0", "\u1680\u2029\u1680\u00A0", "\u1680\u2029\u1680\u00A0\u1680\u2029\u1680"),
                new TestData("\u2029\u1680\u00A0", "\u2029\u1680\u00A0\u1680", "\u2029\u1680\u00A0\u1680\u2029"),
                new TestData("\u00A0\n_", "\u00A0\n_"),
                new TestData("\n\u00A0", "\n\u00A0", "\n\u00A0\n"),
                new TestData("\u00A0\r\n_", "\u00A0\r\n_"),
                new TestData("\r\n\u00A0", "\r\n\u00A0", "\r\n\u00A0\r\n"),
                new TestData("\u00A0\r_", "\u00A0\r_"),
                new TestData("\r\u00A0", "\r\u00A0", "\r\u00A0\r"),
                new TestData("\u00A0\f_", "\u00A0\f_"),
                new TestData("\f\u00A0", "\f\u00A0", "\f\u00A0\f"),
                new TestData("\u00A0\u0085_", "\u00A0\u0085_"),
                new TestData("\u0085\u00A0", "\u0085\u00A0", "\u0085\u00A0\u0085"),
                new TestData("\u00A0\u2028_", "\u00A0\u2028_"),
                new TestData("\u2028\u00A0", "\u2028\u00A0", "\u2028\u00A0\u2028"),
                new TestData("\u00A0\u2029_", "\u00A0\u2029_"),
                new TestData("\u2029\u00A0", "\u2029\u00A0", "\u2029\u00A0\u2029"),
                new TestData("\u00A0\n\n_", "\u00A0\n\n_"),
                new TestData("\n\n\u00A0", "\n\n\u00A0", "\n\n\u00A0\n\n"),
                new TestData("\u00A0\n\r\n_", "\u00A0\n\r\n_"),
                new TestData("\n\r\n\u00A0", "\n\r\n\u00A0", "\n\r\n\u00A0\n\r\n"),
                new TestData("\u00A0\n\r_", "\u00A0\n\r_"),
                new TestData("\n\r\u00A0", "\n\r\u00A0", "\n\r\u00A0\n\r"),
                new TestData("\u00A0\n\f_", "\u00A0\n\f_"),
                new TestData("\n\f\u00A0", "\n\f\u00A0", "\n\f\u00A0\n\f"),
                new TestData("\u00A0\n\u0085_", "\u00A0\n\u0085_"),
                new TestData("\n\u0085\u00A0", "\n\u0085\u00A0", "\n\u0085\u00A0\n\u0085"),
                new TestData("\u00A0\n\u2028_", "\u00A0\n\u2028_"),
                new TestData("\n\u2028\u00A0", "\n\u2028\u00A0", "\n\u2028\u00A0\n\u2028"),
                new TestData("\u00A0\n\u2029_", "\u00A0\n\u2029_"),
                new TestData("\n\u2029\u00A0", "\n\u2029\u00A0", "\n\u2029\u00A0\n\u2029"),
                new TestData("\u00A0\r\n\n_", "\u00A0\r\n\n_"),
                new TestData("\r\n\n\u00A0", "\r\n\n\u00A0", "\r\n\n\u00A0\r\n\n"),
                new TestData("\u00A0\r\n\r\n_", "\u00A0\r\n\r\n_"),
                new TestData("\r\n\r\n\u00A0", "\r\n\r\n\u00A0", "\r\n\r\n\u00A0\r\n\r\n"),
                new TestData("\u00A0\r\n\r_", "\u00A0\r\n\r_"),
                new TestData("\r\n\r\u00A0", "\r\n\r\u00A0", "\r\n\r\u00A0\r\n\r"),
                new TestData("\u00A0\r\n\f_", "\u00A0\r\n\f_"),
                new TestData("\r\n\f\u00A0", "\r\n\f\u00A0", "\r\n\f\u00A0\r\n\f"),
                new TestData("\u00A0\r\n\u0085_", "\u00A0\r\n\u0085_"),
                new TestData("\r\n\u0085\u00A0", "\r\n\u0085\u00A0", "\r\n\u0085\u00A0\r\n\u0085"),
                new TestData("\u00A0\r\n\u2028_", "\u00A0\r\n\u2028_"),
                new TestData("\r\n\u2028\u00A0", "\r\n\u2028\u00A0", "\r\n\u2028\u00A0\r\n\u2028"),
                new TestData("\u00A0\r\n\u2029_", "\u00A0\r\n\u2029_"),
                new TestData("\r\n\u2029\u00A0", "\r\n\u2029\u00A0", "\r\n\u2029\u00A0\r\n\u2029"),
                new TestData("\u00A0\r\r\n_", "\u00A0\r\r\n_"),
                new TestData("\r\r\n\u00A0", "\r\r\n\u00A0", "\r\r\n\u00A0\r\r\n"),
                new TestData("\u00A0\r\r_", "\u00A0\r\r_"),
                new TestData("\r\r\u00A0", "\r\r\u00A0", "\r\r\u00A0\r\r"),
                new TestData("\u00A0\r\f_", "\u00A0\r\f_"),
                new TestData("\r\f\u00A0", "\r\f\u00A0", "\r\f\u00A0\r\f"),
                new TestData("\u00A0\r\u0085_", "\u00A0\r\u0085_"),
                new TestData("\r\u0085\u00A0", "\r\u0085\u00A0", "\r\u0085\u00A0\r\u0085"),
                new TestData("\u00A0\r\u2028_", "\u00A0\r\u2028_"),
                new TestData("\r\u2028\u00A0", "\r\u2028\u00A0", "\r\u2028\u00A0\r\u2028"),
                new TestData("\u00A0\r\u2029_", "\u00A0\r\u2029_"),
                new TestData("\r\u2029\u00A0", "\r\u2029\u00A0", "\r\u2029\u00A0\r\u2029"),
                new TestData("\u00A0\f\n_", "\u00A0\f\n_"),
                new TestData("\f\n\u00A0", "\f\n\u00A0", "\f\n\u00A0\f\n"),
                new TestData("\u00A0\f\r\n_", "\u00A0\f\r\n_"),
                new TestData("\f\r\n\u00A0", "\f\r\n\u00A0", "\f\r\n\u00A0\f\r\n"),
                new TestData("\u00A0\f\r_", "\u00A0\f\r_"),
                new TestData("\f\r\u00A0", "\f\r\u00A0", "\f\r\u00A0\f\r"),
                new TestData("\u00A0\f\f_", "\u00A0\f\f_"),
                new TestData("\f\f\u00A0", "\f\f\u00A0", "\f\f\u00A0\f\f"),
                new TestData("\u00A0\f\u0085_", "\u00A0\f\u0085_"),
                new TestData("\f\u0085\u00A0", "\f\u0085\u00A0", "\f\u0085\u00A0\f\u0085"),
                new TestData("\u00A0\f\u2028_", "\u00A0\f\u2028_"),
                new TestData("\f\u2028\u00A0", "\f\u2028\u00A0", "\f\u2028\u00A0\f\u2028"),
                new TestData("\u00A0\f\u2029_", "\u00A0\f\u2029_"),
                new TestData("\f\u2029\u00A0", "\f\u2029\u00A0", "\f\u2029\u00A0\f\u2029"),
                new TestData("\u00A0\u0085\n_", "\u00A0\u0085\n_"),
                new TestData("\u0085\n\u00A0", "\u0085\n\u00A0", "\u0085\n\u00A0\u0085\n"),
                new TestData("\u00A0\u0085\r\n_", "\u00A0\u0085\r\n_"),
                new TestData("\u0085\r\n\u00A0", "\u0085\r\n\u00A0", "\u0085\r\n\u00A0\u0085\r\n"),
                new TestData("\u00A0\u0085\r_", "\u00A0\u0085\r_"),
                new TestData("\u0085\r\u00A0", "\u0085\r\u00A0", "\u0085\r\u00A0\u0085\r"),
                new TestData("\u00A0\u0085\f_", "\u00A0\u0085\f_"),
                new TestData("\u0085\f\u00A0", "\u0085\f\u00A0", "\u0085\f\u00A0\u0085\f"),
                new TestData("\u00A0\u0085\u0085_", "\u00A0\u0085\u0085_"),
                new TestData("\u0085\u0085\u00A0", "\u0085\u0085\u00A0", "\u0085\u0085\u00A0\u0085\u0085"),
                new TestData("\u00A0\u0085\u2028_", "\u00A0\u0085\u2028_"),
                new TestData("\u0085\u2028\u00A0", "\u0085\u2028\u00A0", "\u0085\u2028\u00A0\u0085\u2028"),
                new TestData("\u00A0\u0085\u2029_", "\u00A0\u0085\u2029_"),
                new TestData("\u0085\u2029\u00A0", "\u0085\u2029\u00A0", "\u0085\u2029\u00A0\u0085\u2029"),
                new TestData("\u00A0\u2028\n_", "\u00A0\u2028\n_"),
                new TestData("\u2028\n\u00A0", "\u2028\n\u00A0", "\u2028\n\u00A0\u2028\n"),
                new TestData("\u00A0\u2028\r\n_", "\u00A0\u2028\r\n_"),
                new TestData("\u2028\r\n\u00A0", "\u2028\r\n\u00A0", "\u2028\r\n\u00A0\u2028\r\n"),
                new TestData("\u00A0\u2028\r_", "\u00A0\u2028\r_"),
                new TestData("\u2028\r\u00A0", "\u2028\r\u00A0", "\u2028\r\u00A0\u2028\r"),
                new TestData("\u00A0\u2028\f_", "\u00A0\u2028\f_"),
                new TestData("\u2028\f\u00A0", "\u2028\f\u00A0", "\u2028\f\u00A0\u2028\f"),
                new TestData("\u00A0\u2028\u0085_", "\u00A0\u2028\u0085_"),
                new TestData("\u2028\u0085\u00A0", "\u2028\u0085\u00A0", "\u2028\u0085\u00A0\u2028\u0085"),
                new TestData("\u00A0\u2028\u2028_", "\u00A0\u2028\u2028_"),
                new TestData("\u2028\u2028\u00A0", "\u2028\u2028\u00A0", "\u2028\u2028\u00A0\u2028\u2028"),
                new TestData("\u00A0\u2028\u2029_", "\u00A0\u2028\u2029_"),
                new TestData("\u2028\u2029\u00A0", "\u2028\u2029\u00A0", "\u2028\u2029\u00A0\u2028\u2029"),
                new TestData("\u00A0\u2029\n_", "\u00A0\u2029\n_"),
                new TestData("\u2029\n\u00A0", "\u2029\n\u00A0", "\u2029\n\u00A0\u2029\n"),
                new TestData("\u00A0\u2029\r\n_", "\u00A0\u2029\r\n_"),
                new TestData("\u2029\r\n\u00A0", "\u2029\r\n\u00A0", "\u2029\r\n\u00A0\u2029\r\n"),
                new TestData("\u00A0\u2029\r_", "\u00A0\u2029\r_"),
                new TestData("\u2029\r\u00A0", "\u2029\r\u00A0", "\u2029\r\u00A0\u2029\r"),
                new TestData("\u00A0\u2029\f_", "\u00A0\u2029\f_"),
                new TestData("\u2029\f\u00A0", "\u2029\f\u00A0", "\u2029\f\u00A0\u2029\f"),
                new TestData("\u00A0\u2029\u0085_", "\u00A0\u2029\u0085_"),
                new TestData("\u2029\u0085\u00A0", "\u2029\u0085\u00A0", "\u2029\u0085\u00A0\u2029\u0085"),
                new TestData("\u00A0\u2029\u2028_", "\u00A0\u2029\u2028_"),
                new TestData("\u2029\u2028\u00A0", "\u2029\u2028\u00A0", "\u2029\u2028\u00A0\u2029\u2028"),
                new TestData("\u00A0\u2029\u2029_", "\u00A0\u2029\u2029_"),
                new TestData("\u2029\u2029\u00A0", "\u2029\u2029\u00A0", "\u2029\u2029\u00A0\u2029\u2029")
        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(s -> {
                String d = TestHelper.toStringDescription(s);
                String a = StringHelper.trimEnd(s);
                assertEquals(d, expected, a);
            });
        }
    }

    @Test
    public void toNormalizerFlags() {
        HashMap<StringNormalizationOption, Integer> map = new HashMap<>();
        map.put(StringNormalizationOption.PASS_NULL_VALUE, 0x01);
        map.put(StringNormalizationOption.NO_TRIM_START, 0x02);
        map.put(StringNormalizationOption.NO_TRIM_END, 0x04);
        map.put(StringNormalizationOption.NO_TRIM, 0x06);
        map.put(StringNormalizationOption.SINGLE_LINE, 0x08);
        map.put(StringNormalizationOption.LEAVE_WHITESPACE, 0x10);
        map.put(StringNormalizationOption.LEAVE_BLANK_LINES, 0x20);

        int expected = 0x00;
        int actual = StringNormalizationOption.toFlags();
        assertEquals(expected, actual);
        map.keySet().forEach(t -> {
            String desc = t.name();
            int expv = map.get(t);
            int actv = StringNormalizationOption.toFlags(t);
            assertEquals(desc, expv, actv);
            actv = StringNormalizationOption.toFlags(t, t);
            assertEquals(desc, expv, actv);
            map.keySet().forEach(u -> {
                String des = String.format("%s, %s", desc, u.name());
                int exp = (u == StringNormalizationOption.SINGLE_LINE || t == StringNormalizationOption.SINGLE_LINE) ? (map.get(u) | expv) & 0x1F : map.get(u) | expv;
                int act = StringNormalizationOption.toFlags(t, u);
                assertEquals(des, exp, act);
                act = StringNormalizationOption.toFlags(u, t);
                assertEquals(des, exp, act);
                act = StringNormalizationOption.toFlags(t, u, t);
                assertEquals(des, exp, act);
                act = StringNormalizationOption.toFlags(u, u, t, t, u, u);
                assertEquals(des, exp, act);
                map.keySet().forEach(s -> {
                    String de = String.format("%s, %s", des, s.name());
                    int ev = (s == StringNormalizationOption.SINGLE_LINE || (exp & 0x08) != 0) ? (map.get(s) | exp) & 0x1F : map.get(s) | exp;
                    int av = StringNormalizationOption.toFlags(t, u, s);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(u, t, s);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(u, s, t);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(s, u, t);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(s, t, u);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(t, s, u);
                    assertEquals(de, ev, av);
                    av = StringNormalizationOption.toFlags(t, u, s, s, t, u);
                    assertEquals(de, ev, av);
                    map.keySet().forEach(v -> {
                        String d = String.format("%s, %s", de, v.name());
                        int e = (v == StringNormalizationOption.SINGLE_LINE || (ev & 0x08) != 0) ? (map.get(v) | ev) & 0x1F : map.get(v) | ev;
                        int a = StringNormalizationOption.toFlags(t, u, s, v);
                        assertEquals(d, e, a);
                        map.keySet().forEach(w -> {
                            String dd = String.format("%s, %s", d, w.name());
                            int ee = (w == StringNormalizationOption.SINGLE_LINE || (e & 0x08) != 0) ? (map.get(w) | e) & 0x1F : map.get(w) | e;
                            int aa = StringNormalizationOption.toFlags(t, u, s, v, w);
                            assertEquals(dd, ee, aa);
                            map.keySet().forEach(x -> {
                                String ddd = String.format("%s, %s", dd, x.name());
                                int eee = (x == StringNormalizationOption.SINGLE_LINE || (ee & 0x08) != 0) ? (map.get(x) | ee) & 0x1F : map.get(x) | ee;
                                int aaa = StringNormalizationOption.toFlags(t, u, s, v, w, x);
                                assertEquals(ddd, eee, aaa);
                            });
                        });
                    });
                });
            });
        });
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: Both; Whitespace: Normalize.
     * Was: StringNormalizationOption.TRIM, StringNormalizationOption.REMOVE_BLANK_LINES
     */
    @Test
    public void getStringNormalizer00() {
        Function<String, String> target1 = StringHelper.getNormalizer();
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: End; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_START}</code>
     * Was: StringNormalizationOption.TRIM_END, StringNormalizationOption.REMOVE_BLANK_LINES
     */
    @Test
    public void getStringNormalizer02() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: Start; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_END}</code>
     * Was: StringNormalizationOption.TRIM_START, StringNormalizationOption.REMOVE_BLANK_LINES
     */
    @Test
    public void getStringNormalizer04() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: None; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM}</code>
     * Was: StringNormalizationOption.REMOVE_BLANK_LINES
     */
    @Test
    public void getStringNormalizer06() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = " ";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = " \n ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = " \n ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: Both; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#SINGLE_LINE}</code>
     * Was: StringNormalizationOption.TRIM, StringNormalizationOption.SINGLE_LINE
     * Now: StringNormalizationOption.SINGLE_LINE
     */
    @Test
    public void getStringNormalizer08() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: End; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_START} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     * Was: StringNormalizationOption.TRIM_END, StringNormalizationOption.SINGLE_LINE
     */
    @Test
    public void getStringNormalizer0A() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: Start; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_END} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     * Was: StringNormalizationOption.TRIM_START, StringNormalizationOption.SINGLE_LINE
     */
    @Test
    public void getStringNormalizer0C() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: None; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     * Was: StringNormalizationOption.SINGLE_LINE
     */
    @Test
    public void getStringNormalizer0E() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = " ";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = " ";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: Both; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#LEAVE_WHITESPACE}</code>
     */
    @Test
    public void getStringNormalizer10() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws1 + ws2 + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = source;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = source;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: End; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#NO_TRIM_START}</code>
     */
    @Test
    public void getStringNormalizer12() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM_START);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1 + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = ws + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: Start; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#NO_TRIM_END}</code>
     */
    @Test
    public void getStringNormalizer14() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM_END);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Omit; Trim: None; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#NO_TRIM}</code>
     */
    @Test
    public void getStringNormalizer16() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.NO_TRIM, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = source;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = source;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1 + "\n" + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1 + "\n" + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = Character.toString(ws);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = ws + "\n" + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = Character.toString(ws);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = ws + "\n" + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = ws + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1 + ws + nws2 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + "\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + "\n" + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + "\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + ws + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + "\n" + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + "\n" + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: Both; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     */
    @Test
    public void getStringNormalizer18() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws1 + ws2 + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: End; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#NO_TRIM_START} | @link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     */
    @Test
    public void getStringNormalizer1A() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1 + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = ws + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: Start; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#NO_TRIM_END} | @link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     */
    @Test
    public void getStringNormalizer1C() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Single-Line; Trim: None; Whitespace: Leave.
     * <code>{@link StringNormalizationOption#NO_TRIM} | @link StringNormalizationOption#LEAVE_WHITESPACE} | {@link StringNormalizationOption#SINGLE_LINE}</code>
     */
    @Test
    public void getStringNormalizer1E() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.LEAVE_WHITESPACE, StringNormalizationOption.SINGLE_LINE, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        ArrayList<String> allNlSeq = TestHelper.combineCollections(SINGLE_LINE_SEPARATOR_SEQ, DOUBLE_LINE_SEPARATOR_SEQ);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = source;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = source;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : allNlSeq) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws1) + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = Character.toString(ws2) + ws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = ws2 + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = ws2 + ws1 + nws1 + ws1 + ws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = ws2 + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = ws2 + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : allNlSeq) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = " ";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = Character.toString(ws);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : allNlSeq) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + ws;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = ws + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1 + ws + nws2 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : allNlSeq) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + ws;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = ws + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + ws + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + ws + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = ws + nws1 + " " + nws2 + ws;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Leave; Trim: Both; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#LEAVE_BLANK_LINES}</code>
     * Was: StringNormalizationOption.TRIM
     */
    @Test
    public void getStringNormalizer20() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_BLANK_LINES);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.LEAVE_BLANK_LINES, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + "\n" + nws2 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + "\n\n" + nws2 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n" + nws1 + " " + nws2 + "\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n\n" + nws1 + " " + nws2 + "\n\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Leave; Trim: End; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_START} | {@link StringNormalizationOption#LEAVE_BLANK_LINES}</code>
     * Was: StringNormalizationOption.TRIM_END
     */
    @Test
    public void getStringNormalizer22() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl + ws;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + "\n" + nws2 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + "\n\n" + nws2 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n" + nws1 + " " + nws2 + "\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n\n" + nws1 + " " + nws2 + "\n\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Leave; Trim: Start; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM_END} | {@link StringNormalizationOption#LEAVE_BLANK_LINES}</code>
     * Was: StringNormalizationOption.TRIM_START
     */
    @Test
    public void getStringNormalizer24() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.LEAVE_BLANK_LINES);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_END, StringNormalizationOption.LEAVE_BLANK_LINES, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = "";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = "";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n\n\n\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + "\n" + nws2 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + "\n\n" + nws2 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " \n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n" + nws1 + " " + nws2 + "\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " \n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n\n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n\n" + nws1 + " " + nws2 + "\n\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    /**
     * Mode: Multi-Line; Blank Lines: Leave; Trim: None; Whitespace: Normalize.
     * <code>{@link StringNormalizationOption#NO_TRIM} | {@link StringNormalizationOption#LEAVE_BLANK_LINES}</code>
     * Was:
     */
    @Test
    public void getStringNormalizer26() {
        Function<String, String> target1 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.LEAVE_BLANK_LINES);
        Function<String, String> target2 = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM, StringNormalizationOption.LEAVE_BLANK_LINES, StringNormalizationOption.PASS_NULL_VALUE);
        String source = null;
        String expected = "";
        String actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertNull(actual);
        source = "";
        actual = target1.apply(source);
        assertEquals(expected, actual);
        actual = target2.apply(source);
        assertEquals(expected, actual);
        String[] allNws1 = new String[]{"Test", "\u00A0"};
        String[] allNws2 = new String[]{"Data", "_"};

        for (char ws1 : WHITESPACE_CHARACTERS) {
            source = Character.toString(ws1);
            String description = TestHelper.toStringDescription(source);
            expected = " ";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws2 : WHITESPACE_CHARACTERS) {
                source = Character.toString(ws1) + ws2;
                description = TestHelper.toStringDescription(source);
                expected = " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n \n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = Character.toString(ws2) + ws1 + nl + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n\n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws2 + ws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n \n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nws1 : allNws1) {
                    source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = Character.toString(ws2) + ws1 + nws1 + ws1 + ws2 + nws2 + ws2 + ws1;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + " " + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
        for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = " \n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n \n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n \n \n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
            source = nl;
            String description = TestHelper.toStringDescription(source);
            expected = "\n\n";
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (char ws : WHITESPACE_CHARACTERS) {
                source = nl + ws;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = " \n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n \n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + ws + nl + ws + nl;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n \n\n \n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);
            }
        }
        for (String nws1 : allNws1) {
            source = nws1;
            String description = TestHelper.toStringDescription(source);
            expected = nws1;
            actual = target1.apply(source);
            assertEquals(description, expected, actual);
            actual = target2.apply(source);
            assertEquals(description, expected, actual);

            for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + "\n" + nws2 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                source = nws1 + nl;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + "\n\n";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = nl + nws1;
                description = TestHelper.toStringDescription(source);
                expected = "\n\n" + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + nl + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n" + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + nl + nws2 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + "\n\n" + nws2 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
            }
            for (char ws : WHITESPACE_CHARACTERS) {
                source = nws1 + ws;
                description = TestHelper.toStringDescription(source);
                expected = nws1 + " ";
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                source = ws + nws1;
                description = TestHelper.toStringDescription(source);
                expected = " " + nws1;
                actual = target1.apply(source);
                assertEquals(description, expected, actual);
                actual = target2.apply(source);
                assertEquals(description, expected, actual);

                for (String nws2 : allNws2) {
                    source = nws1 + ws + nws2;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " " + nws2;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + ws + nws2 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + " " + nws2 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);
                }
                for (String nl : SINGLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " \n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n" + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + "\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n \n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n" + nws1 + " " + nws2 + "\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
                for (String nl : DOUBLE_LINE_SEPARATOR_SEQ) {
                    source = nws1 + ws + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + " \n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + nws1 + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n" + nws1 + " ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nws1 + nl + ws;
                    description = TestHelper.toStringDescription(source);
                    expected = nws1 + "\n\n ";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nws1 + nl;
                    description = TestHelper.toStringDescription(source);
                    expected = " " + nws1 + "\n\n";
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = nl + ws + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = "\n\n " + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    source = ws + nl + nws1;
                    description = TestHelper.toStringDescription(source);
                    expected = " \n\n" + nws1;
                    actual = target1.apply(source);
                    assertEquals(description, expected, actual);
                    actual = target2.apply(source);
                    assertEquals(description, expected, actual);

                    for (String nws2 : allNws2) {
                        source = nws1 + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + nl + ws + nl + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + "\n\n \n\n" + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nws1 + ws + nl + ws + nws2;
                        description = TestHelper.toStringDescription(source);
                        expected = nws1 + " \n\n " + nws2;
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = nl + nws1 + ws + nws2 + nl;
                        description = TestHelper.toStringDescription(source);
                        expected = "\n\n" + nws1 + " " + nws2 + "\n\n";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);

                        source = ws + nws1 + nl + nws2 + ws;
                        description = TestHelper.toStringDescription(source);
                        expected = " " + nws1 + "\n\n" + nws2 + " ";
                        actual = target1.apply(source);
                        assertEquals(description, expected, actual);
                        actual = target2.apply(source);
                        assertEquals(description, expected, actual);
                    }
                }
            }
        }
    }

    public static class TestData {

        //        private final List<SourceValue> source;
        private final List<String> source;
        private final String expected;

        public TestData(String expected, String... source) {
//            List<SourceValue> src = new ArrayList<>();
//            for (String s : source) {
//                src.add(new SourceValue(s));
//            }
            List<String> src = new ArrayList<>();
            Collections.addAll(src, source);
            this.source = Collections.unmodifiableList(src);
            this.expected = expected;
        }
    }

}