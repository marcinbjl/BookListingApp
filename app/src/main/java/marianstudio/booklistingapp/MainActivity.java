package marianstudio.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int EARTHQUAKE_LOADER_ID = 1;
    public static Bitmap noThumbnail;
    private static String GOOGLE_BOOKS_REQUEST_URL = null;
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.no_thumbnail);

        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        Button searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartLoader();
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.list_view);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(mAdapter);

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, GOOGLE_BOOKS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        } else if (QueryUtils.noResult()) {
            mEmptyStateTextView.setText(R.string.no_results);
        } else {
            mEmptyStateTextView.setText(R.string.how_to_start);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    public void restartLoader() {

        if (isConnected()) {

            String queryUrl = getUrlForHttpRequest();

            if (!queryUrl.isEmpty()) {
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
            } else {
                Toast.makeText(this, R.string.enter_your_keywords,
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getUrlForHttpRequest() {
        String queryUrlPartOne = getString(R.string.url_part_one);
        String queryUrlPartTwo = getString(R.string.url_part_two);

        EditText editText = (EditText) findViewById(R.id.edit_text);
        String keyword = editText.getText().toString().trim().replace(" ", "+");

        GOOGLE_BOOKS_REQUEST_URL = queryUrlPartOne + keyword + queryUrlPartTwo;

        return GOOGLE_BOOKS_REQUEST_URL;
    }

}



