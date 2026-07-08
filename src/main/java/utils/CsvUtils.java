package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CsvUtils {
    private CsvUtils() {}

    public static List<String[]> readCsv(String filePath) {
        try (Reader reader = Files.newBufferedReader(toPath(filePath), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            throw new IllegalStateException("Unable to read CSV file: " + filePath, e);
        }
    }

    public static List<Map<String, String>> readCsvAsMaps(String filePath) {
        List<String[]> rows = readCsv(filePath);
        if (rows.isEmpty()) {
            return List.of();
        }
        return toMaps(rows.get(0), rows.subList(1, rows.size()));
    }

    public static List<Map<String, String>> readCsvAsMaps(String filePath, String[] headers) {
        List<String[]> rows = readCsv(filePath);
        return toMaps(headers, rows);
    }

    public static void writeCsv(String filePath, List<String[]> rows) {
        writeCsv(filePath, null, rows);
    }

    public static void writeCsv(String filePath, String[] headers, List<String[]> rows) {
        Path path = toPath(filePath);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                 CSVWriter csvWriter = new CSVWriter(writer)) {
                if (headers != null && headers.length > 0) {
                    csvWriter.writeNext(headers);
                }
                csvWriter.writeAll(rows == null ? List.of() : rows);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write CSV file: " + filePath, e);
        }
    }

    private static List<Map<String, String>> toMaps(String[] headers, List<String[]> rows) {
        List<String> headerList = Arrays.asList(headers);
        List<Map<String, String>> data = new ArrayList<>();
        for (String[] row : rows) {
            Map<String, String> record = new LinkedHashMap<>();
            for (int i = 0; i < headerList.size(); i++) {
                record.put(headerList.get(i), i < row.length ? row[i] : "");
            }
            data.add(record);
        }
        return data;
    }

    private static Path toPath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("CSV file path must not be null or blank");
        }
        return Paths.get(filePath);
    }
}
