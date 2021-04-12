package Giet.group12.udailynotes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

public class AddPdf extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 13;
    Button choose,upload;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filepath;
    SharedPreferences sp;
    String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);

        choose= findViewById(R.id.btnChoose);
        upload=findViewById(R.id.btnUpload);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        sp=getSharedPreferences("Udaily_Login",MODE_PRIVATE);

        choose.setOnClickListener(v -> selectImage());

        upload.setOnClickListener(v -> uploadImage(v));
    }

    private void uploadImage(View view) {

        if(filepath!=null){

            ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            String msg="Uploading File at \n"+filepath;
            progressDialog.setCancelable(true);
            StorageReference ref;
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setOnCancelListener(dialog -> Snackbar.make(view,"Cancelled By User",BaseTransientBottomBar.LENGTH_LONG).setAction("Retry?", v -> uploadImage(view)).show());
            StorageTask<UploadTask.TaskSnapshot> upload_progress;
            String id=sp.getString("Id","nullz");

            if(id=="nullz") {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra("adding",true));
                return;
            }
            fname= id+":::"+ fname;
            ref= storageReference.child(fname);
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
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                    })

                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount()));
                        progressDialog.setMessage(ref.getName() + " " + progress + " %");
                    });

            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"CANCEL", (dialog, which) -> {
                upload_progress.cancel();
                progressDialog.cancel();
            });

            progressDialog.show();
        }

    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
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
            fname= new File(data.getData().getPath()).getPath();
            Log.d("fname",fname);
            filepath=data.getData();
            choose.setText(filepath.toString());
        }
    }
}