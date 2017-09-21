package org.jline.demo;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) {
        try {
            while (true) {
                ParsedLine line = readLine("prompt> ");
                if (line == null) {
                    return;
                }
                String cmd = line.words().get(0).toLowerCase(Locale.ENGLISH);
                switch (cmd) {
                    case "quit":
                    case "exit":
                        return;
                    default:
                        runCommand(line);
                        break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder();

    private static ParsedLine readLine(String prompt) throws IOException {
        try (Terminal terminal = TerminalBuilder.terminal()) {
            LineReader reader = lineReaderBuilder
                    .terminal(terminal)
                    .build();
            while (true) {
                try {
                    String line = reader.readLine(prompt);
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    if (!line.isEmpty()) {
                        terminal.writer().println("======>\"" + line + "\"");
                        terminal.flush();
                        return reader.getParsedLine();
                    }
                } catch (UserInterruptException e) {
                    terminal.writer().println("User Interruption !");
                } catch (EndOfFileException e) {
                    break;
                }
            }
            return null;
        }
    }

    private static int runCommand(ParsedLine command) {
        int returnCode = -1;
        try {
            ProcessBuilder pb = new ProcessBuilder(command.words());
            pb.inheritIO();
            Process p = pb.start();
            if (!p.waitFor(60, TimeUnit.SECONDS))
                System.out.println("Timeout");
            else {
                returnCode = p.exitValue();
                System.out.println("return code :" + returnCode);
            }
            return returnCode;
        } catch (Throwable t) {
            t.printStackTrace();
            returnCode = -1;
        }
        return returnCode;
    }

}