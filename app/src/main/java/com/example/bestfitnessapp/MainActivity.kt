package com.example.bestfitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bestfitnessapp.adapters.CategoryAdapter
import com.example.bestfitnessapp.adapters.ContentManager
import com.example.bestfitnessapp.databinding.ActivityMainBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {
    private lateinit var bindingA : ActivityMainBinding
    private var adapter : CategoryAdapter? = null
    private var interAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingA = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingA.root)
        (application as AppMainState).showAdIfAvailable(this){//запускаем рекламу ДО открытия основного окна
            //Toast.makeText(this, "Реклама закончилась :)", Toast.LENGTH_LONG).show() //это происходит, когда пользователь закрыл рекламу
        }
        initAdMod()
        initRcView()
    }
    private fun initRcView() = with(bindingA) {
        adapter = CategoryAdapter()
        rcViewCat.layoutManager = LinearLayoutManager(this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        rcViewCat.adapter = adapter
        adapter?.submitList(ContentManager.list)
    }

    override fun onResume() {
        super.onResume()
        bindingA.adView.resume()
        loadInterAd()
    }

    override fun onPause() {
        super.onPause()
        bindingA.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingA.adView.destroy()
    }

    private fun initAdMod(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        bindingA.adView.loadAd(adRequest)
    }

    private fun loadInterAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
        })
    }
    private fun showInterAd(){
        if (interAd != null) {//если рекламный объект НЕ равен нулю
            interAd?.fullScreenContentCallback =
                object : FullScreenContentCallback(){
                    override fun onAdDismissedFullScreenContent() { //пользователь нажал на крестик во время просмотра рекламы (сволочь)
                        showContent() //пускаем пользователя к пост-рекламному контенту
                        interAd = null //обнуляем рекламу
                        loadInterAd() //загружаем новую рекламу
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        showContent() //пускаем пользователя к пост-рекламному контенту
                        interAd = null //обнуляем рекламу
                        loadInterAd() //загружаем новую рекламу
                    }

                    override fun onAdShowedFullScreenContent() { //смотрим рекламу
                        interAd = null //обнуляем рекламу
                        loadInterAd() //загружаем новую рекламу
                    }
            }
            interAd?.show(this)
        }else{ //если рекламный объект равен нулю
            showContent() //пускаем пользователя к пост-рекламному контенту
        }
    }

    private fun showContent(){ //имитация запуска контента после просмотра рекламы
        //Toast.makeText(this, "Запуск контента", Toast.LENGTH_LONG).show()
    }
}