package database;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import models.Film;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class FilmDB {
	
	Dotenv dotenv = Dotenv.load();
	

	Connection connection = null;
	Statement sqlStatement = null;
	
	String dbURL = "jdbc:mysql://localhost:3306/";
	String user = dotenv.get("USER");
	String password = dotenv.get("PASSWORD");
	String databaseName = dotenv.get("DB_NAME");
	String connectionURL = dbURL + databaseName + "?user=" + user + "&password=" + password;
	
	
	private Connection getConnectionToDB() throws SQLException {
		String dbDriver = "com.mysql.jdbc.Driver";
		
		try {
			Class.forName(dbDriver);
		}
		catch (ClassNotFoundException error) {
			error.printStackTrace();
		}
		
		System.out.println("Connection established successfully!");
		connection = DriverManager.getConnection(connectionURL, user, password);
		
		return connection;
	}
	
	private Film getNextFilm(ResultSet res) {
		
		Film film = null;
		
		try {
			film = new Film(res.getInt("id"), res.getString("name"),res.getString("description"), res.getString("director"), res.getInt("rating"), res.getString("color"), res.getTimestamp("created"), res.getTimestamp("updated"));
		}
		catch (SQLException error) {
			error.printStackTrace();
		}
		
		return film;
	}
	
	
	public ArrayList<Film> getAllFilms() {
		
		ArrayList<Film> allFilms = new ArrayList<Film>();
		String sqlQuery = "SELECT * FROM films;";
		
		try (Connection conn = this.getConnectionToDB();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sqlQuery)) {
			// Retrieve the results
			while (rs.next()) {
				oneFilm = getNextFilm(rs);
				allFilms.add(oneFilm);
			}
			
			stmt.close();
		}
		catch (SQLException se) {
			System.out.println(se);
		}
		
		return allFilms;
	}
	
	/**
	 * Retrieve all Films with pagination
	 *
	 * @param limit The maximum number of films to return
	 * @param offset The number of films to skip
	 * @return Collection of films found in the database within the limit and offset
	 */
	public ArrayList<Film> getAllFilmsPagination(int limit, int offset) {
		ArrayList<Film> allFilms = new ArrayList<>();
		String sqlQuery = "SELECT * FROM films LIMIT ? OFFSET ?;";
		
		try (Connection conn = this.getConnectionToDB();
		PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			
			pstmt.setInt(1, limit);
			pstmt.setInt(2, offset);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Film oneFilm = getNextFilm(rs);
					allFilms.add(oneFilm);
				}
			}
		}
		catch (SQLException sqlException) {
			System.out.println(sqlException);
		}
		
		return allFilms;
	}
	
	public ArrayList<Film> searchFilm(String searchStr) {
		ArrayList<Film> searchResult = new ArrayList<>();
		
		// SQL query checks for the title, director and stars columns
		String sqlQuery = "SELECT * FROM films WHERE LOWER(title) LIKE LOWER(?) OR LOWER(director) LIKE LOWER(?) OR LOWER(stars) LIKE LOWER(?);";
		
		try (Connection conn = this.getConnectionToDB();
		PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			
			// Prepare the search string to include the wildcard character before and after
			String searchWithWildcards = "%" + searchStr + "%";
			
			// Set the prepared statement's parameters for title, director, and stars
			pstmt.setString(1, searchWithWildcards);
			pstmt.setString(2, searchWithWildcards);
			pstmt.setString(3, searchWithWildcards);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Film film = getNextFilm(rs);
					searchResult.add(film);
				}
			}
		}
		catch (SQLException se) {
			System.out.println(se);
		}
		
		return searchResult;
	}
	
	public ArrayList<Film> searchFilmPagination(String searchStr, int limit, int offset) {
		ArrayList<Film> searchResult = new ArrayList<>();
		// Update the SQL query to include LIMIT and OFFSET parameters
		String sqlQuery = "SELECT * FROM films WHERE LOWER(title) LIKE LOWER(?) OR LOWER(director) LIKE LOWER(?) OR LOWER(stars) LIKE LOWER(?) ORDER BY title ASC LIMIT ? OFFSET ?;";
		
		try (Connection conn = this.getConnectionToDB();
		PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			
			// Prepare the search string to include the wildcard character before and after
			String searchWithWildcards = "%" + searchStr + "%";
			
			// Set the prepared statement's parameters for title, director, stars, limit, and offset
			pstmt.setString(1, searchWithWildcards);
			pstmt.setString(2, searchWithWildcards);
			pstmt.setString(3, searchWithWildcards);
			pstmt.setInt(4, limit); // Set limit
			pstmt.setInt(5, offset); // Set offset
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Film film = getNextFilm(rs);
					searchResult.add(film);
				}
			}
		}
		catch (SQLException se) {
			System.out.println(se);
		}
		
		return searchResult;
	}
	
	
	public Film getFilmByID(int id) {
		String sqlQuery = "SELECT * FROM films WHERE id = ?;";
		Film film = null;
		
		try (Connection conn = this.getConnectionToDB();
		PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					film = getNextFilm(rs);
				}
			}
		}
		catch (SQLException error) {
			System.out.println(error);
		}
		
		return film;
	}
	
	public boolean insertFilm(Film f) throws SQLException {
		String sql = "INSERT INTO films (title, year, director, stars, review, added, last_modified) VALUES (?, ?, ?, ?, ?, NOW(), NOW());";
		
		try (Connection conn = this.getConnectionToDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, f.getName());
			pstmt.setInt(2, f.getRating());
			pstmt.setString(3, f.getDirector());
			pstmt.setString(4, f.getDescription());
			pstmt.setString(5, f.getColor());
			
			
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				// Retrieve the generated id
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						f.setId(generatedKeys.getInt(1)); // Set the id back to the film object, if necessary
					}
					else {
						throw new SQLException("Creating film failed, no ID obtained.");
					}
				}
			}
			System.out.print("Successfully Added New Film");
			return true;
		}
		catch (SQLException error) {
			error.printStackTrace(); // Error handling
			return false;
		}
	}
	
	public boolean updateFilm(Film f) throws SQLException {
		boolean b = false;
		String sqlQuery = "UPDATE films SET name = ?, description = ?, director = ?, rating = ?, genre = ?, last_modified = NOW() WHERE id = ?;";
		try (Connection conn = this.getConnectionToDB(); PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			pstmt.setString(1, f.getName());
			pstmt.setInt(2, f.getRating());
			pstmt.setString(3, f.getDirector());
			pstmt.setString(6, f.getDescription());
			pstmt.setInt(4, f.getRating());
			pstmt.setString(5, f.getColor());
			pstmt.setInt(6, f.getId());
			
			int affectedRows = pstmt.executeUpdate();
			b = affectedRows > 0;
		}
		catch (SQLException error) {
			throw new SQLException("Could not update film", error);
		}
		System.out.print("Film Updated");
		return b;
	}
	
	public boolean deleteFilm(int id) throws SQLException {
		boolean b = false;
		String sqlQuery = "DELETE FROM films WHERE id = ?;";
		try (Connection conn = (Connection) this.getConnectionToDB();
		PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, id);
			
			int affectedRows = pstmt.executeUpdate();
			b = affectedRows > 0;
		}
		catch (SQLException error) {
			throw new SQLException("Could not delete film", error);
		}
		return b;
	}
}










