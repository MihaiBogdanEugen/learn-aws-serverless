package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

public final class MovieAndStat {

    private String id;
    private String name;
    private String countryOfOrigin;
    private String productionDate;
    private Double budget;
    private Boolean directToStreaming;
    private Integer rottenTomatoesRating;
    private Integer imdbRating;
    private Double boxOffice;
    private String releaseDate;

    public MovieAndStat() {
    }

    public MovieAndStat(final String id, final String name, final String countryOfOrigin, final String productionDate, final Double budget, final Boolean directToStreaming, final Integer rottenTomatoesRating, final Integer imdbRating, final Double boxOffice, final String releaseDate) {
        this.id = id;
        this.name = name;
        this.countryOfOrigin = countryOfOrigin;
        this.productionDate = productionDate;
        this.budget = budget;
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(final String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(final String productionDate) {
        this.productionDate = productionDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(final Double budget) {
        this.budget = budget;
    }

    public Boolean getDirectToStreaming() {
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

    public void setMovie(final Movie movie) {
        this.setName(movie.getName());
        this.setCountryOfOrigin(movie.getCountryOfOrigin());
        this.setProductionDate(movie.getProductionDate());
        this.setBudget(movie.getBudget());
    }

    public void setStat(final Stat stat) {
        this.setDirectToStreaming(stat.isDirectToStreaming());
        this.setRottenTomatoesRating(stat.getRottenTomatoesRating());
        this.setImdbRating(stat.getImdbRating());
        this.setBoxOffice(stat.getBoxOffice());
        this.setReleaseDate(stat.getReleaseDate());
    }
}