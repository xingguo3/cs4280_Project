/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lookup;
import beans.BookedTicketBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date ;
import beans.FlightBean;
import beans.UserBean;
import java.util.Calendar;
/**
 *
 * @author GUOXING
 */
public class CustFlights {
    public static boolean bookFlight(int fid,int uid,String fname,String lname,int membership){
        Connection con=null;
      //  Statement stmt=null;
        PreparedStatement prst=null;
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
            int status=getFlightStatus(fid,con);
            int price=getFlightPrice(fid,con);
          //  System.out.println("debug1");
            price=Discount.giveDiscountByMem(price, membership);
            int id=getHisColNo(con);
            String sql="Insert into dbo.history values(?,?,?,?,?,2,?,?,GETDATE())";
            prst=con.prepareStatement(sql);
            //System.out.println("debug2");
            prst.setInt(1, id);
            prst.setInt(2, fid);
            prst.setInt(3, uid);
            prst.setString(4, lname);
            prst.setString(5, fname);
            prst.setInt(6, status);
            prst.setInt(7, price);
            prst.execute();
            //System.out.println("debug3");
            updateSeatNo(fid,con);
            //System.out.println("debug4");
            con.commit();
            prst.close();
            con.close();
            return true;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
            try {
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        return false;
           
    }
    
    public static void updateSeatNo(int fid,Connection con) throws SQLException{
        Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql="update dbo.flight set remainseat=(select remainseat from dbo.flight where fid= "+fid+" )-1 where fid="+fid;
        stmt.execute(sql);
 //       stmt.close();
    }
    public static int getFlightStatus(int fid, Connection con) throws SQLException{
        Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql="select status from dbo.flight where Status!=0 AND fid="+fid;
        ResultSet rs=stmt.executeQuery(sql);
        int status=1;
        while(rs.next())
            status=rs.getInt(1);
        stmt.close();
        rs.close();
        return status;
        
    }
    
    public static int getFlightPrice(int fid, Connection con) throws SQLException{
        Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql="select price,remainseat from dbo.flight where Status!=0 AND fid="+fid;
        ResultSet rs=stmt.executeQuery(sql);
        int price=1;
        int r=0;
        while(rs.next()){
            price=rs.getInt(1);
            r=rs.getInt(2);
        }
        stmt.close();
        rs.close();
        price=Discount.giveDicountByRemainSeat(price, r);
        return price;
        
    }
    
    public static int getHisColNo(Connection con) throws SQLException{
        Statement stmt=con.createStatement();
        String sql="select MAX(id) FROM dbo.history";
        ResultSet rs=stmt.executeQuery(sql);
        int n=1;
        while(rs.next())
            n=rs.getInt(1);
        stmt.close();
        rs.close();
        return n+1;
    }
    
    public static ArrayList<BookedTicketBean> findHistoryByUid(int uid){
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        ArrayList<BookedTicketBean> blist=new ArrayList<>();
         try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
             updateStatus(con);
             String sql="select * from dbo.history where FlightStatus!=0 AND uid= "+uid+" order by bookingTime DESC";
             stmt=con.createStatement();
             rs=stmt.executeQuery(sql);
             while(rs.next()){
                 BookedTicketBean b=new BookedTicketBean();
                 b.setId(rs.getInt("ID"));
                 b.setFlightId(rs.getInt("FID"));
                 b.setLname(rs.getString("LastName"));
                 b.setFname(rs.getString("FirstName"));
                 b.setStatus(rs.getInt("BookingStatus"));
                 
                    
                 b.setUserID(rs.getInt("UID"));
                 b.setActualPrice(rs.getInt("ActualPrice"));
//                 b.setFStatus(rs.getInt("FlightStatus"));
                 b.setBTime(rs.getDate("bookingTime"));
                 FlightBean f=SearchFlight.searchByFid(rs.getInt("FID"));
                 b.setFlight(f);
                 blist.add(b);
             }
             rs.close();
             stmt.close();
             con.close();
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }
         return blist;
    }


    public static ArrayList<BookedTicketBean> findHistoryByStatus(int status) {
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        ArrayList<BookedTicketBean> blist=new ArrayList<>();
         try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
             String sql=null;
             if(status==10){
                 sql="SELECT * FROM dbo.history WHERE BookingStatus!= 2 AND FlightStatus!=0";
             }else{
                 sql="SELECT * FROM dbo.history WHERE FlightStatus!=0 AND BookingStatus= '"+status+"'";
             }
             
             stmt=con.createStatement();
             rs=stmt.executeQuery(sql);
             while(rs.next()){
                 BookedTicketBean b=new BookedTicketBean();
                 b.setId(rs.getInt("ID"));
                 b.setFlightId(rs.getInt("FID"));
                 b.setLname(rs.getString("LastName"));
                 b.setFname(rs.getString("FirstName"));
                 b.setStatus(rs.getInt("BookingStatus"));
                 b.setUserID(rs.getInt("UID"));
                 b.setActualPrice(rs.getInt("ActualPrice"));
//                 b.setFStatus(rs.getInt("FlightStatus"));
                 b.setBTime(rs.getDate("bookingTime"));
                 FlightBean f=SearchFlight.searchByFid(rs.getInt("FID"));
                 b.setFlight(f);
                 blist.add(b);
             }
             rs.close();
             stmt.close();
             con.close();
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }

