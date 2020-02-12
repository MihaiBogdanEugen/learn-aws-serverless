package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

public final class Stat {

    private String id;
    private Boolean directToStreaming;
    private Integer rottenTomatoesRating;
    private Integer imdbRating;
    private Double boxOffice;
    private String releaseDate;


    public Stat() {

    }

    public Stat(final String id, final Boolean directToStreaming, final Integer rottenTomatoesRating, final Integer imdbRating, final Double boxOffice, final String releaseDate) {
        this.id = id;
        this.directToStreaming = directToStreaming;
        this.rottenTomatoesRating = rottenTomatoesRating;
        this.imdbRating = imdbRating;
        this.boxOffice = boxOffice;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Boolean isDirectToStreaming() {
        return directToStreaming;
    }

    public void setDirectToStreaming(final Boolean directToStreaming) {
        this.directToStreaming = directToStreaming;
    }

    public Integer getRottenTomatoesRating() {
        return rottenTomatoesRating;
    }

    public void setRottenTomatoesRating(final Integer rottenTomatoesRating) {
        this.rottenTomatoesRating = rottenTomatoesRating;
    }

    public Integer getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(final Integer imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Double getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(final Double boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
