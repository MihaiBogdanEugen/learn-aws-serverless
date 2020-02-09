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
    private boolean directToStreaming;

    @DynamoDBAttribute(attributeName = "rotten_tomatoes_rating")
    private int rottenTomatoesRating;

    @DynamoDBAttribute(attributeName = "imdb_rating")
    private int imdbRating;

    @DynamoDBAttribute(attributeName = "box_office")
    private double boxOffice;

    @DynamoDBAttribute(attributeName = "release_date")
    private String releaseDate;

    public Stat() {

    }

    public Stat(final String id, final boolean directToStreaming, final int rottenTomatoesRating, final int imdbRating, final double boxOffice, final String releaseDate) {
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
