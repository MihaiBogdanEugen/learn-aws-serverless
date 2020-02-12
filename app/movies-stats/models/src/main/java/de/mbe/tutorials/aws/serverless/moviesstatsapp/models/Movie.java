package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

public final class Movie {

    private String id;
    private String name;
    private String countryOfOrigin;
    private String productionDate;
    private Double budget;

    public Movie() {

    }

    public Movie(final String id, final String name, final String countryOfOrigin, final String productionDate, final Double budget) {
        this.id = id;
        this.name = name;
        this.countryOfOrigin = countryOfOrigin;
        this.productionDate = productionDate;
        this.budget = budget;
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
}
