package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeInstructionFragment extends Fragment
        implements ExoPlayer.EventListener{

    public static final String TAG = "RecipeInstruction";

    @BindView(R.id.Media) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.StepDescription) TextView mDescriptionTextView;
    private Unbinder unbinder;

    private int position;
    private int itemCount;
    private Cursor mCursor;
    private View rootView;

    private SimpleExoPlayer mExoPlayer;
    private String mUriString;
    private String mDescription;
    private String mThumbnailUri;
    private MediaSource mMediaSource;
    private boolean mTwoPane;

    DataCommunications mCallback;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            rootView.setVisibility(View.VISIBLE);
            position = savedInstanceState.getInt("AdapterPosition");
            itemCount = savedInstanceState.getInt("ItemCount");
            mDescription = savedInstanceState.getString("Description");
            mUriString = savedInstanceState.getString("VideoURL");
            mThumbnailUri = savedInstanceState.getString("ThumbnailURL");
            mTwoPane = savedInstanceState.getBoolean("TwoPaneMode");

            checkPortraitOrLandscape(rootView);
            initializePlayer(position);
            mExoPlayer.seekTo(savedInstanceState.getLong("SeekTime"));
            mExoPlayer.setPlayWhenReady(true);
        } else {
            if (!mTwoPane) {
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
                // Check if this is landscape or portrait mode. Buttons and textView
                // are absent in landscape mode.
                checkPortraitOrLandscape(rootView);
                initializePlayer(position);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_instruction_display, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        if (getActivity() instanceof RecipeDisplayActivity){
            if(((RecipeDisplayActivity) getActivity()).isTwoPane()) {
                mTwoPane = ((RecipeDisplayActivity) getActivity()).isTwoPane();
                try {
                    mCallback = (DataCommunications) getActivity();
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString()
                            + " must implement DataCommunication");
                }
            }
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void checkPortraitOrLandscape(View rootView){
        final Button previousButton = (Button) rootView.findViewById(R.id.Previous);
        final Button nextButton = (Button) rootView.findViewById(R.id.Next);
        if (nextButton != null && previousButton != null) {
            if (position == 0) {
                previousButton.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.VISIBLE);
            } else if (position == itemCount - 1) {
                nextButton.setVisibility(View.INVISIBLE);
                previousButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.VISIBLE);
                previousButton.setVisibility(View.VISIBLE);
            }

            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != 0) {
                        nextButton.setVisibility(View.VISIBLE);
                        mCursor.moveToPrevious();
                        position = position - 1;
                        mDescription = mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION);
                        mDescriptionTextView.setText(mDescription);
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
                        position = position + 1;
                        mDescription = mCursor.getString(RecipeDisplayActivity.INDEX_DESCRIPTION);
                        mDescriptionTextView.setText(mDescription);
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

    public void updateFragmentViews(){
        String[] recipeData = mCallback.getStringData();
        int[] adapterData = mCallback.getAdapterData();

        mDescription = recipeData[0];
        mUriString = recipeData[1];
        mThumbnailUri = recipeData[2];
        position = adapterData[0];
        itemCount = adapterData[1];

        mDescriptionTextView.setText(mDescription);
        rootView.setVisibility(View.VISIBLE);
        releasePlayer();
        initializePlayer(position);
        mExoPlayer.setPlayWhenReady(true);
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
        mCursor.moveToPosition(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("AdapterPosition", position);
        outState.putInt("ItemCount", itemCount);
        outState.putString("Description", mDescription);
        outState.putString("VideoURL", mUriString);
        outState.putString("ThumbnailURL", mThumbnailUri);
        outState.putBoolean("TwoPaneMode", mTwoPane);
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
