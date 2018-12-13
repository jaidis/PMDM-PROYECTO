package com.ayala.dam.modulableapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"

class FragmentLogin : Fragment() {

    private var param1: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signin.setOnClickListener { callLogin() }
        text_forgot.setOnClickListener { callRegister() }
        //TODO: Comprobar el token al iniciar el Fragment de Logueo
        checkToken()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        activity!!.invalidateOptionsMenu()
    }

    fun message(message: String) {
        Snackbar.make(getActivity()!!.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    fun storeToken(token: String) {
        Log.d("TOKEN-MSG", MainActivity.prefs.token)
        MainActivity.prefs.token = token
        Log.d("TOKEN-MSG", MainActivity.prefs.token)
    }

    fun callHome() {

        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_main, FragmentHome.newInstance("Nothing to do here"), "rageComicList")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun callRegister() {

        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_main, FragmentRegister.newInstance("Nothing to do here"), "rageComicList")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun checkToken() {

        var jsonData = JSONObject()

        if (MainActivity.prefs.token.equals(""))
        {
            MainActivity.prefs.token = "NO_TOKEN"
        }

        jsonData.put("token", MainActivity.prefs.token)

        Log.d("TOKEN-MSG", jsonData.toString())

        var url = BuildConfig.URL_SERVER + ":8000/usuarios/android/comprobar_token/"
        val queue = Volley.newRequestQueue(context!!.getApplicationContext())
        val solicitud = object: StringRequest(Request.Method.POST, url, Response.Listener<String> { response ->
            try {
//                Log.d("resultado", response)
                val respuesta: JSONObject = JSONObject(response)
                if (respuesta["result"].equals("ok")) {
                    callHome()
                } else {
                    message(respuesta["message"].toString())
                }

            } catch (e: Exception) {
                Log.d("TOKEN-MSG", e.toString())
            }
        }, Response.ErrorListener { error ->
            Log.d("TOKEN-MSG", error.toString())
        }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("data", jsonData.toString())
                Log.d("TOKEN-MSG", params.toString())
                return params
            }
        }
        queue.add(solicitud)
    }

    fun callLogin() {
        var jsonData = JSONObject()
        jsonData.put("usuario", username.text)
        jsonData.put("password", password.text)

        Log.d("LOGIN-MSG", jsonData.toString())

        var url = BuildConfig.URL_SERVER + ":8000/usuarios/android/login/"
        val queue = Volley.newRequestQueue(context!!.getApplicationContext())
        val solicitud = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            try {
//                Log.d("resultado", response)
                val respuesta: JSONObject = JSONObject(response)
                if (respuesta["result"].equals("ok")) {
                    storeToken(respuesta["token"].toString())
                    callHome()
                } else {
                    message(respuesta["message"].toString())
                    Log.d("LOGIN-MSG", respuesta.toString())
                }

            } catch (e: Exception) {
                Log.d("LOGIN-MSG", e.toString())
            }
        }, Response.ErrorListener { error ->
            Log.d("LOGIN-MSG", error.toString())
        }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("data", jsonData.toString())
                Log.d("LOGIN-MSG", params.toString())
                return params
            }
        }
        queue.add(solicitud)
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
                FragmentLogin().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }

}