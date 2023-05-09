package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Compass compass;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    Button btnCalculatePath;
    Button btnScanQRCode;
    TextView tvDirections;
    Spinner sp_depart;
    String[] T_depart = new String[] {"Choix Depart","ENTREE","A001","A002","A003","A005","A006","A007","A101","A102","A103","A105","A106","A107","B001","B002","B003","B004","B006","B007","B008","B009","B010","B101","B102","B103","B105","B106","B107","B108","B109","B110"};
    Spinner sp_destination;
    String[] T_destination = new String[] {"Choix Destination","ENTREE","A001","A002","A003","A005","A006","A007","A101","A102","A103","A105","A106","A107","B001","B002","B003","B004","B006","B007","B008","B009","B010","B101","B102","B103","B105","B106","B107","B108","B109","B110"};
    String[] T_depart_BAT_C = new String[] {"Choix Depart","JARDIN","TOILETTE 0","BDLS","ARENA","EPICURIA","RESERVE DES ASSOS","SALLE PROF","DOUCHE GARCON","DOUCHE FILLE","MADEIN","QUANTUM","BDA","TYO","WAVE","MUSCU","PASSERELLE BATIMENT D","PAR VIE 1ER","C101","C102","C103","C104","C105","C106","C207","C208", "C201","C202","C203","C204","C205","C206","C307","C308","C301","C302","C303","C304","C305","C306"};
    String[] T_destination_BAT_C = new String[] {"Choix Destination","JARDIN","TOILETTE 0","BDLS","ARENA","EPICURIA","RESERVE DES ASSOS","SALLE PROF","DOUCHE GARCON","DOUCHE FILLE","MADEIN","QUANTUM","BDA","TYO","WAVE","MUSCU","PASSERELLE BATIMENT D","PAR VIE 1ER","C101","C102","C103","C104","C105","C106","C207","C208", "C201","C202","C203","C204","C205","C206","C307","C308","C301","C302","C303","C304","C305","C306"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vérifier la permission d'accès aux capteurs
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // Initialiser la boussole
            compass = new Compass(this, findViewById(R.id.iv_compass));
            compass.start();
        }

        // Trouver les vues dans le fichier de mise en page
        btnCalculatePath = findViewById(R.id.btn_calculate_path);
        btnScanQRCode = findViewById(R.id.btn_scan_qr);
        tvDirections = findViewById(R.id.tv_directions);
        sp_depart = findViewById(R.id.spinner_depart);
        ArrayAdapter<String> dep = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,T_depart_BAT_C);
        sp_depart.setAdapter(dep);
        sp_destination = findViewById(R.id.spinner_destination);
        ArrayAdapter<String> des = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,T_destination_BAT_C);
        sp_destination.setAdapter(des);

        // Configurer le bouton pour calculer le chemin
        btnCalculatePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String depart = sp_depart.getSelectedItem().toString();
                String destination = sp_destination.getSelectedItem().toString();

                if (!depart.isEmpty() && !destination.isEmpty()) {
                    List<String> directions = Dijkstra.dijkstra(depart, destination);
                    if (directions.isEmpty()) {
                        tvDirections.setText("Aucun chemin trouvé.");
                    } else {
                        String directionsString = TextUtils.join(" -> ", directions);
                        tvDirections.setText("Directions : " + directionsString);
                    }
                } else {
                    tvDirections.setText("Veuillez entrer un départ et une destination.");
                }
            }
        });

        // Configurer le bouton pour scanner le QR code
        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scanner le QR code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_LONG).show();
            } else {
                String url = result.getContents();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Arrêter la boussole lorsque l'activité est détruite
        if (compass != null) {
            compass.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission a été accordée
                    compass = new Compass(this, findViewById(R.id.iv_compass));
                    compass.start();
                } else {
                    // La permission a été refusée
                    Toast.makeText(this, "Permission refusée. La boussole ne fonctionnera pas.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

