package com.fengyuhe.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //View：表达式
        val textView_expression = findViewById<TextView>(R.id.expression) //View：表达式
        textView_expression.setOnClickListener {
            
        }

        val textView_result = findViewById<TextView>(R.id.result) //View：显示结果
        val btn_ac = findViewById<Button>(R.id.btn_AC) //按钮：删除所有
        val btn_delete = findViewById<Button>(R.id.btn_delete) //按钮：删除一位
        val btn_plus = findViewById<Button>(R.id.btn_plus) //按钮：+
        val btn_minus = findViewById<Button>(R.id.btn_minus) //按钮：-
        val btn_divide = findViewById<Button>(R.id.btn_divide) //按钮：/
        val btn_multipy = findViewById<Button>(R.id.btn_multipy) //按钮：x
        val btn_equal = findViewById<Button>(R.id.btn_equal) //按钮：等于
        val btn_bracket_left = findViewById<Button>(R.id.btn_bracket_left) //按钮：(
        val btn_bracket_right = findViewById<Button>(R.id.btn_bracket_right) //按钮：)
        val btn_negative = findViewById<Button>(R.id.btn_negative) //按钮：+/-





    }
}