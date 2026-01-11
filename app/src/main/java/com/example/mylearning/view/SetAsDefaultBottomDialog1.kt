package com.example.mylearning.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    companion object {
        const val TAG = "SetAsDefaultBottomDialog1"
    }
}