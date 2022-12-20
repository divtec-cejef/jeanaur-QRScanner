package com.example.qrscanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    // Création des variables
    Button btn_scan;
    Button btn_localisation;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des données
        btn_scan=findViewById(R.id.btn_scan);
        btn_localisation=findViewById(R.id.btn_localisation);
        // Quand on click sur le bouton cela exécute la méthode : scanCode()
        btn_scan.setOnClickListener(v -> {

            scanCode();
        });
        // Quand on click sur le bouton cela exécute l'Activité Maps
        btn_localisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Méthode appelé lorsque l'on clique sur le bouton "Scan Me"
     */
    private void scanCode() {
        // S'occupe de scanner notre code QR
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    /**
     * Prépare notre activité à renvoyer un résultat.
     */
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(),result -> {

        // Affiche ce que contient notre code QR à l'aide d'une boîte de dialogue
        if (result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}