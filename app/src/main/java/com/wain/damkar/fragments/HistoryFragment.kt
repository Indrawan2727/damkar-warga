package com.wain.damkar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wain.damkar.R
import com.wain.damkar.adapter.AdapterHistory
import com.wain.damkar.app.ApiConfig
import com.wain.damkar.halper.SharedPref
import com.wain.damkar.model.Laporan
import com.wain.damkar.model.ResponModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistoryFragment : Fragment() {

    lateinit var s: SharedPref
    lateinit var rvHistory: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_history, container, false)
        init(view)

        getHistory()

        return view
    }

    fun displayHistory() {
        Log.d("cekini", "size:" + listLaporan.size)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rvHistory.adapter = AdapterHistory(requireActivity(), listLaporan)
        rvHistory.layoutManager = layoutManager

    }

    private var listLaporan: ArrayList<Laporan> = ArrayList()
    fun getHistory() {
        val id = SharedPref(requireActivity()).getUser()!!.id
        ApiConfig.instanceRetrofit.getHistory(
                ).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    val arrayLaporan = ArrayList<Laporan>()
                    for (q in res.laporans) {
                        if(q.status == "Selesai" && q.user_id == id) {
                            arrayLaporan.add(q)
                        }
                    }
                    listLaporan = arrayLaporan
                    displayHistory()
                }
            }
        })
    }
    fun init(view: View) {
        rvHistory = view.findViewById(R.id.rv_history)
    }
}