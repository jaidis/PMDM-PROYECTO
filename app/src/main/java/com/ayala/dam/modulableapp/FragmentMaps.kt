package com.ayala.dam.modulableapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.app_bar_main.*

private const val ARG_PARAM1 = "param1"


class FragmentMaps : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


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
    private var param1: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Personalizar el mapa
//        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        // https://mapstyle.withgoogle.com/ web para personalizar el mapa

        val exitoCambioMapa = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.estilo_mapa))

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





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val myview=inflater.inflate(R.layout.fragment_maps, container, false)
        var mapFragment = myview.findViewById<MapView>(R.id.mapView)
        mapFragment.onCreate(savedInstanceState)
        mapFragment.onResume()

        mapFragment.getMapAsync(this)

        fusedLocationClient = FusedLocationProviderClient(context!!)
        inicializarLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if(mMap != null) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                    for (ubicacion in locationResult?.locations!!) {
                        Toast.makeText(context, ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(), Toast.LENGTH_LONG).show()
                        val miPosicion = LatLng(ubicacion.latitude, ubicacion.longitude)
                        mMap.addMarker(MarkerOptions().position(miPosicion).title("Aqui Estoy"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicion))
                    }
                }
            }
        }
        return myview
    }


    override fun onMarkerClick(marcador: Marker?): Boolean {
        var numeroClicks = marcador?.tag as? Int

        if(numeroClicks != null){
            numeroClicks++
            marcador?.tag = numeroClicks

            Toast.makeText(context, "Se han dado "+ numeroClicks.toString()+ " clicks", Toast.LENGTH_LONG).show()
        }


        return false
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
private fun validarPermisosUbicacion(): Boolean {
    val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(context!!, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
    val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(context!!, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

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

    val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permisoFineLocation)

    if (deboProveerContexto) {
        // mandar mensaje con explicación adicional
        solicitudPermiso()

    } else {
        solicitudPermiso()
    }
}

    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 1000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
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
                Toast.makeText(context, "No diste permiso para acceder a la ubicación", Toast.LENGTH_LONG).show()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //nombre_del_boton.setOnClickListener { funcionLlamada() }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    fun funcionaLlamada(){
        print("funcion que no hace nada")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment RecyclerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                FragmentMaps().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }

}