package Giet.group12.udailynotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddPdf extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 13;
    Button choose,upload;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filepath;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);

        choose= findViewById(R.id.btnChoose);
        upload=findViewById(R.id.btnUpload);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        sp=getSharedPreferences("Udaily Login",MODE_PRIVATE);



        choose.setOnClickListener(v -> selectImage());

        upload.setOnClickListener(v -> uploadImage());
    }

    private void uploadImage() {

        if(filepath!=null){
            ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setTitle("Uploading File at \n"+filepath);
            progressDialog.show();
            String id=sp.getString("Id","nullz");
            if(id=="nullz") {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return;
            }
            Log.d("filepath", String.valueOf(filepath));
            String fname=id+":::"+ UUID.randomUUID().toString();
            StorageReference ref= storageReference.child(fname);

            ref.putFile(filepath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Snackbar.make(getApplicationContext(),
                                null,
                                "Upload Complete",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                    })

                    .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                        Snackbar.make(getApplicationContext(),
                                null,
                                "Failed "+e.getLocalizedMessage()+" "+e.getMessage(),
                                BaseTransientBottomBar.LENGTH_LONG).show();

                    })
                    .addOnProgressListener(snapshot -> {
                        Double progress=(100.0*(snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
                        progressDialog.setMessage("Uploaded "+(Math.round(progress*100.0)/100.0)+" %");
                    });

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
            filepath=data.getData();
            choose.setText(filepath.toString());
        }
    }
}