         return blist;
    }

    public static BookedTicketBean findHistoryByid(int id){
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        BookedTicketBean b=new BookedTicketBean();
         try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
             String sql="select * from dbo.history where FlightStatus!=0 AND ID= "+id;
             stmt=con.createStatement();
             rs=stmt.executeQuery(sql);
             while(rs.next()){
                 b.setId(rs.getInt("ID"));
                 b.setFlightId(rs.getInt("FID"));
                 b.setLname(rs.getString("LastName"));
                 b.setFname(rs.getString("FirstName"));
                 b.setStatus(rs.getInt("BookingStatus"));
                 
                 b.setUserID(rs.getInt("UID"));
                 b.setActualPrice(rs.getInt("ActualPrice"));
                 //b.setFStatus(rs.getInt("FlightStatus"));
                 b.setBTime(rs.getDate("bookingTime"));
                 FlightBean f=SearchFlight.searchByFid(rs.getInt("FID"));
                 b.setFlight(f);
                 
             }
             rs.close();
             stmt.close();
             con.close();
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }
         return b;
    }
    
    public static boolean applyForRefund(int id,int uid){
         Connection con=null;
        PreparedStatement prst=null;
        try {
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
            String sql="UPDATE dbo.history SET BookingStatus = ? WHERE ID = ?";
            prst=con.prepareStatement(sql);
            prst.setInt(1, 0);
            prst.setInt(2, id);
            prst.execute();    
            con.commit();
            con.close();
            return true;
            
            
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public static void updateStatus(Connection con) throws SQLException{
            String sql="update dbo.history set BookingStatus=1 where getdate()>(select takeoff from dbo.flight b where dbo.history.fid=b.FID)"
                    + " AND BookingStatus=2";
            Statement stmt=con.createStatement();
            stmt.execute(sql);
            stmt.close();
    }

    public static ArrayList<BookedTicketBean> findHistoryByDate(int period) {
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        period=-period;
        ArrayList<BookedTicketBean> blist=new ArrayList<>();
         try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
             String sql="SELECT * FROM dbo.history WHERE FlightStatus!=0 AND bookingTime>=DATEADD(DAY,"+period+",GETDATE())" ;
             stmt=con.createStatement();
             rs=stmt.executeQuery(sql);
             while(rs.next()){
                 BookedTicketBean b=new BookedTicketBean();
                 b.setId(rs.getInt("ID"));
                 b.setFlightId(rs.getInt("FID"));
                 b.setLname(rs.getString("LastName"));
                 b.setFname(rs.getString("FirstName"));
                 b.setStatus(rs.getInt("BookingStatus"));
                 b.setUserID(rs.getInt("UID"));
                 b.setActualPrice(rs.getInt("ActualPrice"));
//                 b.setFStatus(rs.getInt("FlightStatus"));
                 b.setBTime(rs.getDate("bookingTime"));
                 FlightBean f=SearchFlight.searchByFid(rs.getInt("FID"));
                 b.setFlight(f);
                 blist.add(b);
             }
             rs.close();
             stmt.close();
             con.close();
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }

         return blist;
    }
    
    public static ArrayList<BookedTicketBean> findHistoryByPlace(String from, String to) {
        Connection con=null;
        Statement stmt=null;
        ResultSet rs=null;
        ArrayList<BookedTicketBean> blist=new ArrayList<>();
         try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
             String sql="SELECT * FROM dbo.history WHERE FlightStatus!=0";
             stmt=con.createStatement();
             rs=stmt.executeQuery(sql);
             while(rs.next()){
                 BookedTicketBean b=new BookedTicketBean();
                 b.setId(rs.getInt("ID"));
                 b.setFlightId(rs.getInt("FID"));
                 b.setLname(rs.getString("LastName"));
                 b.setFname(rs.getString("FirstName"));
                 b.setStatus(rs.getInt("BookingStatus"));
                 b.setUserID(rs.getInt("UID"));
                 b.setActualPrice(rs.getInt("ActualPrice"));
//                 b.setFStatus(rs.getInt("FlightStatus"));
                 b.setBTime(rs.getDate("bookingTime"));
                 FlightBean f=SearchFlight.searchByFid(rs.getInt("FID"));
                 b.setFlight(f);
                 if(f.getFrom().contains(from)&&f.getTo().contains(to)){
                       blist.add(b);
                 }else{
                     continue;
                 }
                 
             }
             rs.close();
             stmt.close();
             con.close();
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }

         return blist;
    }
    
    public static boolean updateMem(int uid,int membership){
         Connection con=null;
        Statement stmt=null;
        try{
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
             con = DriverManager.getConnection("jdbc:sqlserver://w2ksa.cs.cityu.edu.hk:1433;databaseName=aiad092_db", "aiad092", "aiad092");
            membership=membership+1;
             String sql="update dbo.userslist set membership="+membership+"where userid="+uid;
             stmt=con.createStatement();
             stmt.execute(sql);
             stmt.close();
             con.close();
             return true;
        
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CustFlights.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    return false;
    }


}
