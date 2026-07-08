package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtils {
    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelUtils() {}

    public static String readCell(String file, int sheetIndex, int row, int column) {
        try (Workbook workbook = openWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Row excelRow = sheet.getRow(row);
            if (excelRow == null) {
                return "";
            }
            return getCellValue(excelRow.getCell(column));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read Excel cell from file: " + file, e);
        }
    }

    public static Object[][] readSheet(String filePath, String sheetName) {
        try (Workbook workbook = openWorkbook(filePath)) {
            Sheet sheet = getSheet(workbook, sheetName);
            int rowCount = sheet.getLastRowNum() + 1;
            int columnCount = getMaxColumnCount(sheet);
            Object[][] data = new Object[rowCount][columnCount];

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    data[rowIndex][columnIndex] = row == null ? "" : getTypedCellValue(row.getCell(columnIndex));
                }
            }
            return data;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read Excel sheet '" + sheetName + "' from: " + filePath, e);
        }
    }

    public static Object[][] readSheetWithoutHeader(String filePath, String sheetName) {
        Object[][] allRows = readSheet(filePath, sheetName);
        if (allRows.length <= 1) {
            return new Object[0][0];
        }

        Object[][] dataRows = new Object[allRows.length - 1][allRows[0].length];
        System.arraycopy(allRows, 1, dataRows, 0, allRows.length - 1);
        return dataRows;
    }

    public static List<Map<String, String>> readSheetAsMaps(String filePath, String sheetName) {
        try (Workbook workbook = openWorkbook(filePath)) {
            Sheet sheet = getSheet(workbook, sheetName);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return List.of();
            }

            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                headers.add(getCellValue(headerRow.getCell(i)));
            }

            List<Map<String, String>> rows = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                Map<String, String> record = new LinkedHashMap<>();
                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    record.put(headers.get(columnIndex), getCellValue(row.getCell(columnIndex)));
                }
                rows.add(record);
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read Excel sheet as maps '" + sheetName + "' from: "
                    + filePath, e);
        }
    }

    public static void writeExcel(String filePath, String sheetName, List<String> headers, List<List<Object>> rows) {
        Path path = toPath(filePath);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Workbook workbook = new XSSFWorkbook();
                 OutputStream outputStream = Files.newOutputStream(path)) {
                Sheet sheet = workbook.createSheet(sheetName);
                int rowIndex = 0;

                if (headers != null && !headers.isEmpty()) {
                    Row headerRow = sheet.createRow(rowIndex++);
                    for (int i = 0; i < headers.size(); i++) {
                        headerRow.createCell(i).setCellValue(headers.get(i));
                    }
                }

                if (rows != null) {
                    for (List<Object> rowData : rows) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 0; i < rowData.size(); i++) {
                            setCellValue(row.createCell(i), rowData.get(i));
                        }
                    }
                }

                workbook.write(outputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write Excel file: " + filePath, e);
        }
    }

    private static Workbook openWorkbook(String filePath) throws IOException {
        Path path = toPath(filePath);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return new XSSFWorkbook(inputStream);
        }
    }

    private static Sheet getSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Excel sheet not found: " + sheetName);
        }
        return sheet;
    }

    private static int getMaxColumnCount(Sheet sheet) {
        int max = 0;
        for (Row row : sheet) {
            max = Math.max(max, row.getLastCellNum());
        }
        return Math.max(max, 0);
    }

    private static String getCellValue(Cell cell) {
        return cell == null ? "" : FORMATTER.formatCellValue(cell).trim();
    }

    private static Object getTypedCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> FORMATTER.formatCellValue(cell);
            default -> "";
        };
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof Boolean bool) {
            cell.setCellValue(bool);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static Path toPath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Excel file path must not be null or blank");
        }
        return Paths.get(filePath);
    }
}
