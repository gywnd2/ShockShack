package com.udangtangtang.shockshack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.udangtangtang.shockshack.databinding.ActivityCreditBinding
import com.udangtangtang.shockshack.databinding.LayoutToolbarMainBinding

class Credit : AppCompatActivity() {
    private lateinit var binding : ActivityCreditBinding
    private lateinit var toolbarBinding : LayoutToolbarMainBinding
    private lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreditBinding.inflate(layoutInflater)
        toolbarBinding=LayoutToolbarMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set ToolBar
        setSupportActionBar(toolbarBinding.root)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Git readme webview
        binding.webviewCredit.loadUrl(getString(R.string.url_credit_readme_md))
    }
}