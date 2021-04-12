package Giet.group12.udailynotes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Discover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        if(getIntent().hasExtra("uploaded"))
            Snackbar.make(findViewById(R.id.fab),
                    "Complete ",
                    BaseTransientBottomBar.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("Discover Notes");

        FloatingActionButton fab = findViewById(R.id.fab);//TODO add an option for adding your own notes
        fab.setOnClickListener(v -> {
            startActivity(new Intent(Discover.this,AddPdf.class));
            finish();
        });

    }
}