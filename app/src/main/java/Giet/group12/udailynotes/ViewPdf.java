package Giet.group12.udailynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPdf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromAsset("test.pdf").load();
        pdfView.loadPages();
    }

}