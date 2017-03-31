package com.ttqeducation.teacher.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 行对象
 * 
 * @author 王勤为
 * 
 */
public class DataRow {

	// 用于存储行数据的Map对象，这里保存的对象不包括顺序信息，数据获取的索引通过行信息标识
	private Map<String, Object> itemMap;

	public List<DataColumn> Columns;// 列

	/**
	 * 构造器
	 * 
	 * @param iMap
	 * @throws dataTableWrongException
	 */
	public DataRow(Map<String, Object> iMap) throws dataTableWrongException {
		if (iMap.size() != 0) {
			this.itemMap = iMap;

			// 初始化列数据
			this.Columns = new LinkedList<DataColumn>();
			// Iterator it = iMap.keySet().iterator();
			// while (it.hasNext()) {
			// Map.Entry<String, Object> entry = (Entry<String, Object>)
			// it.next();
			// DataColumn col = new DataColumn((String) entry.getKey());
			// this.Columns.add(col);
			// }
			for (String key : iMap.keySet()) {
				DataColumn col = new DataColumn(key);
				this.Columns.add(col);
			}

		} else {
			throw new dataTableWrongException(" no data in map !");
		}
	}

	/**
	 * 获得当前行某列的值
	 * 
	 * @param colNum
	 * @return
	 */
	public String getCell(int colNum) throws dataTableWrongException {
		if (colNum <= this.Columns.size()) { // 如果索引值没有超出范围
			String value = null;
			int i = 0;
			for (Object key : this.itemMap.keySet()) {
				if (i == colNum) {
					value = this.itemMap.get(key).toString();
				}
				i++;
			}

			return value;
		} else {// 如果索引值超出范围
			throw new dataTableWrongException("out of index!");
		}
	}

	/**
	 * 获得当前行某列的值
	 * 
	 * @param colNum
	 * @return
	 */
	public String getCell(String colName) throws dataTableWrongException {
		String value = null;
		for (Object key : this.itemMap.keySet()) {
			if (key.toString().equals(colName)) {
				value = this.itemMap.get(key).toString();
			}
		}
		return value;
	}
}
