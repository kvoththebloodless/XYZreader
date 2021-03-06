package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView, subtitleView;
        public DynamicImageGradient shadow;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            shadow = (DynamicImageGradient) view.findViewById(R.id.shadow);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {


                                        @Override
                                        public void onClick(View view) {


                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                                                View decor = getWindow().getDecorView();
//                        View statusBar = decor.findViewById(android.R.id.statusBarBackground);
//                        statusBar.setTransitionName(getResources().getResourceName(R.string.shared_statusbar));
                                                View navBar = decor.findViewById(android.R.id.navigationBarBackground);
                                                navBar.setTransitionName(getResources().getResourceName(R.string.shared_softkeys));
//                        View actionBar = findViewById(getResources().getIdentifier(
//                                "action_bar_container", "id", "android"));
//                        actionBar.setTransitionName(getResources().getResourceName(R.string.shared_actionbar));
                                                view.setTransitionName(getResources().getResourceName(R.string.shared_image) + vh.getLayoutPosition());
                                                Pair<View, String> p1 = Pair.create(view, view.getTransitionName());
                                                // Pair<View, String> p2 = Pair.create(statusBar,statusBar.getTransitionName() );
                                                Pair<View, String> p3 = Pair.create(navBar, navBar.getTransitionName());
                                                //  Pair<View, String> p4 = Pair.create(actionBar, actionBar.getTransitionName());
                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ArticleListActivity.this, p1, p3);
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))), options.toBundle());
                                            } else
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));


                                        }

                                    }

            );
            vh.setIsRecyclable(false);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.subtitleView.setText(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR));
            holder.shadow.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
            Picasso.with(ArticleListActivity.this).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).fetch();//prefetching of full bleed photos for smoother shared element transition
            Picasso.with(ArticleListActivity.this).load(mCursor.getString(ArticleLoader.Query.THUMB_URL)).placeholder(R.drawable.empty_detail).into(holder.thumbnailView);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}
