package com.example.ricescan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ricescan.databinding.FragmentTabPreventionBinding
import com.example.ricescan.ml.DiseaseRepository

class PreventionFragment : Fragment() {

    private var _binding: FragmentTabPreventionBinding? = null
    private val binding get() = _binding!!
    private var diseaseName: String = "brown_spot"

    companion object {
        fun newInstance(diseaseName: String): PreventionFragment {
            return PreventionFragment().apply {
                arguments = Bundle().apply {
                    putString("diseaseName", diseaseName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diseaseName = arguments?.getString("diseaseName") ?: "brown_spot"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabPreventionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = DiseaseRepository(requireContext())
        val info = repo.getDisease(diseaseName)
        binding.tvPrevention.text = info?.prevention ?: "No prevention data available."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}