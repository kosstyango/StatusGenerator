package com.example.bestfitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bestfitnessapp.adapters.CategoryAdapter
import com.example.bestfitnessapp.adapters.ContentManager
import com.example.bestfitnessapp.adapters.MainConst
import com.example.bestfitnessapp.adapters.MainConst.imageList
import com.example.bestfitnessapp.databinding.ActivityMainBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CategoryAdapter.Listener {
    private lateinit var bindingA : ActivityMainBinding
    private var adapter : CategoryAdapter? = null
    private var interAd: InterstitialAd? = null
    private var timerA: CountDownTimer? = null
    private var posM: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingA = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingA.root)
        (application as AppMainState).showAdIfAvailable(this){//запускаем рекламу ДО открытия основного окна
            //Toast.makeText(this, "Реклама закончилась :)", Toast.LENGTH_LONG).show() //это происходит, когда пользователь закрыл рекламу
        }
        initAdMod()
        initRcView()
//        bindingA.imageBg.setOnClickListener(){
//            getResult()
//        }
    }
    private fun initRcView() = with(bindingA) {
        adapter = CategoryAdapter(this@MainActivity)
        rcViewCat.layoutManager = LinearLayoutManager(this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false)
        rcViewCat.adapter = adapter
        adapter?.submitList(ContentManager.emogyList)
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

    private fun getResult() = with(bindingA){ //перебор картинок
        tvMessage.text = null //обнуляем верхний экран
        tvMessage.setBackgroundColor(0x00000000) //никакой
        tvName.text = null //обнуляем нижний экран
        tvName.setBackgroundColor(0x00000000) //никакой

        timerA?.cancel()
        timerA = object : CountDownTimer(5000, 200){
            override fun onTick(p0: Long) {
                bindingA.imageBg.setImageResource(imageList[Random.nextInt(12)])
            }

            override fun onFinish() {
                getMessage()
            }

        }.start()
    }

    private fun initAdMod(){ //запускаем баннерную рекламу
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        bindingA.adView.loadAd(adRequest)
    }

    private fun loadInterAd(){ //загружаем межстраничную рекламу
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
    private fun showInterAd(){ //показываем межстраничную рекламу
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
    private fun getMessage() = with(bindingA){
        val currentArray = resources.getStringArray(MainConst.emotionsList[posM]) //достаем из памяти массив строк под номером posM
        val message = currentArray[Random.nextInt(currentArray.size)] //выбираем случайную строку из массива
        val messageList = message.split("~") //делим строку на части по знаку "~"
        tvMessage.text = messageList[0] //первую часть отправляем на верхний экран
        tvMessage.setBackgroundColor(0x40808080.toInt()) //очень прозрачный серый
        tvName.text = messageList[1] //вторую часть - на нижний экран
        tvName.setBackgroundColor(0x60FF0000.toInt()) //сильно-прозрачный красный
        imageBg.setImageResource((MainConst.imageList[Random.nextInt(12)])) //берём случайную картинку
}
    private fun showContent(){ //имитация запуска контента после просмотра рекламы
        //Toast.makeText(this, "Запуск контента", Toast.LENGTH_LONG).show()
    }

    override fun onClick(pos: Int) {
        posM = pos
        getResult()
    }
}