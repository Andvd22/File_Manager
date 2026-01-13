package com.example.mylearning.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mylearning.R
import com.example.mylearning.databinding.DialogSetAsDefault3Binding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetAsDefaultBottomDialog3(
    private val onGoToSettings: () -> Unit) : BottomSheetDialogFragment(){
    private lateinit var binding: DialogSetAsDefault3Binding
    private val defaultPkg by lazy { arguments?.getString("arg_pkg") }
    private val appName by lazy { arguments?.getString("arg_app_name") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSetAsDefault3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        styleTexts()
        setupViews()
    }

    private fun setupViews(){
        binding.tvContent.text = appName
        defaultPkg?.let {
            val icon = requireContext().packageManager.getApplicationIcon(it)
            binding.ivIconll.setImageDrawable(icon)
        }
    }

    private fun setupClickListeners(){
        binding.tvBtnConfirm.setOnClickListener {
            onGoToSettings.invoke()
            if (!defaultPkg.isNullOrBlank()) {
                val settingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$defaultPkg")
                )
                startActivity(settingsIntent)
            }
            dismiss()
        }

        binding.tvBtnLater.setOnClickListener {
            dismiss()
        }
    }

    private fun styleTexts(){
        val appName = getString(R.string.dialog3_app_name)

        val title = getString(R.string.dialog3_title, appName)

        val primaryColor = requireContext().getColor(R.color.primaryColor)


        binding.tvTitle.text = colorize(title, primaryColor, appName)
    }

    private fun colorize(text: String, color: Int, targetWord: String): SpannableString{
        val span = SpannableString(text)
        val start = text.indexOf(targetWord)
        val end = start + targetWord.length
        span.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

//    override fun onStop() {
//        super.onStop()
//        val intent = Intent(requireContext(), ClearDefaultGuideActivity::class.java)
//        startActivity(intent)
//    }

    companion object {
        const val TAG = "SetAsDefaultBottomDialog3"
    }
}