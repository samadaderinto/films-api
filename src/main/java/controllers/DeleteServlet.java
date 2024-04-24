package controllers;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.FilmDB;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name="deleteServlet", urlPatterns = "/delete")
public class DeleteServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "localhost:5173");
		response.setHeader("Access-Control-Allow-Methods", "DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		response.setHeader("Access-Control-Max-Age", "86400");
		
		int id;
		
		try {
			id = Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Invalid film ID provided.");
			return;
		}
		
		FilmDB dao = new FilmDB();
		
		try {
			dao.deleteFilm(id);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect("/films");
	}
	
}



