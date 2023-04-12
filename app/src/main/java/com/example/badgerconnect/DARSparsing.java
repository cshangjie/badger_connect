package com.example.badgerconnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DARSparsing extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST_CODE = 1;

    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
    }

    private String extractCourseNamesFromDarsReport(String pdfFilePath) {

        // creating a string for
        // storing our extracted text.
        String extractedText = "";

        try {
            // creating a variable for pdf reader
            // and passing our PDF file in it.
            PdfReader reader = new PdfReader(pdfFilePath);

            // below line is for getting number
            // of pages of PDF file.
            int n = reader.getNumberOfPages();

            // running a for loop to get the data from PDF
            // we are storing that data inside our string.
            for (int i = 0; i < n; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                // to extract the PDF content from the different pages
            }

            // below line is used for closing reader.
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractedText;
    }

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission has already been granted, access external storage here
            parsePDF();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            // If the request is cancelled, the grantResults array is empty
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, access external storage here
                parsePDF();
            } else {
                // Permission has been denied, show a message to the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View v) {
        requestExternalStoragePermission();
    }
    private void parsePDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        launchSomeActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri pdfUri = data.getData();
                        String pdfFilePath = pdfUri.getPath(); // Helper method to convert the Uri to a file path
                        Log.d("filepath", String.valueOf(pdfUri));
                        //TODO: get the url of the file from the file system.
                        String courseNames = extractCourseNamesFromDarsReport("res/raw/dars_audit.pdf");
                        TextView extractedTV = findViewById(R.id.courseNamesTextView);
                        extractedTV.setText(courseNames);
                    }
                }
            });

}
