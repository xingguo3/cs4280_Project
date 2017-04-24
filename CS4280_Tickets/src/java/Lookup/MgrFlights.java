/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lookup;

import beans.FlightBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author GUOXING
 */
public class MgrFlights {
    
    public static void DeleteFlights(FlightBean f) throws ClassNotFoundException, SQLException{
        f =SearchFlight.searchByFid(f.getFID());
        Connection con = null;
        Statement stmt = null;
        try{

           Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
           con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");

           stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           String cancelTick=null;
           cancelTick = "UPDATE dbo.history SET FlightStatus = 0 WHERE FID = '"+f.getFID()+"'";
           stmt.execute(cancelTick);
           
           String strQl = null;
           strQl = "UPDATE dbo.Flight SET Status = 0 WHERE FID = '"+f.getFID()+"'";
           stmt.execute(strQl);
           // add thic action into another database
           // action: delete, fid:
        }finally{
            if (stmt!=null) {
                stmt.close();
            } 
           try{
              if(con!=null)
                 con.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }
    
    public static void AddFlights(String value) throws ClassNotFoundException, SQLException{
        
        Connection con = null;
        Statement stmt = null;
        try{

           Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
           con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");

           stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           String strQl = null;
           strQl = "INSERT INTO dbo.Flight VALUES("+value+")";
           stmt.execute(strQl);
           // add thic action into another database
           // action: add, fid:
        }finally{
            if (stmt!=null) {
                stmt.close();
            } 
           try{
              if(con!=null)
                 con.close();
           }catch(SQLException se){
              se.printStackTrace();
           }
        }
    }    
    
    public static void UpdateFlight(int fid, String takeoff, String land, int price, int seats, int status) throws ClassNotFoundException, SQLException{
            Connection con = null;
            Statement stmt = null;
            try{

               Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
               con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");

               String strQl = null;
               strQl="UPDATE dbo.Flight SET Takeoff=?, Land=?,Price=?,RemainSeat=?, Status=? WHERE FID=?";
               PreparedStatement ps = con.prepareStatement(strQl);
               ps.setString(1, takeoff);
               ps.setString(2, land);
               ps.setInt(3, price);
               ps.setInt(4, seats);
               ps.setInt(5, status);
               ps.setInt(6, fid);
               ps.execute();
               
               
               // add thic action into another database
               // action: update, fid:, old, new
            }finally{
                if (stmt!=null) {
                    stmt.close();
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
