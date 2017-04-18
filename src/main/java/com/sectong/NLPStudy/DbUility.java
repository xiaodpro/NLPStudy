package com.sectong.NLPStudy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * Created by vego on 17/4/18.
 */
public class DbUility {

    private Connection con;
    public Connection InitMysqlConnection(){
        try {
//            Connection con = null; //定义一个MYSQL链接对象
            Class.forName("com.mysql.jdbc.Driver").newInstance(); //MYSQL驱动
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test", "root", "root"); //链接本地MYSQL
            System.out.print("yes");
            return  con;
        } catch (Exception e) {
            System.out.println("MYSQL ERROR:" + e.getMessage());
            return null;
        }
    }

    public boolean Select(String sql){
        try {
            Statement stmt; //创建声明
            stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(sql);
            return res.next();
        } catch (Exception e) {
            System.out.println("MYSQL  Select ERROR:" + e.getMessage());
            return false;
        }
    }

    public int Insert(String sql) {
        try {
            Statement stmt; //创建声明
            stmt = con.createStatement();
            //新增一条数据
            return stmt.executeUpdate(sql);
        } catch (Exception e) {
            //新增一条数据
            System.out.println("MYSQL  Insert ERROR:" + e.getMessage());
            return 0;
        }
    }
//    public
}
