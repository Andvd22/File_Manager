package com.example.mylearning.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityPremiumBinding

private lateinit var binding: ActivityPremiumBinding

class PremiumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupListener()
    }

    private fun setupUI(){
        binding.itemRead.ivIcon.setImageResource(R.drawable.premium_activity_read)
        binding.itemRead.tvTitle.setText(R.string.read)
        binding.itemRead.ivIcon2.setImageResource(R.drawable.premium_activity_check)
        binding.itemRead.ivIcon3.setImageResource(R.drawable.premium_activity_check)

        binding.itemEdit.ivIcon.setImageResource(R.drawable.premium_activity_edit)
        binding.itemEdit.tvTitle.setText(R.string.edit)
        binding.itemEdit.ivIcon2.setImageResource(R.drawable.premium_activity_close)
        binding.itemEdit.ivIcon3.setImageResource(R.drawable.premium_activity_check)

        binding.itemAdd.ivIcon.setImageResource(R.drawable.premium_activity_add)
        binding.itemAdd.tvTitle.setText(R.string.add)
        binding.itemAdd.ivIcon2.setImageResource(R.drawable.premium_activity_close)
        binding.itemAdd.ivIcon3.setImageResource(R.drawable.premium_activity_check)

        binding.itemScan.ivIcon.setImageResource(R.drawable.premium_activity_scan)
        binding.itemScan.tvTitle.setText(R.string.scan)
        binding.itemScan.ivIcon2.setImageResource(R.drawable.premium_activity_close)
        binding.itemScan.ivIcon3.setImageResource(R.drawable.premium_activity_check)

        binding.itemSign.ivIcon.setImageResource(R.drawable.premium_activity_sign)
        binding.itemSign.tvTitle.setText(R.string.sign)
        binding.itemSign.ivIcon2.setImageResource(R.drawable.premium_activity_close)
        binding.itemSign.ivIcon3.setImageResource(R.drawable.premium_activity_check)

        binding.itemRemove.ivIcon.setImageResource(R.drawable.premium_activity_remove)
        binding.itemRemove.tvTitle.setText(R.string.remove)
        binding.itemRemove.ivIcon2.setImageResource(R.drawable.premium_activity_close)
        binding.itemRemove.ivIcon3.setImageResource(R.drawable.premium_activity_check)
    }

    private fun setupListener(){
        binding.ivClose.setOnClickListener {
            finish()
        }
    }
}