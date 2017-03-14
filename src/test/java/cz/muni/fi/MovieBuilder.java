package cz.muni.fi;

/**
 * Builder of movies for tests
 */
public class MovieBuilder {
    private Long id = 123L;
    private String name = "Ján Jakub";
    private Integer year = 1999;
    private String classification = "18";
    private String description = "Nejaký popis, autori, žánre";
    private String location = "doma";

    public Movie build()
    {
        Movie movie =  new Movie();
        movie.setId(id);
        movie.setName(name);
        movie.setYear(year);
        movie.setClassification(classification);
        movie.setDescription(description);
        movie.setLocation(location);
        return movie;
    }

    public MovieBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public MovieBuilder withYear(Integer year)
    {
        this.year = year;
        return this;
    }

    public MovieBuilder withClassification(String classification)
    {
        this.classification = classification;
        return this;
    }

    public MovieBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    public MovieBuilder withLocation(String location)
    {
        this.location = location;
        return this;
    }

    public MovieBuilder withId(Long id) {
        this.id = id;
        return this;
    }
}
