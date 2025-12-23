package com.example.mylearning.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityFeedbackBinding



class FeedbackActivity : AppCompatActivity() {

    private var firstStartActivity: Boolean = false
    private lateinit var binding: ActivityFeedbackBinding

    private var hasTouched = false

    private val prefs by lazy {
        getSharedPreferences("feedback_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDefaultState()
        setupUi()
        setupListeners()
    }

    private fun setupUi() {
        binding.fbIssueCannotOpenFile.tvText.setText(R.string.fb_issue_cannot_open_file)
        binding.fbIssueErrorBug.tvText.setText(R.string.fb_issue_error_bug)
        binding.fbIssueSlow.tvText.setText(R.string.fb_issue_slow)
        binding.fbIssueTooManyNotifications.tvText.setText(R.string.fb_issue_too_many_notifications)
        binding.fbIssueSuggestion.tvText.setText(R.string.fb_issue_suggestion)
        binding.fbIssueOther.tvText.setText(R.string.fb_issue_other)
    }

    private val items by lazy {
        listOf(
            binding.fbIssueCannotOpenFile.tvText,
            binding.fbIssueErrorBug.tvText,
            binding.fbIssueSlow.tvText,
            binding.fbIssueTooManyNotifications.tvText,
            binding.fbIssueSuggestion.tvText,
            binding.fbIssueOther.tvText
        )
    }
    private fun initDefaultState() {
        if(firstStartActivity) return

        val editor = prefs.edit()

        items.forEachIndexed { index, tv ->
            editor.putBoolean("issue_$index", false).apply()
            tv.isSelected = false
        }
        firstStartActivity = true
    }

    private fun setupListeners() {
        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                hasTouched = true
                checkCharBeforeSend()
            }
        }
        binding.etDetail.doAfterTextChanged  {
            if(hasTouched) checkCharBeforeSend()
        }
        items.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                val state = !prefs.getBoolean("issue_$index", false)
                prefs.edit().putBoolean("issue_$index",state).apply()
                tv.isSelected = state
            }
        }
        binding.ivIconBack.setOnClickListener {
            finish()
        }
        binding.tvBtnSend.setOnClickListener {
            if (!checkCharBeforeSend()) return@setOnClickListener
            if(checkFeedbackBeforeSend()) {
                var toastt = StringBuilder()
                items.forEachIndexed { index, tv ->
                    if(tv.isSelected) toastt.append(tv.text.toString() )
                }
                Toast.makeText(this, "$toastt", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCharBeforeSend(): Boolean{
        val length = binding.etDetail.text?.length?:0
        val check2 = length >= 6
        binding.tvMinChars.visibility = if (hasTouched&&!check2) View.VISIBLE else View.GONE
        binding.tvBtnSend.isEnabled = check2
        binding.tvBtnSend.alpha = if(check2) 1f else 0.5f
        return check2
    }

    private fun checkFeedbackBeforeSend(): Boolean{
        val check1 = items.indices.any{ index ->
            prefs.getBoolean("issue_$index",false)
        }
        binding.tvMinFeedbacks.visibility = if(check1) View.GONE else View.VISIBLE

        return check1
    }

}