package com.example.android.bakingapp.ui;

interface DataCommunications {
    public String[] getStringData();
    public void setStringData(String description, String videoURL, String thumbnailURL, String recipe);
    public int[] getAdapterData();
    public void setAdapterData(int position, int adapterCount);
    public RecipeInstructionFragment getInstructionFragment();
    public String getBitmap();
}
