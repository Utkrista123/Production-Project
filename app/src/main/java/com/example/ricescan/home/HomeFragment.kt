package com.example.ricescan.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ricescan.R
import com.example.ricescan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardIdentify.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }
        binding.homeFab.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }

        binding.cardPlants.setOnClickListener {
            Toast.makeText(requireContext(), "My Plants coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.cardReminder.setOnClickListener {
            Toast.makeText(requireContext(), "Reminder coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.cardWeather.setOnClickListener {
            Toast.makeText(requireContext(), "Weather coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
