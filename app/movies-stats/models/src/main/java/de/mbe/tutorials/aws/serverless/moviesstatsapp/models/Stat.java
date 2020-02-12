package de.mbe.tutorials.aws.serverless.moviesstatsapp.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "stats")
public final class Stat {

    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "direct_to_streaming")
    private Boolean directToStreaming;

    @DynamoDBAttribute(attributeName = "rotten_tomatoes_rating")
    private Integer rottenTomatoesRating;

    @DynamoDBAttribute(attributeName = "imdb_rating")
    private Integer imdbRating;

    @DynamoDBAttribute(attributeName = "box_office")
    private Double boxOffice;

    @DynamoDBAttribute(attributeName = "release_date")
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
