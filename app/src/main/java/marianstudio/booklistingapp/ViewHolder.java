package marianstudio.booklistingapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewHolder {

    public TextView titleTextView;
    public TextView authorTextView;
    public TextView yearTextView;
    public ImageView thumbnailImageView;

    public ViewHolder(View view) {
        this.titleTextView = (TextView) view
                .findViewById(R.id.title);
        this.authorTextView = (TextView) view
                .findViewById(R.id.author);
        this.yearTextView = (TextView) view
                .findViewById(R.id.year_published);
        this.thumbnailImageView = (ImageView) view
                .findViewById(R.id.book_cover);
    }
}
