package com.example.mylearning.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.mylearning.R
import com.example.mylearning.adapter.WidgetPagerAdapter
import com.example.mylearning.databinding.DialogAddWidgetBinding
import com.example.mylearning.widget.WidgetPage1Provider
import com.example.mylearning.widget.WidgetPage2Provider
import com.example.mylearning.widget.WidgetPage3Provider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddWidgetBottomSheet : BottomSheetDialogFragment(){
    private lateinit var binding: DialogAddWidgetBinding
    private lateinit var dots: List<View>
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAddWidgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager(){
        val adapter = WidgetPagerAdapter()
        binding.viewPager.adapter = adapter

        dots = listOf(binding.dot1, binding.dot2, binding.dot3)

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    currentPage = position
                    updateDots(position)
                }
            }
        )
    }

    private fun updateDots(selectedPosition: Int){
        dots.forEachIndexed { index, dot ->
            val params = dot.layoutParams as ViewGroup.LayoutParams
            if(index == selectedPosition) {
                dot.setBackgroundResource(R.drawable.widget_dot_selected)
                params.width = dpToPx(20f)
            }
            else{
                dot.setBackgroundResource(R.drawable.widget_dot_unselected)
                params.width = dpToPx(8f)
            }
            dot.layoutParams = params
        }
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * binding.root.resources.displayMetrics.density).toInt()
    }

    private fun setupButtons(){
        binding.btnCancel.setOnClickListener{
            dismiss()
        }
        binding.btnAdd.setOnClickListener{
            requestPinWidget()
        }
    }


    private fun requestPinWidget(){
        val context = requireContext()
        val appWidgetManager = AppWidgetManager.getInstance(context)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val providerClass: Class<out AppWidgetProvider> = when (currentPage) {
                0 -> WidgetPage1Provider::class.java
                1 -> WidgetPage2Provider::class.java
                2 -> WidgetPage3Provider::class.java
                else -> WidgetPage1Provider::class.java
            }

            val componentName = ComponentName(context, providerClass)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                appWidgetManager.requestPinAppWidget(componentName, null, null)
                dismiss()
            } else {
                Toast.makeText(context, "Thiết bị không hỗ trợ ghim widget", Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(context, "Cần Android 8.0+ để ghim widget", Toast.LENGTH_SHORT).show()
        }


    }

    companion object {
        const val TAG = "AddWidgetBottomSheet"
    }

}