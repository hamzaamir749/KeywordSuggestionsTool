package com.akstudios.KSTWV.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.akstudios.KSTWV.R
import com.akstudios.KSTWV.databinding.ActivityMainBinding
import com.akstudios.KSTWV.utils.inAppMessagingInitialization
import com.applovin.adview.AppLovinAdView
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinAdSize
import com.applovin.sdk.AppLovinSdk
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    //Ads
    private lateinit var rewardedAd: MaxRewardedAd
    private var retryAttempt = 0.0
    private var showAttempt = 0

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttemptInterstitial = 0.0
    private var showAttemptInterstitial = 0
    private var isFirstTimeRewardAd: Boolean = true
    private var onAdActions: ((Boolean) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        clickListeners()

    }

    private fun initViews() {
        navController = findNavController(R.id.main_fragment)
        inAppMessagingInitialization(this, true, "main_activity_in_app_messaging")
        loadApplovinSdk()
    }

    private fun loadApplovinSdk() {
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk {
            createBannerAd()
            createInterstitialAd()
            createRewardedAd()
        }
    }


    private fun clickListeners() {
        binding.bottomBar.setOnNavigationItemSelectedListener {
            loadAd(true)
            when (it.itemId) {
                R.id.bottomNav_competition -> {
                    navController.navigate(R.id.second_fragment)
                    true
                }
                R.id.bottomNav_search -> {
                    navController.navigate(R.id.first_fragment)
                    true
                }

                R.id.bottomNav_bulk -> {
                    navController.navigate(R.id.third_fragment)
                    true
                }
                R.id.bottomNav_menu -> {
                    navController.navigate(R.id.fourth_fragment)
                    true
                }
                else -> false
            }

        }
    }


    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.first_fragment) {
            navController.navigate(R.id.first_fragment)
            binding.bottomBar.selectedItemId = R.id.bottomNav_search
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    //Ads Started

    private fun createRewardedAd() {
        rewardedAd =
            MaxRewardedAd.getInstance(resources?.getString(R.string.admob_rewarded_ad_id), this)
        rewardedAd.setListener(object : MaxRewardedAdListener {

            override fun onAdLoaded(ad: MaxAd?) {
                retryAttempt = 0.0
                loadAd()

            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                retryAttempt++
                val delayMillis =
                    TimeUnit.SECONDS.toMillis(2.0.pow(min(6.0, retryAttempt)).toLong())

                Handler(Looper.getMainLooper()).postDelayed({ rewardedAd.loadAd() }, delayMillis)
            }

            override fun onAdDisplayed(ad: MaxAd?) {

            }

            override fun onAdHidden(ad: MaxAd?) {
                rewardedAd.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                rewardedAd.loadAd()
            }

            override fun onRewardedVideoStarted(ad: MaxAd?) {
            }

            override fun onRewardedVideoCompleted(ad: MaxAd?) {
            }

            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                onAdActions
                    ?.invoke(true)
                rewardedAd.loadAd()
            }

        })

        rewardedAd.loadAd()
    }

    private fun createInterstitialAd() {
        interstitialAd = MaxInterstitialAd(resources?.getString(R.string.admob_interistitial), this)
        interstitialAd.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttemptInterstitial = 0.0
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                retryAttemptInterstitial++
                val delayMillis = TimeUnit.SECONDS.toMillis(
                    2.0.pow(
                        6.0.coerceAtMost(retryAttemptInterstitial)
                    ).toLong()
                )

                Handler(Looper.getMainLooper()).postDelayed(
                    { interstitialAd.loadAd() },
                    delayMillis
                )
            }

            override fun onAdDisplayed(ad: MaxAd?) {


            }

            override fun onAdHidden(ad: MaxAd?) {
                onAdActions?.invoke(true)
                interstitialAd.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {

            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {

                interstitialAd.loadAd()
            }

        })

        // Load the first ad
        interstitialAd.loadAd()
    }

    private fun createBannerAd() {
        val adBanner = AppLovinAdView(AppLovinAdSize.BANNER, this)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)
        adBanner.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        adBanner.setBackgroundColor(getColor(R.color.backgroundFragment))
        binding.bannerAdLayout.addView(adBanner)
        adBanner.loadNextAd()
    }


    fun loadAd(isInterstitial: Boolean = false, action: ((Boolean) -> Unit)? = null) {
        onAdActions = action
        if (isInterstitial) {
            if (showAttemptInterstitial == 5) {
                if (interstitialAd.isReady) {
                    showAttemptInterstitial = 0
                    interstitialAd.showAd()
                } else {
                    onAdActions?.invoke(true)
                    interstitialAd.loadAd()
                }
            } else {
                onAdActions?.invoke(true)
                ++showAttemptInterstitial
            }
        } else {
            if (isFirstTimeRewardAd) {
                rewardedAd.showAd()
                isFirstTimeRewardAd = false
            } else {
                if (showAttempt == 3) {
                    if (rewardedAd.isReady) {
                        rewardedAd.showAd()
                        showAttempt = 0
                    } else {
                        onAdActions?.invoke(true)
                        rewardedAd.loadAd()
                    }
                } else {
                    onAdActions?.invoke(true)
                    ++showAttempt
                }
            }
        }
    }
//Ads Started


}