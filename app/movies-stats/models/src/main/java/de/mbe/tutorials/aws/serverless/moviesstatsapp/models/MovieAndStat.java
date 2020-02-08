package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.convertors.LocalDateConverter;

public final class MovieAndStat {

    private String id;
    private String name;
    private String countryOfOrigin;
    private String productionDateAsString;
    private double budget;
    private boolean directToStreaming;
    private int rottenTomatoesRating;
    private int imdbRating;
    private double boxOffice;
    private String releaseDateAsString;

    public MovieAndStat() {
    }

    public MovieAndStat(final String id, final String name, final String countryOfOrigin, final String productionDateAsString, final double budget, final boolean directToStreaming, final int rottenTomatoesRating, final int imdbRating, final double boxOffice, final String releaseDateAsString) {
        this.id = id;
        this.name = name;
        this.countryOfOrigin = countryOfOrigin;
        this.productionDateAsString = productionDateAsString;
        this.budget = budget;
        this.directToStreaming = directToStreaming;
        this.rottenTomatoesRating = rottenTomatoesRating;
        this.imdbRating = imdbRating;
        this.boxOffice = boxOffice;
        this.releaseDateAsString = releaseDateAsString;
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

    public String getProductionDateAsString() {
        return productionDateAsString;
    }

    public void setProductionDateAsString(final String productionDateAsString) {
        this.productionDateAsString = productionDateAsString;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(final double budget) {
        this.budget = budget;
    }

    public boolean isDirectToStreaming() {
        return directToStreaming;
    }

    public void setDirectToStreaming(final boolean directToStreaming) {
        this.directToStreaming = directToStreaming;
    }

    public int getRottenTomatoesRating() {
        return rottenTomatoesRating;
    }

    public void setRottenTomatoesRating(final int rottenTomatoesRating) {
        this.rottenTomatoesRating = rottenTomatoesRating;
    }

    public int getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(final int imdbRating) {
        this.imdbRating = imdbRating;
    }

    public double getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(final double boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getReleaseDateAsString() {
        return releaseDateAsString;
    }

    public void setReleaseDateAsString(final String releaseDateAsString) {
        this.releaseDateAsString = releaseDateAsString;
    }

    public void setMovie(final Movie movie) {
        this.setName(movie.getName());
        this.setCountryOfOrigin(movie.getCountryOfOrigin());
        this.setProductionDateAsString(LocalDateConverter.convertToString(movie.getProductionDate()));
        this.setBudget(movie.getBudget());
    }

    public void setStat(final Stat stat) {
        this.setDirectToStreaming(stat.isDirectToStreaming());
        this.setRottenTomatoesRating(stat.getRottenTomatoesRating());
        this.setImdbRating(stat.getImdbRating());
        this.setBoxOffice(stat.getBoxOffice());
        this.setReleaseDateAsString(LocalDateConverter.convertToString(stat.getReleaseDate()));
    }
}