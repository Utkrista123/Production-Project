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
        binding.tvPrevention.text = info?.prevention ?: "No prevention data available."

        // Add best practice tips dynamically
        val tips = when (diseaseName) {
            "leaf_blast"       -> listOf(
                "Use certified blast-resistant varieties",
                "Avoid excessive nitrogen fertilizer",
                "Ensure proper plant spacing for airflow",
                "Monitor fields regularly during humid weather"
            )
            "bacterial_blight" -> listOf(
                "Use disease-free certified seeds",
                "Avoid deep flooding of fields",
                "Remove and burn infected plant debris",
                "Apply copper-based bactericide preventively"
            )
            "brown_spot"       -> listOf(
                "Maintain balanced soil nutrition",
                "Apply potassium fertilizer regularly",
                "Avoid water stress during growing season",
                "Use resistant varieties when available"
            )
            "tungro"           -> listOf(
                "Control green leafhopper population",
                "Synchronize planting with neighboring farms",
                "Remove infected plants immediately",
                "Use tungro-resistant rice varieties"
            )
            else               -> listOf(
                "Continue proper irrigation management",
                "Use balanced fertilizers",
                "Monitor crop health regularly"
            )
        }

        binding.tipsContainer.removeAllViews()
        tips.forEach { tip ->
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