package my.db.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Hbase 测试<br/>
 * 创建表，删除表
 * 对表的增删改查
 * @author mzy
 *
 */
public class HbaseUtil {
	private static Configuration conf = null;
	private static HBaseAdmin admin = null;
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "localhost");
		conf = HBaseConfiguration.create(HBASE_CONFIG);
	}

	/**
	 * 创建表
	 * @param tName
	 * @param colFamilys
	 * @throws IOException
	 */
	public static void createTable(String tName, String[] colFamilys)
			throws IOException {
		admin = new HBaseAdmin(conf);
		if (admin.tableExists(tName)) {
			System.out.println("table already exisit!");
		} else {
			HTableDescriptor tableDesc = new HTableDescriptor(tName);
			for (int i = 0; i < colFamilys.length; i++) {
				tableDesc.addFamily(new HColumnDescriptor(colFamilys[i]));
			}
			admin.createTable(tableDesc);
			System.out.println("create table :" + tName + " successfully");
		}
	}

	/**
	 * 删除表
	 * @param tableName
	 */
	public static void deleteTable(String tableName) {
		try {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
			System.out.println("delete table :" + tableName + " successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 添加记录
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @param value
	 * @throws IOException
	 */
	public static void addRecord(String tableName, String rowKey,
			String family, String qualifier, String value) throws IOException {
		HTable ht = new HTable(conf, tableName);
		Put put = new Put(Bytes.toBytesBinary(rowKey));
		put.add(Bytes.toBytesBinary(family), Bytes.toBytesBinary(qualifier),
				Bytes.toBytesBinary(value));
		ht.put(put);
		System.out.println("insert recored " + rowKey + " to table "
				+ tableName + " successfully.");

	}

	/**
	 * 删除记录
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void delRecord(String tableName, String rowKey)
			throws IOException {
		HTable table = new HTable(conf, tableName);
		List list = new ArrayList();
		Delete del = new Delete(rowKey.getBytes());
		list.add(del);
		table.delete(list);
		System.out.println("del recored " + rowKey + " ok.");
	}

	/**
	 * 取得一条数据
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void getOneRecord(String tableName, String rowKey)
			throws IOException {
		HTable table = new HTable(conf, tableName);
		Get get = new Get(rowKey.getBytes());
		Result rs = table.get(get);
		for (KeyValue kv : rs.raw()) {
			System.out.print(new String(kv.getRow()) + " ");
			System.out.print(new String(kv.getFamily()) + ":");
			System.out.print(new String(kv.getQualifier()) + " ");
			//System.out.print(kv.getTimestamp() + " ");
			System.out.println(new String(kv.getValue()));
		}
	}

	/**
	 * 取得全部记录
	 * @param tableName
	 */
	public static void getAllRecord(String tableName) {
		try {
			HTable table = new HTable(conf, tableName);
			Scan s = new Scan();
			ResultScanner ss = table.getScanner(s);
			for (Result r : ss) {
				for (KeyValue kv : r.raw()) {
					System.out.print(new String(kv.getRow()) + " ");
					System.out.print(new String(kv.getFamily()) + ":");
					System.out.print(new String(kv.getQualifier()) + " ");
				//	System.out.print(kv.getTimestamp() + " ");
					System.out.println(new String(kv.getValue()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		admin = new HBaseAdmin(conf);
		String tablename = "PCOnline_Mobile";
		String[] familys = { "id", "product_name","product_properties"};
		createTable(tablename, familys);
//		// add record zkb
		 addRecord(tablename,"1","id","","095832");
		 addRecord(tablename,"1","product_name","","lenovo");
		 addRecord(tablename,"1","product_properties","price","201");
		 addRecord(tablename,"1","product_properties","type","7799");
		 
		 addRecord(tablename,"2","id","","095833");
		 addRecord(tablename,"2","product_name","","nokia");
		 addRecord(tablename,"2","product_properties","price","97");
		 addRecord(tablename,"2","product_properties","color","blue");
		 addRecord(tablename,"2","product_properties","type","n85");
		// add record baoniu

		System.out.println("===========get one record========");
		getOneRecord(tablename, "1");

		System.out.println("===========show all record========");
		getAllRecord(tablename);

		System.out.println("===========del one record========");
		//delRecord(tablename, "2");
		//getAllRecord(tablename);
	//	deleteTable(tablename);
	}
}
