package com.example.ricescan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ricescan.databinding.FragmentDetailBinding
import com.example.ricescan.ml.DiseaseRepository
import com.google.android.material.tabs.TabLayoutMediator

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var diseaseName: String = "brown_spot"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diseaseName = arguments?.getString("diseaseName") ?: "brown_spot"

        // Set title
        val repo = DiseaseRepository(requireContext())
        val info = repo.getDisease(diseaseName)
        binding.tvDetailTitle.text = info?.name ?: "Disease Detail"

        // Setup ViewPager + Tabs
        val adapter = DetailPagerAdapter(this, diseaseName)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Symptoms"
                1 -> "Prevention"
                2 -> "Treatment"
                else -> ""
            }
        }.attach()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DetailPagerAdapter(
    fragment: Fragment,
    private val diseaseName: String
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SymptomsFragment.newInstance(diseaseName)
            1 -> PreventionFragment.newInstance(diseaseName)
            2 -> TreatmentFragment.newInstance(diseaseName)
            else -> SymptomsFragment.newInstance(diseaseName)
        }
    }
}