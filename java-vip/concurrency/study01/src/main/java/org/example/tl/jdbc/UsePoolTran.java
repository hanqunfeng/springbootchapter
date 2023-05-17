package org.example.tl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类说明：使用连接池和事务的JDBC
 */
public class UsePoolTran {

    /*数据库连接池*/
    private static DBPool dbPool = new DBPool(10);

    /*使用事务*/
    public void business(){
        /*获取连接*/
        Connection conn = null;
        try {
            conn = dbPool.fetchConnection(1000);
            conn.setAutoCommit(false);/*开启事务*/

            insert("13号");
            System.out.println("其他业务工作");
            insert("14号");

            conn.commit();/*提交*/
        } catch (Exception e) {
            try {
                /*发生异常时，回滚*/
                if(null!=conn) conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*Insert数据，Update,Delete用法与之类似*/
    private static int insert(String student) {
        int i = 0;
        try {
            Connection conn = dbPool.fetchConnection(1000);
            String sql = "insert into students (Name,Sex,Age) values(?,?,?)";
            PreparedStatement pstmt;
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, student);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /*查询数据*/
    private static Integer getAll() {
        try {
            Connection conn = dbPool.fetchConnection(1000);
            String sql = "select * from students";
            PreparedStatement pstmt;
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                /*读取数据做业务处理*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
