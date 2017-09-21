package com.example.android.bakingapp.ui;

import android.app.Fragment;
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
    private String mRecipe;
    private MediaSource mMediaSource;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_instruction_display, container, false);

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.Media);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.StepDescription);
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle != null) {
            String[] intentString = bundle.getStringArray("InstructionSet");
            position = bundle.getInt("ItemPosition");
            itemCount = bundle.getInt("ItemCount");
            mDescription = intentString[0];
            mUriString = intentString[1];
            mThumbnailUri = intentString[2];

            Log.d(TAG, "onCreateView: " + Integer.toString(position));
        }
        final Button previousButton = (Button) rootView.findViewById(R.id.Previous);
        final Button nextButton = (Button) rootView.findViewById(R.id.Next);

        if(position == 0) {
            previousButton.setVisibility(View.INVISIBLE);
        } else previousButton.setVisibility(View.VISIBLE);
        if(position == itemCount){
            nextButton.setVisibility(View.INVISIBLE);
        } else nextButton.setVisibility(View.VISIBLE);

//        mCursor.moveToPosition(position);

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(position != 0) {
                    nextButton.setVisibility(View.VISIBLE);
                    mCursor.moveToPrevious();
                    position--;
                    mDescriptionTextView.setText(mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION));
                }
                if (position == 1) previousButton.setVisibility(View.INVISIBLE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != itemCount - 1) {
                    previousButton.setVisibility(View.VISIBLE);
                    mCursor.moveToNext();
                    position++;
                    mDescriptionTextView.setText(mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION));
                }
                if (position == itemCount - 1) nextButton.setVisibility(View.INVISIBLE);
            }
        });
        mDescriptionTextView.setText(mDescription);
        return rootView;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
        mCursor.moveToPosition(position);
    }

    public void destroyCursor(){
        mCursor = null;
    }

    private void initializeMediaSession(){
    }

    private void initializePlayer(){
        if (mExoPlayer == null){
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            if(!mUriString.matches("")) {
                String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
                mMediaSource = new ExtractorMediaSource(Uri.parse(mUriString), new DefaultDataSourceFactory(
                        getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mMediaSource);
                mExoPlayer.setPlayWhenReady(true);
            }
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

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
