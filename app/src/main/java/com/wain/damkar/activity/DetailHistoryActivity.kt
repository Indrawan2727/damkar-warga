package com.wain.damkar.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.inyongtisto.tokoonline.helper.Helper
import com.squareup.picasso.Picasso
import com.wain.damkar.R
import com.wain.damkar.model.Laporan
import com.wain.damkar.utils.Config
import kotlinx.android.synthetic.main.activity_detail_history.*
import kotlinx.android.synthetic.main.toolbar_custom.*

class DetailHistoryActivity : AppCompatActivity() {
    lateinit var laporan : Laporan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

        getInfo()

    }

    private fun getInfo() {
        val data = intent.getStringExtra("extra")
        laporan = Gson().fromJson<Laporan>(data, Laporan::class.java)

        // set Value
        tv_namana.text = laporan.name
        tv_Keterangana.text = laporan.kategori
        tv_alamata.text = laporan.lokasi
        tv_deskripsia.text = laporan.deskripsi

        val img = Config.productUrl + laporan.image
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.gambar)
            .error(R.drawable.gambar)
            .resize(400, 400)
            .into(image)

        // setToolbar
        Helper().setToolbar(this, toolbar, laporan.kategori)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}