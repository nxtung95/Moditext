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
     * issue creating the file
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
     * issue creating the file
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
     * issue reading the file
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
        // Frame #: 1.2.1.1.1
        Path inputFile = createFile("non-empty" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest8() {
        // Frame #: 1.2.1.1.2
        Path inputFile = createFile("non-empty test" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("non-empty test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest9() {
        // Frame #: 1.2.1.2.1
        Path inputFile = createFile("test non-empty" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test non-empty" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest10() {
        // Frame #: 1.2.1.2.2
        Path inputFile = createFile("non-empty" + System.lineSeparator() + "test" + System.lineSeparator() + "test" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);

        String actualOutput = capture.stdout();

        Assertions.assertEquals("test" + System.lineSeparator() + "test" + System.lineSeparator(), actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest11() {
        // Frame #: 1.2.1.3.1
        Path inputFile = createFile("different content" + System.lineSeparator().repeat(500));
        String[] args = {"-k", "test", inputFile.toString()};

        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest12() {
        // Frame #: 1.2.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(1000) + "test" + System.lineSeparator());
        String[] args = {"-k", "test", inputFile.toString()};
        Main.main(args);
        String expectedOutput = "test content" + System.lineSeparator() + "test" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest13() {
        // Frame #: 2.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest14() {
        // Frame #: 2.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        String output = capture.stdout();
        System.out.println("Captured output: '" + output + "'");

        Assertions.assertEquals("sample content" + System.lineSeparator(), output);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest15() {
        // Frame #: 2.1.1.2.1
        Path inputFile = createFile("test content" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        String output = capture.stdout();
        System.out.println("Captured Output: '" + output + "'");

        Assertions.assertEquals("test content" + System.lineSeparator(), output);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest16() {
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
    void moditextTest17() {
        // Frame #: 2.1.1.3.1
        Path inputFile = createFile("test content" + System.lineSeparator() + System.lineSeparator().repeat(10));
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder("test content" + System.lineSeparator());
        for (int i = 0; i < 10; i++) {
            expectedOutputBuilder.append("**********" + System.lineSeparator());
        }

        String expectedOutput = expectedOutputBuilder.toString();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest18() {
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
    void moditextTest19() {
        // Frame #: 2.2.1.1.1
        Path inputFile = createFile("non-empty" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*non-empty" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest20() {
        // Frame #: 2.2.1.1.2
        Path inputFile = createFile("non-empty test" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("non-empty test").append(System.lineSeparator());

        String expectedOutput = expectedOutputBuilder.toString();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest21() {
        // Frame #: 2.2.1.2.1
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-p", "*", "5", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "*test" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest22() {
        // Frame #: 2.2.1.2.2
        Path inputFile = createFile("test" + System.lineSeparator().repeat(5));
        String[] args = {"-p", "*", "5", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("*test" + System.lineSeparator());
        for (int i = 0; i < 4; i++) {
            expectedOutputBuilder.append("*****" + System.lineSeparator());
        }

        String expectedOutput = expectedOutputBuilder.toString();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest23() {
        // Frame #: 2.2.1.3.1
        Path inputFile = createFile("test" + System.lineSeparator().repeat(10));
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("******test" + System.lineSeparator());
        for (int i = 0; i < 9; i++) {
            expectedOutputBuilder.append("**********" + System.lineSeparator());
        }

        String expectedOutput = expectedOutputBuilder.toString();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest24() {
        // Frame #: 2.2.1.3.2
        Path inputFile = createFile("test" + System.lineSeparator().repeat(10) + "test" + System.lineSeparator());
        String[] args = {"-p", "*", "10", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        expectedOutputBuilder.append("******test" + System.lineSeparator());
        for (int i = 0; i < 9; i++) {
            expectedOutputBuilder.append("**********" + System.lineSeparator());
        }
        expectedOutputBuilder.append("******test" + System.lineSeparator());

        String expectedOutput = expectedOutputBuilder.toString();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest25() {
        // Frame #: 3.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest26() {
        // Frame #: 3.1.1.1.2
        Path inputFile = createFile("");
        String[] args = {"-t", "5", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest27() {
        // Frame #: 3.1.1.2.1
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "1", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "t" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest28() {
        // Frame #: 3.1.1.2.2
        Path inputFile = createFile("test" + System.lineSeparator() + "content" + System.lineSeparator());
        String[] args = {"-t", "1", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "t" + System.lineSeparator() + "c" + System.lineSeparator();
        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest29() {
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
    void moditextTest30() {
        // Frame #: 3.1.1.3.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(20) + "test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test conte" + System.lineSeparator().repeat(20) + "test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest31() {
        // Frame #: 3.2.1.1.1
        Path inputFile = createFile("test input content" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("test input" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest32() {
        // Frame #: 3.2.1.1.2
        Path inputFile = createFile("test test test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("test test " + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest33() {
        // Frame #: 3.2.1.2.1
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest34() {
        // Frame #: 3.2.1.2.2
        Path inputFile = createFile("non-empty test test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("non-empty " + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest35() {
        // Frame #: 3.2.1.3.1
        Path inputFile = createFile("different test content" + System.lineSeparator().repeat(50));
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("different " + System.lineSeparator().repeat(50), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest36() {
        // Frame #: 3.2.1.3.2
        Path inputFile = createFile("different test content" + System.lineSeparator().repeat(100) + "test" + System.lineSeparator());
        String[] args = {"-t", "10", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("different " + System.lineSeparator().repeat(100) + "test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest37() {
        // Frame #: 4.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest38() {
        // Frame #: 4.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("sample content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest39() {
        // Frame #: 4.1.1.2.1
        Path inputFile = createFile("test content" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest40() {
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
    void moditextTest41() {
        // Frame #: 4.1.1.3.1
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(1000));
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        StringBuilder expectedOutputBuilder = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            expectedOutputBuilder.append(System.lineSeparator());
        }
        expectedOutputBuilder.append("test content" + System.lineSeparator());

        Assertions.assertEquals(expectedOutputBuilder.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest42() {
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
    void moditextTest43() {
        // Frame #: 4.2.1.1.1
        Path inputFile = createFile("non-empty" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("non-empty" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest44() {
        // Frame #: 4.2.1.1.2
        Path inputFile = createFile("non-empty test" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("non-empty test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest45() {
        // Frame #: 4.2.1.2.1
        Path inputFile = createFile("test non-empty" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        Assertions.assertEquals("test non-empty" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest46() {
        // Frame #: 4.2.1.2.2
        Path inputFile = createFile("non-empty" + System.lineSeparator() + "test1" + System.lineSeparator() + "test2" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "test2" + System.lineSeparator() + "test1" + System.lineSeparator() + "non-empty" + System.lineSeparator();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest47() {
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
    void moditextTest48() {
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
    void moditextTest49() {
        // Frame #: 5.1.1.1.1
        Path inputFile = createFile("");
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest50() {
        // Frame #: 5.1.1.1.2
        Path inputFile = createFile("sample content" + System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);

        String expectedOutput = "sample content" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest51() {
        // Frame #: 5.1.1.2.1
        Path inputFile = createFile("test content" + System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test content" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest52() {
        // Frame #: 5.1.1.2.2
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(10));
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test content" + System.lineSeparator().repeat(10), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest53() {
        // Frame #: 5.1.1.3.1
        Path inputFile = createFile("test content" + System.lineSeparator().repeat(1000));
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test content" + System.lineSeparator().repeat(1000), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest54() {
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

        Assertions.assertEquals(expectedOutputBuilder.toString(), actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest55() {
        Path inputFile = createFile("test content" + System.lineSeparator() + "other content" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "other content" + System.lineSeparator() + "test content" + System.lineSeparator();

        Assertions.assertEquals(expectedOutput, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest56() {
        // Frame #: 5.1.2.1.2
        Path inputFile = createFile("test content" + System.lineSeparator() + "other content" + System.lineSeparator() + "test" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "test" + System.lineSeparator() + "other content" + System.lineSeparator() + "test content" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest57() {
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
    void moditextTest58() {
        // Frame #: 5.1.2.2.2
        Path inputFile = createFile("another" + System.lineSeparator() + "test" + System.lineSeparator() + "content" + System.lineSeparator() + "here" + System.lineSeparator() + "again" + System.lineSeparator() + "test" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "test" + System.lineSeparator() + "again" + System.lineSeparator() + "here" + System.lineSeparator() + "content" + System.lineSeparator() + "test" + System.lineSeparator() + "another" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest59() {
        // Frame #: 5.2.1.3.1
        Path inputFile = createFile("different test content" + System.lineSeparator().repeat(500));
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("different test content" + System.lineSeparator().repeat(500), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest60() {
        // Frame #: 5.1.2.3.2
        Path inputFile = createFile("mixed" + System.lineSeparator() + "content" + System.lineSeparator() + "test" + System.lineSeparator() + "lines" + System.lineSeparator() + "again" + System.lineSeparator() + "test" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-r", inputFile.toString()};
        Main.main(args);

        String expectedOutput = "again" + System.lineSeparator() + "test" + System.lineSeparator() + "again" + System.lineSeparator() + "lines" + System.lineSeparator() + "test" + System.lineSeparator() + "content" + System.lineSeparator() + "mixed" + System.lineSeparator();

        String actualOutput = capture.stdout();

        Assertions.assertEquals(expectedOutput, actualOutput);
        Assertions.assertEquals("", capture.stderr());
    }


    @Test
    void moditextTest61() {
        // Combine -p and -k
        Path inputFile = createFile("test" + System.lineSeparator() + "example" + System.lineSeparator() + "testing" + System.lineSeparator() + "check" + System.lineSeparator());
        String[] args = {"-p", "#", "10", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("######test" + System.lineSeparator() + "###testing" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest62() {
        // Combine -t and -k
        Path inputFile = createFile("test content" + System.lineSeparator() + "another test" + System.lineSeparator() + "random line" + System.lineSeparator() + "more tests" + System.lineSeparator());
        String[] args = {"-t", "5", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test " + System.lineSeparator() + "anoth" + System.lineSeparator() + "more " + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest63() {
        // Combine -r and -k
        Path inputFile = createFile("line1" + System.lineSeparator() + "line2" + System.lineSeparator() + "test1" + System.lineSeparator() + "test2" + System.lineSeparator());
        String[] args = {"-r", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test2" + System.lineSeparator() + "test1" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest64() {
        // Combine -f with different styles and -k
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simple line" + System.lineSeparator());
        String[] args = {"-f", "bold", "test", "-k", "test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this is a **test**" + System.lineSeparator() + "another **test** line" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest65() {
        // Combine -g and -f
        Path inputFile = createFile("int is an integer" + System.lineSeparator() + "int types are common" + System.lineSeparator() + "integer types are ints" + System.lineSeparator());
        String[] args = {"-g", "-f", "code", "int", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("`int` is an `int`eger" + System.lineSeparator() + "`int` types are common" + System.lineSeparator() + "`int`eger types are `int`s" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest66() {
        // File errors
        Path inputFile = createFile(System.lineSeparator());
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals(System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest67() {
        // File errors
        Path inputFile = createFile("Test");
        String[] args = {inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest68() {
        // File errors
        String[] args = {"-g", "-f", "code", "int"};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest69() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-k", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest70() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest71() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "a", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest72() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest73() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "3","", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest74() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "","3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest75() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "-","0", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest76() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "-","101", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest77() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "-","-1", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest78() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "-","2.5", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest79() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "abcdef","2", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest80() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-p", "a","2", "-t","3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest81() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest82() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-k", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("Test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest83() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest84() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", "-1", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest85() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", "101", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest86() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", "2.5", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest87() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-t", "2", "-p", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest88() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-g", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest89() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-g", "","-f","bold","test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest90() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-g", "abcd","-f","bold","test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest91() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-f", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest92() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-f", "italic", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest93() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-f","test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest94() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-f","test","test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest95() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-f","italic", "", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest96() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-r","", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest97() {
        // Option errors
        Path inputFile = createFile("Test" + System.lineSeparator());
        String[] args = {"-r","abcd", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest98() {
        // single -f
        Path inputFile = createFile("test" + System.lineSeparator());
        String[] args = {"-f","bold","again", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("test" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest99() {
        // single -f
        Path inputFile = createFile("lines" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-f","italic","again", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("lines" + System.lineSeparator() + "*again*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest100() {
        // single -f
        Path inputFile = createFile("lines" + System.lineSeparator() + "again" + System.lineSeparator());
        String[] args = {"-f","code","again", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("lines" + System.lineSeparator() + "`again`" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest101() {
        // combine -k and -g
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simpleline" + System.lineSeparator());
        String[] args = {"-g","-k","test", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest102() {
        // combine -p and -g
        Path inputFile = createFile("this is a test" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simpleline" + System.lineSeparator());
        String[] args = {"-g","-p","t","3", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest103() {
        // combine -p and -f
        Path inputFile = createFile("this" + System.lineSeparator() + "another test line" + System.lineSeparator() + "simpleline" + System.lineSeparator());
        String[] args = {"-p","-","5","-f","italic","line", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-this" + System.lineSeparator() + "another test *line*" + System.lineSeparator() + "simple*line*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest104() {
        // combine -p and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-p","-","5","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-line" + System.lineSeparator() + "another" + System.lineSeparator() + "-this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest105() {
        // combine -t and -g
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-t","5","-g", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest106() {
        // combine -t and -f
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-t","5","-f","italic","this", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*this*" + System.lineSeparator() + "anoth" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest107() {
        // combine -t and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator() + "line" + System.lineSeparator());
        String[] args = {"-t","5","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line" + System.lineSeparator() + "anoth" + System.lineSeparator() + "this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest108() {
        // combine -g and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-g","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest109() {
        // combine -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "another" + System.lineSeparator());
        String[] args = {"-f","italic","this","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("another" + System.lineSeparator() + "*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest110() {
        // combine -k and -p and -f
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-k","this","-p","-","5","-f","italic","this", inputFile.toString()};
        Main.main(args);
        String output = "-*this*" + System.lineSeparator();
        Assertions.assertEquals(output, capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest111() {
        // combine -k and -p and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "this line" + System.lineSeparator());
        String[] args = {"-k","this","-p","-","5","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this line" + System.lineSeparator() + "-this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest112() {
        // combine -k and -t and -f
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-k","this","-t","3","-f","italic","thi", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("*thi*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest113() {
        // combine -k and -t and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this" + System.lineSeparator());
        String[] args = {"-k","this","-t","4","-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line" + System.lineSeparator() + "this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest114() {
        // combine -k and -p and -g and -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this abc this" + System.lineSeparator());
        String[] args = {"-k","a","-k","this","-p","-","1","-p","-","5","-g","-f","bold","a","-f","italic","this","-r", inputFile.toString()};

        Main.main(args);
        Assertions.assertEquals("line *this* abc *this*" + System.lineSeparator() + "-*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest115() {
        // combine -k and -t and -g and -f and -r
        Path inputFile = createFile("this" + System.lineSeparator() + "line this abc this" + System.lineSeparator());
        String[] args = {"-k", "a", "-k", "this", "-t", "1", "-t", "4", "-g", "-f", "bold", "a", "-f", "italic", "this", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("line" + System.lineSeparator() + "*this*" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest116() {
        // single -p
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-p", "-", "100", inputFile.toString()};
        Main.main(args);
        StringBuilder strOutput = new StringBuilder("this" + System.lineSeparator());
        for (int i = 0; i < 96; i++) {
            strOutput.insert(0, "-");
        }
        Assertions.assertEquals(strOutput.toString(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest117() {
        // single -p
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-p", "-r", "100", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest118() {
        // single -t
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-t", "0", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals(System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest119() {
        // single -t
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-t", "100", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest120() {
        // single -k
        Path inputFile = createFile("this" + System.lineSeparator() + "-rabbit" + System.lineSeparator());
        String[] args = {"-k", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-rabbit" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest121() {
        // single -k
        Path inputFile = createFile("this" + System.lineSeparator() + "-rabbit" + System.lineSeparator());
        String[] args = {"-k", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("-rabbit" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }

    @Test
    void moditextTest122() {
        // single -f
        Path inputFile = createFile("this" + System.lineSeparator());
        String[] args = {"-f", "ITALIC", "this", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("", capture.stdout());
        Assertions.assertEquals(usageStr, capture.stderr());
    }

    @Test
    void moditextTest123() {
        // single -f
        Path inputFile = createFile("this" + System.lineSeparator() + "-rabbit" + System.lineSeparator());
        String[] args = {"-f", "italic", "-r", inputFile.toString()};
        Main.main(args);
        Assertions.assertEquals("this" + System.lineSeparator() + "*-r*abbit" + System.lineSeparator(), capture.stdout());
        Assertions.assertEquals("", capture.stderr());
    }
}
