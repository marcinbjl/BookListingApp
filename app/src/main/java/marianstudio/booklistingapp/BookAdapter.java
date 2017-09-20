package marianstudio.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    ViewHolder viewHolder;

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);


            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Book currentBook = getItem(position);

        viewHolder.titleTextView.setText(currentBook.getTitle());
        viewHolder.authorTextView.setText(currentBook.getAuthors());
        viewHolder.yearTextView.setText(currentBook.getYearPublished());
        viewHolder.thumbnailImageView.setImageBitmap(currentBook.getThumbnail());


        return convertView;
    }
}