package com.example.ricescan.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ricescan.R
import com.example.ricescan.databinding.FragmentTabSymptomsBinding
import com.example.ricescan.ml.DiseaseInfo
import com.example.ricescan.ml.DiseaseRepository

class SymptomsFragment : Fragment() {

    private var _binding: FragmentTabSymptomsBinding? = null
    private val binding get() = _binding!!
    private var diseaseName: String = "brown_spot"

    companion object {
        fun newInstance(diseaseName: String): SymptomsFragment {
            return SymptomsFragment().apply {
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
        _binding = FragmentTabSymptomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = DiseaseRepository(requireContext())
        val info = repo.getDisease(diseaseName)
        info?.let { populateData(it) }
    }

    private fun populateData(info: DiseaseInfo) {
        binding.tvCause.text = info.cause
        binding.tvDiseaseType.text = when (diseaseName) {
            "leaf_blast", "brown_spot" -> "Fungal Disease"
            "bacterial_blight"         -> "Bacterial Disease"
            "tungro"                   -> "Viral Disease"
            else                       -> "Healthy Plant"
        }

        // Add symptom items
        binding.symptomsContainer.removeAllViews()
        info.symptoms.forEach { symptom ->
            val item = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_symptom_dark, binding.symptomsContainer, false)
            item.findViewById<TextView>(R.id.tvSymptom).text = symptom
            binding.symptomsContainer.addView(item)
        }

        // Severity bars
        val (spread, damage, treat) = when (diseaseName) {
            "leaf_blast"       -> Triple(0.80f, 0.85f, 0.60f)
            "bacterial_blight" -> Triple(0.75f, 0.80f, 0.55f)
            "brown_spot"       -> Triple(0.65f, 0.50f, 0.80f)
            "tungro"           -> Triple(0.70f, 0.90f, 0.20f)
            else               -> Triple(0.10f, 0.05f, 1.00f)
        }
        setSeverityBar(binding.barSpread, spread)
        setSeverityBar(binding.barDamage, damage)
        setSeverityBar(binding.barTreat, treat)

        // Recommended action
        binding.tvRecommendedAction.text = info.treatment
    }

    private fun setSeverityBar(bar: View, percent: Float) {
        bar.post {
            val parent = bar.parent as View
            val targetWidth = (parent.width * percent).toInt()
            val params = bar.layoutParams
            params.width = targetWidth
            bar.layoutParams = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}