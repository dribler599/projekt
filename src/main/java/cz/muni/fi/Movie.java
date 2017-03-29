package cz.muni.fi;

/**
 * Class representing movie
 */
public class Movie {
    private Long id;
    private String name;
    private Integer year;
    private String classification;
    private String description;
    private String location;

    public Movie(Long nid, String name, int year, String classification, String description, String location) {
        this.id = nid;
        this.name = name;
        this.year = year;
        this.classification = classification;
        this.description = description;
        this.location = location;
    }

    public Movie() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name=" + name +
                ", year=" + year +
                ", classification=" + classification +
                ", description=" + description +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id != null && id.equals(movie.id);
    }
}
