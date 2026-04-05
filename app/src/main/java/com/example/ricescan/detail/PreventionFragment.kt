package com.example.ricescan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ricescan.R
import com.example.ricescan.databinding.FragmentTabPreventionBinding
import com.example.ricescan.ml.DiseaseRepository

class PreventionFragment : Fragment() {

    private var _binding: FragmentTabPreventionBinding? = null
    private val binding get() = _binding!!
    private var diseaseName: String = "brown_spot"

    companion object {
        fun newInstance(diseaseName: String): PreventionFragment {
            return PreventionFragment().apply {
                arguments = Bundle().apply { putString("diseaseName", diseaseName) }
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
        val preventionPoints = info?.prevention
            ?.split("\n")
            ?.map { it.trim().removePrefix("•").removePrefix("-").removePrefix("*").trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()

        binding.tvPrevention.text = if (preventionPoints.isNotEmpty()) {
            "Key prevention points:"
        } else {
            "No prevention data available."
        }

        binding.tipsContainer.removeAllViews()
        preventionPoints.forEach { tip ->
            val item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_symptom_dark, binding.tipsContainer, false)
            item.findViewById<TextView>(R.id.tvSymptom).text = tip
            binding.tipsContainer.addView(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
