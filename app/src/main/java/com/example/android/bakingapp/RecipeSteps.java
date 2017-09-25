package com.example.android.bakingapp;

public class RecipeSteps {

    private final int stepID;
    private final String shortDescription;
    private final String description;
    private final String videoURL;
    private final String thumbnailURL;

    public RecipeSteps(int stepID, String shortDescription, String description,
                       String videoURL, String thumbnailURL){
        this.stepID = stepID;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public int getStepID() {
        return stepID;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
}
