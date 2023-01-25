package com.example.qrscanner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Création variable
    Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Task<Location> task;
    private static final int REQUEST_CODE = 101;  // identifiant de l'appel de l'autorisation

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Fournisseur d'emplacement fusionné pour récupérer le dernier emplacement connu de l'appareil
        // API de localisation des services Google Play
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        askLocationPermission();
    }

    /**
     * S'occupe de vérifier si les permissions sont accordée.
     */
    private void askLocationPermission() {
        // Vérification des permissions de la map et de la localisation de l'utilisateur
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Demande d'autorisation, un pop up s'affiche pour accepter ou refuser la demande d'autorisation
            // le résultat de cette demande est renvoyé à la méthode onRequestPermissionResult qui se chargera de la suite
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        // Autorisation déjà accordée, on obtient le dernier emplacement
        task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener((OnSuccessListener<? super Location>) (location) -> {
            if (location != null) {
                currentLocation = location;

                // Afficher la latitude et la longitude avec un Toast
                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" +
                        currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().
                        findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    /**
     * Création de la map
     *
     * @param googleMap La map Google
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Créer un objet LatLng qui stocke la latitude et la longitude de la localisation actuelle
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Ajouter un Marker de la localisation actuelle dans la carte
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Vous êtes ici !");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        mMap.addMarker(markerOptions);
    }

    /**
     * On regarde si on a toute les permissions nécessaire
     *
     * @param requestCode  The request code passed.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // Dans le cas lorsque vous cliquez sur le bouton "autoriser" de la pop up,
            // il y aura un deuxième appel de la méthode
            // askLocationPermission pour obtenir la dernière localisation
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askLocationPermission();
                }
                break;
        }
    }
}