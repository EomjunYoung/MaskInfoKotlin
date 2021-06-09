package com.example.maskinfokotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maskinfokotlin.model.Store
import com.example.maskinfokotlin.repository.MaskService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//클래스를 상속받을때는 ()< 필수, MainViewModel2는 기존 MainViewModel말고 내가 직접 코딩하기 위해 만든 것
class MainViewModel2: ViewModel() {
    val itemLiveData = MutableLiveData<List<Store>>()
    val loadingLiveData = MutableLiveData<Boolean>()

    private var service: MaskService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(MaskService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        service = retrofit.create(MaskService::class.java)

        fetchStoreInfo()
    }

    //코루틴은 자바의 비동기통신과는 다르게 콜백메소드가 따로없어서 굉장히 간단하다.
    fun fetchStoreInfo() {
        //로딩시작
        loadingLiveData.value = true

        viewModelScope.launch {

            val storeInfo = service.fetchStoreInfo(37.188078, 127.043002)
            itemLiveData.value = storeInfo.stores

            //로딩끝
            loadingLiveData.value = false
        }
    }

}