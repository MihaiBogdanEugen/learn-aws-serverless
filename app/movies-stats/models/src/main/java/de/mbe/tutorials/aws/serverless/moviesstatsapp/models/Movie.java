package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import de.mbe.tutorials.aws.serverless.moviesstatsapp.models.convertors.LocalDateConverter;

import java.time.LocalDate;

@DynamoDBTable(tableName = "movies")
public class Movie {

    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "country_of_origin")
    private String countryOfOrigin;

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "production_date")
    private LocalDate productionDate;

    @DynamoDBAttribute(attributeName = "budget")
    private double budget;

    public Movie() {

    }

    public Movie(final String id, final String name, final String countryOfOrigin, final LocalDate productionDate, final double budget) {
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

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(final LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(final double budget) {
        this.budget = budget;
    }
}
