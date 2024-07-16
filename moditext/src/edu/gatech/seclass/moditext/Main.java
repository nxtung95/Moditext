package edu.gatech.seclass.moditext;

import java.io.File;
import java.nio.file.*;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

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
        String fileContent = "";
        try {
            lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
            fileContent = Files.readString(filePath, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] validOptions = new String[] {"-k", "-p", "-t", "-g", "-f", "-r"};
//        boolean filterApplied = false;
        List<String> filteredLines = new ArrayList<>(lines);

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
                    String nextOpt = args[i + 1];
                    if (Arrays.asList(validOptions).contains(nextOpt)) {
                        usage();
                        return;
                    }
                    value1 = nextOpt;
                } catch (Exception e) {
                    return;
                }
                try {
                    String nextOpt = args[i + 2];
                    if (Arrays.asList(validOptions).contains(nextOpt)) {
                        usage();
                        return;
                    }
                    value2 = nextOpt;
                } catch (Exception e) {
                    return;
                }
                optionMap.put(option, value1 + "&&@@!!" + value2);
                i = i + 2;
            } else {
                try {
                    String nextOpt = args[i + 1];
                    if (Arrays.asList(validOptions).contains(nextOpt)) {
                        usage();
                        return;
                    }
                    value1 = nextOpt;
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

        if (fileContent.length() < 2) {
            return;
        } else if ((System.lineSeparator().length() == 2 && !fileContent.substring(fileContent.length() - 2).equals(System.lineSeparator()))) {
            usage();
            return;
        } else if ((System.lineSeparator().length() == 1 && !fileContent.substring(fileContent.length() - 1).equals(System.lineSeparator()))) {
            usage();
            return;
        }

        Map<String, String> sortMap = new LinkedHashMap<>();
        if (keys.contains("-k")) {
            sortMap.put("-k", optionMap.get("-k"));
        }

        if (keys.contains("-p")) {
            String[] value = optionMap.get("-p").split("&&@@!!");
            String cha = value[0];
            String num = value[1];

            if (cha.length() > 1 && !Character.isLetter(cha.charAt(0))) {
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
            sortMap.put("-p", optionMap.get("-p"));
        } else if (keys.contains("-t")) {
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

//            switch (option) {
//                case "-k":
//                    if (i + 1 >= args.length - 1) {
//                        usage();
//                        return;
//                    }
//                    String substring = args[++i];
//                    filteredLines.removeIf(line -> !line.contains(substring));
//                    filterApplied = true;
//                    break;
//
//                case "-p":
//                    if (i + 2 >= args.length - 1) {
//                        usage();
//                        return;
//                    }
//                    String symbol = args[++i];
//                    int maxPadding = Integer.parseInt(args[++i]);
//                    if (maxPadding < 1 || maxPadding > 100) {
//                        usage();
//                        return;
//                    }
//                    for (int j = 0; j < filteredLines.size(); j++) {
//                        String line = filteredLines.get(j);
//                        if (line.length() < maxPadding) {
//                            line = symbol.repeat(maxPadding - line.length()) + line;
//                        }
//                        filteredLines.set(j, line);
//                    }
//                    break;
//
//                case "-t":
//                    if (i + 1 >= args.length - 1) {
//                        usage();
//                        return;
//                    }
//                    int num = Integer.parseInt(args[++i]);
//                    if (num < 0 || num > 100) {
//                        usage();
//                        return;
//                    }
//                    for (int j = 0; j < filteredLines.size(); j++) {
//                        String line = filteredLines.get(j);
//                        if (line.length() > num) {
//                            line = line.substring(0, num);
//                        }
//                        filteredLines.set(j, line);
//                    }
//                    break;
//
//                case "-f":
//                    if (i + 2 >= args.length - 1) {
//                        usage();
//                        return;
//                    }
//                    String style = args[++i];
//                    substring = args[++i];
//                    for (int j = 0; j < filteredLines.size(); j++) {
//                        String line = filteredLines.get(j);
//                        switch (style) {
//                            case "bold":
//                                line = line.replaceFirst(substring, "**" + substring + "**");
//                                break;
//                            case "italic":
//                                line = line.replaceFirst(substring, "*" + substring + "*");
//                                break;
//                            case "code":
//                                line = line.replaceFirst(substring, "`" + substring + "`");
//                                break;
//                            default:
//                                usage();
//                                return;
//                        }
//                        filteredLines.set(j, line);
//                    }
//                    break;
//
//                case "-r":
//                    Collections.reverse(filteredLines);
//                    break;
//
//                default:
//                    usage();
//                    return;
//            }
//        }

//        if (filterApplied && filteredLines.isEmpty()) {
//            // No output if filter was applied and no lines matched
//            return;
//        }

        for (String line : filteredLines) {
            System.out.println(line);
        }
    }

    private static void usage() {
        System.err.println("Usage: moditext [ -k substring | -p ch num | -t num | -g | -f style substring | -r ] FILE");
    }

//    public static boolean isInteger(String s) {
//        return isInteger(s,10);
//    }
//
//    public static boolean isInteger(String s, int radix) {
//        if(s.isEmpty()) return false;
//        for(int i = 0; i < s.length(); i++) {
//            if(i == 0 && s.charAt(i) == '-') {
//                if(s.length() == 1) return false;
//                else continue;
//            }
//            if(Character.digit(s.charAt(i),radix) < 0) return false;
//        }
//        return true;
//    }
}

