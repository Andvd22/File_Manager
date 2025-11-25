package com.example.mylearning.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mylearning.R
import com.example.mylearning.databinding.DialogSortOptionBinding
import com.example.mylearning.model.SortCriteria
import com.example.mylearning.model.SortOrder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetDialog (
    private val onSortSelected: (SortCriteria, SortOrder) -> Unit
): BottomSheetDialogFragment(){
    private lateinit var binding: DialogSortOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSortOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApply.setOnClickListener {
            val selectedCriteria = when (binding.rgSortCriteria.checkedRadioButtonId) {
                R.id.rbDate -> SortCriteria.DATE
                R.id.rbName -> SortCriteria.NAME
                R.id.rbSize -> SortCriteria.SIZE
                else -> SortCriteria.NAME
            }

            val selectedOrder = when (binding.rgSortOrder.checkedRadioButtonId) {
                R.id.rbAsc -> SortOrder.ASCENDING
                R.id.rbDesc -> SortOrder.DESCENDING
                else -> SortOrder.ASCENDING
            }

            onSortSelected(selectedCriteria, selectedOrder)
            dismiss()
        }
    }
}