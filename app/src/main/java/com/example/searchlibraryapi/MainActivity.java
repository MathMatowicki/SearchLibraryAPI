package com.example.searchlibraryapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.searchlibraryapi.Model.Book;
import com.example.searchlibraryapi.Model.BookContainer;
import com.example.searchlibraryapi.Model.BookService;
import com.example.searchlibraryapi.Retrofit.RetrofitInstance;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private class BookHolder extends RecyclerView.ViewHolder {

        private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

        private final TextView bookTitleTextView;
        private final TextView bookAuthorTextView;
        private final ImageView bookCoverImageView;


        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = (TextView) itemView.findViewById(R.id.txt_book_title);
            bookAuthorTextView = (TextView) itemView.findViewById(R.id.txt_book_author);
            bookCoverImageView = (ImageView) itemView.findViewById(R.id.img_cover);
        }

        public void bind(Book book) {
            if (book != null && checkNullOrEmpty(book.getTitle()) && book.getAuthors() != null) {
                bookTitleTextView.setText(book.getTitle());
                bookAuthorTextView.setText(TextUtils.join(", ", book.getAuthors()));
                if (book.getCover() != null) {
                    Picasso.get().load(IMAGE_URL_BASE + book.getCover() + "-S.jpg")
                            .placeholder(R.drawable.ic_baseline_book_24).into(bookCoverImageView);
                } else {
                    bookCoverImageView.setImageResource(R.drawable.ic_baseline_book_24);
                }
            }
        }

    }

    private boolean checkNullOrEmpty(String title) {
        return (title != null && !TextUtils.isEmpty(title));
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> bookList;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (bookList != null) {
                Book book = bookList.get(position);
                holder.bind(book);
            } else {
                Toast.makeText(MainActivity.this, "No books", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            if (bookList != null) {
                return bookList.size();
            } else {
                return 0;
            }
        }

        public void setBookList(List<Book> bookList) {
            this.bookList = bookList;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchBooksData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchBooksData(String query) {
        String finalQuery = prepareQuery(query);
        BookService bookService = RetrofitInstance.getRetrofitInstance().create(BookService.class);

        Call<BookContainer> booksApiCall = bookService.findBooks(finalQuery);

        booksApiCall.enqueue(new Callback<BookContainer>() {
            @Override
            public void onResponse(@NotNull Call<BookContainer> call, @NotNull Response<BookContainer> response) {
                assert response.body() != null;
                setupBookListView(response.body().getBookList());
            }

            @Override
            public void onFailure(@NotNull Call<BookContainer> call, @NotNull Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong... Please try again later!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBookListView(List<Book> bookList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        adapter.setBookList(bookList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailBookActivity.class);
                Bundle b = new Bundle();
                intent.putExtra("title", bookList.get(position).getTitle());
                intent.putExtra("cover", bookList.get(position).getCover());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    private String prepareQuery(String query) {
        String[] queryParts = query.split("\\s+");
        return TextUtils.join("+", queryParts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}