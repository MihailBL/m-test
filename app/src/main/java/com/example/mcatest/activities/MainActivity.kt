package com.example.mcatest.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcatest.Constants
import com.example.mcatest.R
import com.example.mcatest.adapters.RecyclerViewAdapter
import com.example.mcatest.apiServices.EndPointService
import com.example.mcatest.databinding.ActivityMainBinding
import com.example.mcatest.dto.MainDTO
import com.google.android.material.snackbar.Snackbar
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    /** View binding object */
    private var binding: ActivityMainBinding? = null
    /** Fetched data */
    private var data: ArrayList<MainDTO> = arrayListOf()
    /** Main adapter instance */
    private var adapter: RecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding?.root)

        // Check for network connectivity
        if (Constants.isNetworkAvailable(this)){
            fetchData()
        }else{
            Snackbar.make(this, this.binding?.root!!, "No network active", Snackbar.LENGTH_LONG).show()
        }

        // Set up the toolbar
        setUpToolbar()
    }

    /** Method for getting the data */
    private fun fetchData(){
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        try {
            val apiService: EndPointService = retrofit.create(EndPointService::class.java)
            val dataFetch: Call<List<MainDTO>> = apiService.getData()
            dataFetch.enqueue(object : Callback<List<MainDTO>>{
                override fun onResponse(call: Call<List<MainDTO>>, response: Response<List<MainDTO>>) {
                    if (response.isSuccessful){
                        data = response.body() as ArrayList<MainDTO>
                        setUpRecyclerView()
                    }
                }

                override fun onFailure(call: Call<List<MainDTO>>, t: Throwable) {
                    Log.e("ERROR GETTING DATA", t.message.toString())
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    /** Method for setting up the recycler view */
    private fun setUpRecyclerView(){
        adapter = RecyclerViewAdapter(this.data.subList(0,10), this)
        adapter?.setOnItemClickListener(object : RecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, ItemDetailsActivity::class.java)
                intent.putExtra(Constants.ACTIVITY_ITEM_DETAILS_SEND_DATA_CODE, data[position])
                startActivity(intent)
            }
        })

        val recyclerView = this.binding?.rvMain
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    /** Method for setting up the toolbar as action bar */
    private fun setUpToolbar(){
        val toolbarTitle = findViewById<TextView>(R.id.tv_toolbarTitle)
        val toolbarBack = findViewById<ImageView>(R.id.iv_toolbarBack)
        val toolbarSave = findViewById<Button>(R.id.btn_toolbarSave)

        toolbarTitle.setText(R.string.toolbar_title_text_main_activity)
        toolbarSave.visibility = View.INVISIBLE
        toolbarBack.visibility = View.INVISIBLE
    }

    // TODO
    override fun onResume() {
        super.onResume()
        if (Constants.TEMP_ITEM != null && Constants.TEMP_ITEM?.toDelete == true){
            this.adapter?.deleteItem(Constants.TEMP_ITEM!!)
            Constants.TEMP_ITEM = null
        }
        else if (Constants.TEMP_ITEM != null && Constants.TEMP_ITEM?.toDelete == false){
            this.adapter?.updateItem(Constants.TEMP_ITEM!!)
            Constants.TEMP_ITEM = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.binding != null)
            this.binding = null
    }
}