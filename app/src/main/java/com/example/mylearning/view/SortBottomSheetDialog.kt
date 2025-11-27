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
    private val currentCriteria: SortCriteria,
    private val currentOrder: SortOrder,
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

        val criteriaId = when(currentCriteria) {
            SortCriteria.DATE -> R.id.rbDate
            SortCriteria.NAME -> R.id.rbName
            SortCriteria.SIZE -> R.id.rbSize
        }
        binding.rgSortCriteria.check(criteriaId)

        val orderId = when(currentOrder) {
            SortOrder.ASCENDING -> R.id.rbAsc
            SortOrder.DESCENDING -> R.id.rbDesc
        }
        binding.rgSortOrder.check(orderId)

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