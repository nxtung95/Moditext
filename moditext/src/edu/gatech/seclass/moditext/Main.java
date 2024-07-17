package edu.gatech.seclass.moditext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//public class Main {
//    // Empty Main class for compiling Individual Project
//    // During Deliverable 1 and Deliverable 2, DO NOT ALTER THIS CLASS or implement it
//
//    public static void main(String[] args) {
//        // Empty Skeleton Method
//    }
//
//    private static void usage() {
//        System.err.println("Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE");
//    }
//}

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
            return;
        }

        String filePathStr = args[args.length - 1];
        Path filePath = Path.of(filePathStr);
        boolean existFile;
        try {
            File f = new File(filePathStr);
            existFile = f.exists() && f.isFile();
        } catch (Exception e) {
            existFile = false;
        }
        if (!filePathStr.isEmpty() && existFile) {
            String extension = "";
            try {
                Optional<String> opt = Optional.ofNullable(filePathStr)
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(filePathStr.lastIndexOf(".") + 1));
                extension = opt.orElse(null);
            } catch (Exception e) {
                extension = null;
            }
            if (extension == null || !"txt".equalsIgnoreCase(extension)) {
                usage();
                return;
            }
        } else {
            usage();
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] validOptions = new String[] {"-k", "-p", "-t", "-g", "-f", "-r"};

        Map<String, String> optionMap = new HashMap<>();
        for (int i = 0; i < args.length - 1; i++) {
            String option = args[i];
            if (!Arrays.asList(validOptions).contains(option)) {
                usage();
                return;
            }
            String value1 = "";
            String value2 = "";

            if ("-g".equals(option) || "-r".equals(option)) {
                optionMap.put(option, "");
            } else if ("-p".equals(option) || "-f".equals(option)) {
                try {
                    value1 = args[i + 1];
                } catch (Exception e) {
                    return;
                }
                try {
                    value2 = args[i + 2];
                } catch (Exception e) {
                    return;
                }
                optionMap.put(option, value1 + "&&@@!!" + value2);
                i = i + 2;
            } else {
                try {
                    value1 = args[i + 1];
                } catch (Exception e) {
                    return;
                }
                optionMap.put(option, value1);
                i = i + 1;
            }
        }

        Set<String> keys = optionMap.keySet();
        if (keys.contains("-p") && keys.contains("-t")) {
            usage();
            return;
        } else if (keys.contains("-g") && !keys.contains("-f")) {
            usage();
            return;
        }

        if (lines.isEmpty()) {
            return;
        }

        Map<String, String> sortMap = new LinkedHashMap<>();
        if (keys.contains("-k")) {
            sortMap.put("-k", optionMap.get("-k"));
        }

        if (keys.contains("-p")) {
            sortMap.put("-p", optionMap.get("-p"));
        } else if (keys.contains("-t")) {
            sortMap.put("-t", optionMap.get("-t"));
        }

        if (keys.contains("-g")) {
            sortMap.put("-g", optionMap.get("-g"));
        }

        if (keys.contains("-f")) {
            sortMap.put("-f", optionMap.get("-f"));
        }

        if (keys.contains("-r")) {
            sortMap.put("-r", optionMap.get("-r"));
        }

        List<String> filteredLines = new ArrayList<>(lines);
        filteredLines = filteredLines.stream().map(f -> f + System.lineSeparator()).collect(Collectors.toList());
        for (Map.Entry<String, String> entry : sortMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if ("-k".equals(key)) {
                filteredLines = getKeepLineContainValue(value, filteredLines);
            }

            if ("-p".equals(key)) {
                String[] padding = optionMap.get("-p").split("&&@@!!");
                String cha = padding[0];
                String num = padding[1];

                if (cha.length() != 1) {
                    usage();
                    return;
                }

                int padNum;
                try {
                    padNum = Integer.parseInt(num);
                } catch (Exception e) {
                    usage();
                    return;
                }

                if (padNum < 1 || padNum > 100) {
                    usage();
                    return;
                }

                filteredLines = getPaddingLines(filteredLines, cha, padNum);
            } else if ("-t".equals(key)) {
                String num = optionMap.get("-t");
                int trimNum;
                try {
                    trimNum = Integer.parseInt(num);
                } catch (Exception e) {
                    usage();
                    return;
                }
                if (trimNum < 0 || trimNum > 100) {
                    usage();
                    return;
                }
                filteredLines = getTrimLines(filteredLines, trimNum);
            }

            if ("-f".equals(key)) {
                String[] formatText = optionMap.get("-f").split("&&@@!!");
                String style = formatText[0];
                String substring = formatText[1];
                if (!Arrays.asList("bold", "italic", "code").contains(style)) {
                    usage();
                    return;
                } else if (substring.isEmpty()) {
                    usage();
                    return;
                }
                boolean isGlobal = keys.contains("-g");
                filteredLines = getFormatText(filteredLines, style, substring, isGlobal);
            }

            if ("-r".equals(key)) {
                Collections.reverse(filteredLines);
            }
        }

        for (String line : filteredLines) {
            System.out.print(line);
        }
    }

    private static List<String> getFormatText(List<String> filteredLines, String style, String substring, boolean isGlobal) {
        List<String> result = new ArrayList<>();
        String special;
        if ("bold".equals(style)) {
            special = "**";
        } else if ("italic".equals(style)) {
            special = "*";
        } else {
            special = "`";
        }
        for (String line : filteredLines) {
            String newLine = line;
            if (line.contains(substring)) {
                if (isGlobal) {
                    newLine = line.replaceAll(substring, special + substring + special);
                } else {
                    newLine = line.replaceFirst(substring, special + substring + special);
                }
            }
            result.add(newLine);
        }
        return result;
    }

    private static List<String> getTrimLines(List<String> filteredLines, int trimNum) {
        List<String> result = new ArrayList<>();
        for (String line : filteredLines) {
            String formatLine = line.substring(0, line.length() - System.lineSeparator().length());
            if (formatLine.length() > trimNum) {
                formatLine = formatLine.substring(0, trimNum);
            }
            result.add(formatLine + System.lineSeparator());
        }
        return result;
    }

    private static List<String> getPaddingLines(List<String> filteredLines, String cha, int padNum) {
        List<String> result = new ArrayList<>();
        for (String line : filteredLines) {
            String formatLine = line.substring(0, line.length() - System.lineSeparator().length());
            if (formatLine.length() < padNum) {
                int addPadNum = padNum - formatLine.length();
                StringBuilder lineBuilder = new StringBuilder(line);
                for (int i = 0; i < addPadNum; i++) {
                    lineBuilder.insert(0, cha);
                }
                line = lineBuilder.toString();
            }
            result.add(line);
        }
        return result;
    }

    private static List<String> getKeepLineContainValue(String value, List<String> lines) {
        if (value.isEmpty()) {
            return lines;
        }
        List<String> keepLines = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(value)) {
                keepLines.add(line);
            }
        }
        return keepLines;
    }

    private static void usage() {
        System.err.println("Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE");
    }
}

