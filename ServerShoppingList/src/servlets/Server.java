package servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.DatabaseOperations;



/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/Server")
public class Server extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Server() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//JSONObject json = new JSONObject();
		response.getOutputStream().println("Hurray !! This Servlet Works");
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try{
			int length = request.getContentLength();
			byte[] input = new byte[length];
			ServletInputStream sin = request.getInputStream();
			int c, count = 0 ;
			while ((c = sin.read(input, count, input.length-count)) != -1) {
				count +=c;
			}
			sin.close();
			
			String received = new String(input);
			
			if(received.length()>0) System.out.println("Vacsie\n");
			else System.out.println("Mensie");
			
			char type = received.charAt(0);
			//String type = received.substring(0, 1);
			String tmp = received;
			received = tmp.substring(1);
			System.out.println(type);
		
			DatabaseOperations dbsCon = new DatabaseOperations();
			String toSend = null;
			
			switch (type) {
            case '1': toSend = dbsCon.createHuman(received);
                     break;
            case '2': toSend = dbsCon.leaveGroup(received);
            		break;
            case '3': toSend = dbsCon.allGroups(received);
            		break;
            case '4': toSend = dbsCon.showGroupText(received);
            		break;
            case '5': toSend = dbsCon.addGroup(received);
            		break;
            case '6': toSend = dbsCon.addToGroup(received);
            		break;
            case '7': toSend = dbsCon.addItemToGroup(received);
            		break;
            case '8': toSend = dbsCon.deleteText(received);
            default: break;
			}
			
						
			response.setStatus(HttpServletResponse.SC_OK);
			OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
			if(toSend != null){
				writer.write(toSend);
				writer.flush();
				writer.close();
			}
			
			
		}
		catch (IOException e) {
			try{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().print(e.getMessage());
				response.getWriter().close();
				
				} catch (IOException ioe) {
			
				}
		}
	}
		
		
	

}

