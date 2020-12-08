package com.nguyen.fetchrewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyen.fetchrewards.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://fetch-hiring.s3.amazonaws.com/"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        // set up the RecyclerView
        val hires = mutableListOf<Hire>()
        val adapter = HireAdapter(this, hires)
        binding.recyclerHires.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerHires.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(binding.recyclerHires.context, layoutManager.orientation)
        binding.recyclerHires.addItemDecoration(divider)

        // set up the network call to Amazon AWS
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(AWSService::class.java)
        service.fetchHiring().enqueue(object : Callback<List<Hire>> {
            override fun onResponse(call: Call<List<Hire>>, response: Response<List<Hire>>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from AWS")
                } else {
                    val processed = preprocess(body)
                    hires.addAll(processed)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Hire>>, t: Throwable) {
                Log.w(TAG, "onFailure $t")
            }
        })
    }

    private fun preprocess(list: List<Hire>): List<Hire> {
        val cleaned = cleanNames(list)
        return sort(cleaned)
    }

    // remove records with null or empty name
    private fun cleanNames(list: List<Hire>): List<Hire> {
        return list.filter {
            !it.name.isNullOrEmpty()
        }
    }

    // sort first by listId, then by name
    private fun sort(list: List<Hire>): List<Hire> {
        // return list.sortedWith(compareBy<Hire> { it.listId }.thenBy { it.name })
        return list.sortedWith(object : Comparator<Hire> {
            override fun compare(o1: Hire, o2: Hire): Int {
                return if (o1.listId == o2.listId) {
                    extractInt(o1) - extractInt(o2)
                } else {
                    o1.listId - o2.listId
                }
            }

            // name field is always "Item something" where something is an integer
            // so extract that integer for object comparison in sorting
            fun extractInt(hire: Hire): Int {
                val number = hire.name!!.replace("\\D".toRegex(), "")
                return number.toInt()
            }
        })
    }
}