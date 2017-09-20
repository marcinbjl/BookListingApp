package marianstudio.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static boolean noResult;

    public static List<Book> fetchBookData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the bitmap request.", e);
            return null;
        }
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static boolean noResult() {
        return noResult;
    }

    private static List<Book> extractFeatureFromJson(String bookJSON) {

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            noResult = baseJsonResponse.getString("totalItems").equals("0");
            if (noResult) {
                Log.v(LOG_TAG, "No items in JSON");
            } else {
                JSONArray bookArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < bookArray.length(); i++) {

                    JSONObject currentBook = bookArray.getJSONObject(i);

                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");

                    String authors = "no info";
                    if (volumeInfo.has("authors")) {
                        StringBuilder authorsString = new StringBuilder();
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authorsArray.length(); j++) {
                            try {
                                String author = authorsArray.getString(j);
                                authorsString.append(author + ", ");
                            } catch (JSONException e) {
                                Log.e("BookAdapter", "Problem parsing JSON authorsArray", e);
                            }
                        }
                        authors = authorsString.toString();
                        authors = (authorsString.toString()).substring(0, authors.length() - 2);
                    }

                    String year = "no year";
                    if (volumeInfo.has("publishedDate")) {
                        String date = volumeInfo.getString("publishedDate");
                        year = date.substring(0, 4);
                    }

                    JSONObject imageLinks;
                    Bitmap thumbnail;
                    if (volumeInfo.has("imageLinks")) {
                        imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String imageUrl = imageLinks.getString("thumbnail");
                        thumbnail = getBitmapFromURL(imageUrl);
                    } else {
                        thumbnail = MainActivity.noThumbnail;
                    }
                    Book book = new Book(title, authors, year, thumbnail);

                    books.add(book);
                }
            }
        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return books;
    }
}
