package com.akstudios.KSTWV.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akstudios.KSTWV.databinding.FragmentMenuBinding
import com.akstudios.KSTWV.ui.activities.MainActivity
import com.akstudios.KSTWV.utils.*
import com.applovin.sdk.AppLovinSdk

// need website,need youtube link
class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickListeners()
    }

    private fun clickListeners() {
        binding.ShareappCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().shareApp()
            }
        }
        binding.websiteCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.WEBSITE_LINK)
            }

        }
        binding.youtubeCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.YOUTUBE_LINK)
            }

        }
        binding.blogpostCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.BLOG_LINK)
            }

        }
        binding.rateusCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().rateUs()
            }

        }
        binding.feedbackCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().feedbackUs(GlobalVariables.EMAIL)
            }

        }
    }

    private fun initViews() {

        loadApplovinSdk()
    }

    private fun loadApplovinSdk() {
        AppLovinSdk.getInstance(requireContext()).mediationProvider = "max"
        AppLovinSdk.getInstance(requireContext()).initializeSdk {
            requireContext().createBannerAd(binding.bannerAdLayout)
        }
    }
}