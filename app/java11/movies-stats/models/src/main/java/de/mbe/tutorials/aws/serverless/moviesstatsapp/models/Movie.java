package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "movies")
public final class Movie {

    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "country_of_origin")
    private String countryOfOrigin;

    @DynamoDBAttribute(attributeName = "production_date")
    private String productionDate;

    @DynamoDBAttribute(attributeName = "budget")
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
