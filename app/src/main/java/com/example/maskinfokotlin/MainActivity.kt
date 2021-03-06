package com.example.maskinfokotlin

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maskinfokotlin.databinding.ActivityMainBinding
import com.example.maskinfokotlin.model.Store
import java.util.Observer
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel2 by viewModels()

    /**
     *
     * private lateinit var viewModel: MainViewModel 이런식으로 하면
     * viewModel이 도중에 값이 변할 수 있다는 불안이 있다. 이를 해소하기 위해
     * by viewModels가 나온것임
     *
     * cf. 그렇다고   private val viewModel2: MainViewModel? = null 이렇게 쓰는건
     * 코틀린을 너무 자바스럽게 사용하는 거임.. (코틀린을 코틀린 답게..!)
     *
//     */
//    private var _binding: ActivityMainBinding? = null
//    private val binding get() = _binding!!

//    @Inject
//    lateinit var storeAdapter: StoreAdapter

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        if (map[Manifest.permission.ACCESS_FINE_LOCATION]!!
            && map[Manifest.permission.ACCESS_COARSE_LOCATION]!!) {
            viewModel.fetchStoreInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        //gradle에 viewBinding true를 해야 사용할 수 있음
        val binding = ActivityMainBinding.inflate(layoutInflater)



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            return
        }


        /**
         * TODO: .apply가 쓰이는 경우.. 주석1)을 보면 binding.recyclerView.이 2개이상쓰임.. 이때 줄일수있다.
         * **/

        val storeAdapter = StoreAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity,
                RecyclerView.VERTICAL,
                false
            )//context를 그냥 this쓰면안됨.. (왜냐면 여기안에는 this가 RecyclerView를 가르키고 있기 때문)
            adapter = storeAdapter
        }

        viewModel.apply {
            itemLiveData.observe(this@MainActivity, androidx.lifecycle.Observer {
                storeAdapter.updateItems(it)
            })


            loadingLiveData.observe(this@MainActivity, androidx.lifecycle.Observer {
                isLoading ->
                if(isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }

                //위 코드말고 아래식으로 간단하게해도 됨
                //binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
            })
        }
        /**
         * 주석1)
         * 위의 코드는 다음코드를 축약한 것임.(코틀린은 축약할 수 있는 다양한 기법을 제공)
         *
         * binding.recyclerView.layoutManager = LinearLayoutMananger(this,
         * RecyclerView.VERTICAL, false)
         * val adapter = StoreAdapter()
         * binding.recyclerView.adapter = adapter
         *
         **/
//
//        viewModel.apply {
//            itemLiveData.observe(this@MainActivity, Observer {
//                storeAdapter.updateItems(it)
//            })
//
//            loadingLiveData.observe(this@MainActivity, Observer { isLoading ->
//                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            })
//        }

//        if (savedInstanceState == null) {
//            viewModel.fetchStoreInfo()
//        }

//        val items = listOf(
//            Store("abc", "111", "111", 33.33, 33.33, "약국",
//            "plenty", "33", "33"),
//            Store("abc", "111", "111", 33.33, 33.33, "약국",
//                "plenty", "33", "33"),
//            Store("abc", "111", "111", 33.33, 33.33, "약국",
//                "plenty", "33", "33")
//        )
//
//        storeAdapter.updateItems(items)


        setContentView(binding.root)
    }
}