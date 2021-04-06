    package Giet.group12.udailynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread a= new Thread(() -> {  //throw Splash
            setContentView(R.layout.activity_main);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sp= getSharedPreferences("Udaily_Login",MODE_PRIVATE);
        Intent intent;
        if(sp.getBoolean("logged",false)){
            intent=new Intent(this,Discover.class);
        }
        else{
            intent=new Intent(this,LoginActivity.class);
        }
        startActivity(intent);
        finish();


    }
}