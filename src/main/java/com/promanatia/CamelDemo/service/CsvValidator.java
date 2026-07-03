package com.promanatia.CamelDemo.service;

import org.springframework.stereotype.Service;

@Service
public class CsvValidator {

    private static final int DESCRIPTION_INDEX = 5;
    private static final int DEPARTMENT_INDEX = 11;
    private static final int LOCATION_INDEX = 14;
    private static final int INDEX = 15;

    public void validateFile(String[] rows) {

        if (rows == null || rows.length <= 1) {
            throw new RuntimeException(
                    "CSV file is empty or contains only header.");
        }
    }

    public void validateRow(String[] columns,
                            String[] headers,
                            int rowNum,
                            String row) {

        for (int colNum = 0; colNum < headers.length; colNum++) {

            // Skip optional columns
            if (isOptionalColumn(colNum)) {
                continue;
            }

            // Missing column or empty value
            if (colNum >= columns.length
                    || isEmpty(columns[colNum])) {

                String columnName =
                        getColumnName(headers, colNum);

                throw new RuntimeException(
                        "Empty value found in Column '"
                                + columnName
                                + "' at Row "
                                + (rowNum + 1)
                                + ". Row Data : "
                                + row);
            }
        }
    }

    private boolean isOptionalColumn(int colNum) {

        return colNum == DESCRIPTION_INDEX
                || colNum == DEPARTMENT_INDEX
                || colNum == LOCATION_INDEX
                || colNum == INDEX;
    }

    private String getColumnName(String[] headers,
                                 int colNum) {

        if (headers != null
                && colNum < headers.length) {

            return headers[colNum].trim();
        }

        return "Unknown Column";
    }

    public String getColumnValue(String[] columns, int columnIndex) {

        if (columns.length > columnIndex
                && columns[columnIndex] != null) {

            return columns[columnIndex].trim();
        }

        return "";
    }

    public boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}