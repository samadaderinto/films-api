package controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import java.util.ArrayList;

import database.FilmDB;
import models.Film;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.PrintWriter;
import java.io.InputStream;

@WebServlet(name = "AddServlet", urlPatterns = "/add")
public class AddServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String contentType = request.getContentType();
		Film film = null;
		
		
		if ("application/json".equals(contentType)) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			
			response.setHeader("Access-Control-Allow-Origin", "localhost:5173");
			response.setHeader("Access-Control-Allow-Methods", "POST");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
			
			
			try (BufferedReader reader = request.getReader()) {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			}
			catch (Exception exception) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("You have an error reading request body");
				return;
			}
			
			Gson gsonSerializer = new Gson();
			
			try {
				film = gsonSerializer.fromJson(sb.toString(), Film.class);
			}
			catch (JsonSyntaxException exception) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Error parsing JSON request");
				return;
			}
		}
		
		else if ("application/xml".equals(contentType)) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				film = (Film) unmarshaller.unmarshal(request.getInputStream());
			}
			catch (JAXBException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Invalid XML format");
				return;
			}
		}
		
		else {
			
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			response.getWriter().write("Unsupported Content-Type. Please use application/json or application/xml.");
			return;
		}
		
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		
		
		if (film.getName() == null || film.getName().trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Title is a required field and cannot be empty.");
			out.close();
			return;
		}
		
		if (String.valueOf(film.getRating()) == null || String.valueOf(film.getRating()).isEmpty() || film.getRating() < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Year is a required field and cannot be zero.");
			out.close();
			return;
		}
		
		if (film.getDirector() == null || film.getDirector().trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Director is a required field and cannot be empty.");
			out.close();
			return;
		}
		
		if (film.getDescription() == null || film.getDescription().trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Stars is a required field and cannot be empty.");
			out.close();
			return;
		}
		
		if (film.getColor() == null || film.getColor().trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Review is a required field and cannot be empty.");
			out.close();
			return;
		}
		
		film.setName(film.getName().toUpperCase());
		film.setDirector(film.getDirector().toUpperCase());
		film.setDescription(film.getDescription());
		
		FilmDB dao = new FilmDB();
		
		try {
			dao.insertFilm(film);
			response.setStatus(HttpServletResponse.SC_CREATED);
			out.write(film.getName() + " has been added successfully.");
		}
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("An error occurred while adding the film.");
			e.printStackTrace();
		}
		
		finally {
			out.close();
		}
		
	}
}



















