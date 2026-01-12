package com.example.mylearning.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.DialogSetAsDefault1Binding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetAsDefaultBottomDialog1 : BottomSheetDialogFragment(){
    private lateinit var binding: DialogSetAsDefault1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSetAsDefault1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        styleTitle()
    }

    private fun setupClickListeners(){
        binding.btnSetAsDefault.setOnClickListener {
            if(parentFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog2.TAG)==null){
                val bottomSheet2 = SetAsDefaultBottomDialog2()
                bottomSheet2.show(parentFragmentManager, SetAsDefaultBottomDialog2.TAG)
            }
            dismiss()
        }
    }

    private fun styleTitle() {
        val appName = getString(R.string.app_display_name) // đã theo locale
        val title = getString(R.string.set_default_title, appName)

        val color = ContextCompat.getColor(requireContext(), R.color.primaryColor)

        val span = SpannableString(title).apply {
            val start = title.indexOf(appName)
            if (start >= 0) {
                val end = start + appName.length
                setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        binding.tvTitle.text = span
    }

    companion object {
        const val TAG = "SetAsDefaultBottomDialog1"
    }
}