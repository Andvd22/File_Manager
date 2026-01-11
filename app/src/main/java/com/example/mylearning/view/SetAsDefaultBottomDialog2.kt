package com.example.mylearning.view

import com.example.mylearning.databinding.DialogSetAsDefault2Binding
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetAsDefaultBottomDialog2 : BottomSheetDialogFragment(){
    private lateinit var binding: DialogSetAsDefault2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSetAsDefault2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners(){
        binding.btnConfirm.setOnClickListener {
            Toast.makeText(context, "OK con de", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    companion object {
        const val TAG = "SetAsDefaultBottomDialog2"
    }
}