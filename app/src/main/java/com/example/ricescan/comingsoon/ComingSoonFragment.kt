package com.example.ricescan.comingsoon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ricescan.R
import com.example.ricescan.databinding.FragmentComingSoonBinding

class ComingSoonFragment : Fragment() {

    private var _binding: FragmentComingSoonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComingSoonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        // Simple entrance animation
        binding.comingTitle.alpha = 0f
        binding.comingBody.alpha = 0f
        binding.comingTitle.translationY = 24f
        binding.comingBody.translationY = 24f

        binding.comingTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()

        binding.comingBody.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(120)
            .setDuration(320)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
