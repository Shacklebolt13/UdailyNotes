package Giet.group12.udailynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private SignInButton signInButton;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=1;
    TextView tv;
    SharedPreferences sp;
    GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv=findViewById(R.id.textView);
        sp=getSharedPreferences("Udaily_Login",MODE_PRIVATE);
        tv.setText(sp.getString("email","please log in"));

        signInButton= findViewById(R.id.gO);

        signInButton= (SignInButton) findViewById(R.id.gO);
        mAuth=FirebaseAuth.getInstance();

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

       mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this,gso);

       signInButton.setOnClickListener(this::signIn);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            HandleSignIn(task);

        }
    }

    private void HandleSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this,"Signed In Successfully",Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(acc);

        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this,"Signed Out Successfully",Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        Log.d("cred: ",authCredential.toString());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_LONG).show();
                if(gso.getAccount()!=null)
                    tv.setText(gso.getAccount().toString());
                UpdateUI (mAuth.getCurrentUser());
            }
            else{
                Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_LONG).show();
                UpdateUI(null);
            }

        });
    }

    private void UpdateUI(FirebaseUser currentUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){
            String personName=account.getDisplayName();
            String personGivenName=account.getGivenName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            sp.edit().putString("email",personEmail).apply();
            sp.edit().putBoolean("logged",true).apply();
            tv.setText(sp.getString("email","please log in"));
            Toast.makeText(this,personName+" "+personGivenName+" "+personEmail+" "+personId,Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this,Discover.class));
            finish();
        }
    }

    public void signIn(View view) {
        Toast.makeText(this,"pressed",Toast.LENGTH_LONG);
        Intent SignInIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent,RC_SIGN_IN);
    }


}