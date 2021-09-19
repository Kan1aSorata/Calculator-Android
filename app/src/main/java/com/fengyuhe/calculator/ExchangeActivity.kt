package com.fengyuhe.calculator

import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.os.Binder
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.fengyuhe.calculator.databinding.ActivityHorizontalBinding
import com.fengyuhe.calculator.databinding.ExchangedialogBinding
import java.time.temporal.TemporalAmount
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

class ExchangeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var countryOriginItem = "CNY"
    var countryAfterItem = "CNY"

    var eBinding : ExchangedialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eBinding = ExchangedialogBinding.inflate(layoutInflater)
        setContentView(eBinding!!.root)

        //要兑换的货币种类
        val originSpinner: Spinner = findViewById(R.id.origin_country_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            originSpinner.adapter = adapter
        }
        originSpinner.onItemSelectedListener = this

        //兑换后的货币种类
        val afterSpinner: Spinner = findViewById(R.id.after_country_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            afterSpinner.adapter = adapter
        }
        afterSpinner.onItemSelectedListener = this

        eBinding!!.btnExchange.setOnClickListener {
            submitPost(countryOriginItem, countryAfterItem, eBinding!!.originNum.text.toString())
        }

    }

    private fun submitPost(from: String, to: String, amount: String) {
        val urlAPI = "https://api.jisuapi.com/exchange/convert?appkey=c21b766e0c5070af"
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(8000, TimeUnit.MILLISECONDS)
            .build()

        val url = "$urlAPI&from=$from&to=$to&amount=$amount"

        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailure$e")
            }

            override fun onResponse(call: Call, response: Response) {
                val result = JSONObject(response.body()!!.string())
                runOnUiThread {
                    eBinding!!.textViewAfterExchange.text = result.getJSONObject("result").getString("camount")
                }
            }

        })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0!!.id == R.id.origin_country_spinner) {
            countryOriginItem = p0.getItemAtPosition(p2).toString()
            println(countryOriginItem)
        } else if (p0!!.id == R.id.after_country_spinner) {
            countryAfterItem = p0.getItemAtPosition(p2).toString()
            println(countryAfterItem)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        return
    }

}