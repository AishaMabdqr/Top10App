package com.example.top10app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.CertPath

class MainActivity : AppCompatActivity() {

    lateinit var rvItems: RecyclerView
    lateinit var rvAdapter: RVAdapter
    lateinit var bFeed: Button

    var topApps = ArrayList<Apps>()
    var url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bFeed = findViewById(R.id.bFeed)
        rvItems = findViewById(R.id.rvItems)
        bFeed.setOnClickListener {
            requestAPI(url)
        }

    }

    private fun requestAPI(url: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val data = async { fetchData(url) }.await()

            if (data.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    rvAdapter = RVAdapter(topApps)
                    rvItems.adapter = rvAdapter
                    rvItems.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }
        }
    }

    private fun fetchData(urlPath: String?): ArrayList<Apps> {

        val parser = XMLParser()
        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            topApps = connection.getInputStream()?.let {
                parser.parse(it)
            } as ArrayList<Apps>


        } catch (e: Exception) {
            Log.d("Main", "Issue: $e")
        }
        return topApps
    }
}

