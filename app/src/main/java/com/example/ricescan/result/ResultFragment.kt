package com.example.ricescan.result

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ricescan.R
import com.example.ricescan.databinding.FragmentResultBinding
import com.example.ricescan.ml.DiseaseClassifier
import com.example.ricescan.ml.DiseaseRepository
import kotlinx.coroutines.*

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var classifier: DiseaseClassifier
    private lateinit var repository: DiseaseRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classifier = DiseaseClassifier(requireContext())
        repository = DiseaseRepository(requireContext())

        val imageUriString = arguments?.getString("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        // Show captured image
        imageUri?.let {
            binding.ivLeafImage.setImageURI(it)
        }

        // Run classification in background
        CoroutineScope(Dispatchers.IO).launch {
            val result = if (imageUri != null) {
                classifier.classify(imageUri)
            } else {
                classifier.classify(Uri.EMPTY)
            }

            val diseaseInfo = repository.getDisease(result.diseaseName)

            withContext(Dispatchers.Main) {
                // Set confidence ring
                binding.confidenceRing.setConfidence(result.confidence)

                // Set disease name
                binding.tvDiseaseName.text = result.displayName

                // Set severity color
                val severity = diseaseInfo?.severity ?: "Unknown"
                binding.tvSeverity.text = severity
                binding.tvSeverity.setTextColor(
                    when (severity) {
                        "High"   -> android.graphics.Color.parseColor("#E74C3C")
                        "Medium" -> android.graphics.Color.parseColor("#FFC107")
                        "None"   -> android.graphics.Color.parseColor("#2ECC71")
                        else     -> android.graphics.Color.WHITE
                    }
                )

                // Navigate to detail screen
                binding.tvViewDetail.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("diseaseName", result.diseaseName)
                    }
                    findNavController().navigate(R.id.action_result_to_detail, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        classifier.close()
        _binding = null
    }
}