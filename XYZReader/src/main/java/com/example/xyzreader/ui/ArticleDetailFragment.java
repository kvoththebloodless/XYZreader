package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "ArticleDetailFragment";
    private static final float PARALLAX_FACTOR = 1.25f;
    public CollapsingToolbarLayout mCtool;
    public Toolbar tooly;
    public android.support.design.widget.FloatingActionButton fab;
    public TextView articleContent;
    public TextView titletext, subtitletext, titletextt, caption;
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private int mVibrantColor = 0xFF333333;
    private int mDarkMutedColor = 0xFF333333;
    private ObservableScrollView mScrollView;
    private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
    private ColorDrawable mStatusBarColorDrawable;
    private int mTopInset;
    private View mPhotoContainerView;
    private DynamicHeightNetworkImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);

        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(1, null, this);
//        getActivityCast().setSupportActionBar(tooly);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);


        // mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        // mPhotoContainerView = mRootView.findViewById(R.id.photo_container);


        fab = (android.support.design.widget.FloatingActionButton) mRootView.findViewById(R.id.share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        bindViews();

        return mRootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        tooly = (Toolbar) mRootView.findViewById(R.id.toolbardetail);
        mCtool = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing);
        mPhotoView = (DynamicHeightNetworkImageView) mRootView.findViewById(R.id.bleedpic);
        articleContent = (TextView) mRootView.findViewById(R.id.articleContent);
        // titletext=(TextView)mRootView.findViewById(R.id.titletext);
//        subtitletext=(TextView)mRootView.findViewById(R.id.subtitletext);
        titletextt = (TextView) mRootView.findViewById(R.id.titletextt);
        caption = (TextView) mRootView.findViewById(R.id.caption);
        if (mCursor != null) {
            // TRAIN: DESTROY LOADER HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE////


//            mRootView.setAlpha(0);
//            mRootView.setVisibility(View.VISIBLE);
//            mRootView.animate().alpha(1);

            tooly.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

            getActivityCast().setSupportActionBar(tooly);
            //  titletext.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            titletextt.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            caption.setText(DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR));
//            subtitletext.setText( DateUtils.getRelativeTimeSpanString(
//                    mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
//                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                    DateUtils.FORMAT_ABBREV_ALL).toString()
//                    + " by "
//                    + mCursor.getString(ArticleLoader.Query.AUTHOR));

            // titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
//         //   bylineView.setText(Html.fromHtml(
//                    DateUtils.getRelativeTimeSpanString(
//                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
//                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                            DateUtils.FORMAT_ABBREV_ALL).toString()
//                            + " by <font color='#ffffff'>"
//                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                            + "</font>")
            // bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));

            //picasso and target
            mPhotoView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
            Picasso.with(getActivity()).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).into(mPhotoView, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap b = ((BitmapDrawable) mPhotoView.getDrawable()).getBitmap();
                    Palette p = Palette.from(b).generate();
                    mMutedColor = p.getDarkMutedColor(0xFF333333);
                    //  mPhotoView.setImageBitmap(bitmap);
                    //  if(mCursor!=null)
                    //    Picasso.with(getActivityCast()).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).fit().into(mPhotoView);
                    mVibrantColor = p.getVibrantColor(0xFF333333);
                    mDarkMutedColor = p.getDarkMutedColor(0xFF333333);
                    mCtool.setBackgroundColor(mMutedColor);
                    mCtool.setContentScrimColor(mVibrantColor);
                    mCtool.setStatusBarScrimColor(mMutedColor);

                    // mRootView.setBackgroundColor(mVibrantColor);


                }

                @Override
                public void onError() {

                }
            });
            ////////
            articleContent.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        } else {
            DatabaseUtils.dumpCursor(cursor);
            bindViews();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

}



