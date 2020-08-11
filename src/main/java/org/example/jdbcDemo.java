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
//        System.out.println("setDataBase");
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
        String PASSWORD = "Sr617917";
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

        String sql8="select skuid from goods";
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
        jdbcDemo jd1 = new jdbcDemo();
        jd1.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn1 = jd1.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt1 = conn1.prepareStatement(sql1);
        ResultSet rs1 = pstt1.executeQuery();
        rs1.close();
        conn1.close();

        jdbcDemo jd2 = new jdbcDemo();
        jd2.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn2 = jd2.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt2 = conn2.prepareStatement(sql2);
        ResultSet rs2 = pstt2.executeQuery();
        rs2.close();
        conn2.close();

        jdbcDemo jd3 = new jdbcDemo();
        jd3.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn3 = jd3.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt3 = conn3.prepareStatement(sql3);
        ResultSet rs3 = pstt3.executeQuery();
        rs3.close();
        conn3.close();

        jdbcDemo jd4 = new jdbcDemo();
        jd4.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn4 = jd4.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt4 = conn4.prepareStatement(sql4);
        ResultSet rs4 = pstt4.executeQuery();
        rs4.close();
        conn4.close();

        jdbcDemo jd5 = new jdbcDemo();
        jd5.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn5 = jd5.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt5 = conn5.prepareStatement(sql5);
        ResultSet rs5 = pstt5.executeQuery();
        rs5.close();
        conn5.close();
//        插入删除使用下列操作：
        jdbcDemo jd6 = new jdbcDemo();
        jd6.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn6 = jd6.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt6 = conn6.prepareStatement(sql6);
        pstt6.executeUpdate();
        pstt6.close();
        conn6.close();

        jdbcDemo jd7 = new jdbcDemo();
        jd7.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn7 = jd7.getConnetion(DRIVER,URL,USER,PASSWORD);
        PreparedStatement pstt7 = conn7.prepareStatement(sql7);
        pstt7.executeUpdate();
        pstt7.close();
        conn7.close();

        jdbcDemo jd8 = new jdbcDemo();
        jd8.setDataBase(DRIVER,URL,USER,PASSWORD);
        Connection conn8 = jd8.getConnetion(DRIVER,URL,USER,PASSWORD);
        Statement sta = conn8.createStatement();
        ResultSet rs = sta.executeQuery(sql8);
        rs.close();
        conn8.close();

        System.out.println("main()已结束。");

    }

}
