package com.mike;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Database {
    Connection conn = null;
    static String user= "admin";
    static String pw = "mypass";
    static String useSLL ="?useSSL=false";
    static String db_Path= "jdbc:mysql://localhost:3306/log_db"+useSLL;

    public void getConnection(){

        try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        try {
            conn = DriverManager.getConnection(db_Path,user,pw);
            if(conn.isValid(0))
                System.out.println("Connection Successful");
            conn.close();

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public  void runQuery(String startDate, String duration, int threshold){
        try {
            //TODO needs to be tweaked
            String stringEndDate = null;
            Date endDate;
            Connection conn;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
                Date date = formatter.parse(startDate);
                System.out.println("START DATE " + date.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                if(duration=="daily"){
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }
                else{
                    cal.add(Calendar.HOUR,1);
                }
                endDate = cal.getTime();
                stringEndDate = formatter.format(endDate).toString();
                System.out.println("END DATE: "+ formatter.format(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(db_Path,user,pw);
            System.out.println("Connected database successfully...");
            String sql ="select ipAddress,count(ipAddress) as Occurences "+
                        "from log   " +
                        "where date between ? AND "+ "?" +
                        "group by ipAddress " +
                        "having count(*)>=? " +
                        "order by count(*)asc";
            PreparedStatement myStmt = conn.prepareStatement(sql);
            myStmt.setString(1,startDate);
            myStmt.setString(2,stringEndDate);
            myStmt.setInt(3,threshold);
            ResultSet myRs = myStmt.executeQuery();
            // Do something with the Connection
            while (myRs.next()) {
                System.out.println("IP ADDRESS: "+myRs.getString("ipAddress") +" | OCCURENCES :"+myRs.getString("Occurences"));
            }
        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        System.out.println("Finished Running Query");
    }

    private static void populateLogTable(List<LogItem> itemList){

        Connection conn = null;
        PreparedStatement stmt = null;
        try{

            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(db_Path,user,pw);
            System.out.println("Connected database successfully...");
            System.out.println("Inserting values in given table...");

            stmt = conn.prepareStatement("insert into log(id,ipAddress,request,userAgent,status,date)"+
                                              "value (0,?,?,?,?,?)");
            System.out.println("Loading...");
            for (LogItem logItem : itemList) {
                stmt.setString(1,logItem.ipAddress);
                stmt.setString(2,logItem.request);
                stmt.setString(3,logItem.userAgent);
                stmt.setInt(4,logItem.status);
                stmt.setString(5,logItem.date);
                stmt.executeUpdate();
            }
            System.out.println("Updated values in given Table...");
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Success!");

    }
    public static void createLogTable(List<LogItem> itemList){

        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(db_Path,user,pw);
            System.out.println("Connected database successfully...");
            System.out.println("Creating table in given database...");

            stmt = conn.createStatement();
            String sqlCheck="DROP TABLE IF EXISTS LOG";
            String sql =
                    "CREATE TABLE LOG " +
                    "(id INTEGER not NULL AUTO_INCREMENT, " +
                    " ipAddress VARCHAR(255), " +
                    " request VARCHAR(255), " +
                    " userAgent VARCHAR(255), " +
                    " status INTEGER, " +
                    " date DATETIME, " +
                    " PRIMARY KEY ( id ))";

            //stmt.executeUpdate(sqlCheck);
            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
            populateLogTable(itemList);
        }catch(SQLException se){
            //Handle errors for JDBC
            //suppress StackTrace
            //se.printStackTrace();
            //System.out.println("Table already exists!");
            System.out.println(se.getMessage());
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
                System.out.println("Success!");
            }//end finally try
        }//end try
        
    }

}