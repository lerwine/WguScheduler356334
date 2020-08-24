package Erwine.Leonard.T.wguscheduler356334.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringLineIteratorTest {

    @Test
    public void nullTest() {
        String source = null;
        StringLineIterator target = StringLineIterator.create(source, false, false);
        assertFalse(target.hasNext());
        target = StringLineIterator.create(source, false, true);
        assertFalse(target.hasNext());
        target = StringLineIterator.create(source, true, false);
        assertFalse(target.hasNext());
        target = StringLineIterator.create(source, true, true);
        assertFalse(target.hasNext());
    }

    @Test
    public void emptyTest() {
        String source = "";

        StringLineIterator target = StringLineIterator.create(source, false, false);
        assertTrue(target.hasNext());
        String actual = target.next();
        assertEquals(source, actual);
        assertFalse(target.hasNext());

        target = StringLineIterator.create(source, false, true);
        assertTrue(target.hasNext());
        actual = target.next();
        assertEquals(source, actual);
        assertFalse(target.hasNext());

        target = StringLineIterator.create(source, true, false);
        assertTrue(target.hasNext());
        actual = target.next();
        assertEquals(source, actual);
        assertFalse(target.hasNext());

        assertFalse(target.hasNext());
        target = StringLineIterator.create(source, true, true);
        assertTrue(target.hasNext());
        actual = target.next();
        assertEquals(source, actual);
        assertFalse(target.hasNext());
    }

    @Test
    public void noTrimTest() {
        TestData[] testData = new TestData[]{
                new TestData("\n", "\r", "\n", "\r\n", "\u0085", "\u2028", "\u2029"),
                new TestData("_\n", "_\r", "_\n", "_\r\n", "_\u0085", "_\u2028", "_\u2029"),
                new TestData("Test\n", "Test\r", "Test\n", "Test\r\n", "Test\u0085", "Test\u2028", "Test\u2029"),
                new TestData(" \n", " \r", " \n", " \r\n", " \u0085", " \u2028", " \u2029"),
                new TestData("\t\n", "\t\r", "\t\n", "\t\r\n", "\t\u0085", "\t\u2028", "\t\u2029"),
                new TestData("\u000B\n", "\u000B\r", "\u000B\n", "\u000B\r\n", "\u000B\u0085", "\u000B\u2028", "\u000B\u2029"),
                new TestData("\f\n", "\f\r", "\f\n", "\f\r\n", "\f\u0085", "\f\u2028", "\f\u2029"),
                new TestData("\u00A0\n", "\u00A0\r", "\u00A0\n", "\u00A0\r\n", "\u00A0\u0085", "\u00A0\u2028", "\u00A0\u2029"),
                new TestData("\u1680\n", "\u1680\r", "\u1680\n", "\u1680\r\n", "\u1680\u0085", "\u1680\u2028", "\u1680\u2029"),
                new TestData("\n_", "\r_", "\n_", "\r\n_", "\u0085_", "\u2028_", "\u2029_"),
                new TestData("\nTest", "\rTest", "\nTest", "\r\nTest", "\u0085Test", "\u2028Test", "\u2029Test"),
                new TestData("\n ", "\r ", "\n ", "\r\n ", "\u0085 ", "\u2028 ", "\u2029 "),
                new TestData("\n\t", "\r\t", "\n\t", "\r\n\t", "\u0085\t", "\u2028\t", "\u2029\t"),
                new TestData("\n\u000B", "\r\u000B", "\n\u000B", "\r\n\u000B", "\u0085\u000B", "\u2028\u000B", "\u2029\u000B"),
                new TestData("\n\f", "\r\f", "\n\f", "\r\n\f", "\u0085\f", "\u2028\f", "\u2029\f"),
                new TestData("\n\u00A0", "\r\u00A0", "\n\u00A0", "\r\n\u00A0", "\u0085\u00A0", "\u2028\u00A0", "\u2029\u00A0"),
                new TestData("\n\u1680", "\r\u1680", "\n\u1680", "\r\n\u1680", "\u0085\u1680", "\u2028\u1680", "\u2029\u1680"),
                new TestData("\n\n", "\r\r", "\r\r\n", "\r\u0085", "\r\u2028", "\r\u2029", "\n\r", "\n\n", "\n\r\n", "\n\u0085", "\n\u2028", "\n\u2029", "\r\n\r", "\r\n\n", "\r\n\r\n", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\u0085\r", "\u0085\n", "\u0085\r\n", "\u0085\u0085", "\u0085\u2028", "\u0085\u2029", "\u2028\r", "\u2028\n", "\u2028\r\n", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\r", "\u2029\n", "\u2029\r\n", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029"),
                new TestData("_ .", "_ ."),
                new TestData("_ Data", "_ Data"),
                new TestData("_\t.", "_\t."),
                new TestData("_\tData", "_\tData"),
                new TestData("_\u000B.", "_\u000B."),
                new TestData("_\u000BData", "_\u000BData"),
                new TestData("_\f.", "_\f."),
                new TestData("_\fData", "_\fData"),
                new TestData("_\u00A0.", "_\u00A0."),
                new TestData("_\u00A0Data", "_\u00A0Data"),
                new TestData("_\u1680.", "_\u1680."),
                new TestData("_\u1680Data", "_\u1680Data"),
                new TestData("Test .", "Test ."),
                new TestData("Test Data", "Test Data"),
                new TestData("Test\t.", "Test\t."),
                new TestData("Test\tData", "Test\tData"),
                new TestData("Test\u000B.", "Test\u000B."),
                new TestData("Test\u000BData", "Test\u000BData"),
                new TestData("Test\f.", "Test\f."),
                new TestData("Test\fData", "Test\fData"),
                new TestData("Test\u00A0.", "Test\u00A0."),
                new TestData("Test\u00A0Data", "Test\u00A0Data"),
                new TestData("Test\u1680.", "Test\u1680."),
                new TestData("Test\u1680Data", "Test\u1680Data"),
                new TestData(" \n ", " \r ", " \n ", " \r\n ", " \u0085 ", " \u2028 ", " \u2029 "),
                new TestData("\t\n\t", "\t\r\t", "\t\n\t", "\t\r\n\t", "\t\u0085\t", "\t\u2028\t", "\t\u2029\t"),
                new TestData("\u000B\n\u000B", "\u000B\r\u000B", "\u000B\n\u000B", "\u000B\r\n\u000B", "\u000B\u0085\u000B", "\u000B\u2028\u000B", "\u000B\u2029\u000B"),
                new TestData("\f\n\f", "\f\r\f", "\f\n\f", "\f\r\n\f", "\f\u0085\f", "\f\u2028\f", "\f\u2029\f"),
                new TestData("\u00A0\n\u00A0", "\u00A0\r\u00A0", "\u00A0\n\u00A0", "\u00A0\r\n\u00A0", "\u00A0\u0085\u00A0", "\u00A0\u2028\u00A0", "\u00A0\u2029\u00A0"),
                new TestData("\u1680\n\u1680", "\u1680\r\u1680", "\u1680\n\u1680", "\u1680\r\n\u1680", "\u1680\u0085\u1680", "\u1680\u2028\u1680", "\u1680\u2029\u1680"),
                new TestData("\n_\n", "\r_\r", "\n_\n", "\r\n_\r\n", "\u0085_\u0085", "\u2028_\u2028", "\u2029_\u2029"),
                new TestData("\nTest\n", "\rTest\r", "\nTest\n", "\r\nTest\r\n", "\u0085Test\u0085", "\u2028Test\u2028", "\u2029Test\u2029"),
                new TestData("\n \n", "\r \r", "\n \n", "\r\n \r\n", "\u0085 \u0085", "\u2028 \u2028", "\u2029 \u2029"),
                new TestData("\n\t\n", "\r\t\r", "\n\t\n", "\r\n\t\r\n", "\u0085\t\u0085", "\u2028\t\u2028", "\u2029\t\u2029"),
                new TestData("\n\u000B\n", "\r\u000B\r", "\n\u000B\n", "\r\n\u000B\r\n", "\u0085\u000B\u0085", "\u2028\u000B\u2028", "\u2029\u000B\u2029"),
                new TestData("\n\f\n", "\r\f\r", "\n\f\n", "\r\n\f\r\n", "\u0085\f\u0085", "\u2028\f\u2028", "\u2029\f\u2029"),
                new TestData("\n\u00A0\n", "\r\u00A0\r", "\n\u00A0\n", "\r\n\u00A0\r\n", "\u0085\u00A0\u0085", "\u2028\u00A0\u2028", "\u2029\u00A0\u2029"),
                new TestData("\n\u1680\n", "\r\u1680\r", "\n\u1680\n", "\r\n\u1680\r\n", "\u0085\u1680\u0085", "\u2028\u1680\u2028", "\u2029\u1680\u2029"),
                new TestData("_ \n.", "_ \r.", "_ \n.", "_ \r\n.", "_ \u0085.", "_ \u2028.", "_ \u2029."),
                new TestData("_ \nData", "_ \rData", "_ \nData", "_ \r\nData", "_ \u0085Data", "_ \u2028Data", "_ \u2029Data"),
                new TestData("_\t\n.", "_\t\r.", "_\t\n.", "_\t\r\n.", "_\t\u0085.", "_\t\u2028.", "_\t\u2029."),
                new TestData("_\t\nData", "_\t\rData", "_\t\nData", "_\t\r\nData", "_\t\u0085Data", "_\t\u2028Data", "_\t\u2029Data"),
                new TestData("_\u000B\n.", "_\u000B\r.", "_\u000B\n.", "_\u000B\r\n.", "_\u000B\u0085.", "_\u000B\u2028.", "_\u000B\u2029."),
                new TestData("_\u000B\nData", "_\u000B\rData", "_\u000B\nData", "_\u000B\r\nData", "_\u000B\u0085Data", "_\u000B\u2028Data", "_\u000B\u2029Data"),
                new TestData("_\f\n.", "_\f\r.", "_\f\n.", "_\f\r\n.", "_\f\u0085.", "_\f\u2028.", "_\f\u2029."),
                new TestData("_\f\nData", "_\f\rData", "_\f\nData", "_\f\r\nData", "_\f\u0085Data", "_\f\u2028Data", "_\f\u2029Data"),
                new TestData("_\u00A0\n.", "_\u00A0\r.", "_\u00A0\n.", "_\u00A0\r\n.", "_\u00A0\u0085.", "_\u00A0\u2028.", "_\u00A0\u2029."),
                new TestData("_\u00A0\nData", "_\u00A0\rData", "_\u00A0\nData", "_\u00A0\r\nData", "_\u00A0\u0085Data", "_\u00A0\u2028Data", "_\u00A0\u2029Data"),
                new TestData("_\u1680\n.", "_\u1680\r.", "_\u1680\n.", "_\u1680\r\n.", "_\u1680\u0085.", "_\u1680\u2028.", "_\u1680\u2029."),
                new TestData("_\u1680\nData", "_\u1680\rData", "_\u1680\nData", "_\u1680\r\nData", "_\u1680\u0085Data", "_\u1680\u2028Data", "_\u1680\u2029Data"),
                new TestData("Test \n.", "Test \r.", "Test \n.", "Test \r\n.", "Test \u0085.", "Test \u2028.", "Test \u2029."),
                new TestData("Test \nData", "Test \rData", "Test \nData", "Test \r\nData", "Test \u0085Data", "Test \u2028Data", "Test \u2029Data"),
                new TestData("Test\t\n.", "Test\t\r.", "Test\t\n.", "Test\t\r\n.", "Test\t\u0085.", "Test\t\u2028.", "Test\t\u2029."),
                new TestData("Test\t\nData", "Test\t\rData", "Test\t\nData", "Test\t\r\nData", "Test\t\u0085Data", "Test\t\u2028Data", "Test\t\u2029Data"),
                new TestData("Test\u000B\n.", "Test\u000B\r.", "Test\u000B\n.", "Test\u000B\r\n.", "Test\u000B\u0085.", "Test\u000B\u2028.", "Test\u000B\u2029."),
                new TestData("Test\u000B\nData", "Test\u000B\rData", "Test\u000B\nData", "Test\u000B\r\nData", "Test\u000B\u0085Data", "Test\u000B\u2028Data", "Test\u000B\u2029Data"),
                new TestData("Test\f\n.", "Test\f\r.", "Test\f\n.", "Test\f\r\n.", "Test\f\u0085.", "Test\f\u2028.", "Test\f\u2029."),
                new TestData("Test\f\nData", "Test\f\rData", "Test\f\nData", "Test\f\r\nData", "Test\f\u0085Data", "Test\f\u2028Data", "Test\f\u2029Data"),
                new TestData("Test\u00A0\n.", "Test\u00A0\r.", "Test\u00A0\n.", "Test\u00A0\r\n.", "Test\u00A0\u0085.", "Test\u00A0\u2028.", "Test\u00A0\u2029."),
                new TestData("Test\u00A0\nData", "Test\u00A0\rData", "Test\u00A0\nData", "Test\u00A0\r\nData", "Test\u00A0\u0085Data", "Test\u00A0\u2028Data", "Test\u00A0\u2029Data"),
                new TestData("Test\u1680\n.", "Test\u1680\r.", "Test\u1680\n.", "Test\u1680\r\n.", "Test\u1680\u0085.", "Test\u1680\u2028.", "Test\u1680\u2029."),
                new TestData("Test\u1680\nData", "Test\u1680\rData", "Test\u1680\nData", "Test\u1680\r\nData", "Test\u1680\u0085Data", "Test\u1680\u2028Data", "Test\u1680\u2029Data"),
                new TestData(".\n _", ".\r _", ".\n _", ".\r\n _", ".\u0085 _", ".\u2028 _", ".\u2029 _"),
                new TestData("Data\n _", "Data\r _", "Data\n _", "Data\r\n _", "Data\u0085 _", "Data\u2028 _", "Data\u2029 _"),
                new TestData(".\n\t_", ".\r\t_", ".\n\t_", ".\r\n\t_", ".\u0085\t_", ".\u2028\t_", ".\u2029\t_"),
                new TestData("Data\n\t_", "Data\r\t_", "Data\n\t_", "Data\r\n\t_", "Data\u0085\t_", "Data\u2028\t_", "Data\u2029\t_"),
                new TestData(".\n\u000B_", ".\r\u000B_", ".\n\u000B_", ".\r\n\u000B_", ".\u0085\u000B_", ".\u2028\u000B_", ".\u2029\u000B_"),
                new TestData("Data\n\u000B_", "Data\r\u000B_", "Data\n\u000B_", "Data\r\n\u000B_", "Data\u0085\u000B_", "Data\u2028\u000B_", "Data\u2029\u000B_"),
                new TestData(".\n\f_", ".\r\f_", ".\n\f_", ".\r\n\f_", ".\u0085\f_", ".\u2028\f_", ".\u2029\f_"),
                new TestData("Data\n\f_", "Data\r\f_", "Data\n\f_", "Data\r\n\f_", "Data\u0085\f_", "Data\u2028\f_", "Data\u2029\f_"),
                new TestData(".\n\u00A0_", ".\r\u00A0_", ".\n\u00A0_", ".\r\n\u00A0_", ".\u0085\u00A0_", ".\u2028\u00A0_", ".\u2029\u00A0_"),
                new TestData("Data\n\u00A0_", "Data\r\u00A0_", "Data\n\u00A0_", "Data\r\n\u00A0_", "Data\u0085\u00A0_", "Data\u2028\u00A0_", "Data\u2029\u00A0_"),
                new TestData(".\n\u1680_", ".\r\u1680_", ".\n\u1680_", ".\r\n\u1680_", ".\u0085\u1680_", ".\u2028\u1680_", ".\u2029\u1680_"),
                new TestData("Data\n\u1680_", "Data\r\u1680_", "Data\n\u1680_", "Data\r\n\u1680_", "Data\u0085\u1680_", "Data\u2028\u1680_", "Data\u2029\u1680_"),
                new TestData(".\n Test", ".\r Test", ".\n Test", ".\r\n Test", ".\u0085 Test", ".\u2028 Test", ".\u2029 Test"),
                new TestData("Data\n Test", "Data\r Test", "Data\n Test", "Data\r\n Test", "Data\u0085 Test", "Data\u2028 Test", "Data\u2029 Test"),
                new TestData(".\n\tTest", ".\r\tTest", ".\n\tTest", ".\r\n\tTest", ".\u0085\tTest", ".\u2028\tTest", ".\u2029\tTest"),
                new TestData("Data\n\tTest", "Data\r\tTest", "Data\n\tTest", "Data\r\n\tTest", "Data\u0085\tTest", "Data\u2028\tTest", "Data\u2029\tTest"),
                new TestData(".\n\u000BTest", ".\r\u000BTest", ".\n\u000BTest", ".\r\n\u000BTest", ".\u0085\u000BTest", ".\u2028\u000BTest", ".\u2029\u000BTest"),
                new TestData("Data\n\u000BTest", "Data\r\u000BTest", "Data\n\u000BTest", "Data\r\n\u000BTest", "Data\u0085\u000BTest", "Data\u2028\u000BTest", "Data\u2029\u000BTest"),
                new TestData(".\n\fTest", ".\r\fTest", ".\n\fTest", ".\r\n\fTest", ".\u0085\fTest", ".\u2028\fTest", ".\u2029\fTest"),
                new TestData("Data\n\fTest", "Data\r\fTest", "Data\n\fTest", "Data\r\n\fTest", "Data\u0085\fTest", "Data\u2028\fTest", "Data\u2029\fTest"),
                new TestData(".\n\u00A0Test", ".\r\u00A0Test", ".\n\u00A0Test", ".\r\n\u00A0Test", ".\u0085\u00A0Test", ".\u2028\u00A0Test", ".\u2029\u00A0Test"),
                new TestData("Data\n\u00A0Test", "Data\r\u00A0Test", "Data\n\u00A0Test", "Data\r\n\u00A0Test", "Data\u0085\u00A0Test", "Data\u2028\u00A0Test", "Data\u2029\u00A0Test"),
                new TestData(".\n\u1680Test", ".\r\u1680Test", ".\n\u1680Test", ".\r\n\u1680Test", ".\u0085\u1680Test", ".\u2028\u1680Test", ".\u2029\u1680Test"),
                new TestData("Data\n\u1680Test", "Data\r\u1680Test", "Data\n\u1680Test", "Data\r\n\u1680Test", "Data\u0085\u1680Test", "Data\u2028\u1680Test", "Data\u2029\u1680Test"),
                new TestData("\n _ \n", "\r _ \r", "\n _ \n", "\r\n _ \r\n", "\u0085 _ \u0085", "\u2028 _ \u2028", "\u2029 _ \u2029"),
                new TestData("\n\t_\t\n", "\r\t_\t\r", "\n\t_\t\n", "\r\n\t_\t\r\n", "\u0085\t_\t\u0085", "\u2028\t_\t\u2028", "\u2029\t_\t\u2029"),
                new TestData("\n\u000B_\u000B\n", "\r\u000B_\u000B\r", "\n\u000B_\u000B\n", "\r\n\u000B_\u000B\r\n", "\u0085\u000B_\u000B\u0085", "\u2028\u000B_\u000B\u2028", "\u2029\u000B_\u000B\u2029"),
                new TestData("\n\f_\f\n", "\r\f_\f\r", "\n\f_\f\n", "\r\n\f_\f\r\n", "\u0085\f_\f\u0085", "\u2028\f_\f\u2028", "\u2029\f_\f\u2029"),
                new TestData("\n\u00A0_\u00A0\n", "\r\u00A0_\u00A0\r", "\n\u00A0_\u00A0\n", "\r\n\u00A0_\u00A0\r\n", "\u0085\u00A0_\u00A0\u0085", "\u2028\u00A0_\u00A0\u2028", "\u2029\u00A0_\u00A0\u2029"),
                new TestData("\n\u1680_\u1680\n", "\r\u1680_\u1680\r", "\n\u1680_\u1680\n", "\r\n\u1680_\u1680\r\n", "\u0085\u1680_\u1680\u0085", "\u2028\u1680_\u1680\u2028", "\u2029\u1680_\u1680\u2029"),
                new TestData("\n Test \n", "\r Test \r", "\n Test \n", "\r\n Test \r\n", "\u0085 Test \u0085", "\u2028 Test \u2028", "\u2029 Test \u2029"),
                new TestData("\n\tTest\t\n", "\r\tTest\t\r", "\n\tTest\t\n", "\r\n\tTest\t\r\n", "\u0085\tTest\t\u0085", "\u2028\tTest\t\u2028", "\u2029\tTest\t\u2029"),
                new TestData("\n\u000BTest\u000B\n", "\r\u000BTest\u000B\r", "\n\u000BTest\u000B\n", "\r\n\u000BTest\u000B\r\n", "\u0085\u000BTest\u000B\u0085", "\u2028\u000BTest\u000B\u2028", "\u2029\u000BTest\u000B\u2029"),
                new TestData("\n\fTest\f\n", "\r\fTest\f\r", "\n\fTest\f\n", "\r\n\fTest\f\r\n", "\u0085\fTest\f\u0085", "\u2028\fTest\f\u2028", "\u2029\fTest\f\u2029"),
                new TestData("\n\u00A0Test\u00A0\n", "\r\u00A0Test\u00A0\r", "\n\u00A0Test\u00A0\n", "\r\n\u00A0Test\u00A0\r\n", "\u0085\u00A0Test\u00A0\u0085", "\u2028\u00A0Test\u00A0\u2028", "\u2029\u00A0Test\u00A0\u2029"),
                new TestData("\n\u1680Test\u1680\n", "\r\u1680Test\u1680\r", "\n\u1680Test\u1680\n", "\r\n\u1680Test\u1680\r\n", "\u0085\u1680Test\u1680\u0085", "\u2028\u1680Test\u1680\u2028", "\u2029\u1680Test\u1680\u2029"),
                new TestData(" \n_\n ", " \r_\r ", " \n_\n ", " \r\n_\r\n ", " \u0085_\u0085 ", " \u2028_\u2028 ", " \u2029_\u2029 "),
                new TestData("\t\n_\n\t", "\t\r_\r\t", "\t\n_\n\t", "\t\r\n_\r\n\t", "\t\u0085_\u0085\t", "\t\u2028_\u2028\t", "\t\u2029_\u2029\t"),
                new TestData("\u000B\n_\n\u000B", "\u000B\r_\r\u000B", "\u000B\n_\n\u000B", "\u000B\r\n_\r\n\u000B", "\u000B\u0085_\u0085\u000B", "\u000B\u2028_\u2028\u000B", "\u000B\u2029_\u2029\u000B"),
                new TestData("\f\n_\n\f", "\f\r_\r\f", "\f\n_\n\f", "\f\r\n_\r\n\f", "\f\u0085_\u0085\f", "\f\u2028_\u2028\f", "\f\u2029_\u2029\f"),
                new TestData("\u00A0\n_\n\u00A0", "\u00A0\r_\r\u00A0", "\u00A0\n_\n\u00A0", "\u00A0\r\n_\r\n\u00A0", "\u00A0\u0085_\u0085\u00A0", "\u00A0\u2028_\u2028\u00A0", "\u00A0\u2029_\u2029\u00A0"),
                new TestData("\u1680\n_\n\u1680", "\u1680\r_\r\u1680", "\u1680\n_\n\u1680", "\u1680\r\n_\r\n\u1680", "\u1680\u0085_\u0085\u1680", "\u1680\u2028_\u2028\u1680", "\u1680\u2029_\u2029\u1680"),
                new TestData(" \nTest\n ", " \rTest\r ", " \nTest\n ", " \r\nTest\r\n ", " \u0085Test\u0085 ", " \u2028Test\u2028 ", " \u2029Test\u2029 "),
                new TestData("\t\nTest\n\t", "\t\rTest\r\t", "\t\nTest\n\t", "\t\r\nTest\r\n\t", "\t\u0085Test\u0085\t", "\t\u2028Test\u2028\t", "\t\u2029Test\u2029\t"),
                new TestData("\u000B\nTest\n\u000B", "\u000B\rTest\r\u000B", "\u000B\nTest\n\u000B", "\u000B\r\nTest\r\n\u000B", "\u000B\u0085Test\u0085\u000B", "\u000B\u2028Test\u2028\u000B", "\u000B\u2029Test\u2029\u000B"),
                new TestData("\f\nTest\n\f", "\f\rTest\r\f", "\f\nTest\n\f", "\f\r\nTest\r\n\f", "\f\u0085Test\u0085\f", "\f\u2028Test\u2028\f", "\f\u2029Test\u2029\f"),
                new TestData("\u00A0\nTest\n\u00A0", "\u00A0\rTest\r\u00A0", "\u00A0\nTest\n\u00A0", "\u00A0\r\nTest\r\n\u00A0", "\u00A0\u0085Test\u0085\u00A0", "\u00A0\u2028Test\u2028\u00A0", "\u00A0\u2029Test\u2029\u00A0"),
                new TestData("\u1680\nTest\n\u1680", "\u1680\rTest\r\u1680", "\u1680\nTest\n\u1680", "\u1680\r\nTest\r\n\u1680", "\u1680\u0085Test\u0085\u1680", "\u1680\u2028Test\u2028\u1680", "\u1680\u2029Test\u2029\u1680"),
                new TestData("\n _", "\r _", "\n _", "\r\n _", "\u0085 _", "\u2028 _", "\u2029 _"),
                new TestData("\n\t_", "\r\t_", "\n\t_", "\r\n\t_", "\u0085\t_", "\u2028\t_", "\u2029\t_"),
                new TestData("\n\u000B_", "\r\u000B_", "\n\u000B_", "\r\n\u000B_", "\u0085\u000B_", "\u2028\u000B_", "\u2029\u000B_"),
                new TestData("\n\f_", "\r\f_", "\n\f_", "\r\n\f_", "\u0085\f_", "\u2028\f_", "\u2029\f_"),
                new TestData("\n\u00A0_", "\r\u00A0_", "\n\u00A0_", "\r\n\u00A0_", "\u0085\u00A0_", "\u2028\u00A0_", "\u2029\u00A0_"),
                new TestData("\n\u1680_", "\r\u1680_", "\n\u1680_", "\r\n\u1680_", "\u0085\u1680_", "\u2028\u1680_", "\u2029\u1680_"),
                new TestData("\n Test", "\r Test", "\n Test", "\r\n Test", "\u0085 Test", "\u2028 Test", "\u2029 Test"),
                new TestData("\n\tTest", "\r\tTest", "\n\tTest", "\r\n\tTest", "\u0085\tTest", "\u2028\tTest", "\u2029\tTest"),
                new TestData("\n\u000BTest", "\r\u000BTest", "\n\u000BTest", "\r\n\u000BTest", "\u0085\u000BTest", "\u2028\u000BTest", "\u2029\u000BTest"),
                new TestData("\n\fTest", "\r\fTest", "\n\fTest", "\r\n\fTest", "\u0085\fTest", "\u2028\fTest", "\u2029\fTest"),
                new TestData("\n\u00A0Test", "\r\u00A0Test", "\n\u00A0Test", "\r\n\u00A0Test", "\u0085\u00A0Test", "\u2028\u00A0Test", "\u2029\u00A0Test"),
                new TestData("\n\u1680Test", "\r\u1680Test", "\n\u1680Test", "\r\n\u1680Test", "\u0085\u1680Test", "\u2028\u1680Test", "\u2029\u1680Test"),
                new TestData("_ \n", "_ \r", "_ \n", "_ \r\n", "_ \u0085", "_ \u2028", "_ \u2029"),
                new TestData("_\t\n", "_\t\r", "_\t\n", "_\t\r\n", "_\t\u0085", "_\t\u2028", "_\t\u2029"),
                new TestData("_\u000B\n", "_\u000B\r", "_\u000B\n", "_\u000B\r\n", "_\u000B\u0085", "_\u000B\u2028", "_\u000B\u2029"),
                new TestData("_\f\n", "_\f\r", "_\f\n", "_\f\r\n", "_\f\u0085", "_\f\u2028", "_\f\u2029"),
                new TestData("_\u00A0\n", "_\u00A0\r", "_\u00A0\n", "_\u00A0\r\n", "_\u00A0\u0085", "_\u00A0\u2028", "_\u00A0\u2029"),
                new TestData("_\u1680\n", "_\u1680\r", "_\u1680\n", "_\u1680\r\n", "_\u1680\u0085", "_\u1680\u2028", "_\u1680\u2029"),
                new TestData("Test \n", "Test \r", "Test \n", "Test \r\n", "Test \u0085", "Test \u2028", "Test \u2029"),
                new TestData("Test\t\n", "Test\t\r", "Test\t\n", "Test\t\r\n", "Test\t\u0085", "Test\t\u2028", "Test\t\u2029"),
                new TestData("Test\u000B\n", "Test\u000B\r", "Test\u000B\n", "Test\u000B\r\n", "Test\u000B\u0085", "Test\u000B\u2028", "Test\u000B\u2029"),
                new TestData("Test\f\n", "Test\f\r", "Test\f\n", "Test\f\r\n", "Test\f\u0085", "Test\f\u2028", "Test\f\u2029"),
                new TestData("Test\u00A0\n", "Test\u00A0\r", "Test\u00A0\n", "Test\u00A0\r\n", "Test\u00A0\u0085", "Test\u00A0\u2028", "Test\u00A0\u2029"),
                new TestData("Test\u1680\n", "Test\u1680\r", "Test\u1680\n", "Test\u1680\r\n", "Test\u1680\u0085", "Test\u1680\u2028", "Test\u1680\u2029"),
                new TestData(" \n_", " \r_", " \n_", " \r\n_", " \u0085_", " \u2028_", " \u2029_"),
                new TestData("\t\n_", "\t\r_", "\t\n_", "\t\r\n_", "\t\u0085_", "\t\u2028_", "\t\u2029_"),
                new TestData("\u000B\n_", "\u000B\r_", "\u000B\n_", "\u000B\r\n_", "\u000B\u0085_", "\u000B\u2028_", "\u000B\u2029_"),
                new TestData("\f\n_", "\f\r_", "\f\n_", "\f\r\n_", "\f\u0085_", "\f\u2028_", "\f\u2029_"),
                new TestData("\u00A0\n_", "\u00A0\r_", "\u00A0\n_", "\u00A0\r\n_", "\u00A0\u0085_", "\u00A0\u2028_", "\u00A0\u2029_"),
                new TestData("\u1680\n_", "\u1680\r_", "\u1680\n_", "\u1680\r\n_", "\u1680\u0085_", "\u1680\u2028_", "\u1680\u2029_"),
                new TestData("\n\n_", "\r\r_", "\n\r_", "\r\n\r_", "\u0085\r_", "\u2028\r_", "\u2029\r_", "\n\n_", "\r\n\n_", "\u0085\n_", "\u2028\n_", "\u2029\n_", "\r\r\n_", "\n\r\n_", "\r\n\r\n_", "\u0085\r\n_", "\u2028\r\n_", "\u2029\r\n_", "\r\u0085_", "\n\u0085_", "\r\n\u0085_", "\u0085\u0085_", "\u2028\u0085_", "\u2029\u0085_", "\r\u2028_", "\n\u2028_", "\r\n\u2028_", "\u0085\u2028_", "\u2028\u2028_", "\u2029\u2028_", "\r\u2029_", "\n\u2029_", "\r\n\u2029_", "\u0085\u2029_", "\u2028\u2029_", "\u2029\u2029_"),
                new TestData(" \nTest", " \rTest", " \nTest", " \r\nTest", " \u0085Test", " \u2028Test", " \u2029Test"),
                new TestData("\t\nTest", "\t\rTest", "\t\nTest", "\t\r\nTest", "\t\u0085Test", "\t\u2028Test", "\t\u2029Test"),
                new TestData("\u000B\nTest", "\u000B\rTest", "\u000B\nTest", "\u000B\r\nTest", "\u000B\u0085Test", "\u000B\u2028Test", "\u000B\u2029Test"),
                new TestData("\f\nTest", "\f\rTest", "\f\nTest", "\f\r\nTest", "\f\u0085Test", "\f\u2028Test", "\f\u2029Test"),
                new TestData("\u00A0\nTest", "\u00A0\rTest", "\u00A0\nTest", "\u00A0\r\nTest", "\u00A0\u0085Test", "\u00A0\u2028Test", "\u00A0\u2029Test"),
                new TestData("\u1680\nTest", "\u1680\rTest", "\u1680\nTest", "\u1680\r\nTest", "\u1680\u0085Test", "\u1680\u2028Test", "\u1680\u2029Test"),
                new TestData("\n\nTest", "\r\rTest", "\n\rTest", "\r\n\rTest", "\u0085\rTest", "\u2028\rTest", "\u2029\rTest", "\n\nTest", "\r\n\nTest", "\u0085\nTest", "\u2028\nTest", "\u2029\nTest", "\r\r\nTest", "\n\r\nTest", "\r\n\r\nTest", "\u0085\r\nTest", "\u2028\r\nTest", "\u2029\r\nTest", "\r\u0085Test", "\n\u0085Test", "\r\n\u0085Test", "\u0085\u0085Test", "\u2028\u0085Test", "\u2029\u0085Test", "\r\u2028Test", "\n\u2028Test", "\r\n\u2028Test", "\u0085\u2028Test", "\u2028\u2028Test", "\u2029\u2028Test", "\r\u2029Test", "\n\u2029Test", "\r\n\u2029Test", "\u0085\u2029Test", "\u2028\u2029Test", "\u2029\u2029Test"),
                new TestData("_ .\n", "_ .\r", "_ .\n", "_ .\r\n", "_ .\u0085", "_ .\u2028", "_ .\u2029"),
                new TestData("_ Data\n", "_ Data\r", "_ Data\n", "_ Data\r\n", "_ Data\u0085", "_ Data\u2028", "_ Data\u2029"),
                new TestData("_\t.\n", "_\t.\r", "_\t.\n", "_\t.\r\n", "_\t.\u0085", "_\t.\u2028", "_\t.\u2029"),
                new TestData("_\tData\n", "_\tData\r", "_\tData\n", "_\tData\r\n", "_\tData\u0085", "_\tData\u2028", "_\tData\u2029"),
                new TestData("_\u000B.\n", "_\u000B.\r", "_\u000B.\n", "_\u000B.\r\n", "_\u000B.\u0085", "_\u000B.\u2028", "_\u000B.\u2029"),
                new TestData("_\u000BData\n", "_\u000BData\r", "_\u000BData\n", "_\u000BData\r\n", "_\u000BData\u0085", "_\u000BData\u2028", "_\u000BData\u2029"),
                new TestData("_\f.\n", "_\f.\r", "_\f.\n", "_\f.\r\n", "_\f.\u0085", "_\f.\u2028", "_\f.\u2029"),
                new TestData("_\fData\n", "_\fData\r", "_\fData\n", "_\fData\r\n", "_\fData\u0085", "_\fData\u2028", "_\fData\u2029"),
                new TestData("_\u00A0.\n", "_\u00A0.\r", "_\u00A0.\n", "_\u00A0.\r\n", "_\u00A0.\u0085", "_\u00A0.\u2028", "_\u00A0.\u2029"),
                new TestData("_\u00A0Data\n", "_\u00A0Data\r", "_\u00A0Data\n", "_\u00A0Data\r\n", "_\u00A0Data\u0085", "_\u00A0Data\u2028", "_\u00A0Data\u2029"),
                new TestData("_\u1680.\n", "_\u1680.\r", "_\u1680.\n", "_\u1680.\r\n", "_\u1680.\u0085", "_\u1680.\u2028", "_\u1680.\u2029"),
                new TestData("_\u1680Data\n", "_\u1680Data\r", "_\u1680Data\n", "_\u1680Data\r\n", "_\u1680Data\u0085", "_\u1680Data\u2028", "_\u1680Data\u2029"),
                new TestData("Test .\n", "Test .\r", "Test .\n", "Test .\r\n", "Test .\u0085", "Test .\u2028", "Test .\u2029"),
                new TestData("Test Data\n", "Test Data\r", "Test Data\n", "Test Data\r\n", "Test Data\u0085", "Test Data\u2028", "Test Data\u2029"),
                new TestData("Test\t.\n", "Test\t.\r", "Test\t.\n", "Test\t.\r\n", "Test\t.\u0085", "Test\t.\u2028", "Test\t.\u2029"),
                new TestData("Test\tData\n", "Test\tData\r", "Test\tData\n", "Test\tData\r\n", "Test\tData\u0085", "Test\tData\u2028", "Test\tData\u2029"),
                new TestData("Test\u000B.\n", "Test\u000B.\r", "Test\u000B.\n", "Test\u000B.\r\n", "Test\u000B.\u0085", "Test\u000B.\u2028", "Test\u000B.\u2029"),
                new TestData("Test\u000BData\n", "Test\u000BData\r", "Test\u000BData\n", "Test\u000BData\r\n", "Test\u000BData\u0085", "Test\u000BData\u2028", "Test\u000BData\u2029"),
                new TestData("Test\f.\n", "Test\f.\r", "Test\f.\n", "Test\f.\r\n", "Test\f.\u0085", "Test\f.\u2028", "Test\f.\u2029"),
                new TestData("Test\fData\n", "Test\fData\r", "Test\fData\n", "Test\fData\r\n", "Test\fData\u0085", "Test\fData\u2028", "Test\fData\u2029"),
                new TestData("Test\u00A0.\n", "Test\u00A0.\r", "Test\u00A0.\n", "Test\u00A0.\r\n", "Test\u00A0.\u0085", "Test\u00A0.\u2028", "Test\u00A0.\u2029"),
                new TestData("Test\u00A0Data\n", "Test\u00A0Data\r", "Test\u00A0Data\n", "Test\u00A0Data\r\n", "Test\u00A0Data\u0085", "Test\u00A0Data\u2028", "Test\u00A0Data\u2029"),
                new TestData("Test\u1680.\n", "Test\u1680.\r", "Test\u1680.\n", "Test\u1680.\r\n", "Test\u1680.\u0085", "Test\u1680.\u2028", "Test\u1680.\u2029"),
                new TestData("Test\u1680Data\n", "Test\u1680Data\r", "Test\u1680Data\n", "Test\u1680Data\r\n", "Test\u1680Data\u0085", "Test\u1680Data\u2028", "Test\u1680Data\u2029"),
        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(u -> {
                StringLineIterator target = StringLineIterator.create(u.source, false, false);
                String description = u.description;
                assertTrue(description, target.hasNext());
                String line = target.next();
                String d = String.format("%s, index 0", description);
                Assert.assertNotNull(d, line);
                Assert.assertFalse(d, line.contains("\n"));
                StringBuilder result = new StringBuilder(line);
                int index = 0;
                while (target.hasNext()) {
                    line = target.next();
                    d = String.format("%s, index %d", description, ++index);
                    Assert.assertNotNull(d, line);
                    Assert.assertFalse(d, line.contains("\n"));
                    result.append("\n").append(line);
                }
                assertEquals(description, expected, result.toString());
            });
        }
    }

    @Test
    public void trimStartTest() {
        TestData[] testData = new TestData[]{
                new TestData("", " ", "\t", "\u000B", "\f", "\u00A0", "\u1680", "  ", " \t", " \u000B", " \f", " \u00A0", " \u1680", "\t ", "\t\t", "\t\u000B", "\t\f", "\t\u00A0", "\t\u1680", "\u000B ", "\u000B\t", "\u000B\u000B", "\u000B\f", "\u000B\u00A0", "\u000B\u1680", "\f ", "\f\t", "\f\u000B", "\f\f", "\f\u00A0", "\f\u1680", "\u00A0 ", "\u00A0\t", "\u00A0\u000B", "\u00A0\f", "\u00A0\u00A0", "\u00A0\u1680", "\u1680 ", "\u1680\t", "\u1680\u000B", "\u1680\f", "\u1680\u00A0", "\u1680\u1680"),
                new TestData("\n", "\r", "\n", "\r\n", "\u0085", "\u2028", "\u2029", " \r", " \n", " \r\n", " \u0085", " \u2028", " \u2029", "\t\r", "\t\n", "\t\r\n", "\t\u0085", "\t\u2028", "\t\u2029", "\u000B\r", "\u000B\n", "\u000B\r\n", "\u000B\u0085", "\u000B\u2028", "\u000B\u2029", "\f\r", "\f\n", "\f\r\n", "\f\u0085", "\f\u2028", "\f\u2029", "\u00A0\r", "\u00A0\n", "\u00A0\r\n", "\u00A0\u0085", "\u00A0\u2028", "\u00A0\u2029", "\u1680\r", "\u1680\n", "\u1680\r\n", "\u1680\u0085", "\u1680\u2028", "\u1680\u2029", "\r ", "\n ", "\r\n ", "\u0085 ", "\u2028 ", "\u2029 ", "\r\t", "\n\t", "\r\n\t", "\u0085\t", "\u2028\t", "\u2029\t", "\r\u000B", "\n\u000B", "\r\n\u000B", "\u0085\u000B", "\u2028\u000B", "\u2029\u000B", "\r\f", "\n\f", "\r\n\f", "\u0085\f", "\u2028\f", "\u2029\f", "\r\u00A0", "\n\u00A0", "\r\n\u00A0", "\u0085\u00A0", "\u2028\u00A0", "\u2029\u00A0", "\r\u1680", "\n\u1680", "\r\n\u1680", "\u0085\u1680", "\u2028\u1680", "\u2029\u1680", " \r ", " \n ", " \r\n ", " \u0085 ", " \u2028 ", " \u2029 ", "\t\r\t", "\t\n\t", "\t\r\n\t", "\t\u0085\t", "\t\u2028\t", "\t\u2029\t", "\u000B\r\u000B", "\u000B\n\u000B", "\u000B\r\n\u000B", "\u000B\u0085\u000B", "\u000B\u2028\u000B", "\u000B\u2029\u000B", "\f\r\f", "\f\n\f", "\f\r\n\f", "\f\u0085\f", "\f\u2028\f", "\f\u2029\f", "\u00A0\r\u00A0", "\u00A0\n\u00A0", "\u00A0\r\n\u00A0", "\u00A0\u0085\u00A0", "\u00A0\u2028\u00A0", "\u00A0\u2029\u00A0", "\u1680\r\u1680", "\u1680\n\u1680", "\u1680\r\n\u1680", "\u1680\u0085\u1680", "\u1680\u2028\u1680", "\u1680\u2029\u1680"),
                new TestData("_", "_", " _", "\t_", "\u000B_", "\f_", "\u00A0_", "\u1680_", "  _", "\t _", "\u000B _", "\f _", "\u00A0 _", "\u1680 _", " \t_", "\t\t_", "\u000B\t_", "\f\t_", "\u00A0\t_", "\u1680\t_", " \u000B_", "\t\u000B_", "\u000B\u000B_", "\f\u000B_", "\u00A0\u000B_", "\u1680\u000B_", " \f_", "\t\f_", "\u000B\f_", "\f\f_", "\u00A0\f_", "\u1680\f_", " \u00A0_", "\t\u00A0_", "\u000B\u00A0_", "\f\u00A0_", "\u00A0\u00A0_", "\u1680\u00A0_", " \u1680_", "\t\u1680_", "\u000B\u1680_", "\f\u1680_", "\u00A0\u1680_", "\u1680\u1680_"),
                new TestData("Test", "Test", " Test", "\tTest", "\u000BTest", "\fTest", "\u00A0Test", "\u1680Test", "  Test", "\t Test", "\u000B Test", "\f Test", "\u00A0 Test", "\u1680 Test", " \tTest", "\t\tTest", "\u000B\tTest", "\f\tTest", "\u00A0\tTest", "\u1680\tTest", " \u000BTest", "\t\u000BTest", "\u000B\u000BTest", "\f\u000BTest", "\u00A0\u000BTest", "\u1680\u000BTest", " \fTest", "\t\fTest", "\u000B\fTest", "\f\fTest", "\u00A0\fTest", "\u1680\fTest", " \u00A0Test", "\t\u00A0Test", "\u000B\u00A0Test", "\f\u00A0Test", "\u00A0\u00A0Test", "\u1680\u00A0Test", " \u1680Test", "\t\u1680Test", "\u000B\u1680Test", "\f\u1680Test", "\u00A0\u1680Test", "\u1680\u1680Test"),
                new TestData("_ ", "_ ", " _ "),
                new TestData("_\t", "_\t", "\t_\t"),
                new TestData("_\u000B", "_\u000B", "\u000B_\u000B"),
                new TestData("_\f", "_\f", "\f_\f"),
                new TestData("_\u00A0", "_\u00A0", "\u00A0_\u00A0"),
                new TestData("_\u1680", "_\u1680", "\u1680_\u1680"),
                new TestData("Test ", "Test ", " Test "),
                new TestData("Test\t", "Test\t", "\tTest\t"),
                new TestData("Test\u000B", "Test\u000B", "\u000BTest\u000B"),
                new TestData("Test\f", "Test\f", "\fTest\f"),
                new TestData("Test\u00A0", "Test\u00A0", "\u00A0Test\u00A0"),
                new TestData("Test\u1680", "Test\u1680", "\u1680Test\u1680"),
                new TestData("_\n", "_\r", "_\n", "_\r\n", "_\u0085", "_\u2028", "_\u2029"),
                new TestData("Test\n", "Test\r", "Test\n", "Test\r\n", "Test\u0085", "Test\u2028", "Test\u2029"),
                new TestData("\n_", "\r_", "\n_", "\r\n_", "\u0085_", "\u2028_", "\u2029_", "\r _", "\n _", "\r\n _", "\u0085 _", "\u2028 _", "\u2029 _", "\r\t_", "\n\t_", "\r\n\t_", "\u0085\t_", "\u2028\t_", "\u2029\t_", "\r\u000B_", "\n\u000B_", "\r\n\u000B_", "\u0085\u000B_", "\u2028\u000B_", "\u2029\u000B_", "\r\f_", "\n\f_", "\r\n\f_", "\u0085\f_", "\u2028\f_", "\u2029\f_", "\r\u00A0_", "\n\u00A0_", "\r\n\u00A0_", "\u0085\u00A0_", "\u2028\u00A0_", "\u2029\u00A0_", "\r\u1680_", "\n\u1680_", "\r\n\u1680_", "\u0085\u1680_", "\u2028\u1680_", "\u2029\u1680_", " \r_", "\t\r_", "\u000B\r_", "\f\r_", "\u00A0\r_", "\u1680\r_", " \n_", "\t\n_", "\u000B\n_", "\f\n_", "\u00A0\n_", "\u1680\n_", " \r\n_", "\t\r\n_", "\u000B\r\n_", "\f\r\n_", "\u00A0\r\n_", "\u1680\r\n_", " \u0085_", "\t\u0085_", "\u000B\u0085_", "\f\u0085_", "\u00A0\u0085_", "\u1680\u0085_", " \u2028_", "\t\u2028_", "\u000B\u2028_", "\f\u2028_", "\u00A0\u2028_", "\u1680\u2028_", " \u2029_", "\t\u2029_", "\u000B\u2029_", "\f\u2029_", "\u00A0\u2029_", "\u1680\u2029_"),
                new TestData("\nTest", "\rTest", "\nTest", "\r\nTest", "\u0085Test", "\u2028Test", "\u2029Test", "\r Test", "\n Test", "\r\n Test", "\u0085 Test", "\u2028 Test", "\u2029 Test", "\r\tTest", "\n\tTest", "\r\n\tTest", "\u0085\tTest", "\u2028\tTest", "\u2029\tTest", "\r\u000BTest", "\n\u000BTest", "\r\n\u000BTest", "\u0085\u000BTest", "\u2028\u000BTest", "\u2029\u000BTest", "\r\fTest", "\n\fTest", "\r\n\fTest", "\u0085\fTest", "\u2028\fTest", "\u2029\fTest", "\r\u00A0Test", "\n\u00A0Test", "\r\n\u00A0Test", "\u0085\u00A0Test", "\u2028\u00A0Test", "\u2029\u00A0Test", "\r\u1680Test", "\n\u1680Test", "\r\n\u1680Test", "\u0085\u1680Test", "\u2028\u1680Test", "\u2029\u1680Test", " \rTest", "\t\rTest", "\u000B\rTest", "\f\rTest", "\u00A0\rTest", "\u1680\rTest", " \nTest", "\t\nTest", "\u000B\nTest", "\f\nTest", "\u00A0\nTest", "\u1680\nTest", " \r\nTest", "\t\r\nTest", "\u000B\r\nTest", "\f\r\nTest", "\u00A0\r\nTest", "\u1680\r\nTest", " \u0085Test", "\t\u0085Test", "\u000B\u0085Test", "\f\u0085Test", "\u00A0\u0085Test", "\u1680\u0085Test", " \u2028Test", "\t\u2028Test", "\u000B\u2028Test", "\f\u2028Test", "\u00A0\u2028Test", "\u1680\u2028Test", " \u2029Test", "\t\u2029Test", "\u000B\u2029Test", "\f\u2029Test", "\u00A0\u2029Test", "\u1680\u2029Test"),
                new TestData("\n\n", "\r\r", "\r\r\n", "\r\u0085", "\r\u2028", "\r\u2029", "\n\r", "\n\n", "\n\r\n", "\n\u0085", "\n\u2028", "\n\u2029", "\r\n\r", "\r\n\n", "\r\n\r\n", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\u0085\r", "\u0085\n", "\u0085\r\n", "\u0085\u0085", "\u0085\u2028", "\u0085\u2029", "\u2028\r", "\u2028\n", "\u2028\r\n", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\r", "\u2029\n", "\u2029\r\n", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029", "\r \r", "\n \n", "\r\n \r\n", "\u0085 \u0085", "\u2028 \u2028", "\u2029 \u2029", "\r\t\r", "\n\t\n", "\r\n\t\r\n", "\u0085\t\u0085", "\u2028\t\u2028", "\u2029\t\u2029", "\r\u000B\r", "\n\u000B\n", "\r\n\u000B\r\n", "\u0085\u000B\u0085", "\u2028\u000B\u2028", "\u2029\u000B\u2029", "\r\f\r", "\n\f\n", "\r\n\f\r\n", "\u0085\f\u0085", "\u2028\f\u2028", "\u2029\f\u2029", "\r\u00A0\r", "\n\u00A0\n", "\r\n\u00A0\r\n", "\u0085\u00A0\u0085", "\u2028\u00A0\u2028", "\u2029\u00A0\u2029", "\r\u1680\r", "\n\u1680\n", "\r\n\u1680\r\n", "\u0085\u1680\u0085", "\u2028\u1680\u2028", "\u2029\u1680\u2029"),
                new TestData("_ .", "_ ."),
                new TestData("_ Data", "_ Data"),
                new TestData("_\t.", "_\t."),
                new TestData("_\tData", "_\tData"),
                new TestData("_\u000B.", "_\u000B."),
                new TestData("_\u000BData", "_\u000BData"),
                new TestData("_\f.", "_\f."),
                new TestData("_\fData", "_\fData"),
                new TestData("_\u00A0.", "_\u00A0."),
                new TestData("_\u00A0Data", "_\u00A0Data"),
                new TestData("_\u1680.", "_\u1680."),
                new TestData("_\u1680Data", "_\u1680Data"),
                new TestData("Test .", "Test ."),
                new TestData("Test Data", "Test Data"),
                new TestData("Test\t.", "Test\t."),
                new TestData("Test\tData", "Test\tData"),
                new TestData("Test\u000B.", "Test\u000B."),
                new TestData("Test\u000BData", "Test\u000BData"),
                new TestData("Test\f.", "Test\f."),
                new TestData("Test\fData", "Test\fData"),
                new TestData("Test\u00A0.", "Test\u00A0."),
                new TestData("Test\u00A0Data", "Test\u00A0Data"),
                new TestData("Test\u1680.", "Test\u1680."),
                new TestData("Test\u1680Data", "Test\u1680Data"),
                new TestData("\n_\n", "\r_\r", "\n_\n", "\r\n_\r\n", "\u0085_\u0085", "\u2028_\u2028", "\u2029_\u2029", " \r_\r ", "\t\r_\r\t", "\u000B\r_\r\u000B", "\f\r_\r\f", "\u00A0\r_\r\u00A0", "\u1680\r_\r\u1680", " \n_\n ", "\t\n_\n\t", "\u000B\n_\n\u000B", "\f\n_\n\f", "\u00A0\n_\n\u00A0", "\u1680\n_\n\u1680", " \r\n_\r\n ", "\t\r\n_\r\n\t", "\u000B\r\n_\r\n\u000B", "\f\r\n_\r\n\f", "\u00A0\r\n_\r\n\u00A0", "\u1680\r\n_\r\n\u1680", " \u0085_\u0085 ", "\t\u0085_\u0085\t", "\u000B\u0085_\u0085\u000B", "\f\u0085_\u0085\f", "\u00A0\u0085_\u0085\u00A0", "\u1680\u0085_\u0085\u1680", " \u2028_\u2028 ", "\t\u2028_\u2028\t", "\u000B\u2028_\u2028\u000B", "\f\u2028_\u2028\f", "\u00A0\u2028_\u2028\u00A0", "\u1680\u2028_\u2028\u1680", " \u2029_\u2029 ", "\t\u2029_\u2029\t", "\u000B\u2029_\u2029\u000B", "\f\u2029_\u2029\f", "\u00A0\u2029_\u2029\u00A0", "\u1680\u2029_\u2029\u1680"),
                new TestData("\nTest\n", "\rTest\r", "\nTest\n", "\r\nTest\r\n", "\u0085Test\u0085", "\u2028Test\u2028", "\u2029Test\u2029", " \rTest\r ", "\t\rTest\r\t", "\u000B\rTest\r\u000B", "\f\rTest\r\f", "\u00A0\rTest\r\u00A0", "\u1680\rTest\r\u1680", " \nTest\n ", "\t\nTest\n\t", "\u000B\nTest\n\u000B", "\f\nTest\n\f", "\u00A0\nTest\n\u00A0", "\u1680\nTest\n\u1680", " \r\nTest\r\n ", "\t\r\nTest\r\n\t", "\u000B\r\nTest\r\n\u000B", "\f\r\nTest\r\n\f", "\u00A0\r\nTest\r\n\u00A0", "\u1680\r\nTest\r\n\u1680", " \u0085Test\u0085 ", "\t\u0085Test\u0085\t", "\u000B\u0085Test\u0085\u000B", "\f\u0085Test\u0085\f", "\u00A0\u0085Test\u0085\u00A0", "\u1680\u0085Test\u0085\u1680", " \u2028Test\u2028 ", "\t\u2028Test\u2028\t", "\u000B\u2028Test\u2028\u000B", "\f\u2028Test\u2028\f", "\u00A0\u2028Test\u2028\u00A0", "\u1680\u2028Test\u2028\u1680", " \u2029Test\u2029 ", "\t\u2029Test\u2029\t", "\u000B\u2029Test\u2029\u000B", "\f\u2029Test\u2029\f", "\u00A0\u2029Test\u2029\u00A0", "\u1680\u2029Test\u2029\u1680"),
                new TestData("_ \n.", "_ \r.", "_ \n.", "_ \r\n.", "_ \u0085.", "_ \u2028.", "_ \u2029."),
                new TestData("_ \nData", "_ \rData", "_ \nData", "_ \r\nData", "_ \u0085Data", "_ \u2028Data", "_ \u2029Data"),
                new TestData("_\t\n.", "_\t\r.", "_\t\n.", "_\t\r\n.", "_\t\u0085.", "_\t\u2028.", "_\t\u2029."),
                new TestData("_\t\nData", "_\t\rData", "_\t\nData", "_\t\r\nData", "_\t\u0085Data", "_\t\u2028Data", "_\t\u2029Data"),
                new TestData("_\u000B\n.", "_\u000B\r.", "_\u000B\n.", "_\u000B\r\n.", "_\u000B\u0085.", "_\u000B\u2028.", "_\u000B\u2029."),
                new TestData("_\u000B\nData", "_\u000B\rData", "_\u000B\nData", "_\u000B\r\nData", "_\u000B\u0085Data", "_\u000B\u2028Data", "_\u000B\u2029Data"),
                new TestData("_\f\n.", "_\f\r.", "_\f\n.", "_\f\r\n.", "_\f\u0085.", "_\f\u2028.", "_\f\u2029."),
                new TestData("_\f\nData", "_\f\rData", "_\f\nData", "_\f\r\nData", "_\f\u0085Data", "_\f\u2028Data", "_\f\u2029Data"),
                new TestData("_\u00A0\n.", "_\u00A0\r.", "_\u00A0\n.", "_\u00A0\r\n.", "_\u00A0\u0085.", "_\u00A0\u2028.", "_\u00A0\u2029."),
                new TestData("_\u00A0\nData", "_\u00A0\rData", "_\u00A0\nData", "_\u00A0\r\nData", "_\u00A0\u0085Data", "_\u00A0\u2028Data", "_\u00A0\u2029Data"),
                new TestData("_\u1680\n.", "_\u1680\r.", "_\u1680\n.", "_\u1680\r\n.", "_\u1680\u0085.", "_\u1680\u2028.", "_\u1680\u2029."),
                new TestData("_\u1680\nData", "_\u1680\rData", "_\u1680\nData", "_\u1680\r\nData", "_\u1680\u0085Data", "_\u1680\u2028Data", "_\u1680\u2029Data"),
                new TestData("Test \n.", "Test \r.", "Test \n.", "Test \r\n.", "Test \u0085.", "Test \u2028.", "Test \u2029."),
                new TestData("Test \nData", "Test \rData", "Test \nData", "Test \r\nData", "Test \u0085Data", "Test \u2028Data", "Test \u2029Data"),
                new TestData("Test\t\n.", "Test\t\r.", "Test\t\n.", "Test\t\r\n.", "Test\t\u0085.", "Test\t\u2028.", "Test\t\u2029."),
                new TestData("Test\t\nData", "Test\t\rData", "Test\t\nData", "Test\t\r\nData", "Test\t\u0085Data", "Test\t\u2028Data", "Test\t\u2029Data"),
                new TestData("Test\u000B\n.", "Test\u000B\r.", "Test\u000B\n.", "Test\u000B\r\n.", "Test\u000B\u0085.", "Test\u000B\u2028.", "Test\u000B\u2029."),
                new TestData("Test\u000B\nData", "Test\u000B\rData", "Test\u000B\nData", "Test\u000B\r\nData", "Test\u000B\u0085Data", "Test\u000B\u2028Data", "Test\u000B\u2029Data"),
                new TestData("Test\f\n.", "Test\f\r.", "Test\f\n.", "Test\f\r\n.", "Test\f\u0085.", "Test\f\u2028.", "Test\f\u2029."),
                new TestData("Test\f\nData", "Test\f\rData", "Test\f\nData", "Test\f\r\nData", "Test\f\u0085Data", "Test\f\u2028Data", "Test\f\u2029Data"),
                new TestData("Test\u00A0\n.", "Test\u00A0\r.", "Test\u00A0\n.", "Test\u00A0\r\n.", "Test\u00A0\u0085.", "Test\u00A0\u2028.", "Test\u00A0\u2029."),
                new TestData("Test\u00A0\nData", "Test\u00A0\rData", "Test\u00A0\nData", "Test\u00A0\r\nData", "Test\u00A0\u0085Data", "Test\u00A0\u2028Data", "Test\u00A0\u2029Data"),
                new TestData("Test\u1680\n.", "Test\u1680\r.", "Test\u1680\n.", "Test\u1680\r\n.", "Test\u1680\u0085.", "Test\u1680\u2028.", "Test\u1680\u2029."),
                new TestData("Test\u1680\nData", "Test\u1680\rData", "Test\u1680\nData", "Test\u1680\r\nData", "Test\u1680\u0085Data", "Test\u1680\u2028Data", "Test\u1680\u2029Data"),
                new TestData(".\n_", ".\r _", ".\n _", ".\r\n _", ".\u0085 _", ".\u2028 _", ".\u2029 _", ".\r\t_", ".\n\t_", ".\r\n\t_", ".\u0085\t_", ".\u2028\t_", ".\u2029\t_", ".\r\u000B_", ".\n\u000B_", ".\r\n\u000B_", ".\u0085\u000B_", ".\u2028\u000B_", ".\u2029\u000B_", ".\r\f_", ".\n\f_", ".\r\n\f_", ".\u0085\f_", ".\u2028\f_", ".\u2029\f_", ".\r\u00A0_", ".\n\u00A0_", ".\r\n\u00A0_", ".\u0085\u00A0_", ".\u2028\u00A0_", ".\u2029\u00A0_", ".\r\u1680_", ".\n\u1680_", ".\r\n\u1680_", ".\u0085\u1680_", ".\u2028\u1680_", ".\u2029\u1680_"),
                new TestData("Data\n_", "Data\r _", "Data\n _", "Data\r\n _", "Data\u0085 _", "Data\u2028 _", "Data\u2029 _", "Data\r\t_", "Data\n\t_", "Data\r\n\t_", "Data\u0085\t_", "Data\u2028\t_", "Data\u2029\t_", "Data\r\u000B_", "Data\n\u000B_", "Data\r\n\u000B_", "Data\u0085\u000B_", "Data\u2028\u000B_", "Data\u2029\u000B_", "Data\r\f_", "Data\n\f_", "Data\r\n\f_", "Data\u0085\f_", "Data\u2028\f_", "Data\u2029\f_", "Data\r\u00A0_", "Data\n\u00A0_", "Data\r\n\u00A0_", "Data\u0085\u00A0_", "Data\u2028\u00A0_", "Data\u2029\u00A0_", "Data\r\u1680_", "Data\n\u1680_", "Data\r\n\u1680_", "Data\u0085\u1680_", "Data\u2028\u1680_", "Data\u2029\u1680_"),
                new TestData(".\nTest", ".\r Test", ".\n Test", ".\r\n Test", ".\u0085 Test", ".\u2028 Test", ".\u2029 Test", ".\r\tTest", ".\n\tTest", ".\r\n\tTest", ".\u0085\tTest", ".\u2028\tTest", ".\u2029\tTest", ".\r\u000BTest", ".\n\u000BTest", ".\r\n\u000BTest", ".\u0085\u000BTest", ".\u2028\u000BTest", ".\u2029\u000BTest", ".\r\fTest", ".\n\fTest", ".\r\n\fTest", ".\u0085\fTest", ".\u2028\fTest", ".\u2029\fTest", ".\r\u00A0Test", ".\n\u00A0Test", ".\r\n\u00A0Test", ".\u0085\u00A0Test", ".\u2028\u00A0Test", ".\u2029\u00A0Test", ".\r\u1680Test", ".\n\u1680Test", ".\r\n\u1680Test", ".\u0085\u1680Test", ".\u2028\u1680Test", ".\u2029\u1680Test"),
                new TestData("Data\nTest", "Data\r Test", "Data\n Test", "Data\r\n Test", "Data\u0085 Test", "Data\u2028 Test", "Data\u2029 Test", "Data\r\tTest", "Data\n\tTest", "Data\r\n\tTest", "Data\u0085\tTest", "Data\u2028\tTest", "Data\u2029\tTest", "Data\r\u000BTest", "Data\n\u000BTest", "Data\r\n\u000BTest", "Data\u0085\u000BTest", "Data\u2028\u000BTest", "Data\u2029\u000BTest", "Data\r\fTest", "Data\n\fTest", "Data\r\n\fTest", "Data\u0085\fTest", "Data\u2028\fTest", "Data\u2029\fTest", "Data\r\u00A0Test", "Data\n\u00A0Test", "Data\r\n\u00A0Test", "Data\u0085\u00A0Test", "Data\u2028\u00A0Test", "Data\u2029\u00A0Test", "Data\r\u1680Test", "Data\n\u1680Test", "Data\r\n\u1680Test", "Data\u0085\u1680Test", "Data\u2028\u1680Test", "Data\u2029\u1680Test"),
                new TestData("\n_ \n", "\r _ \r", "\n _ \n", "\r\n _ \r\n", "\u0085 _ \u0085", "\u2028 _ \u2028", "\u2029 _ \u2029"),
                new TestData("\n_\t\n", "\r\t_\t\r", "\n\t_\t\n", "\r\n\t_\t\r\n", "\u0085\t_\t\u0085", "\u2028\t_\t\u2028", "\u2029\t_\t\u2029"),
                new TestData("\n_\u000B\n", "\r\u000B_\u000B\r", "\n\u000B_\u000B\n", "\r\n\u000B_\u000B\r\n", "\u0085\u000B_\u000B\u0085", "\u2028\u000B_\u000B\u2028", "\u2029\u000B_\u000B\u2029"),
                new TestData("\n_\f\n", "\r\f_\f\r", "\n\f_\f\n", "\r\n\f_\f\r\n", "\u0085\f_\f\u0085", "\u2028\f_\f\u2028", "\u2029\f_\f\u2029"),
                new TestData("\n_\u00A0\n", "\r\u00A0_\u00A0\r", "\n\u00A0_\u00A0\n", "\r\n\u00A0_\u00A0\r\n", "\u0085\u00A0_\u00A0\u0085", "\u2028\u00A0_\u00A0\u2028", "\u2029\u00A0_\u00A0\u2029"),
                new TestData("\n_\u1680\n", "\r\u1680_\u1680\r", "\n\u1680_\u1680\n", "\r\n\u1680_\u1680\r\n", "\u0085\u1680_\u1680\u0085", "\u2028\u1680_\u1680\u2028", "\u2029\u1680_\u1680\u2029"),
                new TestData("\nTest \n", "\r Test \r", "\n Test \n", "\r\n Test \r\n", "\u0085 Test \u0085", "\u2028 Test \u2028", "\u2029 Test \u2029"),
                new TestData("\nTest\t\n", "\r\tTest\t\r", "\n\tTest\t\n", "\r\n\tTest\t\r\n", "\u0085\tTest\t\u0085", "\u2028\tTest\t\u2028", "\u2029\tTest\t\u2029"),
                new TestData("\nTest\u000B\n", "\r\u000BTest\u000B\r", "\n\u000BTest\u000B\n", "\r\n\u000BTest\u000B\r\n", "\u0085\u000BTest\u000B\u0085", "\u2028\u000BTest\u000B\u2028", "\u2029\u000BTest\u000B\u2029"),
                new TestData("\nTest\f\n", "\r\fTest\f\r", "\n\fTest\f\n", "\r\n\fTest\f\r\n", "\u0085\fTest\f\u0085", "\u2028\fTest\f\u2028", "\u2029\fTest\f\u2029"),
                new TestData("\nTest\u00A0\n", "\r\u00A0Test\u00A0\r", "\n\u00A0Test\u00A0\n", "\r\n\u00A0Test\u00A0\r\n", "\u0085\u00A0Test\u00A0\u0085", "\u2028\u00A0Test\u00A0\u2028", "\u2029\u00A0Test\u00A0\u2029"),
                new TestData("\nTest\u1680\n", "\r\u1680Test\u1680\r", "\n\u1680Test\u1680\n", "\r\n\u1680Test\u1680\r\n", "\u0085\u1680Test\u1680\u0085", "\u2028\u1680Test\u1680\u2028", "\u2029\u1680Test\u1680\u2029"),
                new TestData("_ \n", "_ \r", "_ \n", "_ \r\n", "_ \u0085", "_ \u2028", "_ \u2029"),
                new TestData("_\t\n", "_\t\r", "_\t\n", "_\t\r\n", "_\t\u0085", "_\t\u2028", "_\t\u2029"),
                new TestData("_\u000B\n", "_\u000B\r", "_\u000B\n", "_\u000B\r\n", "_\u000B\u0085", "_\u000B\u2028", "_\u000B\u2029"),
                new TestData("_\f\n", "_\f\r", "_\f\n", "_\f\r\n", "_\f\u0085", "_\f\u2028", "_\f\u2029"),
                new TestData("_\u00A0\n", "_\u00A0\r", "_\u00A0\n", "_\u00A0\r\n", "_\u00A0\u0085", "_\u00A0\u2028", "_\u00A0\u2029"),
                new TestData("_\u1680\n", "_\u1680\r", "_\u1680\n", "_\u1680\r\n", "_\u1680\u0085", "_\u1680\u2028", "_\u1680\u2029"),
                new TestData("Test \n", "Test \r", "Test \n", "Test \r\n", "Test \u0085", "Test \u2028", "Test \u2029"),
                new TestData("Test\t\n", "Test\t\r", "Test\t\n", "Test\t\r\n", "Test\t\u0085", "Test\t\u2028", "Test\t\u2029"),
                new TestData("Test\u000B\n", "Test\u000B\r", "Test\u000B\n", "Test\u000B\r\n", "Test\u000B\u0085", "Test\u000B\u2028", "Test\u000B\u2029"),
                new TestData("Test\f\n", "Test\f\r", "Test\f\n", "Test\f\r\n", "Test\f\u0085", "Test\f\u2028", "Test\f\u2029"),
                new TestData("Test\u00A0\n", "Test\u00A0\r", "Test\u00A0\n", "Test\u00A0\r\n", "Test\u00A0\u0085", "Test\u00A0\u2028", "Test\u00A0\u2029"),
                new TestData("Test\u1680\n", "Test\u1680\r", "Test\u1680\n", "Test\u1680\r\n", "Test\u1680\u0085", "Test\u1680\u2028", "Test\u1680\u2029"),
                new TestData("\n\n_", "\r\r_", "\n\r_", "\r\n\r_", "\u0085\r_", "\u2028\r_", "\u2029\r_", "\n\n_", "\r\n\n_", "\u0085\n_", "\u2028\n_", "\u2029\n_", "\r\r\n_", "\n\r\n_", "\r\n\r\n_", "\u0085\r\n_", "\u2028\r\n_", "\u2029\r\n_", "\r\u0085_", "\n\u0085_", "\r\n\u0085_", "\u0085\u0085_", "\u2028\u0085_", "\u2029\u0085_", "\r\u2028_", "\n\u2028_", "\r\n\u2028_", "\u0085\u2028_", "\u2028\u2028_", "\u2029\u2028_", "\r\u2029_", "\n\u2029_", "\r\n\u2029_", "\u0085\u2029_", "\u2028\u2029_", "\u2029\u2029_"),
                new TestData("\n\nTest", "\r\rTest", "\n\rTest", "\r\n\rTest", "\u0085\rTest", "\u2028\rTest", "\u2029\rTest", "\n\nTest", "\r\n\nTest", "\u0085\nTest", "\u2028\nTest", "\u2029\nTest", "\r\r\nTest", "\n\r\nTest", "\r\n\r\nTest", "\u0085\r\nTest", "\u2028\r\nTest", "\u2029\r\nTest", "\r\u0085Test", "\n\u0085Test", "\r\n\u0085Test", "\u0085\u0085Test", "\u2028\u0085Test", "\u2029\u0085Test", "\r\u2028Test", "\n\u2028Test", "\r\n\u2028Test", "\u0085\u2028Test", "\u2028\u2028Test", "\u2029\u2028Test", "\r\u2029Test", "\n\u2029Test", "\r\n\u2029Test", "\u0085\u2029Test", "\u2028\u2029Test", "\u2029\u2029Test"),
                new TestData("_ .\n", "_ .\r", "_ .\n", "_ .\r\n", "_ .\u0085", "_ .\u2028", "_ .\u2029"),
                new TestData("_ Data\n", "_ Data\r", "_ Data\n", "_ Data\r\n", "_ Data\u0085", "_ Data\u2028", "_ Data\u2029"),
                new TestData("_\t.\n", "_\t.\r", "_\t.\n", "_\t.\r\n", "_\t.\u0085", "_\t.\u2028", "_\t.\u2029"),
                new TestData("_\tData\n", "_\tData\r", "_\tData\n", "_\tData\r\n", "_\tData\u0085", "_\tData\u2028", "_\tData\u2029"),
                new TestData("_\u000B.\n", "_\u000B.\r", "_\u000B.\n", "_\u000B.\r\n", "_\u000B.\u0085", "_\u000B.\u2028", "_\u000B.\u2029"),
                new TestData("_\u000BData\n", "_\u000BData\r", "_\u000BData\n", "_\u000BData\r\n", "_\u000BData\u0085", "_\u000BData\u2028", "_\u000BData\u2029"),
                new TestData("_\f.\n", "_\f.\r", "_\f.\n", "_\f.\r\n", "_\f.\u0085", "_\f.\u2028", "_\f.\u2029"),
                new TestData("_\fData\n", "_\fData\r", "_\fData\n", "_\fData\r\n", "_\fData\u0085", "_\fData\u2028", "_\fData\u2029"),
                new TestData("_\u00A0.\n", "_\u00A0.\r", "_\u00A0.\n", "_\u00A0.\r\n", "_\u00A0.\u0085", "_\u00A0.\u2028", "_\u00A0.\u2029"),
                new TestData("_\u00A0Data\n", "_\u00A0Data\r", "_\u00A0Data\n", "_\u00A0Data\r\n", "_\u00A0Data\u0085", "_\u00A0Data\u2028", "_\u00A0Data\u2029"),
                new TestData("_\u1680.\n", "_\u1680.\r", "_\u1680.\n", "_\u1680.\r\n", "_\u1680.\u0085", "_\u1680.\u2028", "_\u1680.\u2029"),
                new TestData("_\u1680Data\n", "_\u1680Data\r", "_\u1680Data\n", "_\u1680Data\r\n", "_\u1680Data\u0085", "_\u1680Data\u2028", "_\u1680Data\u2029"),
                new TestData("Test .\n", "Test .\r", "Test .\n", "Test .\r\n", "Test .\u0085", "Test .\u2028", "Test .\u2029"),
                new TestData("Test Data\n", "Test Data\r", "Test Data\n", "Test Data\r\n", "Test Data\u0085", "Test Data\u2028", "Test Data\u2029"),
                new TestData("Test\t.\n", "Test\t.\r", "Test\t.\n", "Test\t.\r\n", "Test\t.\u0085", "Test\t.\u2028", "Test\t.\u2029"),
                new TestData("Test\tData\n", "Test\tData\r", "Test\tData\n", "Test\tData\r\n", "Test\tData\u0085", "Test\tData\u2028", "Test\tData\u2029"),
                new TestData("Test\u000B.\n", "Test\u000B.\r", "Test\u000B.\n", "Test\u000B.\r\n", "Test\u000B.\u0085", "Test\u000B.\u2028", "Test\u000B.\u2029"),
                new TestData("Test\u000BData\n", "Test\u000BData\r", "Test\u000BData\n", "Test\u000BData\r\n", "Test\u000BData\u0085", "Test\u000BData\u2028", "Test\u000BData\u2029"),
                new TestData("Test\f.\n", "Test\f.\r", "Test\f.\n", "Test\f.\r\n", "Test\f.\u0085", "Test\f.\u2028", "Test\f.\u2029"),
                new TestData("Test\fData\n", "Test\fData\r", "Test\fData\n", "Test\fData\r\n", "Test\fData\u0085", "Test\fData\u2028", "Test\fData\u2029"),
                new TestData("Test\u00A0.\n", "Test\u00A0.\r", "Test\u00A0.\n", "Test\u00A0.\r\n", "Test\u00A0.\u0085", "Test\u00A0.\u2028", "Test\u00A0.\u2029"),
                new TestData("Test\u00A0Data\n", "Test\u00A0Data\r", "Test\u00A0Data\n", "Test\u00A0Data\r\n", "Test\u00A0Data\u0085", "Test\u00A0Data\u2028", "Test\u00A0Data\u2029"),
                new TestData("Test\u1680.\n", "Test\u1680.\r", "Test\u1680.\n", "Test\u1680.\r\n", "Test\u1680.\u0085", "Test\u1680.\u2028", "Test\u1680.\u2029"),
                new TestData("Test\u1680Data\n", "Test\u1680Data\r", "Test\u1680Data\n", "Test\u1680Data\r\n", "Test\u1680Data\u0085", "Test\u1680Data\u2028", "Test\u1680Data\u2029"),
        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(u -> {
                StringLineIterator target = StringLineIterator.create(u.source, false, false);
                String description = u.description;
                assertTrue(description, target.hasNext());
                String line = target.next();
                String d = String.format("%s, index 0", description);
                Assert.assertNotNull(d, line);
                Assert.assertFalse(d, line.contains("\n"));
                StringBuilder result = new StringBuilder(line);
                int index = 0;
                while (target.hasNext()) {
                    line = target.next();
                    d = String.format("%s, index %d", description, ++index);
                    Assert.assertNotNull(d, line);
                    Assert.assertFalse(d, line.contains("\n"));
                    result.append("\n").append(line);
                }
                assertEquals(description, expected, result.toString());
            });
        }
    }

    @Test
    public void trimEndTest() {
        TestData[] testData = new TestData[]{
                new TestData("", " ", "\t", "\u000B", "\f", "\u00A0", "\u1680", "  ", " \t", " \u000B", " \f", " \u00A0", " \u1680", "\t ", "\t\t", "\t\u000B", "\t\f", "\t\u00A0", "\t\u1680", "\u000B ", "\u000B\t", "\u000B\u000B", "\u000B\f", "\u000B\u00A0", "\u000B\u1680", "\f ", "\f\t", "\f\u000B", "\f\f", "\f\u00A0", "\f\u1680", "\u00A0 ", "\u00A0\t", "\u00A0\u000B", "\u00A0\f", "\u00A0\u00A0", "\u00A0\u1680", "\u1680 ", "\u1680\t", "\u1680\u000B", "\u1680\f", "\u1680\u00A0", "\u1680\u1680"),
                new TestData("\n", "\r", "\n", "\r\n", "\u0085", "\u2028", "\u2029", " \r", " \n", " \r\n", " \u0085", " \u2028", " \u2029", "\t\r", "\t\n", "\t\r\n", "\t\u0085", "\t\u2028", "\t\u2029", "\u000B\r", "\u000B\n", "\u000B\r\n", "\u000B\u0085", "\u000B\u2028", "\u000B\u2029", "\f\r", "\f\n", "\f\r\n", "\f\u0085", "\f\u2028", "\f\u2029", "\u00A0\r", "\u00A0\n", "\u00A0\r\n", "\u00A0\u0085", "\u00A0\u2028", "\u00A0\u2029", "\u1680\r", "\u1680\n", "\u1680\r\n", "\u1680\u0085", "\u1680\u2028", "\u1680\u2029", "\r ", "\n ", "\r\n ", "\u0085 ", "\u2028 ", "\u2029 ", "\r\t", "\n\t", "\r\n\t", "\u0085\t", "\u2028\t", "\u2029\t", "\r\u000B", "\n\u000B", "\r\n\u000B", "\u0085\u000B", "\u2028\u000B", "\u2029\u000B", "\r\f", "\n\f", "\r\n\f", "\u0085\f", "\u2028\f", "\u2029\f", "\r\u00A0", "\n\u00A0", "\r\n\u00A0", "\u0085\u00A0", "\u2028\u00A0", "\u2029\u00A0", "\r\u1680", "\n\u1680", "\r\n\u1680", "\u0085\u1680", "\u2028\u1680", "\u2029\u1680", " \r ", " \n ", " \r\n ", " \u0085 ", " \u2028 ", " \u2029 ", "\t\r\t", "\t\n\t", "\t\r\n\t", "\t\u0085\t", "\t\u2028\t", "\t\u2029\t", "\u000B\r\u000B", "\u000B\n\u000B", "\u000B\r\n\u000B", "\u000B\u0085\u000B", "\u000B\u2028\u000B", "\u000B\u2029\u000B", "\f\r\f", "\f\n\f", "\f\r\n\f", "\f\u0085\f", "\f\u2028\f", "\f\u2029\f", "\u00A0\r\u00A0", "\u00A0\n\u00A0", "\u00A0\r\n\u00A0", "\u00A0\u0085\u00A0", "\u00A0\u2028\u00A0", "\u00A0\u2029\u00A0", "\u1680\r\u1680", "\u1680\n\u1680", "\u1680\r\n\u1680", "\u1680\u0085\u1680", "\u1680\u2028\u1680", "\u1680\u2029\u1680"),
                new TestData("_", "_", "_ ", "_\t", "_\u000B", "_\f", "_\u00A0", "_\u1680", "_  ", "_ \t", "_ \u000B", "_ \f", "_ \u00A0", "_ \u1680", "_\t ", "_\t\t", "_\t\u000B", "_\t\f", "_\t\u00A0", "_\t\u1680", "_\u000B ", "_\u000B\t", "_\u000B\u000B", "_\u000B\f", "_\u000B\u00A0", "_\u000B\u1680", "_\f ", "_\f\t", "_\f\u000B", "_\f\f", "_\f\u00A0", "_\f\u1680", "_\u00A0 ", "_\u00A0\t", "_\u00A0\u000B", "_\u00A0\f", "_\u00A0\u00A0", "_\u00A0\u1680", "_\u1680 ", "_\u1680\t", "_\u1680\u000B", "_\u1680\f", "_\u1680\u00A0", "_\u1680\u1680"),
                new TestData("Test", "Test", "Test ", "Test\t", "Test\u000B", "Test\f", "Test\u00A0", "Test\u1680", "Test  ", "Test \t", "Test \u000B", "Test \f", "Test \u00A0", "Test \u1680", "Test\t ", "Test\t\t", "Test\t\u000B", "Test\t\f", "Test\t\u00A0", "Test\t\u1680", "Test\u000B ", "Test\u000B\t", "Test\u000B\u000B", "Test\u000B\f", "Test\u000B\u00A0", "Test\u000B\u1680", "Test\f ", "Test\f\t", "Test\f\u000B", "Test\f\f", "Test\f\u00A0", "Test\f\u1680", "Test\u00A0 ", "Test\u00A0\t", "Test\u00A0\u000B", "Test\u00A0\f", "Test\u00A0\u00A0", "Test\u00A0\u1680", "Test\u1680 ", "Test\u1680\t", "Test\u1680\u000B", "Test\u1680\f", "Test\u1680\u00A0", "Test\u1680\u1680"),
                new TestData("_\n", "_\r", "_\n", "_\r\n", "_\u0085", "_\u2028", "_\u2029", "_ \r", "_ \n", "_ \r\n", "_ \u0085", "_ \u2028", "_ \u2029", "_\t\r", "_\t\n", "_\t\r\n", "_\t\u0085", "_\t\u2028", "_\t\u2029", "_\u000B\r", "_\u000B\n", "_\u000B\r\n", "_\u000B\u0085", "_\u000B\u2028", "_\u000B\u2029", "_\f\r", "_\f\n", "_\f\r\n", "_\f\u0085", "_\f\u2028", "_\f\u2029", "_\u00A0\r", "_\u00A0\n", "_\u00A0\r\n", "_\u00A0\u0085", "_\u00A0\u2028", "_\u00A0\u2029", "_\u1680\r", "_\u1680\n", "_\u1680\r\n", "_\u1680\u0085", "_\u1680\u2028", "_\u1680\u2029"),
                new TestData("Test\n", "Test\r", "Test\n", "Test\r\n", "Test\u0085", "Test\u2028", "Test\u2029", "Test \r", "Test \n", "Test \r\n", "Test \u0085", "Test \u2028", "Test \u2029", "Test\t\r", "Test\t\n", "Test\t\r\n", "Test\t\u0085", "Test\t\u2028", "Test\t\u2029", "Test\u000B\r", "Test\u000B\n", "Test\u000B\r\n", "Test\u000B\u0085", "Test\u000B\u2028", "Test\u000B\u2029", "Test\f\r", "Test\f\n", "Test\f\r\n", "Test\f\u0085", "Test\f\u2028", "Test\f\u2029", "Test\u00A0\r", "Test\u00A0\n", "Test\u00A0\r\n", "Test\u00A0\u0085", "Test\u00A0\u2028", "Test\u00A0\u2029", "Test\u1680\r", "Test\u1680\n", "Test\u1680\r\n", "Test\u1680\u0085", "Test\u1680\u2028", "Test\u1680\u2029"),
                new TestData(" _", " _", " _ "),
                new TestData("\t_", "\t_", "\t_\t"),
                new TestData("\u000B_", "\u000B_", "\u000B_\u000B"),
                new TestData("\f_", "\f_", "\f_\f"),
                new TestData("\u00A0_", "\u00A0_", "\u00A0_\u00A0"),
                new TestData("\u1680_", "\u1680_", "\u1680_\u1680"),
                new TestData(" Test", " Test", " Test "),
                new TestData("\tTest", "\tTest", "\tTest\t"),
                new TestData("\u000BTest", "\u000BTest", "\u000BTest\u000B"),
                new TestData("\fTest", "\fTest", "\fTest\f"),
                new TestData("\u00A0Test", "\u00A0Test", "\u00A0Test\u00A0"),
                new TestData("\u1680Test", "\u1680Test", "\u1680Test\u1680"),
                new TestData("\n_", "\r_", "\n_", "\r\n_", "\u0085_", "\u2028_", "\u2029_", " \r_", "\t\r_", "\u000B\r_", "\f\r_", "\u00A0\r_", "\u1680\r_", " \n_", "\t\n_", "\u000B\n_", "\f\n_", "\u00A0\n_", "\u1680\n_", " \r\n_", "\t\r\n_", "\u000B\r\n_", "\f\r\n_", "\u00A0\r\n_", "\u1680\r\n_", " \u0085_", "\t\u0085_", "\u000B\u0085_", "\f\u0085_", "\u00A0\u0085_", "\u1680\u0085_", " \u2028_", "\t\u2028_", "\u000B\u2028_", "\f\u2028_", "\u00A0\u2028_", "\u1680\u2028_", " \u2029_", "\t\u2029_", "\u000B\u2029_", "\f\u2029_", "\u00A0\u2029_", "\u1680\u2029_"),
                new TestData("\nTest", "\rTest", "\nTest", "\r\nTest", "\u0085Test", "\u2028Test", "\u2029Test", " \rTest", "\t\rTest", "\u000B\rTest", "\f\rTest", "\u00A0\rTest", "\u1680\rTest", " \nTest", "\t\nTest", "\u000B\nTest", "\f\nTest", "\u00A0\nTest", "\u1680\nTest", " \r\nTest", "\t\r\nTest", "\u000B\r\nTest", "\f\r\nTest", "\u00A0\r\nTest", "\u1680\r\nTest", " \u0085Test", "\t\u0085Test", "\u000B\u0085Test", "\f\u0085Test", "\u00A0\u0085Test", "\u1680\u0085Test", " \u2028Test", "\t\u2028Test", "\u000B\u2028Test", "\f\u2028Test", "\u00A0\u2028Test", "\u1680\u2028Test", " \u2029Test", "\t\u2029Test", "\u000B\u2029Test", "\f\u2029Test", "\u00A0\u2029Test", "\u1680\u2029Test"),
                new TestData("\n\n", "\r\r", "\r\r\n", "\r\u0085", "\r\u2028", "\r\u2029", "\n\r", "\n\n", "\n\r\n", "\n\u0085", "\n\u2028", "\n\u2029", "\r\n\r", "\r\n\n", "\r\n\r\n", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\u0085\r", "\u0085\n", "\u0085\r\n", "\u0085\u0085", "\u0085\u2028", "\u0085\u2029", "\u2028\r", "\u2028\n", "\u2028\r\n", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\r", "\u2029\n", "\u2029\r\n", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029", "\r \r", "\n \n", "\r\n \r\n", "\u0085 \u0085", "\u2028 \u2028", "\u2029 \u2029", "\r\t\r", "\n\t\n", "\r\n\t\r\n", "\u0085\t\u0085", "\u2028\t\u2028", "\u2029\t\u2029", "\r\u000B\r", "\n\u000B\n", "\r\n\u000B\r\n", "\u0085\u000B\u0085", "\u2028\u000B\u2028", "\u2029\u000B\u2029", "\r\f\r", "\n\f\n", "\r\n\f\r\n", "\u0085\f\u0085", "\u2028\f\u2028", "\u2029\f\u2029", "\r\u00A0\r", "\n\u00A0\n", "\r\n\u00A0\r\n", "\u0085\u00A0\u0085", "\u2028\u00A0\u2028", "\u2029\u00A0\u2029", "\r\u1680\r", "\n\u1680\n", "\r\n\u1680\r\n", "\u0085\u1680\u0085", "\u2028\u1680\u2028", "\u2029\u1680\u2029"),
                new TestData("_ .", "_ .", "_ . ", "_ .\t", "_ .\u000B", "_ .\f", "_ .\u00A0", "_ .\u1680"),
                new TestData("_ Data", "_ Data", "_ Data ", "_ Data\t", "_ Data\u000B", "_ Data\f", "_ Data\u00A0", "_ Data\u1680"),
                new TestData("_\t.", "_\t.", "_\t. ", "_\t.\t", "_\t.\u000B", "_\t.\f", "_\t.\u00A0", "_\t.\u1680"),
                new TestData("_\tData", "_\tData", "_\tData ", "_\tData\t", "_\tData\u000B", "_\tData\f", "_\tData\u00A0", "_\tData\u1680"),
                new TestData("_\u000B.", "_\u000B.", "_\u000B. ", "_\u000B.\t", "_\u000B.\u000B", "_\u000B.\f", "_\u000B.\u00A0", "_\u000B.\u1680"),
                new TestData("_\u000BData", "_\u000BData", "_\u000BData ", "_\u000BData\t", "_\u000BData\u000B", "_\u000BData\f", "_\u000BData\u00A0", "_\u000BData\u1680"),
                new TestData("_\f.", "_\f.", "_\f. ", "_\f.\t", "_\f.\u000B", "_\f.\f", "_\f.\u00A0", "_\f.\u1680"),
                new TestData("_\fData", "_\fData", "_\fData ", "_\fData\t", "_\fData\u000B", "_\fData\f", "_\fData\u00A0", "_\fData\u1680"),
                new TestData("_\u00A0.", "_\u00A0.", "_\u00A0. ", "_\u00A0.\t", "_\u00A0.\u000B", "_\u00A0.\f", "_\u00A0.\u00A0", "_\u00A0.\u1680"),
                new TestData("_\u00A0Data", "_\u00A0Data", "_\u00A0Data ", "_\u00A0Data\t", "_\u00A0Data\u000B", "_\u00A0Data\f", "_\u00A0Data\u00A0", "_\u00A0Data\u1680"),
                new TestData("_\u1680.", "_\u1680.", "_\u1680. ", "_\u1680.\t", "_\u1680.\u000B", "_\u1680.\f", "_\u1680.\u00A0", "_\u1680.\u1680"),
                new TestData("_\u1680Data", "_\u1680Data", "_\u1680Data ", "_\u1680Data\t", "_\u1680Data\u000B", "_\u1680Data\f", "_\u1680Data\u00A0", "_\u1680Data\u1680"),
                new TestData("Test .", "Test .", "Test . ", "Test .\t", "Test .\u000B", "Test .\f", "Test .\u00A0", "Test .\u1680"),
                new TestData("Test Data", "Test Data", "Test Data ", "Test Data\t", "Test Data\u000B", "Test Data\f", "Test Data\u00A0", "Test Data\u1680"),
                new TestData("Test\t.", "Test\t.", "Test\t. ", "Test\t.\t", "Test\t.\u000B", "Test\t.\f", "Test\t.\u00A0", "Test\t.\u1680"),
                new TestData("Test\tData", "Test\tData", "Test\tData ", "Test\tData\t", "Test\tData\u000B", "Test\tData\f", "Test\tData\u00A0", "Test\tData\u1680"),
                new TestData("Test\u000B.", "Test\u000B.", "Test\u000B. ", "Test\u000B.\t", "Test\u000B.\u000B", "Test\u000B.\f", "Test\u000B.\u00A0", "Test\u000B.\u1680"),
                new TestData("Test\u000BData", "Test\u000BData", "Test\u000BData ", "Test\u000BData\t", "Test\u000BData\u000B", "Test\u000BData\f", "Test\u000BData\u00A0", "Test\u000BData\u1680"),
                new TestData("Test\f.", "Test\f.", "Test\f. ", "Test\f.\t", "Test\f.\u000B", "Test\f.\f", "Test\f.\u00A0", "Test\f.\u1680"),
                new TestData("Test\fData", "Test\fData", "Test\fData ", "Test\fData\t", "Test\fData\u000B", "Test\fData\f", "Test\fData\u00A0", "Test\fData\u1680"),
                new TestData("Test\u00A0.", "Test\u00A0.", "Test\u00A0. ", "Test\u00A0.\t", "Test\u00A0.\u000B", "Test\u00A0.\f", "Test\u00A0.\u00A0", "Test\u00A0.\u1680"),
                new TestData("Test\u00A0Data", "Test\u00A0Data", "Test\u00A0Data ", "Test\u00A0Data\t", "Test\u00A0Data\u000B", "Test\u00A0Data\f", "Test\u00A0Data\u00A0", "Test\u00A0Data\u1680"),
                new TestData("Test\u1680.", "Test\u1680.", "Test\u1680. ", "Test\u1680.\t", "Test\u1680.\u000B", "Test\u1680.\f", "Test\u1680.\u00A0", "Test\u1680.\u1680"),
                new TestData("Test\u1680Data", "Test\u1680Data", "Test\u1680Data ", "Test\u1680Data\t", "Test\u1680Data\u000B", "Test\u1680Data\f", "Test\u1680Data\u00A0", "Test\u1680Data\u1680"),
                new TestData("\n_\n", "\r_\r", "\n_\n", "\r\n_\r\n", "\u0085_\u0085", "\u2028_\u2028", "\u2029_\u2029", " \r_\r ", "\t\r_\r\t", "\u000B\r_\r\u000B", "\f\r_\r\f", "\u00A0\r_\r\u00A0", "\u1680\r_\r\u1680", " \n_\n ", "\t\n_\n\t", "\u000B\n_\n\u000B", "\f\n_\n\f", "\u00A0\n_\n\u00A0", "\u1680\n_\n\u1680", " \r\n_\r\n ", "\t\r\n_\r\n\t", "\u000B\r\n_\r\n\u000B", "\f\r\n_\r\n\f", "\u00A0\r\n_\r\n\u00A0", "\u1680\r\n_\r\n\u1680", " \u0085_\u0085 ", "\t\u0085_\u0085\t", "\u000B\u0085_\u0085\u000B", "\f\u0085_\u0085\f", "\u00A0\u0085_\u0085\u00A0", "\u1680\u0085_\u0085\u1680", " \u2028_\u2028 ", "\t\u2028_\u2028\t", "\u000B\u2028_\u2028\u000B", "\f\u2028_\u2028\f", "\u00A0\u2028_\u2028\u00A0", "\u1680\u2028_\u2028\u1680", " \u2029_\u2029 ", "\t\u2029_\u2029\t", "\u000B\u2029_\u2029\u000B", "\f\u2029_\u2029\f", "\u00A0\u2029_\u2029\u00A0", "\u1680\u2029_\u2029\u1680"),
                new TestData("\nTest\n", "\rTest\r", "\nTest\n", "\r\nTest\r\n", "\u0085Test\u0085", "\u2028Test\u2028", "\u2029Test\u2029", " \rTest\r ", "\t\rTest\r\t", "\u000B\rTest\r\u000B", "\f\rTest\r\f", "\u00A0\rTest\r\u00A0", "\u1680\rTest\r\u1680", " \nTest\n ", "\t\nTest\n\t", "\u000B\nTest\n\u000B", "\f\nTest\n\f", "\u00A0\nTest\n\u00A0", "\u1680\nTest\n\u1680", " \r\nTest\r\n ", "\t\r\nTest\r\n\t", "\u000B\r\nTest\r\n\u000B", "\f\r\nTest\r\n\f", "\u00A0\r\nTest\r\n\u00A0", "\u1680\r\nTest\r\n\u1680", " \u0085Test\u0085 ", "\t\u0085Test\u0085\t", "\u000B\u0085Test\u0085\u000B", "\f\u0085Test\u0085\f", "\u00A0\u0085Test\u0085\u00A0", "\u1680\u0085Test\u0085\u1680", " \u2028Test\u2028 ", "\t\u2028Test\u2028\t", "\u000B\u2028Test\u2028\u000B", "\f\u2028Test\u2028\f", "\u00A0\u2028Test\u2028\u00A0", "\u1680\u2028Test\u2028\u1680", " \u2029Test\u2029 ", "\t\u2029Test\u2029\t", "\u000B\u2029Test\u2029\u000B", "\f\u2029Test\u2029\f", "\u00A0\u2029Test\u2029\u00A0", "\u1680\u2029Test\u2029\u1680"),
                new TestData("_ .\n.", "_ .\r.", "_ .\n.", "_ .\r\n.", "_ .\u0085.", "_ .\u2028.", "_ .\u2029."),
                new TestData("_ .\nData", "_ .\rData", "_ .\nData", "_ .\r\nData", "_ .\u0085Data", "_ .\u2028Data", "_ .\u2029Data"),
                new TestData("_ Data\n.", "_ Data\r.", "_ Data\n.", "_ Data\r\n.", "_ Data\u0085.", "_ Data\u2028.", "_ Data\u2029."),
                new TestData("_\t.\n.", "_\t.\r.", "_\t.\n.", "_\t.\r\n.", "_\t.\u0085.", "_\t.\u2028.", "_\t.\u2029."),
                new TestData("_\t.\nData", "_\t.\rData", "_\t.\nData", "_\t.\r\nData", "_\t.\u0085Data", "_\t.\u2028Data", "_\t.\u2029Data"),
                new TestData("_\tData\n.", "_\tData\r.", "_\tData\n.", "_\tData\r\n.", "_\tData\u0085.", "_\tData\u2028.", "_\tData\u2029."),
                new TestData("_\n.", "_ \r.", "_ \n.", "_ \r\n.", "_ \u0085.", "_ \u2028.", "_ \u2029.", "_\t\r.", "_\t\n.", "_\t\r\n.", "_\t\u0085.", "_\t\u2028.", "_\t\u2029.", "_\u000B\r.", "_\u000B\n.", "_\u000B\r\n.", "_\u000B\u0085.", "_\u000B\u2028.", "_\u000B\u2029.", "_\f\r.", "_\f\n.", "_\f\r\n.", "_\f\u0085.", "_\f\u2028.", "_\f\u2029.", "_\u00A0\r.", "_\u00A0\n.", "_\u00A0\r\n.", "_\u00A0\u0085.", "_\u00A0\u2028.", "_\u00A0\u2029.", "_\u1680\r.", "_\u1680\n.", "_\u1680\r\n.", "_\u1680\u0085.", "_\u1680\u2028.", "_\u1680\u2029."),
                new TestData("_\nData", "_ \rData", "_ \nData", "_ \r\nData", "_ \u0085Data", "_ \u2028Data", "_ \u2029Data", "_\t\rData", "_\t\nData", "_\t\r\nData", "_\t\u0085Data", "_\t\u2028Data", "_\t\u2029Data", "_\u000B\rData", "_\u000B\nData", "_\u000B\r\nData", "_\u000B\u0085Data", "_\u000B\u2028Data", "_\u000B\u2029Data", "_\f\rData", "_\f\nData", "_\f\r\nData", "_\f\u0085Data", "_\f\u2028Data", "_\f\u2029Data", "_\u00A0\rData", "_\u00A0\nData", "_\u00A0\r\nData", "_\u00A0\u0085Data", "_\u00A0\u2028Data", "_\u00A0\u2029Data", "_\u1680\rData", "_\u1680\nData", "_\u1680\r\nData", "_\u1680\u0085Data", "_\u1680\u2028Data", "_\u1680\u2029Data"),
                new TestData("Test\n.", "Test \r.", "Test \n.", "Test \r\n.", "Test \u0085.", "Test \u2028.", "Test \u2029.", "Test\t\r.", "Test\t\n.", "Test\t\r\n.", "Test\t\u0085.", "Test\t\u2028.", "Test\t\u2029.", "Test\u000B\r.", "Test\u000B\n.", "Test\u000B\r\n.", "Test\u000B\u0085.", "Test\u000B\u2028.", "Test\u000B\u2029.", "Test\f\r.", "Test\f\n.", "Test\f\r\n.", "Test\f\u0085.", "Test\f\u2028.", "Test\f\u2029.", "Test\u00A0\r.", "Test\u00A0\n.", "Test\u00A0\r\n.", "Test\u00A0\u0085.", "Test\u00A0\u2028.", "Test\u00A0\u2029.", "Test\u1680\r.", "Test\u1680\n.", "Test\u1680\r\n.", "Test\u1680\u0085.", "Test\u1680\u2028.", "Test\u1680\u2029."),
                new TestData("Test\nData", "Test \rData", "Test \nData", "Test \r\nData", "Test \u0085Data", "Test \u2028Data", "Test \u2029Data", "Test\t\rData", "Test\t\nData", "Test\t\r\nData", "Test\t\u0085Data", "Test\t\u2028Data", "Test\t\u2029Data", "Test\u000B\rData", "Test\u000B\nData", "Test\u000B\r\nData", "Test\u000B\u0085Data", "Test\u000B\u2028Data", "Test\u000B\u2029Data", "Test\f\rData", "Test\f\nData", "Test\f\r\nData", "Test\f\u0085Data", "Test\f\u2028Data", "Test\f\u2029Data", "Test\u00A0\rData", "Test\u00A0\nData", "Test\u00A0\r\nData", "Test\u00A0\u0085Data", "Test\u00A0\u2028Data", "Test\u00A0\u2029Data", "Test\u1680\rData", "Test\u1680\nData", "Test\u1680\r\nData", "Test\u1680\u0085Data", "Test\u1680\u2028Data", "Test\u1680\u2029Data"),
                new TestData(".\n _", ".\r _", ".\n _", ".\r\n _", ".\u0085 _", ".\u2028 _", ".\u2029 _"),
                new TestData("Data\n _", "Data\r _", "Data\n _", "Data\r\n _", "Data\u0085 _", "Data\u2028 _", "Data\u2029 _"),
                new TestData(".\n\t_", ".\r\t_", ".\n\t_", ".\r\n\t_", ".\u0085\t_", ".\u2028\t_", ".\u2029\t_"),
                new TestData("Data\n\t_", "Data\r\t_", "Data\n\t_", "Data\r\n\t_", "Data\u0085\t_", "Data\u2028\t_", "Data\u2029\t_"),
                new TestData(".\n\u000B_", ".\r\u000B_", ".\n\u000B_", ".\r\n\u000B_", ".\u0085\u000B_", ".\u2028\u000B_", ".\u2029\u000B_"),
                new TestData("Data\n\u000B_", "Data\r\u000B_", "Data\n\u000B_", "Data\r\n\u000B_", "Data\u0085\u000B_", "Data\u2028\u000B_", "Data\u2029\u000B_"),
                new TestData(".\n\f_", ".\r\f_", ".\n\f_", ".\r\n\f_", ".\u0085\f_", ".\u2028\f_", ".\u2029\f_"),
                new TestData("Data\n\f_", "Data\r\f_", "Data\n\f_", "Data\r\n\f_", "Data\u0085\f_", "Data\u2028\f_", "Data\u2029\f_"),
                new TestData(".\n\u00A0_", ".\r\u00A0_", ".\n\u00A0_", ".\r\n\u00A0_", ".\u0085\u00A0_", ".\u2028\u00A0_", ".\u2029\u00A0_"),
                new TestData("Data\n\u00A0_", "Data\r\u00A0_", "Data\n\u00A0_", "Data\r\n\u00A0_", "Data\u0085\u00A0_", "Data\u2028\u00A0_", "Data\u2029\u00A0_"),
                new TestData(".\n\u1680_", ".\r\u1680_", ".\n\u1680_", ".\r\n\u1680_", ".\u0085\u1680_", ".\u2028\u1680_", ".\u2029\u1680_"),
                new TestData("Data\n\u1680_", "Data\r\u1680_", "Data\n\u1680_", "Data\r\n\u1680_", "Data\u0085\u1680_", "Data\u2028\u1680_", "Data\u2029\u1680_"),
                new TestData(".\n Test", ".\r Test", ".\n Test", ".\r\n Test", ".\u0085 Test", ".\u2028 Test", ".\u2029 Test"),
                new TestData("Data\n Test", "Data\r Test", "Data\n Test", "Data\r\n Test", "Data\u0085 Test", "Data\u2028 Test", "Data\u2029 Test"),
                new TestData(".\n\tTest", ".\r\tTest", ".\n\tTest", ".\r\n\tTest", ".\u0085\tTest", ".\u2028\tTest", ".\u2029\tTest"),
                new TestData("Data\n\tTest", "Data\r\tTest", "Data\n\tTest", "Data\r\n\tTest", "Data\u0085\tTest", "Data\u2028\tTest", "Data\u2029\tTest"),
                new TestData(".\n\u000BTest", ".\r\u000BTest", ".\n\u000BTest", ".\r\n\u000BTest", ".\u0085\u000BTest", ".\u2028\u000BTest", ".\u2029\u000BTest"),
                new TestData("Data\n\u000BTest", "Data\r\u000BTest", "Data\n\u000BTest", "Data\r\n\u000BTest", "Data\u0085\u000BTest", "Data\u2028\u000BTest", "Data\u2029\u000BTest"),
                new TestData(".\n\fTest", ".\r\fTest", ".\n\fTest", ".\r\n\fTest", ".\u0085\fTest", ".\u2028\fTest", ".\u2029\fTest"),
                new TestData("Data\n\fTest", "Data\r\fTest", "Data\n\fTest", "Data\r\n\fTest", "Data\u0085\fTest", "Data\u2028\fTest", "Data\u2029\fTest"),
                new TestData(".\n\u00A0Test", ".\r\u00A0Test", ".\n\u00A0Test", ".\r\n\u00A0Test", ".\u0085\u00A0Test", ".\u2028\u00A0Test", ".\u2029\u00A0Test"),
                new TestData("Data\n\u00A0Test", "Data\r\u00A0Test", "Data\n\u00A0Test", "Data\r\n\u00A0Test", "Data\u0085\u00A0Test", "Data\u2028\u00A0Test", "Data\u2029\u00A0Test"),
                new TestData(".\n\u1680Test", ".\r\u1680Test", ".\n\u1680Test", ".\r\n\u1680Test", ".\u0085\u1680Test", ".\u2028\u1680Test", ".\u2029\u1680Test"),
                new TestData("Data\n\u1680Test", "Data\r\u1680Test", "Data\n\u1680Test", "Data\r\n\u1680Test", "Data\u0085\u1680Test", "Data\u2028\u1680Test", "Data\u2029\u1680Test"),
                new TestData("\n _\n", "\r _ \r", "\n _ \n", "\r\n _ \r\n", "\u0085 _ \u0085", "\u2028 _ \u2028", "\u2029 _ \u2029"),
                new TestData("\n\t_\n", "\r\t_\t\r", "\n\t_\t\n", "\r\n\t_\t\r\n", "\u0085\t_\t\u0085", "\u2028\t_\t\u2028", "\u2029\t_\t\u2029"),
                new TestData("\n\u000B_\n", "\r\u000B_\u000B\r", "\n\u000B_\u000B\n", "\r\n\u000B_\u000B\r\n", "\u0085\u000B_\u000B\u0085", "\u2028\u000B_\u000B\u2028", "\u2029\u000B_\u000B\u2029"),
                new TestData("\n\f_\n", "\r\f_\f\r", "\n\f_\f\n", "\r\n\f_\f\r\n", "\u0085\f_\f\u0085", "\u2028\f_\f\u2028", "\u2029\f_\f\u2029"),
                new TestData("\n\u00A0_\n", "\r\u00A0_\u00A0\r", "\n\u00A0_\u00A0\n", "\r\n\u00A0_\u00A0\r\n", "\u0085\u00A0_\u00A0\u0085", "\u2028\u00A0_\u00A0\u2028", "\u2029\u00A0_\u00A0\u2029"),
                new TestData("\n\u1680_\n", "\r\u1680_\u1680\r", "\n\u1680_\u1680\n", "\r\n\u1680_\u1680\r\n", "\u0085\u1680_\u1680\u0085", "\u2028\u1680_\u1680\u2028", "\u2029\u1680_\u1680\u2029"),
                new TestData("\n Test\n", "\r Test \r", "\n Test \n", "\r\n Test \r\n", "\u0085 Test \u0085", "\u2028 Test \u2028", "\u2029 Test \u2029"),
                new TestData("\n\tTest\n", "\r\tTest\t\r", "\n\tTest\t\n", "\r\n\tTest\t\r\n", "\u0085\tTest\t\u0085", "\u2028\tTest\t\u2028", "\u2029\tTest\t\u2029"),
                new TestData("\n\u000BTest\n", "\r\u000BTest\u000B\r", "\n\u000BTest\u000B\n", "\r\n\u000BTest\u000B\r\n", "\u0085\u000BTest\u000B\u0085", "\u2028\u000BTest\u000B\u2028", "\u2029\u000BTest\u000B\u2029"),
                new TestData("\n\fTest\n", "\r\fTest\f\r", "\n\fTest\f\n", "\r\n\fTest\f\r\n", "\u0085\fTest\f\u0085", "\u2028\fTest\f\u2028", "\u2029\fTest\f\u2029"),
                new TestData("\n\u00A0Test\n", "\r\u00A0Test\u00A0\r", "\n\u00A0Test\u00A0\n", "\r\n\u00A0Test\u00A0\r\n", "\u0085\u00A0Test\u00A0\u0085", "\u2028\u00A0Test\u00A0\u2028", "\u2029\u00A0Test\u00A0\u2029"),
                new TestData("\n\u1680Test\n", "\r\u1680Test\u1680\r", "\n\u1680Test\u1680\n", "\r\n\u1680Test\u1680\r\n", "\u0085\u1680Test\u1680\u0085", "\u2028\u1680Test\u1680\u2028", "\u2029\u1680Test\u1680\u2029"),
                new TestData("\n _", "\r _", "\n _", "\r\n _", "\u0085 _", "\u2028 _", "\u2029 _"),
                new TestData("\n\t_", "\r\t_", "\n\t_", "\r\n\t_", "\u0085\t_", "\u2028\t_", "\u2029\t_"),
                new TestData("\n\u000B_", "\r\u000B_", "\n\u000B_", "\r\n\u000B_", "\u0085\u000B_", "\u2028\u000B_", "\u2029\u000B_"),
                new TestData("\n\f_", "\r\f_", "\n\f_", "\r\n\f_", "\u0085\f_", "\u2028\f_", "\u2029\f_"),
                new TestData("\n\u00A0_", "\r\u00A0_", "\n\u00A0_", "\r\n\u00A0_", "\u0085\u00A0_", "\u2028\u00A0_", "\u2029\u00A0_"),
                new TestData("\n\u1680_", "\r\u1680_", "\n\u1680_", "\r\n\u1680_", "\u0085\u1680_", "\u2028\u1680_", "\u2029\u1680_"),
                new TestData("\n Test", "\r Test", "\n Test", "\r\n Test", "\u0085 Test", "\u2028 Test", "\u2029 Test"),
                new TestData("\n\tTest", "\r\tTest", "\n\tTest", "\r\n\tTest", "\u0085\tTest", "\u2028\tTest", "\u2029\tTest"),
                new TestData("\n\u000BTest", "\r\u000BTest", "\n\u000BTest", "\r\n\u000BTest", "\u0085\u000BTest", "\u2028\u000BTest", "\u2029\u000BTest"),
                new TestData("\n\fTest", "\r\fTest", "\n\fTest", "\r\n\fTest", "\u0085\fTest", "\u2028\fTest", "\u2029\fTest"),
                new TestData("\n\u00A0Test", "\r\u00A0Test", "\n\u00A0Test", "\r\n\u00A0Test", "\u0085\u00A0Test", "\u2028\u00A0Test", "\u2029\u00A0Test"),
                new TestData("\n\u1680Test", "\r\u1680Test", "\n\u1680Test", "\r\n\u1680Test", "\u0085\u1680Test", "\u2028\u1680Test", "\u2029\u1680Test"),
                new TestData("\n\n_", "\r\r_", "\n\r_", "\r\n\r_", "\u0085\r_", "\u2028\r_", "\u2029\r_", "\n\n_", "\r\n\n_", "\u0085\n_", "\u2028\n_", "\u2029\n_", "\r\r\n_", "\n\r\n_", "\r\n\r\n_", "\u0085\r\n_", "\u2028\r\n_", "\u2029\r\n_", "\r\u0085_", "\n\u0085_", "\r\n\u0085_", "\u0085\u0085_", "\u2028\u0085_", "\u2029\u0085_", "\r\u2028_", "\n\u2028_", "\r\n\u2028_", "\u0085\u2028_", "\u2028\u2028_", "\u2029\u2028_", "\r\u2029_", "\n\u2029_", "\r\n\u2029_", "\u0085\u2029_", "\u2028\u2029_", "\u2029\u2029_"),
                new TestData("\n\nTest", "\r\rTest", "\n\rTest", "\r\n\rTest", "\u0085\rTest", "\u2028\rTest", "\u2029\rTest", "\n\nTest", "\r\n\nTest", "\u0085\nTest", "\u2028\nTest", "\u2029\nTest", "\r\r\nTest", "\n\r\nTest", "\r\n\r\nTest", "\u0085\r\nTest", "\u2028\r\nTest", "\u2029\r\nTest", "\r\u0085Test", "\n\u0085Test", "\r\n\u0085Test", "\u0085\u0085Test", "\u2028\u0085Test", "\u2029\u0085Test", "\r\u2028Test", "\n\u2028Test", "\r\n\u2028Test", "\u0085\u2028Test", "\u2028\u2028Test", "\u2029\u2028Test", "\r\u2029Test", "\n\u2029Test", "\r\n\u2029Test", "\u0085\u2029Test", "\u2028\u2029Test", "\u2029\u2029Test"),
                new TestData("_ .\n", "_ .\r", "_ .\n", "_ .\r\n", "_ .\u0085", "_ .\u2028", "_ .\u2029"),
                new TestData("_ Data\n", "_ Data\r", "_ Data\n", "_ Data\r\n", "_ Data\u0085", "_ Data\u2028", "_ Data\u2029"),
                new TestData("_\t.\n", "_\t.\r", "_\t.\n", "_\t.\r\n", "_\t.\u0085", "_\t.\u2028", "_\t.\u2029"),
                new TestData("_\tData\n", "_\tData\r", "_\tData\n", "_\tData\r\n", "_\tData\u0085", "_\tData\u2028", "_\tData\u2029"),
                new TestData("_\u000B.\n", "_\u000B.\r", "_\u000B.\n", "_\u000B.\r\n", "_\u000B.\u0085", "_\u000B.\u2028", "_\u000B.\u2029"),
                new TestData("_\u000BData\n", "_\u000BData\r", "_\u000BData\n", "_\u000BData\r\n", "_\u000BData\u0085", "_\u000BData\u2028", "_\u000BData\u2029"),
                new TestData("_\f.\n", "_\f.\r", "_\f.\n", "_\f.\r\n", "_\f.\u0085", "_\f.\u2028", "_\f.\u2029"),
                new TestData("_\fData\n", "_\fData\r", "_\fData\n", "_\fData\r\n", "_\fData\u0085", "_\fData\u2028", "_\fData\u2029"),
                new TestData("_\u00A0.\n", "_\u00A0.\r", "_\u00A0.\n", "_\u00A0.\r\n", "_\u00A0.\u0085", "_\u00A0.\u2028", "_\u00A0.\u2029"),
                new TestData("_\u00A0Data\n", "_\u00A0Data\r", "_\u00A0Data\n", "_\u00A0Data\r\n", "_\u00A0Data\u0085", "_\u00A0Data\u2028", "_\u00A0Data\u2029"),
                new TestData("_\u1680.\n", "_\u1680.\r", "_\u1680.\n", "_\u1680.\r\n", "_\u1680.\u0085", "_\u1680.\u2028", "_\u1680.\u2029"),
                new TestData("_\u1680Data\n", "_\u1680Data\r", "_\u1680Data\n", "_\u1680Data\r\n", "_\u1680Data\u0085", "_\u1680Data\u2028", "_\u1680Data\u2029"),
                new TestData("Test .\n", "Test .\r", "Test .\n", "Test .\r\n", "Test .\u0085", "Test .\u2028", "Test .\u2029"),
                new TestData("Test Data\n", "Test Data\r", "Test Data\n", "Test Data\r\n", "Test Data\u0085", "Test Data\u2028", "Test Data\u2029"),
                new TestData("Test\t.\n", "Test\t.\r", "Test\t.\n", "Test\t.\r\n", "Test\t.\u0085", "Test\t.\u2028", "Test\t.\u2029"),
                new TestData("Test\tData\n", "Test\tData\r", "Test\tData\n", "Test\tData\r\n", "Test\tData\u0085", "Test\tData\u2028", "Test\tData\u2029"),
                new TestData("Test\u000B.\n", "Test\u000B.\r", "Test\u000B.\n", "Test\u000B.\r\n", "Test\u000B.\u0085", "Test\u000B.\u2028", "Test\u000B.\u2029"),
                new TestData("Test\u000BData\n", "Test\u000BData\r", "Test\u000BData\n", "Test\u000BData\r\n", "Test\u000BData\u0085", "Test\u000BData\u2028", "Test\u000BData\u2029"),
                new TestData("Test\f.\n", "Test\f.\r", "Test\f.\n", "Test\f.\r\n", "Test\f.\u0085", "Test\f.\u2028", "Test\f.\u2029"),
                new TestData("Test\fData\n", "Test\fData\r", "Test\fData\n", "Test\fData\r\n", "Test\fData\u0085", "Test\fData\u2028", "Test\fData\u2029"),
                new TestData("Test\u00A0.\n", "Test\u00A0.\r", "Test\u00A0.\n", "Test\u00A0.\r\n", "Test\u00A0.\u0085", "Test\u00A0.\u2028", "Test\u00A0.\u2029"),
                new TestData("Test\u00A0Data\n", "Test\u00A0Data\r", "Test\u00A0Data\n", "Test\u00A0Data\r\n", "Test\u00A0Data\u0085", "Test\u00A0Data\u2028", "Test\u00A0Data\u2029"),
                new TestData("Test\u1680.\n", "Test\u1680.\r", "Test\u1680.\n", "Test\u1680.\r\n", "Test\u1680.\u0085", "Test\u1680.\u2028", "Test\u1680.\u2029"),
                new TestData("Test\u1680Data\n", "Test\u1680Data\r", "Test\u1680Data\n", "Test\u1680Data\r\n", "Test\u1680Data\u0085", "Test\u1680Data\u2028", "Test\u1680Data\u2029"),
        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(u -> {
                StringLineIterator target = StringLineIterator.create(u.source, false, false);
                String description = u.description;
                assertTrue(description, target.hasNext());
                String line = target.next();
                String d = String.format("%s, index 0", description);
                Assert.assertNotNull(d, line);
                Assert.assertFalse(d, line.contains("\n"));
                StringBuilder result = new StringBuilder(line);
                int index = 0;
                while (target.hasNext()) {
                    line = target.next();
                    d = String.format("%s, index %d", description, ++index);
                    Assert.assertNotNull(d, line);
                    Assert.assertFalse(d, line.contains("\n"));
                    result.append("\n").append(line);
                }
                assertEquals(description, expected, result.toString());
            });
        }
    }

    @Test
    public void trimTest() {
        TestData[] testData = new TestData[]{
                new TestData("", " ", "\t", "\u000B", "\f", "\u00A0", "\u1680", "  ", " \t", " \u000B", " \f", " \u00A0", " \u1680", "\t ", "\t\t", "\t\u000B", "\t\f", "\t\u00A0", "\t\u1680", "\u000B ", "\u000B\t", "\u000B\u000B", "\u000B\f", "\u000B\u00A0", "\u000B\u1680", "\f ", "\f\t", "\f\u000B", "\f\f", "\f\u00A0", "\f\u1680", "\u00A0 ", "\u00A0\t", "\u00A0\u000B", "\u00A0\f", "\u00A0\u00A0", "\u00A0\u1680", "\u1680 ", "\u1680\t", "\u1680\u000B", "\u1680\f", "\u1680\u00A0", "\u1680\u1680"),
                new TestData("\n", "\r", "\n", "\r\n", "\u0085", "\u2028", "\u2029", " \r", " \n", " \r\n", " \u0085", " \u2028", " \u2029", "\t\r", "\t\n", "\t\r\n", "\t\u0085", "\t\u2028", "\t\u2029", "\u000B\r", "\u000B\n", "\u000B\r\n", "\u000B\u0085", "\u000B\u2028", "\u000B\u2029", "\f\r", "\f\n", "\f\r\n", "\f\u0085", "\f\u2028", "\f\u2029", "\u00A0\r", "\u00A0\n", "\u00A0\r\n", "\u00A0\u0085", "\u00A0\u2028", "\u00A0\u2029", "\u1680\r", "\u1680\n", "\u1680\r\n", "\u1680\u0085", "\u1680\u2028", "\u1680\u2029", "\r ", "\n ", "\r\n ", "\u0085 ", "\u2028 ", "\u2029 ", "\r\t", "\n\t", "\r\n\t", "\u0085\t", "\u2028\t", "\u2029\t", "\r\u000B", "\n\u000B", "\r\n\u000B", "\u0085\u000B", "\u2028\u000B", "\u2029\u000B", "\r\f", "\n\f", "\r\n\f", "\u0085\f", "\u2028\f", "\u2029\f", "\r\u00A0", "\n\u00A0", "\r\n\u00A0", "\u0085\u00A0", "\u2028\u00A0", "\u2029\u00A0", "\r\u1680", "\n\u1680", "\r\n\u1680", "\u0085\u1680", "\u2028\u1680", "\u2029\u1680", " \r ", " \n ", " \r\n ", " \u0085 ", " \u2028 ", " \u2029 ", "\t\r\t", "\t\n\t", "\t\r\n\t", "\t\u0085\t", "\t\u2028\t", "\t\u2029\t", "\u000B\r\u000B", "\u000B\n\u000B", "\u000B\r\n\u000B", "\u000B\u0085\u000B", "\u000B\u2028\u000B", "\u000B\u2029\u000B", "\f\r\f", "\f\n\f", "\f\r\n\f", "\f\u0085\f", "\f\u2028\f", "\f\u2029\f", "\u00A0\r\u00A0", "\u00A0\n\u00A0", "\u00A0\r\n\u00A0", "\u00A0\u0085\u00A0", "\u00A0\u2028\u00A0", "\u00A0\u2029\u00A0", "\u1680\r\u1680", "\u1680\n\u1680", "\u1680\r\n\u1680", "\u1680\u0085\u1680", "\u1680\u2028\u1680", "\u1680\u2029\u1680"),
                new TestData("_", "_", "_ ", "_\t", "_\u000B", "_\f", "_\u00A0", "_\u1680", " _", "\t_", "\u000B_", "\f_", "\u00A0_", "\u1680_", " _ ", "\t_\t", "\u000B_\u000B", "\f_\f", "\u00A0_\u00A0", "\u1680_\u1680", "  _", "\t _", "\u000B _", "\f _", "\u00A0 _", "\u1680 _", " \t_", "\t\t_", "\u000B\t_", "\f\t_", "\u00A0\t_", "\u1680\t_", " \u000B_", "\t\u000B_", "\u000B\u000B_", "\f\u000B_", "\u00A0\u000B_", "\u1680\u000B_", " \f_", "\t\f_", "\u000B\f_", "\f\f_", "\u00A0\f_", "\u1680\f_", " \u00A0_", "\t\u00A0_", "\u000B\u00A0_", "\f\u00A0_", "\u00A0\u00A0_", "\u1680\u00A0_", " \u1680_", "\t\u1680_", "\u000B\u1680_", "\f\u1680_", "\u00A0\u1680_", "\u1680\u1680_", "_  ", "_ \t", "_ \u000B", "_ \f", "_ \u00A0", "_ \u1680", "_\t ", "_\t\t", "_\t\u000B", "_\t\f", "_\t\u00A0", "_\t\u1680", "_\u000B ", "_\u000B\t", "_\u000B\u000B", "_\u000B\f", "_\u000B\u00A0", "_\u000B\u1680", "_\f ", "_\f\t", "_\f\u000B", "_\f\f", "_\f\u00A0", "_\f\u1680", "_\u00A0 ", "_\u00A0\t", "_\u00A0\u000B", "_\u00A0\f", "_\u00A0\u00A0", "_\u00A0\u1680", "_\u1680 ", "_\u1680\t", "_\u1680\u000B", "_\u1680\f", "_\u1680\u00A0", "_\u1680\u1680"),
                new TestData("Test", "Test", "Test ", "Test\t", "Test\u000B", "Test\f", "Test\u00A0", "Test\u1680", " Test", "\tTest", "\u000BTest", "\fTest", "\u00A0Test", "\u1680Test", " Test ", "\tTest\t", "\u000BTest\u000B", "\fTest\f", "\u00A0Test\u00A0", "\u1680Test\u1680", "  Test", "\t Test", "\u000B Test", "\f Test", "\u00A0 Test", "\u1680 Test", " \tTest", "\t\tTest", "\u000B\tTest", "\f\tTest", "\u00A0\tTest", "\u1680\tTest", " \u000BTest", "\t\u000BTest", "\u000B\u000BTest", "\f\u000BTest", "\u00A0\u000BTest", "\u1680\u000BTest", " \fTest", "\t\fTest", "\u000B\fTest", "\f\fTest", "\u00A0\fTest", "\u1680\fTest", " \u00A0Test", "\t\u00A0Test", "\u000B\u00A0Test", "\f\u00A0Test", "\u00A0\u00A0Test", "\u1680\u00A0Test", " \u1680Test", "\t\u1680Test", "\u000B\u1680Test", "\f\u1680Test", "\u00A0\u1680Test", "\u1680\u1680Test", "Test  ", "Test \t", "Test \u000B", "Test \f", "Test \u00A0", "Test \u1680", "Test\t ", "Test\t\t", "Test\t\u000B", "Test\t\f", "Test\t\u00A0", "Test\t\u1680", "Test\u000B ", "Test\u000B\t", "Test\u000B\u000B", "Test\u000B\f", "Test\u000B\u00A0", "Test\u000B\u1680", "Test\f ", "Test\f\t", "Test\f\u000B", "Test\f\f", "Test\f\u00A0", "Test\f\u1680", "Test\u00A0 ", "Test\u00A0\t", "Test\u00A0\u000B", "Test\u00A0\f", "Test\u00A0\u00A0", "Test\u00A0\u1680", "Test\u1680 ", "Test\u1680\t", "Test\u1680\u000B", "Test\u1680\f", "Test\u1680\u00A0", "Test\u1680\u1680"),
                new TestData("_\n", "_\r", "_\n", "_\r\n", "_\u0085", "_\u2028", "_\u2029", "_ \r", "_ \n", "_ \r\n", "_ \u0085", "_ \u2028", "_ \u2029", "_\t\r", "_\t\n", "_\t\r\n", "_\t\u0085", "_\t\u2028", "_\t\u2029", "_\u000B\r", "_\u000B\n", "_\u000B\r\n", "_\u000B\u0085", "_\u000B\u2028", "_\u000B\u2029", "_\f\r", "_\f\n", "_\f\r\n", "_\f\u0085", "_\f\u2028", "_\f\u2029", "_\u00A0\r", "_\u00A0\n", "_\u00A0\r\n", "_\u00A0\u0085", "_\u00A0\u2028", "_\u00A0\u2029", "_\u1680\r", "_\u1680\n", "_\u1680\r\n", "_\u1680\u0085", "_\u1680\u2028", "_\u1680\u2029"),
                new TestData("Test\n", "Test\r", "Test\n", "Test\r\n", "Test\u0085", "Test\u2028", "Test\u2029", "Test \r", "Test \n", "Test \r\n", "Test \u0085", "Test \u2028", "Test \u2029", "Test\t\r", "Test\t\n", "Test\t\r\n", "Test\t\u0085", "Test\t\u2028", "Test\t\u2029", "Test\u000B\r", "Test\u000B\n", "Test\u000B\r\n", "Test\u000B\u0085", "Test\u000B\u2028", "Test\u000B\u2029", "Test\f\r", "Test\f\n", "Test\f\r\n", "Test\f\u0085", "Test\f\u2028", "Test\f\u2029", "Test\u00A0\r", "Test\u00A0\n", "Test\u00A0\r\n", "Test\u00A0\u0085", "Test\u00A0\u2028", "Test\u00A0\u2029", "Test\u1680\r", "Test\u1680\n", "Test\u1680\r\n", "Test\u1680\u0085", "Test\u1680\u2028", "Test\u1680\u2029"),
                new TestData("\n_", "\r_", "\n_", "\r\n_", "\u0085_", "\u2028_", "\u2029_", "\r _", "\n _", "\r\n _", "\u0085 _", "\u2028 _", "\u2029 _", "\r\t_", "\n\t_", "\r\n\t_", "\u0085\t_", "\u2028\t_", "\u2029\t_", "\r\u000B_", "\n\u000B_", "\r\n\u000B_", "\u0085\u000B_", "\u2028\u000B_", "\u2029\u000B_", "\r\f_", "\n\f_", "\r\n\f_", "\u0085\f_", "\u2028\f_", "\u2029\f_", "\r\u00A0_", "\n\u00A0_", "\r\n\u00A0_", "\u0085\u00A0_", "\u2028\u00A0_", "\u2029\u00A0_", "\r\u1680_", "\n\u1680_", "\r\n\u1680_", "\u0085\u1680_", "\u2028\u1680_", "\u2029\u1680_", " \r_", "\t\r_", "\u000B\r_", "\f\r_", "\u00A0\r_", "\u1680\r_", " \n_", "\t\n_", "\u000B\n_", "\f\n_", "\u00A0\n_", "\u1680\n_", " \r\n_", "\t\r\n_", "\u000B\r\n_", "\f\r\n_", "\u00A0\r\n_", "\u1680\r\n_", " \u0085_", "\t\u0085_", "\u000B\u0085_", "\f\u0085_", "\u00A0\u0085_", "\u1680\u0085_", " \u2028_", "\t\u2028_", "\u000B\u2028_", "\f\u2028_", "\u00A0\u2028_", "\u1680\u2028_", " \u2029_", "\t\u2029_", "\u000B\u2029_", "\f\u2029_", "\u00A0\u2029_", "\u1680\u2029_"),
                new TestData("\nTest", "\rTest", "\nTest", "\r\nTest", "\u0085Test", "\u2028Test", "\u2029Test", "\r Test", "\n Test", "\r\n Test", "\u0085 Test", "\u2028 Test", "\u2029 Test", "\r\tTest", "\n\tTest", "\r\n\tTest", "\u0085\tTest", "\u2028\tTest", "\u2029\tTest", "\r\u000BTest", "\n\u000BTest", "\r\n\u000BTest", "\u0085\u000BTest", "\u2028\u000BTest", "\u2029\u000BTest", "\r\fTest", "\n\fTest", "\r\n\fTest", "\u0085\fTest", "\u2028\fTest", "\u2029\fTest", "\r\u00A0Test", "\n\u00A0Test", "\r\n\u00A0Test", "\u0085\u00A0Test", "\u2028\u00A0Test", "\u2029\u00A0Test", "\r\u1680Test", "\n\u1680Test", "\r\n\u1680Test", "\u0085\u1680Test", "\u2028\u1680Test", "\u2029\u1680Test", " \rTest", "\t\rTest", "\u000B\rTest", "\f\rTest", "\u00A0\rTest", "\u1680\rTest", " \nTest", "\t\nTest", "\u000B\nTest", "\f\nTest", "\u00A0\nTest", "\u1680\nTest", " \r\nTest", "\t\r\nTest", "\u000B\r\nTest", "\f\r\nTest", "\u00A0\r\nTest", "\u1680\r\nTest", " \u0085Test", "\t\u0085Test", "\u000B\u0085Test", "\f\u0085Test", "\u00A0\u0085Test", "\u1680\u0085Test", " \u2028Test", "\t\u2028Test", "\u000B\u2028Test", "\f\u2028Test", "\u00A0\u2028Test", "\u1680\u2028Test", " \u2029Test", "\t\u2029Test", "\u000B\u2029Test", "\f\u2029Test", "\u00A0\u2029Test", "\u1680\u2029Test"),
                new TestData("\n\n", "\r\r", "\r\r\n", "\r\u0085", "\r\u2028", "\r\u2029", "\n\r", "\n\n", "\n\r\n", "\n\u0085", "\n\u2028", "\n\u2029", "\r\n\r", "\r\n\n", "\r\n\r\n", "\r\n\u0085", "\r\n\u2028", "\r\n\u2029", "\u0085\r", "\u0085\n", "\u0085\r\n", "\u0085\u0085", "\u0085\u2028", "\u0085\u2029", "\u2028\r", "\u2028\n", "\u2028\r\n", "\u2028\u0085", "\u2028\u2028", "\u2028\u2029", "\u2029\r", "\u2029\n", "\u2029\r\n", "\u2029\u0085", "\u2029\u2028", "\u2029\u2029", "\r \r", "\n \n", "\r\n \r\n", "\u0085 \u0085", "\u2028 \u2028", "\u2029 \u2029", "\r\t\r", "\n\t\n", "\r\n\t\r\n", "\u0085\t\u0085", "\u2028\t\u2028", "\u2029\t\u2029", "\r\u000B\r", "\n\u000B\n", "\r\n\u000B\r\n", "\u0085\u000B\u0085", "\u2028\u000B\u2028", "\u2029\u000B\u2029", "\r\f\r", "\n\f\n", "\r\n\f\r\n", "\u0085\f\u0085", "\u2028\f\u2028", "\u2029\f\u2029", "\r\u00A0\r", "\n\u00A0\n", "\r\n\u00A0\r\n", "\u0085\u00A0\u0085", "\u2028\u00A0\u2028", "\u2029\u00A0\u2029", "\r\u1680\r", "\n\u1680\n", "\r\n\u1680\r\n", "\u0085\u1680\u0085", "\u2028\u1680\u2028", "\u2029\u1680\u2029"),
                new TestData("_ .", "_ .", "_ . ", "_ .\t", "_ .\u000B", "_ .\f", "_ .\u00A0", "_ .\u1680"),
                new TestData("_ Data", "_ Data", "_ Data ", "_ Data\t", "_ Data\u000B", "_ Data\f", "_ Data\u00A0", "_ Data\u1680"),
                new TestData("_\t.", "_\t.", "_\t. ", "_\t.\t", "_\t.\u000B", "_\t.\f", "_\t.\u00A0", "_\t.\u1680"),
                new TestData("_\tData", "_\tData", "_\tData ", "_\tData\t", "_\tData\u000B", "_\tData\f", "_\tData\u00A0", "_\tData\u1680"),
                new TestData("_\u000B.", "_\u000B.", "_\u000B. ", "_\u000B.\t", "_\u000B.\u000B", "_\u000B.\f", "_\u000B.\u00A0", "_\u000B.\u1680"),
                new TestData("_\u000BData", "_\u000BData", "_\u000BData ", "_\u000BData\t", "_\u000BData\u000B", "_\u000BData\f", "_\u000BData\u00A0", "_\u000BData\u1680"),
                new TestData("_\f.", "_\f.", "_\f. ", "_\f.\t", "_\f.\u000B", "_\f.\f", "_\f.\u00A0", "_\f.\u1680"),
                new TestData("_\fData", "_\fData", "_\fData ", "_\fData\t", "_\fData\u000B", "_\fData\f", "_\fData\u00A0", "_\fData\u1680"),
                new TestData("_\u00A0.", "_\u00A0.", "_\u00A0. ", "_\u00A0.\t", "_\u00A0.\u000B", "_\u00A0.\f", "_\u00A0.\u00A0", "_\u00A0.\u1680"),
                new TestData("_\u00A0Data", "_\u00A0Data", "_\u00A0Data ", "_\u00A0Data\t", "_\u00A0Data\u000B", "_\u00A0Data\f", "_\u00A0Data\u00A0", "_\u00A0Data\u1680"),
                new TestData("_\u1680.", "_\u1680.", "_\u1680. ", "_\u1680.\t", "_\u1680.\u000B", "_\u1680.\f", "_\u1680.\u00A0", "_\u1680.\u1680"),
                new TestData("_\u1680Data", "_\u1680Data", "_\u1680Data ", "_\u1680Data\t", "_\u1680Data\u000B", "_\u1680Data\f", "_\u1680Data\u00A0", "_\u1680Data\u1680"),
                new TestData("Test .", "Test .", "Test . ", "Test .\t", "Test .\u000B", "Test .\f", "Test .\u00A0", "Test .\u1680"),
                new TestData("Test Data", "Test Data", "Test Data ", "Test Data\t", "Test Data\u000B", "Test Data\f", "Test Data\u00A0", "Test Data\u1680"),
                new TestData("Test\t.", "Test\t.", "Test\t. ", "Test\t.\t", "Test\t.\u000B", "Test\t.\f", "Test\t.\u00A0", "Test\t.\u1680"),
                new TestData("Test\tData", "Test\tData", "Test\tData ", "Test\tData\t", "Test\tData\u000B", "Test\tData\f", "Test\tData\u00A0", "Test\tData\u1680"),
                new TestData("Test\u000B.", "Test\u000B.", "Test\u000B. ", "Test\u000B.\t", "Test\u000B.\u000B", "Test\u000B.\f", "Test\u000B.\u00A0", "Test\u000B.\u1680"),
                new TestData("Test\u000BData", "Test\u000BData", "Test\u000BData ", "Test\u000BData\t", "Test\u000BData\u000B", "Test\u000BData\f", "Test\u000BData\u00A0", "Test\u000BData\u1680"),
                new TestData("Test\f.", "Test\f.", "Test\f. ", "Test\f.\t", "Test\f.\u000B", "Test\f.\f", "Test\f.\u00A0", "Test\f.\u1680"),
                new TestData("Test\fData", "Test\fData", "Test\fData ", "Test\fData\t", "Test\fData\u000B", "Test\fData\f", "Test\fData\u00A0", "Test\fData\u1680"),
                new TestData("Test\u00A0.", "Test\u00A0.", "Test\u00A0. ", "Test\u00A0.\t", "Test\u00A0.\u000B", "Test\u00A0.\f", "Test\u00A0.\u00A0", "Test\u00A0.\u1680"),
                new TestData("Test\u00A0Data", "Test\u00A0Data", "Test\u00A0Data ", "Test\u00A0Data\t", "Test\u00A0Data\u000B", "Test\u00A0Data\f", "Test\u00A0Data\u00A0", "Test\u00A0Data\u1680"),
                new TestData("Test\u1680.", "Test\u1680.", "Test\u1680. ", "Test\u1680.\t", "Test\u1680.\u000B", "Test\u1680.\f", "Test\u1680.\u00A0", "Test\u1680.\u1680"),
                new TestData("Test\u1680Data", "Test\u1680Data", "Test\u1680Data ", "Test\u1680Data\t", "Test\u1680Data\u000B", "Test\u1680Data\f", "Test\u1680Data\u00A0", "Test\u1680Data\u1680"),
                new TestData("\n_\n", "\r_\r", "\n_\n", "\r\n_\r\n", "\u0085_\u0085", "\u2028_\u2028", "\u2029_\u2029", "\r _ \r", "\n _ \n", "\r\n _ \r\n", "\u0085 _ \u0085", "\u2028 _ \u2028", "\u2029 _ \u2029", "\r\t_\t\r", "\n\t_\t\n", "\r\n\t_\t\r\n", "\u0085\t_\t\u0085", "\u2028\t_\t\u2028", "\u2029\t_\t\u2029", "\r\u000B_\u000B\r", "\n\u000B_\u000B\n", "\r\n\u000B_\u000B\r\n", "\u0085\u000B_\u000B\u0085", "\u2028\u000B_\u000B\u2028", "\u2029\u000B_\u000B\u2029", "\r\f_\f\r", "\n\f_\f\n", "\r\n\f_\f\r\n", "\u0085\f_\f\u0085", "\u2028\f_\f\u2028", "\u2029\f_\f\u2029", "\r\u00A0_\u00A0\r", "\n\u00A0_\u00A0\n", "\r\n\u00A0_\u00A0\r\n", "\u0085\u00A0_\u00A0\u0085", "\u2028\u00A0_\u00A0\u2028", "\u2029\u00A0_\u00A0\u2029", "\r\u1680_\u1680\r", "\n\u1680_\u1680\n", "\r\n\u1680_\u1680\r\n", "\u0085\u1680_\u1680\u0085", "\u2028\u1680_\u1680\u2028", "\u2029\u1680_\u1680\u2029", " \r_\r ", "\t\r_\r\t", "\u000B\r_\r\u000B", "\f\r_\r\f", "\u00A0\r_\r\u00A0", "\u1680\r_\r\u1680", " \n_\n ", "\t\n_\n\t", "\u000B\n_\n\u000B", "\f\n_\n\f", "\u00A0\n_\n\u00A0", "\u1680\n_\n\u1680", " \r\n_\r\n ", "\t\r\n_\r\n\t", "\u000B\r\n_\r\n\u000B", "\f\r\n_\r\n\f", "\u00A0\r\n_\r\n\u00A0", "\u1680\r\n_\r\n\u1680", " \u0085_\u0085 ", "\t\u0085_\u0085\t", "\u000B\u0085_\u0085\u000B", "\f\u0085_\u0085\f", "\u00A0\u0085_\u0085\u00A0", "\u1680\u0085_\u0085\u1680", " \u2028_\u2028 ", "\t\u2028_\u2028\t", "\u000B\u2028_\u2028\u000B", "\f\u2028_\u2028\f", "\u00A0\u2028_\u2028\u00A0", "\u1680\u2028_\u2028\u1680", " \u2029_\u2029 ", "\t\u2029_\u2029\t", "\u000B\u2029_\u2029\u000B", "\f\u2029_\u2029\f", "\u00A0\u2029_\u2029\u00A0", "\u1680\u2029_\u2029\u1680"),
                new TestData("\nTest\n", "\rTest\r", "\nTest\n", "\r\nTest\r\n", "\u0085Test\u0085", "\u2028Test\u2028", "\u2029Test\u2029", "\r Test \r", "\n Test \n", "\r\n Test \r\n", "\u0085 Test \u0085", "\u2028 Test \u2028", "\u2029 Test \u2029", "\r\tTest\t\r", "\n\tTest\t\n", "\r\n\tTest\t\r\n", "\u0085\tTest\t\u0085", "\u2028\tTest\t\u2028", "\u2029\tTest\t\u2029", "\r\u000BTest\u000B\r", "\n\u000BTest\u000B\n", "\r\n\u000BTest\u000B\r\n", "\u0085\u000BTest\u000B\u0085", "\u2028\u000BTest\u000B\u2028", "\u2029\u000BTest\u000B\u2029", "\r\fTest\f\r", "\n\fTest\f\n", "\r\n\fTest\f\r\n", "\u0085\fTest\f\u0085", "\u2028\fTest\f\u2028", "\u2029\fTest\f\u2029", "\r\u00A0Test\u00A0\r", "\n\u00A0Test\u00A0\n", "\r\n\u00A0Test\u00A0\r\n", "\u0085\u00A0Test\u00A0\u0085", "\u2028\u00A0Test\u00A0\u2028", "\u2029\u00A0Test\u00A0\u2029", "\r\u1680Test\u1680\r", "\n\u1680Test\u1680\n", "\r\n\u1680Test\u1680\r\n", "\u0085\u1680Test\u1680\u0085", "\u2028\u1680Test\u1680\u2028", "\u2029\u1680Test\u1680\u2029", " \rTest\r ", "\t\rTest\r\t", "\u000B\rTest\r\u000B", "\f\rTest\r\f", "\u00A0\rTest\r\u00A0", "\u1680\rTest\r\u1680", " \nTest\n ", "\t\nTest\n\t", "\u000B\nTest\n\u000B", "\f\nTest\n\f", "\u00A0\nTest\n\u00A0", "\u1680\nTest\n\u1680", " \r\nTest\r\n ", "\t\r\nTest\r\n\t", "\u000B\r\nTest\r\n\u000B", "\f\r\nTest\r\n\f", "\u00A0\r\nTest\r\n\u00A0", "\u1680\r\nTest\r\n\u1680", " \u0085Test\u0085 ", "\t\u0085Test\u0085\t", "\u000B\u0085Test\u0085\u000B", "\f\u0085Test\u0085\f", "\u00A0\u0085Test\u0085\u00A0", "\u1680\u0085Test\u0085\u1680", " \u2028Test\u2028 ", "\t\u2028Test\u2028\t", "\u000B\u2028Test\u2028\u000B", "\f\u2028Test\u2028\f", "\u00A0\u2028Test\u2028\u00A0", "\u1680\u2028Test\u2028\u1680", " \u2029Test\u2029 ", "\t\u2029Test\u2029\t", "\u000B\u2029Test\u2029\u000B", "\f\u2029Test\u2029\f", "\u00A0\u2029Test\u2029\u00A0", "\u1680\u2029Test\u2029\u1680"),
                new TestData("_ .\nData", "_ .\rData", "_ .\nData", "_ .\r\nData", "_ .\u0085Data", "_ .\u2028Data", "_ .\u2029Data"),
                new TestData("_ Data\n.", "_ Data\r.", "_ Data\n.", "_ Data\r\n.", "_ Data\u0085.", "_ Data\u2028.", "_ Data\u2029."),
                new TestData("_\n.", "_ \r.", "_ \n.", "_ \r\n.", "_ \u0085.", "_ \u2028.", "_ \u2029.", "_\t\r.", "_\t\n.", "_\t\r\n.", "_\t\u0085.", "_\t\u2028.", "_\t\u2029.", "_\u000B\r.", "_\u000B\n.", "_\u000B\r\n.", "_\u000B\u0085.", "_\u000B\u2028.", "_\u000B\u2029.", "_\f\r.", "_\f\n.", "_\f\r\n.", "_\f\u0085.", "_\f\u2028.", "_\f\u2029.", "_\u00A0\r.", "_\u00A0\n.", "_\u00A0\r\n.", "_\u00A0\u0085.", "_\u00A0\u2028.", "_\u00A0\u2029.", "_\u1680\r.", "_\u1680\n.", "_\u1680\r\n.", "_\u1680\u0085.", "_\u1680\u2028.", "_\u1680\u2029."),
                new TestData("_\nData", "_ \rData", "_ \nData", "_ \r\nData", "_ \u0085Data", "_ \u2028Data", "_ \u2029Data", "_\t\rData", "_\t\nData", "_\t\r\nData", "_\t\u0085Data", "_\t\u2028Data", "_\t\u2029Data", "_\u000B\rData", "_\u000B\nData", "_\u000B\r\nData", "_\u000B\u0085Data", "_\u000B\u2028Data", "_\u000B\u2029Data", "_\f\rData", "_\f\nData", "_\f\r\nData", "_\f\u0085Data", "_\f\u2028Data", "_\f\u2029Data", "_\u00A0\rData", "_\u00A0\nData", "_\u00A0\r\nData", "_\u00A0\u0085Data", "_\u00A0\u2028Data", "_\u00A0\u2029Data", "_\u1680\rData", "_\u1680\nData", "_\u1680\r\nData", "_\u1680\u0085Data", "_\u1680\u2028Data", "_\u1680\u2029Data"),
                new TestData("Test\n.", "Test \r.", "Test \n.", "Test \r\n.", "Test \u0085.", "Test \u2028.", "Test \u2029.", "Test\t\r.", "Test\t\n.", "Test\t\r\n.", "Test\t\u0085.", "Test\t\u2028.", "Test\t\u2029.", "Test\u000B\r.", "Test\u000B\n.", "Test\u000B\r\n.", "Test\u000B\u0085.", "Test\u000B\u2028.", "Test\u000B\u2029.", "Test\f\r.", "Test\f\n.", "Test\f\r\n.", "Test\f\u0085.", "Test\f\u2028.", "Test\f\u2029.", "Test\u00A0\r.", "Test\u00A0\n.", "Test\u00A0\r\n.", "Test\u00A0\u0085.", "Test\u00A0\u2028.", "Test\u00A0\u2029.", "Test\u1680\r.", "Test\u1680\n.", "Test\u1680\r\n.", "Test\u1680\u0085.", "Test\u1680\u2028.", "Test\u1680\u2029."),
                new TestData("Test\nData", "Test \rData", "Test \nData", "Test \r\nData", "Test \u0085Data", "Test \u2028Data", "Test \u2029Data", "Test\t\rData", "Test\t\nData", "Test\t\r\nData", "Test\t\u0085Data", "Test\t\u2028Data", "Test\t\u2029Data", "Test\u000B\rData", "Test\u000B\nData", "Test\u000B\r\nData", "Test\u000B\u0085Data", "Test\u000B\u2028Data", "Test\u000B\u2029Data", "Test\f\rData", "Test\f\nData", "Test\f\r\nData", "Test\f\u0085Data", "Test\f\u2028Data", "Test\f\u2029Data", "Test\u00A0\rData", "Test\u00A0\nData", "Test\u00A0\r\nData", "Test\u00A0\u0085Data", "Test\u00A0\u2028Data", "Test\u00A0\u2029Data", "Test\u1680\rData", "Test\u1680\nData", "Test\u1680\r\nData", "Test\u1680\u0085Data", "Test\u1680\u2028Data", "Test\u1680\u2029Data"),
                new TestData(".\n_", ".\r _", ".\n _", ".\r\n _", ".\u0085 _", ".\u2028 _", ".\u2029 _", ".\r\t_", ".\n\t_", ".\r\n\t_", ".\u0085\t_", ".\u2028\t_", ".\u2029\t_", ".\r\u000B_", ".\n\u000B_", ".\r\n\u000B_", ".\u0085\u000B_", ".\u2028\u000B_", ".\u2029\u000B_", ".\r\f_", ".\n\f_", ".\r\n\f_", ".\u0085\f_", ".\u2028\f_", ".\u2029\f_", ".\r\u00A0_", ".\n\u00A0_", ".\r\n\u00A0_", ".\u0085\u00A0_", ".\u2028\u00A0_", ".\u2029\u00A0_", ".\r\u1680_", ".\n\u1680_", ".\r\n\u1680_", ".\u0085\u1680_", ".\u2028\u1680_", ".\u2029\u1680_"),
                new TestData("Data\n_", "Data\r _", "Data\n _", "Data\r\n _", "Data\u0085 _", "Data\u2028 _", "Data\u2029 _", "Data\r\t_", "Data\n\t_", "Data\r\n\t_", "Data\u0085\t_", "Data\u2028\t_", "Data\u2029\t_", "Data\r\u000B_", "Data\n\u000B_", "Data\r\n\u000B_", "Data\u0085\u000B_", "Data\u2028\u000B_", "Data\u2029\u000B_", "Data\r\f_", "Data\n\f_", "Data\r\n\f_", "Data\u0085\f_", "Data\u2028\f_", "Data\u2029\f_", "Data\r\u00A0_", "Data\n\u00A0_", "Data\r\n\u00A0_", "Data\u0085\u00A0_", "Data\u2028\u00A0_", "Data\u2029\u00A0_", "Data\r\u1680_", "Data\n\u1680_", "Data\r\n\u1680_", "Data\u0085\u1680_", "Data\u2028\u1680_", "Data\u2029\u1680_"),
                new TestData(".\nTest", ".\r Test", ".\n Test", ".\r\n Test", ".\u0085 Test", ".\u2028 Test", ".\u2029 Test", ".\r\tTest", ".\n\tTest", ".\r\n\tTest", ".\u0085\tTest", ".\u2028\tTest", ".\u2029\tTest", ".\r\u000BTest", ".\n\u000BTest", ".\r\n\u000BTest", ".\u0085\u000BTest", ".\u2028\u000BTest", ".\u2029\u000BTest", ".\r\fTest", ".\n\fTest", ".\r\n\fTest", ".\u0085\fTest", ".\u2028\fTest", ".\u2029\fTest", ".\r\u00A0Test", ".\n\u00A0Test", ".\r\n\u00A0Test", ".\u0085\u00A0Test", ".\u2028\u00A0Test", ".\u2029\u00A0Test", ".\r\u1680Test", ".\n\u1680Test", ".\r\n\u1680Test", ".\u0085\u1680Test", ".\u2028\u1680Test", ".\u2029\u1680Test"),
                new TestData("Data\nTest", "Data\r Test", "Data\n Test", "Data\r\n Test", "Data\u0085 Test", "Data\u2028 Test", "Data\u2029 Test", "Data\r\tTest", "Data\n\tTest", "Data\r\n\tTest", "Data\u0085\tTest", "Data\u2028\tTest", "Data\u2029\tTest", "Data\r\u000BTest", "Data\n\u000BTest", "Data\r\n\u000BTest", "Data\u0085\u000BTest", "Data\u2028\u000BTest", "Data\u2029\u000BTest", "Data\r\fTest", "Data\n\fTest", "Data\r\n\fTest", "Data\u0085\fTest", "Data\u2028\fTest", "Data\u2029\fTest", "Data\r\u00A0Test", "Data\n\u00A0Test", "Data\r\n\u00A0Test", "Data\u0085\u00A0Test", "Data\u2028\u00A0Test", "Data\u2029\u00A0Test", "Data\r\u1680Test", "Data\n\u1680Test", "Data\r\n\u1680Test", "Data\u0085\u1680Test", "Data\u2028\u1680Test", "Data\u2029\u1680Test"),
                new TestData("\n\n_", "\r\r_", "\n\r_", "\r\n\r_", "\u0085\r_", "\u2028\r_", "\u2029\r_", "\n\n_", "\r\n\n_", "\u0085\n_", "\u2028\n_", "\u2029\n_", "\r\r\n_", "\n\r\n_", "\r\n\r\n_", "\u0085\r\n_", "\u2028\r\n_", "\u2029\r\n_", "\r\u0085_", "\n\u0085_", "\r\n\u0085_", "\u0085\u0085_", "\u2028\u0085_", "\u2029\u0085_", "\r\u2028_", "\n\u2028_", "\r\n\u2028_", "\u0085\u2028_", "\u2028\u2028_", "\u2029\u2028_", "\r\u2029_", "\n\u2029_", "\r\n\u2029_", "\u0085\u2029_", "\u2028\u2029_", "\u2029\u2029_"),
                new TestData("\n\nTest", "\r\rTest", "\n\rTest", "\r\n\rTest", "\u0085\rTest", "\u2028\rTest", "\u2029\rTest", "\n\nTest", "\r\n\nTest", "\u0085\nTest", "\u2028\nTest", "\u2029\nTest", "\r\r\nTest", "\n\r\nTest", "\r\n\r\nTest", "\u0085\r\nTest", "\u2028\r\nTest", "\u2029\r\nTest", "\r\u0085Test", "\n\u0085Test", "\r\n\u0085Test", "\u0085\u0085Test", "\u2028\u0085Test", "\u2029\u0085Test", "\r\u2028Test", "\n\u2028Test", "\r\n\u2028Test", "\u0085\u2028Test", "\u2028\u2028Test", "\u2029\u2028Test", "\r\u2029Test", "\n\u2029Test", "\r\n\u2029Test", "\u0085\u2029Test", "\u2028\u2029Test", "\u2029\u2029Test"),
                new TestData("_ .\n", "_ .\r", "_ .\n", "_ .\r\n", "_ .\u0085", "_ .\u2028", "_ .\u2029"),
                new TestData("_ Data\n", "_ Data\r", "_ Data\n", "_ Data\r\n", "_ Data\u0085", "_ Data\u2028", "_ Data\u2029"),
                new TestData("_\t.\n", "_\t.\r", "_\t.\n", "_\t.\r\n", "_\t.\u0085", "_\t.\u2028", "_\t.\u2029"),
                new TestData("_\tData\n", "_\tData\r", "_\tData\n", "_\tData\r\n", "_\tData\u0085", "_\tData\u2028", "_\tData\u2029"),
                new TestData("_\u000B.\n", "_\u000B.\r", "_\u000B.\n", "_\u000B.\r\n", "_\u000B.\u0085", "_\u000B.\u2028", "_\u000B.\u2029"),
                new TestData("_\u000BData\n", "_\u000BData\r", "_\u000BData\n", "_\u000BData\r\n", "_\u000BData\u0085", "_\u000BData\u2028", "_\u000BData\u2029"),
                new TestData("_\f.\n", "_\f.\r", "_\f.\n", "_\f.\r\n", "_\f.\u0085", "_\f.\u2028", "_\f.\u2029"),
                new TestData("_\fData\n", "_\fData\r", "_\fData\n", "_\fData\r\n", "_\fData\u0085", "_\fData\u2028", "_\fData\u2029"),
                new TestData("_\u00A0.\n", "_\u00A0.\r", "_\u00A0.\n", "_\u00A0.\r\n", "_\u00A0.\u0085", "_\u00A0.\u2028", "_\u00A0.\u2029"),
                new TestData("_\u00A0Data\n", "_\u00A0Data\r", "_\u00A0Data\n", "_\u00A0Data\r\n", "_\u00A0Data\u0085", "_\u00A0Data\u2028", "_\u00A0Data\u2029"),
                new TestData("_\u1680.\n", "_\u1680.\r", "_\u1680.\n", "_\u1680.\r\n", "_\u1680.\u0085", "_\u1680.\u2028", "_\u1680.\u2029"),
                new TestData("_\u1680Data\n", "_\u1680Data\r", "_\u1680Data\n", "_\u1680Data\r\n", "_\u1680Data\u0085", "_\u1680Data\u2028", "_\u1680Data\u2029"),
                new TestData("Test .\n", "Test .\r", "Test .\n", "Test .\r\n", "Test .\u0085", "Test .\u2028", "Test .\u2029"),
                new TestData("Test Data\n", "Test Data\r", "Test Data\n", "Test Data\r\n", "Test Data\u0085", "Test Data\u2028", "Test Data\u2029"),
                new TestData("Test\t.\n", "Test\t.\r", "Test\t.\n", "Test\t.\r\n", "Test\t.\u0085", "Test\t.\u2028", "Test\t.\u2029"),
                new TestData("Test\tData\n", "Test\tData\r", "Test\tData\n", "Test\tData\r\n", "Test\tData\u0085", "Test\tData\u2028", "Test\tData\u2029"),
                new TestData("Test\u000B.\n", "Test\u000B.\r", "Test\u000B.\n", "Test\u000B.\r\n", "Test\u000B.\u0085", "Test\u000B.\u2028", "Test\u000B.\u2029"),
                new TestData("Test\u000BData\n", "Test\u000BData\r", "Test\u000BData\n", "Test\u000BData\r\n", "Test\u000BData\u0085", "Test\u000BData\u2028", "Test\u000BData\u2029"),
                new TestData("Test\f.\n", "Test\f.\r", "Test\f.\n", "Test\f.\r\n", "Test\f.\u0085", "Test\f.\u2028", "Test\f.\u2029"),
                new TestData("Test\fData\n", "Test\fData\r", "Test\fData\n", "Test\fData\r\n", "Test\fData\u0085", "Test\fData\u2028", "Test\fData\u2029"),
                new TestData("Test\u00A0.\n", "Test\u00A0.\r", "Test\u00A0.\n", "Test\u00A0.\r\n", "Test\u00A0.\u0085", "Test\u00A0.\u2028", "Test\u00A0.\u2029"),
                new TestData("Test\u00A0Data\n", "Test\u00A0Data\r", "Test\u00A0Data\n", "Test\u00A0Data\r\n", "Test\u00A0Data\u0085", "Test\u00A0Data\u2028", "Test\u00A0Data\u2029"),
                new TestData("Test\u1680.\n", "Test\u1680.\r", "Test\u1680.\n", "Test\u1680.\r\n", "Test\u1680.\u0085", "Test\u1680.\u2028", "Test\u1680.\u2029"),
                new TestData("Test\u1680Data\n", "Test\u1680Data\r", "Test\u1680Data\n", "Test\u1680Data\r\n", "Test\u1680Data\u0085", "Test\u1680Data\u2028", "Test\u1680Data\u2029"),
        };
        for (TestData t : testData) {
            String expected = t.expected;
            t.source.forEach(u -> {
                StringLineIterator target = StringLineIterator.create(u.source, false, false);
                String description = u.description;
                assertTrue(description, target.hasNext());
                String line = target.next();
                String d = String.format("%s, index 0", description);
                Assert.assertNotNull(d, line);
                Assert.assertFalse(d, line.contains("\n"));
                StringBuilder result = new StringBuilder(line);
                int index = 0;
                while (target.hasNext()) {
                    line = target.next();
                    d = String.format("%s, index %d", description, ++index);
                    Assert.assertNotNull(d, line);
                    Assert.assertFalse(d, line.contains("\n"));
                    result.append("\n").append(line);
                }
                assertEquals(description, expected, result.toString());
            });
        }
    }

    public static class SourceValue {
        private final String source;
        private final String description;

        public SourceValue(String source) {
            this.source = source;
            this.description = (source == null) ? "[null]" : ((source.isEmpty()) ? "[empty]" : source.replace(" ", "[SPACE]")
                    .replace("\t", "[TAB]").replace("\r", "[CR]").replace("\n", "[LF]")
                    .replace("\u2028", "[LS]").replace("\u2029", "[PS]"));
        }

    }

    public static class TestData {

        private static final Pattern NL = Pattern.compile("\\n");
        private final List<SourceValue> source;
        private final String expected;

        public TestData(String expected, String... source) {
            List<SourceValue> src = new ArrayList<>();
            for (String s : source) {
                src.add(new SourceValue(s));
            }
            this.source = Collections.unmodifiableList(src);
            this.expected = expected;
        }
    }

}