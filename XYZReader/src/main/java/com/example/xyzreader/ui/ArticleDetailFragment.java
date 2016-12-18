package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    public CollapsingToolbarLayout mCtool;
    public Toolbar tooly;
    public android.support.design.widget.FloatingActionButton fab;
    public TextView articleContent, titletextt, caption;
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private DynamicHeightNetworkImageView mPhotoView;
    private int position;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId, int position) {

        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        arguments.putInt("position", position);
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
        if (getArguments().containsKey("position")) {
            position = getArguments().getInt("position");
        }
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
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
        titletextt = (TextView) mRootView.findViewById(R.id.titletextt);
        caption = (TextView) mRootView.findViewById(R.id.caption);
        if (mCursor != null) {
            tooly.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
            getActivityCast().setSupportActionBar(tooly);
            getActivityCast().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            titletextt.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            caption.setText(DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR));

            mPhotoView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPhotoView.setTransitionName(getResources().getResourceName(R.string.shared_image) + position);
                getActivityCast().startPostponedEnterTransition();
            }
            Picasso.with(getActivity()).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).placeholder(R.drawable.empty_detail).into(mPhotoView, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap b = ((BitmapDrawable) mPhotoView.getDrawable()).getBitmap();
                    Palette p = Palette.from(b).generate();
                    int mMutedColor = p.getMutedColor(0xFF333333);
                    int mDarkmutedColor = p.getDarkMutedColor(0xFF333333);
                    mCtool.setBackgroundColor(mDarkmutedColor);
                    mCtool.setContentScrimColor(mMutedColor);
                    mCtool.setStatusBarScrimColor(mDarkmutedColor);


                }

                @Override
                public void onError() {

                }
            });

            articleContent.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivityCast().finishAfterTransition();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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



