import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class tempTest {


    public Set<List<String>> readUniqueValidLines(String resourceName) throws IOException {
        Set<List<String>> uniqueLines = new HashSet<>();
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new FileNotFoundException("Файл " + resourceName + " не найден в ресурсах");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    List<String> parts = Arrays.asList(line.split(";"));
                    uniqueLines.add(parts);
                }
            }
        }
        System.out.println("Файл считан. Уникальных строк: " + uniqueLines.size());
        return uniqueLines;
    }

    public  List<Set<List<String>>> findGroups(Set<List<String>> uniqueLines) {
        List<List<String>> parsedLines = new ArrayList<>();
        for (List<String> line : uniqueLines) {
            List<String> processedLine = new ArrayList<>();
            for (String value : line) {
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1).trim(); // Убираем кавычки
                }
                processedLine.add(value.isEmpty() ? null : value); // Сохраняем null для пустых значений
            }
            parsedLines.add(processedLine);
        }
        // Мапа: ключ - уникальное значение(номер),  значение/ключ - столбец в котором встречается, значение -индексы строк
        Map<String, Map<Integer, List<Integer>>> valueToColumnMap = new HashMap<>();
        int lineCount = parsedLines.size();
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            List<String> line = parsedLines.get(lineIndex);
            for (int col = 0; col < line.size(); col++) {
                String value = line.get(col);
                if (value != null) { // Игнорируем null (пустые значения)
                    valueToColumnMap
                            .computeIfAbsent(value, k -> new HashMap<>())
                            .computeIfAbsent(col, k -> new ArrayList<>())
                            .add(lineIndex);
                }
            }
        }

        int[] parent = new int[lineCount];
        for (int i = 0; i < lineCount; i++) {
            parent[i] = i; // Изначально каждая строка сама по себе
        }

        // Связываем строки, имеющие одинаковые значения в одной колонке
        for (Map<Integer, List<Integer>> columnToIndices : valueToColumnMap.values()) {
            for (List<Integer> indices : columnToIndices.values()) {
                for (int i = 1; i < indices.size(); i++) {
                    union(parent, indices.get(0), indices.get(i));
                }
            }
        }
        Map<Integer, Set<List<String>>> groupsMap = new HashMap<>();
        for (int i = 0; i < lineCount; i++) {
            int root = find(parent, i);
            groupsMap.computeIfAbsent(root, k -> new HashSet<>()).add(parsedLines.get(i));
        }

        // Возвращаем группы с более чем одним элементом, отсортированные по размеру
        return groupsMap.values().stream()
                .filter(group -> group.size() > 1)
                .sorted((g1, g2) -> Integer.compare(g2.size(), g1.size()))
                .collect(Collectors.toList());
    }

    // Union-Find: Находим корень
    private  int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    // Union-Find: Объединяем группы
    private  void union(int[] parent, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);
        if (rootX != rootY) {
            parent[rootY] = rootX;
        }
    }

    public void printGroups(List<Set<List<String>>> groups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write("Число групп: " + groups.size());
            writer.newLine();
            int groupNumber = 1;
            for (Set<List<String>> group : groups) {
                writer.write("Группа " + groupNumber++);
                writer.newLine();
                for (List<String> line : group) {
                    writer.write(String.join(";", line));
                    writer.newLine();
                }
                writer.newLine();
            }
        }
        System.out.println("Результаты записаны в файл output.txt");
    }
}
