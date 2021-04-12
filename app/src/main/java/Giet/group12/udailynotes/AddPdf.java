package Giet.group12.udailynotes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;

public class AddPdf extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 13;
    Button choose,upload;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filepath;
    private SharedPreferences sp;
    private String fname;
    private StorageReference ref;
    String id;
    private StorageTask<UploadTask.TaskSnapshot> upload_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);

        choose = findViewById(R.id.btnChoose);
        upload = findViewById(R.id.btnUpload);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sp = getSharedPreferences("Udaily_Login", MODE_PRIVATE);
        id=sp.getString("Id","nullz");

        choose.setOnClickListener(v -> selectFile());

        upload.setOnClickListener(this::checkExistsAndContinue);
    }

    private void checkExistsAndContinue(View v) {
        Log.d("tag",id+":::"+ fname);
        storageReference.child(id+":::"+ fname).getDownloadUrl()
                .addOnFailureListener(e -> {
                    Log.d("firebaseError",e.toString());
                    uploadFile(v);
                }).addOnSuccessListener(uri ->confirmAndContinue(v));
    }

    private void confirmAndContinue(View v) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setCancelable(false)
                .setTitle("OverWrite?")
                .setPositiveButton("Confirm", (dialog, which) -> {uploadFile(v);})
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setMessage("This will OverWrite the file in the cloud").show();
    }

    private void uploadFile(View view) {

        if(filepath!=null){

            ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(dialog -> Snackbar.make(view,"Cancelled By User",BaseTransientBottomBar.LENGTH_LONG).setAction("Retry?", v -> uploadFile(view)).show());


            if(id=="nullz") {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra("adding",true));
                return;
            }
            ref= storageReference.child( id+":::"+ fname);
            Log.d("filepath", String.valueOf(filepath));

            upload_progress = ref.putFile(filepath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), Discover.class).putExtra("uploaded",true));
                        finish();
                    })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Snackbar.make(view,
                                    "Failed " + e.getLocalizedMessage() + " " + e.getMessage(),
                                    BaseTransientBottomBar.LENGTH_LONG).setAction("Retry?", v -> uploadFile(view)).show();
                    })

                    .addOnProgressListener(snapshot -> {
                        double progress = Math.round ( 10000.0 * ( (double)(snapshot.getBytesTransferred()) / (double)(snapshot.getTotalByteCount()) ) )/100.0;
                        Log.d("progress", ""+progress);
                        progressDialog.setMessage(fname + " " + progress + " %");
                    });

            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"CANCEL", (dialog, which) -> {
                upload_progress.cancel();
                progressDialog.cancel();
            });

            progressDialog.show();
        }

    }

    private void selectFile() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            // Get the Uri of the selected file
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);

            if (uriString.startsWith("content://")) {
                try (Cursor cursor = this.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        Log.d("cursor", Arrays.toString(cursor.getColumnNames()));
                        fname = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uriString.startsWith("file://")) {
                fname = myFile.getName();
            }

            Log.d("fname",fname);
            filepath=data.getData();
            if(!fname.toLowerCase().endsWith(".pdf"))
                fname+=".pdf";
            choose.setText(fname);
        }
    }
}