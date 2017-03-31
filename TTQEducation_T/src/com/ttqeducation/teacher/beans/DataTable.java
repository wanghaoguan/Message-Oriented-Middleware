package com.ttqeducation.teacher.beans;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 实现类似C#中的DataTable的数据类型
 * 
 * @author 王勤为
 * 
 */
public class DataTable {

	// 这个是模仿实现C#中的DataTble类
	public String tableName;// 表名

	public List<DataRow> Rows;// 行
	public List<DataColumn> Columns;// 列

	/**
	 * 构造器1 传入多行数据
	 * 
	 * @param r
	 */
	public DataTable(List<DataRow> r) {
		this.Rows = r; // 初始化行数据
		this.Columns = r.get(0).Columns; // 初始化列信息
	}

	/**
	 * 构造器2 传入表名
	 * 
	 * @param tableName
	 */
	public DataTable(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 构造器3 传入数目可变的列名
	 * 
	 * @param r
	 */
	public DataTable(String... columns) {
		this.Columns = new LinkedList<DataColumn>();// 初始化列；
		int count = columns.length;
		for (int i = 0; i < count; i++) {
			DataColumn col = new DataColumn(columns[i]);
			this.Columns.add(col);

		}
	}

	/**
	 * 获得表中某一行
	 * 
	 * @param index
	 * @return
	 */
	public DataRow getRow(int index) throws dataTableWrongException {
		if (index <= this.Rows.size()) { // 如果索引值没有超出范围
			DataRow dr = this.Rows.get(index);
			return dr;
		} else {
			throw new dataTableWrongException("out of index!");
		}
	}

	/**
	 * 添加新行
	 * 
	 * @param row
	 */
	public void addRow(DataRow row) throws dataTableWrongException {
		if (this.Columns != null && this.Columns.size() != 0) { // 如果列信息不为空
			// 判断列信息是否一致
			int count = this.Columns.size();
			if (count == row.Columns.size()) {// 如果列数相等
				// 遍历,看列名是否相等
				for (int i = 0; i < count; i++) {
					// System.out.println("表中"+this.Columns.get(i).ColumnName);
					// System.out.println(row.Columns.get(i).ColumnName);
					if (this.Columns.get(i).ColumnName.equals(row.Columns
							.get(i).ColumnName) == false) {
						throw new dataTableWrongException(
								" Columns do not match!");
					}
				}
				this.Rows.add(row);

			} else {// 如果列数不相等
				throw new dataTableWrongException(" Columns do not match!");
			}

		} else {// 表中没有列,可以直接添加
			this.Rows = new LinkedList<DataRow>();
			this.Rows.add(row);
			this.Columns = row.Columns;
		}

	}

	/**
	 * 删除行
	 * 
	 * @param index
	 */
	public void deleteRow(int index) {

		this.Rows.remove(index);

	}

	/**
	 * 在表中添加新行，不传入参数
	 * 
	 * @throws dataTableWrongException
	 */
	public void newRow() throws dataTableWrongException {
		Map<String, Object> iMap = new LinkedHashMap<String, Object>();
		if (this.Columns != null) {
			for (DataColumn Col : this.Columns) {
				iMap.put(Col.ColumnName, ""); // 添加值为空的新行
			}
		}
		DataRow dr = new DataRow(iMap);
		this.addRow(dr);

	}

	/**
	 * 在表中添加新行，传入Map参数
	 */
	public void newRow(Map<String, Object> iMap) throws dataTableWrongException {
		DataRow dr = new DataRow(iMap);
		this.addRow(dr);
	}

	/**
	 * 在表中添加新行，传入数目不等的String类型参数(值)
	 */
	public void newRow(Object... values) throws dataTableWrongException {
		if (this.Columns != null) {// 如果列信息不为空
			int count = values.length;
			if (count != this.Columns.size()) {
				throw new dataTableWrongException(
						"value counts do not equal ColumCount !");
			} else {
				// 遍历
				Map<String, Object> iMap = new LinkedHashMap<String, Object>();
				for (int i = 0; i < count; i++) {
					iMap.put(this.Columns.get(i).ColumnName, values[i]);
				}
				DataRow dr = new DataRow(iMap);
				this.addRow(dr);
			}
		} else {// 如果列信息为空
			throw new dataTableWrongException(
					"dataTable does not have column info !");
		}
	}

	/**
	 * 获得行数
	 * 
	 * @return
	 */
	public int getRowCount() {
		if (this.Rows != null) {
			return this.Rows.size();
		} else {
			return 0;
		}
	}

	/**
	 * 获得列数
	 * 
	 * @return
	 */
	public int getColumCount() {
		if (this.Columns != null) {
			return this.Columns.size();
		} else {
			return 0;
		}
	}

	/**
	 * 获取某单元格的值
	 * 
	 * @param rowNum
	 * @param colNum
	 * @return
	 * @throws dataTableWrongException
	 */
	public String getCell(int rowNum, int colNum)
			throws dataTableWrongException {
		String value = this.getRow(rowNum).getCell(colNum);
		return value;
	}

	/**
	 * 获取某单元格的值
	 * 
	 * @param rowNum
	 * @param colNum
	 * @return
	 * @throws dataTableWrongException
	 */
	public String getCell(int rowNum, String colName)
			throws dataTableWrongException {
		String value = this.getRow(rowNum).getCell(colName);
		return value;
	}

	/*
	 * 重写toString方法
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "";
		int rowCount = this.getRowCount();
		if (rowCount != 0) {
			int colCount = this.getColumCount();
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < colCount; j++) {
					try {
						result += this.Columns.get(j).ColumnName + ":"
								+ this.getCell(i, j) + " ";
					} catch (dataTableWrongException e) { // TODO Auto-generated
															// catch block
						e.printStackTrace();
					}
				}
				result += "\n";
			}
		}
		return result;
	}
}
