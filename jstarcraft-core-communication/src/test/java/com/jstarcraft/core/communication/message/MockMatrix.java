package com.jstarcraft.core.communication.message;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class MockMatrix {

    /** 行列大小 */
    private int rowSize, columnSize;

    // Compressed Row Storage (CRS)
    private int[] rowPoints;
    private int[] rowIndexes;

    // Compressed Column Storage (CCS)
    private int[] columnPoints;
    private int[] columnIndexes;

    private int[] termRows;
    private int[] termColumns;
    private double[] termValues;

    private MockMatrix() {
    }

    public static MockMatrix instanceOf(int rowSize, int columnSize, Table<Integer, Integer, Double> dataTable) {
        MockMatrix instance = new MockMatrix();
        instance.rowSize = rowSize;
        instance.columnSize = columnSize;
        int size = dataTable.size();

        // CRS
        instance.rowPoints = new int[rowSize + 1];
        instance.rowIndexes = new int[size];

        // CCS
        instance.columnPoints = new int[columnSize + 1];
        instance.columnIndexes = new int[size];

        instance.termRows = new int[size];
        instance.termColumns = new int[size];
        instance.termValues = new double[size];

        int[] rowCounts = new int[rowSize];
        int[] columnCounts = new int[columnSize];
        Integer[] rowIndexes = new Integer[size];
        Integer[] columnIndexes = new Integer[size];
        int index = 0;
        for (Cell<Integer, Integer, Double> cell : dataTable.cellSet()) {
            int row = cell.getRowKey();
            int column = cell.getColumnKey();
            // 设置term的坐标与值
            instance.termRows[index] = row;
            instance.termColumns[index] = column;
            instance.termValues[index] = cell.getValue();
            // 统计行列的大小
            rowCounts[row]++;
            columnCounts[column]++;
            index++;
        }

        for (int point = 1; point <= rowSize; ++point) {
            // 设置行指针
            int row = point - 1;
            instance.rowPoints[point] = instance.rowPoints[row] + rowCounts[row];
        }

        for (int point = 1; point <= columnSize; ++point) {
            // 设置列指针
            int column = point - 1;
            instance.columnPoints[point] = instance.columnPoints[column] + columnCounts[column];
        }

        for (index = 0; index < size; index++) {
            // 设置行列的索引
            int row = instance.termRows[index];
            int column = instance.termColumns[index];
            rowIndexes[index] = instance.rowPoints[row] + (--rowCounts[row]);
            columnIndexes[index] = instance.columnPoints[column] + (--columnCounts[column]);
        }

        // 排序行的索引
        Arrays.sort(rowIndexes, (left, right) -> {
            int value = instance.termRows[left] - instance.termRows[right];
            if (value == 0) {
                value = instance.termColumns[left] - instance.termColumns[right];
            }
            return value;
        });

        // 排序列的索引
        Arrays.sort(columnIndexes, (left, right) -> {
            int value = instance.termColumns[left] - instance.termColumns[right];
            if (value == 0) {
                value = instance.termRows[left] - instance.termRows[right];
            }
            return value;
        });

        for (index = 0; index < size; index++) {
            // 拷贝行列的索引
            instance.rowIndexes[index] = rowIndexes[index];
            instance.columnIndexes[index] = columnIndexes[index];
        }

        return instance;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockMatrix that = (MockMatrix) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.rowSize, that.rowSize);
        equal.append(this.columnSize, that.columnSize);
        equal.append(this.rowPoints, that.rowPoints);
        equal.append(this.rowIndexes, that.rowIndexes);
        equal.append(this.columnPoints, that.columnPoints);
        equal.append(this.columnIndexes, that.columnIndexes);
        equal.append(this.termRows, that.termRows);
        equal.append(this.termColumns, that.termColumns);
        equal.append(this.termValues, that.termValues);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(rowSize);
        hash.append(columnSize);
        hash.append(rowPoints);
        hash.append(rowIndexes);
        hash.append(columnPoints);
        hash.append(columnIndexes);
        hash.append(termRows);
        hash.append(termColumns);
        hash.append(termValues);
        return hash.toHashCode();
    }

}
