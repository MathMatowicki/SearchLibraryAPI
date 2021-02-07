package com.example.searchlibraryapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class DetailBookActivity extends AppCompatActivity {

    private TextView authorTextView;
    private TextView titleTextView;
    private ImageView coverImageView;

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        titleTextView = findViewById(R.id.txt_book_title_detail);
        coverImageView = findViewById(R.id.img_cover_detail);
        if (getIntent().hasExtra("title") && getIntent().hasExtra("cover")) {
            titleTextView.setText(getIntent().getStringExtra("title"), TextView.BufferType.EDITABLE);
            Picasso.get().load(IMAGE_URL_BASE + getIntent().getStringExtra("cover") + "-L.jpg")
                    .placeholder(R.drawable.ic_baseline_book_24).into(coverImageView);
            setResult(RESULT_OK, new Intent());
        }

    }
}
