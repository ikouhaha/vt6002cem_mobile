package com.example.vt6002cem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var amt = intent.getFloatExtra("amt", 0F)
        setContentView(R.layout.activity_payment)
        findViewById<TextView>(R.id.totalAmt).text = "$$amt"
    }
}