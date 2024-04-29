package controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.FilmDB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import models.Film;
import models.Films;


@WebServlet(name="SearchServlet", urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
	
	FilmDB FilmDBInstance;
	
	public SearchServlet() {
		FilmDBInstance = new FilmDB();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "localhost:5173");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		
		String searchQuery = request.getParameter("searchQuery");
		PrintWriter pw = response.getWriter();
		Gson gson = new Gson();
		ArrayList<Film> searchResults = FilmDBInstance.searchFilm(searchQuery);
		

		String format = request.getHeader("Accept");

        if (searchResults == null) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			pw.write("No results found.");
			return;
		}


        int limit = 10; 
		int offset = 0; 

		try {
			String limitParam = request.getParameter("limit");
			if (limitParam != null) {
				limit = Integer.parseInt(limitParam);
			}

			String offsetParam = request.getParameter("offset");
			if (offsetParam != null) {
				offset = Integer.parseInt(offsetParam);
			}
		} catch (NumberFormatException exception) {
			
			System.out.println("Invalid pagination parameters. Using defaults parameters please.");
		}
		

        
		
		if (format.equals("application/json")) {
			response.setContentType("application/json");
			String json = gson.toJson(searchResults);
			pw.write(json);
		}
		else if (format.equals("application/xml")) {
			response.setContentType("application/xml");
			try {

				JAXBContext jaxbContext = JAXBContext.newInstance(Films.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
				Films wrapper = new Films();
				wrapper.setFilms(searchResults);
				
				marshaller.marshal(wrapper, pw);
				
			}
			catch (JAXBException e) {
				throw new ServletException("Error marshalling films to XML", e);
			}
		}
		pw.close();
	}
}










