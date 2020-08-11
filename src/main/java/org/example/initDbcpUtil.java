package org.example;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class initDbcpUtil {
    private static Properties properties = new Properties();
    private static DataSource dataSource;

//    加载DBCP配置文档
    static {
        try {
            FileInputStream fis = new FileInputStream("D:\\icbctest\\Javaagent\\SQLpooltestM\\src\\main\\resources\\dbcp.properties");
            System.out.println(fis);
            properties.load(fis);
            System.out.println(properties.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
            System.out.println(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    从连接池中获取一个连接

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

//    从DBCP连接池中连接
    public static void writeDBByDBCP(int data) {
        String sql = "insert into orders (useid,skuid,orderid,o_date,o_area,o_sku_num) values " +
                "('80','33948','999999',str_to_date('2020-07-17','%Y-%m-%d'),"+data+","+data+");";

        try {
            Connection conn = initDbcpUtil.getConnection();
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
            conn.commit();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(dataSource);
    }

    public static void testWriteDBByDBCP()throws Exception{
        for(int i = 0;i<100;i++){
            writeDBByDBCP(i);
        }
        System.out.println("WriteDBByDBCP DONE");
    }
    public static void main(String[] args) throws Exception {
        testWriteDBByDBCP();
    }
}
