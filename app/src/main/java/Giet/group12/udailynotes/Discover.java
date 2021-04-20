package Giet.group12.udailynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Discover extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private SharedPreferences sp;
    ListResult listResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(Discover.this,AddPdf.class));
        });

        //my code
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReference.listAll()
                .addOnFailureListener(e -> Snackbar.make(fab,e.getMessage(),Snackbar.LENGTH_LONG))
                .addOnSuccessListener(listResult -> {
                    this.listResult=listResult;
                    for(StorageReference item : listResult.getItems()){
                        Log.d("got references", item.getName());
                    }
                });

    }

}