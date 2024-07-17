package edu.gatech.seclass.moditext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class MyMainTest {
    // Place all of your tests in this class, optionally using MainTest.java as an example
    private final String usageStr =
        "Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE"
            + System.lineSeparator();

    @TempDir
    Path tempDirectory;

    @RegisterExtension
    OutputCapture capture = new OutputCapture();

    /* ----------------------------- Test Utilities ----------------------------- */

    /**
     * Returns path of a new "input.txt" file with specified contents written
     * into it. The file will be created using {@link TempDir TempDir}, so it
     * is automatically deleted after test execution.
     *
     * @param contents the text to include in the file
     * @return a Path to the newly written file, or null if there was an
     *         issue creating the file
     */
    private Path createFile(String contents) {
        return createFile(contents, "input.txt");
    }

    /**
     * Returns path to newly created file with specified contents written into
     * it. The file will be created using {@link TempDir TempDir}, so it is
     * automatically deleted after test execution.
     *
     * @param contents the text to include in the file
     * @param fileName the desired name for the file to be created
     * @return a Path to the newly written file, or null if there was an
     *         issue creating the file
     */
    private Path createFile(String contents, String fileName) {
        Path file = tempDirectory.resolve(fileName);
        try {
            Files.writeString(file, contents);
        } catch (IOException e) {
            return null;
        }

        return file;
    }

    /**
     * Takes the path to some file and returns the contents within.
     *
     * @param file the path to some file
     * @return the contents of the file as a String, or null if there was an
     *         issue reading the file
     */
    private String getFileContent(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ------------------------------- Test Cases ------------------------------- */

    @Test
    void moditextTest1() {
        // Frame #: 1.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest2() {
        // Frame #: 1.1.1.1.2
        Path inputFile = createFile("sample text" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest3() {
        // Frame #: 1.1.1.2.1
        Path inputFile = createFile("test content" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest4() {
        // Frame #: 1.1.1.2.2
        Path inputFile = createFile("test test content" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test test content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest5() {
        // Frame #: 1.1.1.3.1
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("test content").append(System.lineSeparator());
        for (int i = 0; i < 10; i++) {
            contentBuilder.append("line ").append(i).append(System.lineSeparator());
        }
        Path inputFile = createFile(contentBuilder.toString());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("test content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest6() {
        // Frame #: 1.1.1.3.2
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            contentBuilder.append("test line ").append(i).append(System.lineSeparator());
        }
        Path inputFile = createFile(contentBuilder.toString());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            expectedOutputBuilder.append("test line ").append(i).append(System.lineSeparator());
        }

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest7() {
        // Frame #: 2.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest8() {
        // Frame #: 2.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        String output = capture.stdout();

        Assertions.assertEquals("sample content" + System.lineSeparator(), output);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest9() {
        // Frame #: 2.1.1.2.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(10));
        String[] args = {"-p", "*", "20", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "********test content" + System.lineSeparator();
        for (int i = 0; i < 9; i++) {
            expectedOutput += "********************" + System.lineSeparator();
        }

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest10() {
        // Frame #: 2.1.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(10) + "test" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder("test content" + System.lineSeparator());
        for (int i = 0; i < 9; i++) {
            expectedOutputBuilder.append("**********" + System.lineSeparator());
        }
        expectedOutputBuilder.append("******test" + System.lineSeparator());
        String expectedOutput = expectedOutputBuilder.toString();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest11() {
        // Frame #: 2.2.1.1.1
        Path inputFile = createFile("non-empty" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*non-empty" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest12() {
        // Frame #: 3.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest13() {
        // Frame #: 3.1.1.2.1
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "1", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "t" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest14() {
        // Frame #: 3.1.1.2.2
        Path inputFile = createFile("test" + System.lineSeparator() + "content" + System.lineSeparator());
        String[] args = {"-t", "1", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "t" + System.lineSeparator() + "c" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest15() {
        // Frame #: 3.1.1.3.1
        Path inputFile = createFile("test" + System.lineSeparator().repeat(10));
        String[] args = {"-t", "1", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("t" + System.lineSeparator());
        for (int i = 0; i < 9; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }

        String expectedOutput = expectedOutputBuilder.toString();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest16() {
        // Frame #: 3.1.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(20) + "test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test conte" + System.lineSeparator().repeat(20) + "test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest17() {
        // Frame #: 3.2.1.2.2
        Path inputFile = createFile("non-empty test test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("non-empty " + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest18() {
        // Frame #: 4.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest19() {
        // Frame #: 4.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("sample content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest20() {
        // Frame #: 4.1.1.2.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(10));
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }
        expectedOutputBuilder.append("test content" + System.lineSeparator());

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest21() {
        // Frame #: 4.1.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(1000) + "test" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder("test" + System.lineSeparator());
        for (int i = 1; i < 1000; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }
        expectedOutputBuilder.append("test content").append(System.lineSeparator());

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest22() {
        // Frame #: 4.2.1.2.2
        Path inputFile = createFile("non-empty" + System.lineSeparator() + "test1" + System.lineSeparator() + "test2" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "test2" + System.lineSeparator() + "test1" + System.lineSeparator() + "non-empty" + System.lineSeparator();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest23() {
        // Frame #: 4.2.1.3.1
        Path inputFile = createFile("different test content" + System.lineSeparator().repeat(500));
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        for (int i = 1; i < 500; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }
        expectedOutputBuilder.append("different test content").append(System.lineSeparator());

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest24() {
        // Frame #: 4.2.1.3.2
        Path inputFile = createFile("different test content" + System.lineSeparator().repeat(500) + "test" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("test").append(System.lineSeparator());
        for (int i = 1; i < 500; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }

        expectedOutputBuilder.append("different test content").append(System.lineSeparator());

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest25() {
        // Frame #: 5.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest26() {
        // Frame #: 5.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);

        String actualOutput = capture.stdout();

        Assertions.assertEquals("", actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest27() {
        // Frame #: 5.1.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(1000) + "test" + System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder("test content");
        for (int i = 0; i < 1000; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }
        expectedOutputBuilder.append("test").append(System.lineSeparator());

        String actualOutput = capture.stdout();

        Assertions.assertEquals("", actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest28() {
        Path inputFile = createFile("test content" + System.lineSeparator() +"other content" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "other content" + System.lineSeparator() + "test content" + System.lineSeparator();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest29() {
        // Frame #: 5.1.2.2.1
        Path inputFile = createFile("multiple" + System.lineSeparator() + "lines" + System.lineSeparator() + "test" + System.lineSeparator() + "here" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "here" + System.lineSeparator() + "test" + System.lineSeparator() + "lines" + System.lineSeparator() + "multiple" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest30() {
        // Frame #: 5.1.2.3.2
        Path inputFile = createFile("mixed" + System.lineSeparator()
                + "content" + System.lineSeparator()
                + "test" + System.lineSeparator()
                + "lines" + System.lineSeparator()
                + "again" + System.lineSeparator()
                + "test" + System.lineSeparator()
                + "again" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "again" + System.lineSeparator() + "test" + System.lineSeparator()
                + "again" + System.lineSeparator() + "lines" + System.lineSeparator()
                + "test" + System.lineSeparator() + "content" + System.lineSeparator() + "mixed" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest31() {
        // File errors
        Path inputFile = createFile(System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest32() {
        // File errors
        Path inputFile = createFile("test");
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest33() {
        // File errors
        String[] args = {"-g", "-f", "code", "int"};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest34() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-k", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest35() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest36() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "a", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest37() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest38() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "3", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest39() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "", "3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest40() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "-", "0", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest41() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "-", "101", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest42() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "-", "-1", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest43() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "-", "2.5", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest44() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "abcdef", "2", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest45() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "a", "2", "-t", "3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest46() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest47() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-k", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest48() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest49() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "-1", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest50() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "101", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest51() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "2.5", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest52() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "2", "-p", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest53() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-g", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest54() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-g", "", "-f", "bold", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest55() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-g", "abcd", "-f", "bold", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest56() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest57() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f", "italic", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest58() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest59() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f", "test", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest60() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f", "italic", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest61() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-r", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest62() {
        // Option errors
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-r", "abcd", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }


    @Test
    void moditextTest63() {
        // single -f
        Path inputFile = createFile("lines" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-f", "bold", "again", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "lines" + System.lineSeparator() + "**again**" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest64() {
        // single -f
        Path inputFile = createFile("lines" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-f", "italic", "again", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "lines" + System.lineSeparator() + "*again*" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest65() {
        // single -f
        Path inputFile = createFile("lines" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-f", "code", "again", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "lines" + System.lineSeparator() + "`again`" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest66() {
        // Combine -k and -p
        Path inputFile = createFile("test" + System.lineSeparator() + "example" + System.lineSeparator() + "testing" + System.lineSeparator() + "check" + System.lineSeparator());
        String[] args = {"-p", "#", "10", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("######test" + System.lineSeparator() + "###testing" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest67() {
        // Combine -k and -t
        Path inputFile = createFile("test content" + System.lineSeparator() + "another test" + System.lineSeparator() + "random line" + System.lineSeparator() + "more tests" + System.lineSeparator());
        String[] args = {"-t", "5", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test " + System.lineSeparator() + "anoth" + System.lineSeparator() + "more " + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest68() {
        // Combine -k and -r
        Path inputFile = createFile("line1" +System.lineSeparator() +"line2" + System.lineSeparator() + "test1" +System.lineSeparator() + "test2" +System.lineSeparator());
        String[] args = {"-r", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test2" + System.lineSeparator() + "test1" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest69() {
        // Combine -k and -f with different styles
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simple line" + System.lineSeparator());
        String[] args = {"-f", "bold", "test", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this is a **test**" + System.lineSeparator() + "another **test** line" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest70() {
        // Combine -k and -g
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simple line" + System.lineSeparator());
        String[] args = {"-g", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest71() {
        // Combine -p and -g
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simple line" + System.lineSeparator());
        String[] args = {"-g", "-p", "t", "3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest72() {
        // Combine -p and -f
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-p", "-", "5", "-f", "italic", "line", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-this" + System.lineSeparator() + "another" + System.lineSeparator() + "-*line*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest73() {
        // Combine -p and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-p", "-", "5", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-line" + System.lineSeparator() + "another" + System.lineSeparator() + "-this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest74() {
        // Combine -t and -g
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-t", "5", "-g", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest75() {
        // Combine -t and -f
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-t", "5", "-f", "italic", "this", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*this*" + System.lineSeparator() + "anoth" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest76() {
        // Combine -t and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-t", "5", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("anoth" + System.lineSeparator() + "this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest77() {
        // Combine -g and -f
        Path inputFile = createFile("int is an integer" + System.lineSeparator() + "int types are common" + System.lineSeparator() + "integer types are ints" + System.lineSeparator());
        String[] args = {"-g", "-f", "code", "int", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("`int` is an `int`eger" + System.lineSeparator() + "`int` types are common" + System.lineSeparator() + "`int`eger types are `int`s" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest78() {
        // Combine -g and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-g", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest79() {
        // Combine -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-f", "italic", "this", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("another" + System.lineSeparator() + "*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest80() {
        // Combine -k and -p and -f
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-k", "this", "-p", "-", "5", "-f", "italic", "this", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest81() {
        // Combine -k and -p and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "this line" + System.lineSeparator());
        String[] args = {"-k", "this", "-p", "-", "5", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this line" + System.lineSeparator() + "-this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest82() {
        // Combine -k and -t and -f
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-k", "this", "-t", "3", "-f", "italic", "thi", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*thi*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest83() {
        // Combine -k and -t and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this" + System.lineSeparator());
        String[] args = {"-k", "this", "-t", "4", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line" + System.lineSeparator() + "this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest84() {
        // Combine -k and -p and -g and -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this abc this" + System.lineSeparator());
        String[] args = {"-k", "a", "-k", "this", "-p", "-", "1", "-p", "-", "5", "-g", "-f", "bold", "a", "-f", "italic", "this", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line *this* abc *this*" + System.lineSeparator() + "-*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest85() {
        // Combine -k and -t and -g and -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this abc this" + System.lineSeparator());
        String[] args = {"-k", "a", "-k", "this", "-t", "1", "-t", "4", "-g", "-f", "bold", "a", "-f", "italic", "this", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line" + System.lineSeparator() + "*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }
}
