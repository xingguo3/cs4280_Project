/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lookup;

import java.sql.*;

/**
 *
 * @author GUOXING
 */
public class Registration {
    private static int CustomerID=666;
    
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db";
   
   static final String USER = "aiad092";
   static final String PASS = "aiad092";
   
    private String username;
    private String gender;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
   
   public Registration(String s1, String s2, String s3, String s4, String s5, String s6) throws ClassNotFoundException, SQLException {
        this.username=s1;
        this.gender=s2;
        this.password=s3;
        this.email=s4;
        this.firstname=s5;
        this.lastname=s6;
        
   }

   
    public void setCustomerID(){
        this.CustomerID = 666;
    }
    public int getCustomerID(){
        CustomerID++;
        return this.CustomerID;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getGender(){
        return this.gender;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public String getFirstname(){
        return this.firstname;
    }
    
    public String getLastname(){
        return this.lastname;
    }
   
   public void insert() throws SQLException{
        Connection con = null;
        Statement stmt = null;
        try{

           Class.forName("com.mysql.jdbc.Driver");
           con = DriverManager.getConnection(DB_URL, USER, PASS);

           //stmt = con.createStatement();
           stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           //String strQL = "INSERT INTO dbo.usersList VALUES ("+ this.getCustomerID()+ ", '"+this.getUsername()+"', '"+this.getPassword()+ "', '"+this.getGender()+"', '"+this.getFirstname()+"', '"+this.getLastname()+"', "+  "'0', '0', 0, '0', '0'，'"+"this.getEmail()"+"')";
           String strQl = "INSERT INTO dbo.usersList VALUES ("+ this.getCustomerID()+ ", 'chingming', 'ching','M', 'ching', 'ming', '0', '0', 0, '0', '0','sfda')";
           //stmt.executeQuery(strQL);
           stmt.executeQuery(strQl);

        }catch(SQLException se){
           se.printStackTrace();
        }catch(Exception e){
           e.printStackTrace();
        }finally{
            if (stmt!=null) {
                con.close();
            } 
           try{
              if(con!=null)
                 con.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }

   }
    
}


//private String username;
//    //private String gender ;
//    private String password;
//    private String email;
//    private String firstname;
//    private String lastname;
//    private Connection con;
//    public Registration(String s1, String s2, String s3, String s4, String s5, String s6) throws SQLException{
//        this.con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
//        this.username=s1;
//        this.password=s3;
//        this.email=s4;
//        this.firstname=s5;
//        this.lastname=s6;
//    }
//    public static void main(String[] args) {
//        Connection con = null;
//        Statement stmt = null;
//    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        
//    
//    //String strQl = "INSERT INTO dbo.usersList VALUES ("+ this.getCustomerID()+ ", 'chingming', 'ching','M', 'notok', 'ching', 'ming', '0', '0', 0, '0', '0')";
//    
//    Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//    ResultSet rs = stmt.executeQuery(strQL);
//    if (rs != null) {
//        rs.close();
//    }
//    if (stmt != null) {
//        stmt.close();
//    }
//    if (con != null) {
//        con.close();
//    }
//    
//    
//    }

