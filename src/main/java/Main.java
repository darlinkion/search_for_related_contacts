import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        // Читаем строки построчно и сразу удаляем некорректные и дубликаты
        tempTest tempTest = new tempTest();
        Set<List<String>> uniqueLines = tempTest.readUniqueValidLines(args[0]);

        // Группируем строки
        List<Set<List<String>>> groups =tempTest.findGroups(uniqueLines);

        // Печатаем результат
        tempTest.printGroups(groups);

        long endTime = System.nanoTime();
        System.out.printf("Общее время выполнения: %.3f сек%n", (endTime - startTime) / 1_000_000_000.0);

    }

}
