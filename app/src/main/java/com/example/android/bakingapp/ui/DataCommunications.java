package com.example.android.bakingapp.ui;

/**
 * Created by Ben on 9/22/2017.
 */

public interface DataCommunications {
    public String[] getStringData();
    public void setStringData(String description, String videoURL, String thumbnailURL);
    public int[] getAdapterData();
    public void setAdapterData(int position, int adapterCount);
    public RecipeInstructionFragment getInstructionFragment();
}
