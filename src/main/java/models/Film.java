package models;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;


@XmlRootElement(name="film")
public class Film implements Serializable {

    private String director;
    private int id;
    private String name;
    private String description;
    // private String[] genre;
    private String color;
    private int rating;
    private Timestamp created;
    private Timestamp updated;

    public Film(int id, String name, String description, String director, int rating, String color) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.director = director;
        // this.genre = timestamp;
        this.rating = rating;
        this.color = color;
    }

    public Film(String name, String description, String director, int rating, String color) {
        super();
        this.name = name;
        this.description = description;
        this.director = director;
        // this.genre = genre;
        this.rating = rating;
        this.color = color;
    }

    public Film(int id, String name, String description, String director, int rating, String color, Timestamp created, Timestamp updated) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.director = director;
        this.rating = rating;
        this.color = color;
        this.created = created;
        this.updated = updated;
    }

   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

}
