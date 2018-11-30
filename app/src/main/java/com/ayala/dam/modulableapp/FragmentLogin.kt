package com.ayala.dam.modulableapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signin.setOnClickListener { callHome() }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun callHome() {

        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_main, FragmentHome.newInstance("Nothing to do here"))
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun checkToken() {
        var url = BuildConfig.URL_SERVER + "comprobar_token"
        val queue = Volley.newRequestQueue(context!!.getApplicationContext())
        val solicitud = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            try {
//                Log.d("resultado", response)
                val respuesta: JSONObject = JSONObject(response)
                if (respuesta["result"].equals("ok"))
                {
                    callHome()
                }
                else{
                    print("Nothing to do!")
                }

            } catch (e: Exception) {
                Log.d("VolleyGet", e.toString())
            }
        }, Response.ErrorListener { })
        queue.add(solicitud)
    }

    fun login() {

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