package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.core.util.Pair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings({"ConstantConditions", "StreamToLoop"})
public class StringNormalizerTest {

    @Test
    public void trimStart() {
        String actual = StringNormalizer.trimStart(null);
        assertNull(actual);
        String expected = "";
        actual = StringNormalizer.trimStart("");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" \r\n\t ");
        assertEquals(expected, actual);
        expected = "Test";
        actual = StringNormalizer.trimStart("Test");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" Test");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" \r\n\t Test");
        assertEquals(expected, actual);
        expected = "Test ";
        actual = StringNormalizer.trimStart("Test ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" Test ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" \r\n\t Test ");
        assertEquals(expected, actual);
        expected = "Test \n\r\t ";
        actual = StringNormalizer.trimStart("Test \n\r\t ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" Test \n\r\t ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimStart(" \r\n\t Test \n\r\t ");
        assertEquals(expected, actual);
    }

    @Test
    public void trimEnd() {
        String actual = StringNormalizer.trimEnd(null);
        assertNull(actual);
        String expected = "";
        actual = StringNormalizer.trimEnd("");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" \r\n\t ");
        assertEquals(expected, actual);
        expected = "Test";
        actual = StringNormalizer.trimEnd("Test");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd("Test ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd("Test \r\n\t ");
        assertEquals(expected, actual);
        expected = " Test";
        actual = StringNormalizer.trimEnd(" Test");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" Test ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" Test \r\n\t ");
        assertEquals(expected, actual);
        expected = " \n\r\t Test";
        actual = StringNormalizer.trimEnd(" \n\r\t Test");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" \n\r\t Test ");
        assertEquals(expected, actual);
        actual = StringNormalizer.trimEnd(" \n\r\t Test \r\n\t ");
        assertEquals(expected, actual);
    }

    @Test
    public void getStringNormalizer0() {
        assertEquals(0, StringNormalizationOption.toFlags());
        assertEquals(1, StringNormalizationOption.toFlags(StringNormalizationOption.PASS_NULL_VALUE));
        Function<String, String> target1 = StringNormalizer.getNormalizer();
        Function<String, String> target2 = StringNormalizer.getNormalizer(StringNormalizationOption.PASS_NULL_VALUE);
        String expected = "";
        String actual = target1.apply(null);
        assertEquals(expected, actual);
        actual = target2.apply(null);
        assertNull(actual);
        actual = target1.apply("");
        assertEquals(expected, actual);
        actual = target2.apply("");
        assertEquals(expected, actual);
        ArrayList<Pair<String, String>> temp = new ArrayList<>();
        temp.add(new Pair<>("Test", "Test"));
        List<String> spaceOptions = Arrays.asList("[space]", "[tab]", "[space][space]", "[space][tab]", "[tab][space]", "[tab][tab]");
        spaceOptions.forEach(t -> temp.add(new Pair<>("Test Data", "Test" + t + "Data")));
        ArrayList<Pair<String, String>> testData = new ArrayList<>();
        temp.forEach(t -> {
            testData.add(t);
            spaceOptions.forEach(u -> {
                testData.add(new Pair<>(t.first + " ", t.second + u));
            });
        });
        temp.clear();
        testData.forEach(t -> {
            temp.add(t);
            spaceOptions.forEach(u -> {
                temp.add(new Pair<>(" " + t.first, u + t.second));
            });
        });
        List<String> newLineOptions = Arrays.asList("\\r", "\\n", "\\r\\n");
        testData.clear();
        spaceOptions.forEach(t -> {
            testData.add(new Pair<>(" ", t));
            newLineOptions.forEach(u -> {
                testData.add(new Pair<>(" \n", t + u));
                testData.add(new Pair<>("\n ", u + t));
                spaceOptions.forEach(s -> testData.add(new Pair<>(" \n ", t + u + s)));
            });
        });
        newLineOptions.forEach(t -> {
            testData.add(new Pair<>("\n", t));
            spaceOptions.forEach(u -> {
                newLineOptions.forEach(s -> testData.add(new Pair<>("\n \n", t + u + s)));
            });
        });
        temp.forEach(t -> {
            testData.add(t);
            if (!t.first.contains("Data")) {
                newLineOptions.forEach(u -> {
                    testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data"));
                    testData.add(new Pair<>(t.first + "\n", t.second + u));
                    testData.add(new Pair<>("\n" + t.first, u + t.second));
                    newLineOptions.forEach(s -> {
                        testData.add(new Pair<>("\n" + t.first + "\n", u + t.second + s));
                    });
                    spaceOptions.forEach(s -> {
                        testData.add(new Pair<>(t.first + "\nData ", t.second + u + "Data" + s));
                        testData.add(new Pair<>(t.first + "\n Data", t.second + u + s + "Data"));
                        spaceOptions.forEach(v -> {
                            testData.add(new Pair<>(t.first + "\n Data ", t.second + u + s + "Data" + v));
                        });
                    });
                });
            }
        });
        Iterator<Pair<String, String>> iterator = testData.iterator();
        iterator.next();
        do {
            Pair<String, String> p = iterator.next();
            String source = p.second.replace("\\r", "\r").replace("\\n", "\n").replace("[tab]", "\t")
                    .replace("[space]", " ");
            actual = target1.apply(source);
            assertEquals(p.second, p.first, actual);
            actual = target2.apply(source);
            assertEquals(p.second, p.first, actual);
        } while (iterator.hasNext());
    }

    @Test
    public void getStringNormalizer2() {
        assertEquals(2, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM_START));
        assertEquals(3, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM_START, StringNormalizationOption.PASS_NULL_VALUE));
        Function<String, String> target1 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM_START);
        Function<String, String> target2 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM_START, StringNormalizationOption.PASS_NULL_VALUE);
        String expected = "";
        String actual = target1.apply(null);
        assertEquals(expected, actual);
        actual = target2.apply(null);
        assertNull(actual);
        actual = target1.apply("");
        assertEquals(expected, actual);
        actual = target2.apply("");
        assertEquals(expected, actual);
        ArrayList<Pair<String, String>> temp = new ArrayList<>();
        temp.add(new Pair<>("Test", "Test"));
        List<String> spaceOptions = Arrays.asList("[space]", "[tab]", "[space][space]", "[space][tab]", "[tab][space]", "[tab][tab]");
        spaceOptions.forEach(t -> temp.add(new Pair<>("Test Data", "Test" + t + "Data")));
        ArrayList<Pair<String, String>> testData = new ArrayList<>();
        temp.forEach(t -> {
            testData.add(t);
            spaceOptions.forEach(u -> {
                testData.add(new Pair<>(t.first + " ", t.second + u));
            });
        });
        temp.clear();
        testData.forEach(t -> {
            temp.add(t);
            spaceOptions.forEach(u -> {
                temp.add(new Pair<>(t.first, u + t.second));
            });
        });
        List<String> newLineOptions = Arrays.asList("\\r", "\\n", "\\r\\n");
        testData.clear();
        spaceOptions.forEach(t -> {
            testData.add(new Pair<>("", t));
            newLineOptions.forEach(u -> {
                testData.add(new Pair<>("\n", t + u));
                testData.add(new Pair<>("\n", u + t));
                spaceOptions.forEach(s -> testData.add(new Pair<>("\n", t + u + s)));
            });
        });
        newLineOptions.forEach(t -> {
            testData.add(new Pair<>("\n", t));
            spaceOptions.forEach(u -> {
                newLineOptions.forEach(s -> testData.add(new Pair<>("\n\n", t + u + s)));
            });
        });
        temp.forEach(t -> {
            testData.add(t);
            if (!t.first.contains("Data")) {
                newLineOptions.forEach(u -> {
                    testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data"));
                    testData.add(new Pair<>(t.first + "\n", t.second + u));
                    testData.add(new Pair<>("\n" + t.first, u + t.second));
                    newLineOptions.forEach(s -> {
                        testData.add(new Pair<>("\n" + t.first + "\n", u + t.second + s));
                    });
                    spaceOptions.forEach(s -> {
                        testData.add(new Pair<>(t.first + "\nData ", t.second + u + "Data" + s));
                        testData.add(new Pair<>(t.first + "\nData", t.second + u + s + "Data"));
                        spaceOptions.forEach(v -> {
                            testData.add(new Pair<>(t.first + "\nData ", t.second + u + s + "Data" + v));
                        });
                    });
                });
            }
        });
        Iterator<Pair<String, String>> iterator = testData.iterator();
        iterator.next();
        do {
            Pair<String, String> p = iterator.next();
            String source = p.second.replace("\\r", "\r").replace("\\n", "\n").replace("[tab]", "\t")
                    .replace("[space]", " ");
            actual = target1.apply(source);
            assertEquals(p.second, p.first, actual);
            actual = target2.apply(source);
            assertEquals(p.second, p.first, actual);
        } while (iterator.hasNext());

    }

    @Test
    public void getStringNormalizer4() {
        assertEquals(4, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM_END));
        assertEquals(5, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM_END, StringNormalizationOption.PASS_NULL_VALUE));
        Function<String, String> target1 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM_END);
        Function<String, String> target2 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM_END, StringNormalizationOption.PASS_NULL_VALUE);
        String expected = "";
        String actual = target1.apply(null);
        assertEquals(expected, actual);
        actual = target2.apply(null);
        assertNull(actual);
        actual = target1.apply("");
        assertEquals(expected, actual);
        actual = target2.apply("");
        assertEquals(expected, actual);
        ArrayList<Pair<String, String>> temp = new ArrayList<>();
        temp.add(new Pair<>("Test", "Test"));
        List<String> spaceOptions = Arrays.asList("[space]", "[tab]", "[space][space]", "[space][tab]", "[tab][space]", "[tab][tab]");
        spaceOptions.forEach(t -> temp.add(new Pair<>("Test Data", "Test" + t + "Data")));
        ArrayList<Pair<String, String>> testData = new ArrayList<>();
        temp.forEach(t -> {
            testData.add(t);
            spaceOptions.forEach(u -> {
                testData.add(new Pair<>(t.first, t.second + u));
            });
        });
        temp.clear();
        testData.forEach(t -> {
            temp.add(t);
            spaceOptions.forEach(u -> {
                temp.add(new Pair<>(" " + t.first, u + t.second));
            });
        });
        List<String> newLineOptions = Arrays.asList("\\r", "\\n", "\\r\\n");
        testData.clear();
        spaceOptions.forEach(t -> {
            testData.add(new Pair<>("", t));
            newLineOptions.forEach(u -> {
                testData.add(new Pair<>("\n", t + u));
                testData.add(new Pair<>("\n", u + t));
                spaceOptions.forEach(s -> testData.add(new Pair<>("\n", t + u + s)));
            });
        });
        newLineOptions.forEach(t -> {
            testData.add(new Pair<>("\n", t));
            spaceOptions.forEach(u -> {
                newLineOptions.forEach(s -> testData.add(new Pair<>("\n\n", t + u + s)));
            });
        });
        temp.forEach(t -> {
            testData.add(t);
            if (!t.first.contains("Data")) {
                newLineOptions.forEach(u -> {
                    testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data"));
                    testData.add(new Pair<>(t.first + "\n", t.second + u));
                    testData.add(new Pair<>("\n" + t.first, u + t.second));
                    newLineOptions.forEach(s -> {
                        testData.add(new Pair<>("\n" + t.first + "\n", u + t.second + s));
                    });
                    spaceOptions.forEach(s -> {
                        testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data" + s));
                        testData.add(new Pair<>(t.first + "\n Data", t.second + u + s + "Data"));
                        spaceOptions.forEach(v -> {
                            testData.add(new Pair<>(t.first + "\n Data", t.second + u + s + "Data" + v));
                        });
                    });
                });
            }
        });
        Iterator<Pair<String, String>> iterator = testData.iterator();
        iterator.next();
        do {
            Pair<String, String> p = iterator.next();
            String source = p.second.replace("\\r", "\r").replace("\\n", "\n").replace("[tab]", "\t")
                    .replace("[space]", " ");
            actual = target1.apply(source);
            assertEquals(p.second, p.first, actual);
            actual = target2.apply(source);
            assertEquals(p.second, p.first, actual);
        } while (iterator.hasNext());
    }

    @Test
    public void getStringNormalizer6() {
        assertEquals(6, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM));
        assertEquals(7, StringNormalizationOption.toFlags(StringNormalizationOption.TRIM, StringNormalizationOption.PASS_NULL_VALUE));
        Function<String, String> target1 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM);
        Function<String, String> target2 = StringNormalizer.getNormalizer(StringNormalizationOption.TRIM, StringNormalizationOption.PASS_NULL_VALUE);
        String expected = "";
        String actual = target1.apply(null);
        assertEquals(expected, actual);
        actual = target2.apply(null);
        assertNull(actual);
        actual = target1.apply("");
        assertEquals(expected, actual);
        actual = target2.apply("");
        assertEquals(expected, actual);
        ArrayList<Pair<String, String>> temp = new ArrayList<>();
        temp.add(new Pair<>("Test", "Test"));
        List<String> spaceOptions = Arrays.asList("[space]", "[tab]", "[space][space]", "[space][tab]", "[tab][space]", "[tab][tab]");
        spaceOptions.forEach(t -> temp.add(new Pair<>("Test Data", "Test" + t + "Data")));
        ArrayList<Pair<String, String>> testData = new ArrayList<>();
        temp.forEach(t -> {
            testData.add(t);
            spaceOptions.forEach(u -> {
                testData.add(new Pair<>(t.first, t.second + u));
            });
        });
        temp.clear();
        testData.forEach(t -> {
            temp.add(t);
            spaceOptions.forEach(u -> {
                temp.add(new Pair<>(t.first, u + t.second));
            });
        });
        List<String> newLineOptions = Arrays.asList("\\r", "\\n", "\\r\\n");
        testData.clear();
        spaceOptions.forEach(t -> {
            testData.add(new Pair<>("", t));
            newLineOptions.forEach(u -> {
                testData.add(new Pair<>("\n", t + u));
                testData.add(new Pair<>("\n", u + t));
                spaceOptions.forEach(s -> testData.add(new Pair<>("\n", t + u + s)));
            });
        });
        newLineOptions.forEach(t -> {
            testData.add(new Pair<>("\n", t));
            spaceOptions.forEach(u -> {
                newLineOptions.forEach(s -> testData.add(new Pair<>("\n\n", t + u + s)));
            });
        });
        temp.forEach(t -> {
            testData.add(t);
            if (!t.first.contains("Data")) {
                newLineOptions.forEach(u -> {
                    testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data"));
                    testData.add(new Pair<>(t.first + "\n", t.second + u));
                    testData.add(new Pair<>("\n" + t.first, u + t.second));
                    newLineOptions.forEach(s -> {
                        testData.add(new Pair<>("\n" + t.first + "\n", u + t.second + s));
                    });
                    spaceOptions.forEach(s -> {
                        testData.add(new Pair<>(t.first + "\nData", t.second + u + "Data" + s));
                        testData.add(new Pair<>(t.first + "\nData", t.second + u + s + "Data"));
                        spaceOptions.forEach(v -> {
                            testData.add(new Pair<>(t.first + "\nData", t.second + u + s + "Data" + v));
                        });
                    });
                });
            }
        });
        Iterator<Pair<String, String>> iterator = testData.iterator();
        iterator.next();
        do {
            Pair<String, String> p = iterator.next();
            String source = p.second.replace("\\r", "\r").replace("\\n", "\n").replace("[tab]", "\t")
                    .replace("[space]", " ");
            actual = target1.apply(source);
            assertEquals(p.second, p.first, actual);
            actual = target2.apply(source);
            assertEquals(p.second, p.first, actual);
        } while (iterator.hasNext());
    }

}