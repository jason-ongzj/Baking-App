package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDisplayActivity;
import com.example.android.bakingapp.RecipeInstructionActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by Ben on 9/21/2017.
 */

public class RecipeInstructionFragment extends Fragment
        implements ExoPlayer.EventListener{

    public static final String TAG = "RecipeInstruction";

    private int position;
    private int itemCount;
    private Cursor mCursor;
    private TextView mDescriptionTextView;

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSessionCompat mMediaSession;
    private String mUriString;
    private String mDescription;
    private String mThumbnailUri;
    private MediaSource mMediaSource;
    private boolean mTwoPane;

    DataCommunications mCallback;

    public RecipeInstructionFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (DataCommunications) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement DataCommunication");
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_instruction_display, container, false);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.Media);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.StepDescription);

        if(getActivity().getClass() == RecipeInstructionActivity.class)
            mTwoPane = false;
        else mTwoPane = true;

//         Get info from onClickHandler
        if(!mTwoPane) {
            rootView.setVisibility(View.VISIBLE);
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null) {
                String[] intentString = bundle.getStringArray("InstructionSet");
                position = bundle.getInt("ItemPosition");
                itemCount = bundle.getInt("ItemCount");
                mDescription = intentString[0];
                mUriString = intentString[1];
                mThumbnailUri = intentString[2];
                Log.d(TAG, "onCreateView: " + Integer.toString(position));
            }

            if (savedInstanceState != null) {
                position = savedInstanceState.getInt("AdapterPosition");
                mDescription = savedInstanceState.getString("Description");
                mUriString = savedInstanceState.getString("VideoURL");
                mThumbnailUri = savedInstanceState.getString("ThumbnailURL");
                initializePlayer(position);
                mExoPlayer.seekTo(savedInstanceState.getLong("SeekTime"));
            } else {
                initializePlayer(position);
            }
            mExoPlayer.setPlayWhenReady(true);

            // Check if this is landscape or portrait mode. Buttons and textView
            // are absent in landscape mode.
            checkPortraitOrLandscape(rootView);
        } else {
            rootView.setVisibility(View.INVISIBLE);
        }
        return rootView;
    }

    public void checkPortraitOrLandscape(View rootView){
        final Button previousButton = (Button) rootView.findViewById(R.id.Previous);
        final Button nextButton = (Button) rootView.findViewById(R.id.Next);
        if (nextButton != null && previousButton != null) {
            if (position == 0) {
                previousButton.setVisibility(View.INVISIBLE);
            } else previousButton.setVisibility(View.VISIBLE);
            if (position == itemCount) {
                nextButton.setVisibility(View.INVISIBLE);
            } else nextButton.setVisibility(View.VISIBLE);

            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != 0) {
                        nextButton.setVisibility(View.VISIBLE);
                        mCursor.moveToPrevious();
                        position--;
                        mDescriptionTextView.setText(mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION));
                        mUriString = mCursor.getString(RecipeDisplayActivity.INDEX_VIDEO_URL);
                        releasePlayer();
                        initializePlayer(position);
                        mExoPlayer.setPlayWhenReady(true);
                    }
                    if (position == 0) previousButton.setVisibility(View.INVISIBLE);
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != itemCount - 1) {
                        previousButton.setVisibility(View.VISIBLE);
                        mCursor.moveToNext();
                        position++;
                        mDescriptionTextView.setText(mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION));
                        mUriString = mCursor.getString(RecipeDisplayActivity.INDEX_VIDEO_URL);
                        releasePlayer();
                        initializePlayer(position);
                        mExoPlayer.setPlayWhenReady(true);
                    }
                    if (position == itemCount - 1) nextButton.setVisibility(View.INVISIBLE);
                }
            });
        }
        if (mDescriptionTextView != null) {
            mDescriptionTextView.setText(mDescription);
        }
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
        mCursor.moveToPosition(position);
    }

    public void updateFragmentViews(){
        String[] recipeData = mCallback.getStringData();
        int[] adapterData = mCallback.getAdapterData();
        mDescription = recipeData[0];
        mUriString = recipeData[1];
        mThumbnailUri = recipeData[2];
        position = adapterData[0];
        itemCount = adapterData[1];
        Log.d(TAG, "updateFragmentViews: " + mDescription);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("SeekTime", mExoPlayer.getCurrentPosition());
        outState.putInt("PlaybackState", mExoPlayer.getPlaybackState());
        outState.putInt("AdapterPosition", position);
        outState.putString("Description", mDescription);
        outState.putString("VideoURL", mUriString);
        outState.putString("ThumbnailURL", mThumbnailUri);
    }

    public void destroyCursor(){
        mCursor = null;
    }

    private void initializePlayer(int position){
        if (mExoPlayer == null){
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(getActivity(), Integer.toString(position));
            if(!mUriString.matches("")) {
                mMediaSource = new ExtractorMediaSource(Uri.parse(mUriString), new DefaultDataSourceFactory(
                        getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            } else {
                mMediaSource = new ExtractorMediaSource(Uri.parse(mThumbnailUri), new DefaultDataSourceFactory(
                        getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            }
            mExoPlayer.prepare(mMediaSource);
//            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer(){
        if(mMediaSource!= null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
