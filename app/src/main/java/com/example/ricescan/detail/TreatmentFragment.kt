package com.example.ricescan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ricescan.databinding.FragmentTabTreatmentBinding
import com.example.ricescan.ml.DiseaseRepository

class TreatmentFragment : Fragment() {

    private var _binding: FragmentTabTreatmentBinding? = null
    private val binding get() = _binding!!
    private var diseaseName: String = "brown_spot"

    companion object {
        fun newInstance(diseaseName: String): TreatmentFragment {
            return TreatmentFragment().apply {
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
        _binding = FragmentTabTreatmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = DiseaseRepository(requireContext())
        val info = repo.getDisease(diseaseName)
        binding.tvTreatment.text = info?.treatment ?: "No treatment data available."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}