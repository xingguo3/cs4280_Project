<%-- 
    Document   : statResult
    Created on : Apr 22, 2017, 4:30:53 PM
    Author     : GUOXING
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="beans.BookedTicketBean"%>

<% ArrayList<BookedTicketBean> blist=(ArrayList<BookedTicketBean>)request.getAttribute("historyStat");
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <table>
            <% if(blist.size()==0){ %>
            <tr>No History Found</tr>
            <% } else { %>
            <tr>
                <td>Passenger Name</td>
                <td>Book Date</td>
                <td>Flight No</td>
                <td>Route</td>
                <td>Schedule</td>
                <td>Status</td>
                <td>Price</td>
            </tr>    
            <%for(BookedTicketBean b:blist){%>
                <tr >
                    <td><%= b.getLname().toString()%> <%= b.getFname().toString()%></td>
                    <td><%= b.getBTime().toString()%></td>
                    <td><%= b.getFlight().getFlightNo()%></td>
                    <td><%= b.getFlight().getFrom()%> --- <%= b.getFlight().getTo()%></td>
                    <td><%= b.getFlight().getDeptTime()%> --- <%=b.getFlight().getArrivTime()%></td>
                    <td><%= b.getStatus()%></td>
                    <td><%= b.getActualPrice()%>HKD</td>
                    <td><a href="/CS4280_Tickets/ViewDetailHandler?id=<%=b.getId()%>">view detail</a></td>
                </tr>
            <%      }
                }%>
        </table>
        
        
    </body>
</html>