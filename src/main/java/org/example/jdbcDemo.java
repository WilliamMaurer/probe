package org.example;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class jdbcDemo{
    static String DRIVER = "";
    static String URL = "";
    static String USER ="";
    static String PASSWORD = "";

    public static void setDataBase(String DRIVER, String URL, String USER, String PASSWORD) {
        System.out.println("setDataBase");
        jdbcDemo.DRIVER = DRIVER;
        jdbcDemo.URL = URL;
        jdbcDemo.USER = USER;
        jdbcDemo.PASSWORD = PASSWORD;
    }

    public void setDRIVER(String DRIVER) {
        this.DRIVER = DRIVER;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }


    public void setUSER(String USER) {
        this.USER = USER;
    }


    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
    public static Connection getConnetion(String DRIVER, String URL, String USER, String PASSWORD){

        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,USER,PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("数据库链接失败！");
        }
        return connection;
    }
    public static void main(String[] args) throws SQLException {
        System.out.println("开始Demo， main() is loading......");
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String URL = "jdbc:mysql://localhost:3306/icbc?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
        String USER = "root";
        String PASSWORD = "123456";
        //查询1——查询价格在100~200的skuid
        String sql1 = "select skuid from goods where price>= 100 and price<=200;";
        //查询2——价格大于500且cate=71的所有商品
        String sql2 = "select skuid from goods where price>= 500 and cate = 71;";
        //查询3——谁买了cate = 2 的商品
        String sql3 = "select useid from orders,goods where goods.skuid= orders.skuid and goods.cate = 71 group by useid;";
        //查询4--谁买了cate = 2 的商品
        String sql4 = "select distinct useid from orders,goods where goods.skuid= orders.skuid and goods.cate = 71;";
        //查询5——id = xxxx 的人买的最贵的前3件商品
        String sql5 = "select orders.skuid,orders.o_date,goods.price from orders left join goods on orders.skuid=goods.skuid where useid=80 order by price limit 0,3;";
        //在orders添加 useid = 80, skuid = 33948 orderid =999999 o_date = 2020-07-17 o_area = 1 o_sku_num = 1
        String sql6 = "insert into orders (useid,skuid,orderid,o_date,o_area,o_sku_num) values ('80','33948','999999',str_to_date('2020-07-17','%Y-%m-%d'),1,1);";
        //删除orders中‘2020-07-17’的数据
        String sql7 = "delete from orders where o_date=str_to_date('2020-07-17','%Y-%m-%d');";
        List sql = new ArrayList();
        sql.add(sql1);
        sql.add(sql2);
        sql.add(sql3);
        sql.add(sql4);
        sql.add(sql5);

//        测试示例
        hellw d = new hellw();
        String g= d.add("aa","bb");
//        运行SQL
        jdbcDemo jd = new jdbcDemo();
        jd.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn = getConnetion(DRIVER,URL,USER,PASSWORD);
//        System.out.println(conn);
        PreparedStatement pstt1 = conn.prepareStatement(sql1);
        ResultSet rs1 = pstt1.executeQuery();

        PreparedStatement pstt2 = conn.prepareStatement(sql2);
        ResultSet rs2 = pstt2.executeQuery();

        PreparedStatement pstt3 = conn.prepareStatement(sql3);
        ResultSet rs3 = pstt3.executeQuery();

        PreparedStatement pstt4 = conn.prepareStatement(sql4);
        ResultSet rs4 = pstt4.executeQuery();

        PreparedStatement pstt5 = conn.prepareStatement(sql5);
        ResultSet rs5 = pstt5.executeQuery();
//        插入删除使用下列操作：
        PreparedStatement pstt6 = conn.prepareStatement(sql6);
        pstt6.executeUpdate();
        PreparedStatement pstt7 = conn.prepareStatement(sql7);
        pstt7.executeUpdate();
        System.out.println("main()已结束。");

    }

}
