package com.maxclique.tuesday.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Austin on 6/15/2014.
 */
public class DisplayPostsFragment extends Fragment {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private Post[] mPosts = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View resultantView = inflater.inflate(R.layout.display_posts_fragment, container, false);

        mListView = (ListView) resultantView.findViewById(R.id.posts_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                PostListAdapter postListAdapter = (PostListAdapter) arg0.getAdapter();
                Post currentPost = postListAdapter.getPost(position);

                // switch the fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                DisplayPostFragment fragment = new DisplayPostFragment();
                Bundle args = new Bundle();
                args.putParcelable("post", currentPost);
                fragment.setArguments(args);
                fragmentTransaction.replace(R.id.main_content, fragment)
                        .addToBackStack(null).commit();
            }
        });

        mSwipeLayout = (SwipeRefreshLayout) resultantView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light,
                android.R.color.holo_purple);

        if (mPosts == null) {
            refresh();
        } else {
            writeListView(mPosts);
        }

        // set up the app icon as an UP button
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle(getString(R.string.app_name));
        }
        setHasOptionsMenu(true);

        return resultantView;
    }

    public void refresh() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        writeListView(getString(R.string.fetching));
        mSwipeLayout.setRefreshing(true);
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask(ServerURL.getAllPosts(),
                    new DownloadWebpageTask.DownloadWebpageTaskCallback() {
                        @Override
                        public void run(String resultOfTask) {
                            try {
                                mPosts = Post.extractFromJson(resultOfTask);
                                writeListView(mPosts);
                            } catch (Exception e) {
                                writeListView("Failure!");
                            }
                            mSwipeLayout.setRefreshing(false);
                        }
                    }
            ).execute();
        } else {
            // display
            writeListView(getString(R.string.no_network));
        }
    }

    protected void writeListView(String string) {
        // TODO: handle errors better
        // writeListView(JSONParser.convertJSONObjects(getString(R.string.subject), string));
    }

    protected void writeListView(Post[] posts) {
        PostListAdapter postAdapter = new PostListAdapter(getActivity(),
                R.layout.display_post_row, posts);
        mListView.setAdapter(postAdapter);
    }

    private class PostListAdapter extends ArrayAdapter<Post> {
        Context context;
        int layoutResourceId;
        Post[] mPosts;

        public PostListAdapter(Context context, int resource, Post[] objects) {
            super(context, resource, objects);
            this.context = context;
            this.layoutResourceId = resource;
            this.mPosts = objects;
        }

        public Post getPost(int index) {
            return mPosts[index];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = ((Activity) context).getLayoutInflater().inflate(
                        layoutResourceId, parent, false);

            }
            Post post = getPost(position);
            TextView titleView = (TextView) row.findViewById(R.id.title);
            TextView dateView = (TextView) row.findViewById(R.id.date);
            if (titleView != null) {
                titleView.setText(post.getSubject());
            }
            if (dateView != null) {
                dateView.setText(TimeAgoParser.timeAgoSince(getContext(),
                        post.getCreatedAt()));
            }
            return row;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.main_activity_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                DisplayPostsFragment fragment = (DisplayPostsFragment) getFragmentManager()
                        .findFragmentById(R.id.main_content);
                fragment.refresh();
                return true;
            case R.id.action_add:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                AddPostFragment postFragment = new AddPostFragment();
                fragmentTransaction.replace(R.id.main_content, postFragment)
                        .addToBackStack(null).commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
