package com.ayala.dam.modulableapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var mMap: GoogleMap

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION

    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val CODIGO_SOLICITUD_PERMISO = 100

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationRequest: LocationRequest? = null

    private var callback: LocationCallback? = null

    private var listaMarcadores: ArrayList<Marker>? = null

    // Marcadores de mapa

    private var marcadorMadrid: Marker? = null
    private var marcadorBilbao: Marker? = null
    private var marcadorPuenteVallecas: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if(mMap != null) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                    for (ubicacion in locationResult?.locations!!) {
                        Toast.makeText(applicationContext, ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(), Toast.LENGTH_LONG).show()
                        val miPosicion = LatLng(ubicacion.latitude, ubicacion.longitude)
                        mMap.addMarker(MarkerOptions().position(miPosicion).title("Aqui Estoy"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicion))
                    }
                }
            }
        }

    }

    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 1000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Personalizar el mapa
//        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        // https://mapstyle.withgoogle.com/ web para personalizar el mapa

        val exitoCambioMapa = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilo_mapa))

        if(!exitoCambioMapa){
            // Problema al cambiar el tipo de mapa
        }

        val Madrid = LatLng(40.4167754, -3.7037901999999576)
        val Bilbao = LatLng(43.2630126, -2.9349852000000283)
        val PuenteVallecas = LatLng(40.3870042, -4.6695333999999775)

        marcadorMadrid = mMap.addMarker(MarkerOptions()
                .position(Madrid)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) // Color al marcador
                .snippet("Descripción lerele") // Descripción
                .alpha(0.3f)  // opacidad de 0 a 1
                .title("Madrid"))
        marcadorMadrid?.tag = 0


        marcadorBilbao = mMap.addMarker(MarkerOptions()
                .position(Bilbao)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .snippet("lororlo")
                .alpha(0.4f)
                .title("Bilbao"))
        marcadorMadrid?.tag = 0

        marcadorPuenteVallecas = mMap.addMarker(MarkerOptions()
                .position(PuenteVallecas)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono)) // Marcador imagen
                .snippet("lirili")
                .alpha(0.6f)
                .title("PuenteVallecas"))
        marcadorMadrid?.tag = 0

        mMap.setOnMarkerClickListener(this)

        prepararMarcadores()


//        if (validarPermisosUbicacion()) {
//
//            obtenerUbicacion()
//        } else {
//
//            pedirPermisos()
//        }

    }

    private fun prepararMarcadores(){
        listaMarcadores = ArrayList()

        mMap.setOnMapLongClickListener {
            location: LatLng? ->

            listaMarcadores?.add(mMap.addMarker(MarkerOptions()
                    .position(location!!)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono)) // Marcador imagen
                    .snippet("lirili")
                    .alpha(0.6f)
                    .title("PuenteVallecas")))
        }
    }



    override fun onMarkerClick(marcador: Marker?): Boolean {
        var numeroClicks = marcador?.tag as? Int

        if(numeroClicks != null){
            numeroClicks++
            marcador?.tag = numeroClicks

            Toast.makeText(this, "Se han dado "+ numeroClicks.toString()+ " clicks", Toast.LENGTH_LONG).show()
        }


        return false

    }

    private fun validarPermisosUbicacion(): Boolean {
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(this, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(this, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }


    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {

//        fusedLocationClient?.lastLocation?.addOnSuccessListener(this, object: OnSuccessListener<Location>{
//            override fun onSuccess(location: Location?) {
//                if (location != null){
//                    Toast.makeText(applicationContext, location.latitude.toString() + " - " + location.longitude.toString(), Toast.LENGTH_LONG).show()
//                }
//            }
//
//        })

        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)

    }

    private fun pedirPermisos() {

        val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this, permisoFineLocation)

        if (deboProveerContexto) {
            // mandar mensaje con explicación adicional
            solicitudPermiso()

        } else {
            solicitudPermiso()
        }
    }


    private fun solicitudPermiso() {

        requestPermissions(arrayOf(permisoFineLocation, permisoCoarseLocation), CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CODIGO_SOLICITUD_PERMISO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // obtener ubicación
                    obtenerUbicacion()
                } else {
                    Toast.makeText(this, "No diste permiso para acceder a la ubicación", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun detenerActualizacionUbicacion() {
        fusedLocationClient?.removeLocationUpdates(callback)
    }

    override fun onStart() {
        super.onStart()

        if (validarPermisosUbicacion()) {

            obtenerUbicacion()
        } else {

            pedirPermisos()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }
}
