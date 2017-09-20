package marianstudio.booklistingapp;

import android.graphics.Bitmap;

public class Book {
    private String mTitle;
    private String mAuthors;
    private String mYearPublished;
    private Bitmap mThumbnail;

    public Book(String title, String authors, String yearPublished, Bitmap cover) {
        mTitle = title;
        mAuthors = authors;
        mYearPublished = yearPublished;
        mThumbnail = cover;
    }

    public String getYearPublished() {
        return mYearPublished;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

}
