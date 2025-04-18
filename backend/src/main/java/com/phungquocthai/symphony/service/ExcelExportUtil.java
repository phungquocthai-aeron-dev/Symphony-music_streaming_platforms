package com.phungquocthai.symphony.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ExcelExportUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Xuất danh sách object ra file Excel.
     * @param data Danh sách object cần xuất
     * @param excludeFields Danh sách các thuộc tính cần bỏ qua
     * @param sheetName Tên sheet trong Excel
     * @return Mảng byte chứa file Excel
     */
    public <T> byte[] exportToExcel(List<T> data, String[] excludeFields, String sheetName) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Font cho header
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            // Style cho header
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Style cho ngày tháng
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));

            // Chuyển excludeFields thành Set để kiểm tra nhanh
            Set<String> excludeFieldsSet = excludeFields != null ? new HashSet<>(Arrays.asList(excludeFields)) : Collections.emptySet();

            // Lấy danh sách field hợp lệ
            Class<?> clazz = data.get(0).getClass();
            List<Field> validFields = getValidFields(clazz, excludeFieldsSet);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            for (Field field : validFields) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(field.getName());
                cell.setCellStyle(headerCellStyle);
            }

            // Ghi dữ liệu vào Excel
            int rowIndex = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;

                for (Field field : validFields) {
                    Cell cell = row.createCell(colIndex++);
                    try {
                        field.setAccessible(true);
                        Object value = field.get(item);
                        setCellValue(cell, value, dateCellStyle);
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("N/A"); // Nếu có lỗi thì hiện "N/A"
                    }
                }
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < validFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Lấy danh sách field hợp lệ từ class, kể cả field của superclass
     */
    private List<Field> getValidFields(Class<?> clazz, Set<String> excludeFieldsSet) {
        List<Field> validFields = new ArrayList<>();

        while (clazz != null && !clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (!excludeFieldsSet.contains(field.getName())) {
                    validFields.add(field);
                }
            }
            clazz = clazz.getSuperclass(); // Lấy field từ superclass
        }

        return validFields;
    }

    /**
     * Thiết lập giá trị cho ô trong Excel
     */
    private void setCellValue(Cell cell, Object value, CellStyle dateCellStyle) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue(DATE_FORMAT.format((Date) value));
            cell.setCellStyle(dateCellStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
