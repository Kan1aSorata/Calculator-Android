package com.fengyuhe.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.fengyuhe.calculator.databinding.ActivityHorizontalBinding

class HorizontalActivity : AppCompatActivity() {

    private var hBinding: ActivityHorizontalBinding? = null
    private var exp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hBinding = ActivityHorizontalBinding.inflate(layoutInflater)
        setContentView(hBinding!!.root)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setContentView(mBinding!!.root)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("exp", exp)
            intent.putExtra("result", hBinding!!.result.text)
            print("intent extra put")
            startActivityForResult(intent, RESULT_OK)
            println("go portrait")
        }
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(hBinding!!.root)
//        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setContentView(mBinding!!.root)
//        }

        window.decorView.apply {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }

        hBinding!!.btnPlus.setOnClickListener {
            addExp("+")
            print("clicked")
        }
        hBinding!!.btnMinus.setOnClickListener { addExp("-") }
        hBinding!!.btnMultipy.setOnClickListener { addExp("*") }
        hBinding!!.btnDivide.setOnClickListener { addExp("/") }
        hBinding!!.btnBracketLeft.setOnClickListener { addExp("(") }
        hBinding!!.btnBracketRight.setOnClickListener { addExp(")") }
        hBinding!!.btnDot.setOnClickListener { addExp(".") }
        hBinding!!.btnNegative.setOnClickListener { addExp("-") }

        hBinding!!.btnAC.setOnClickListener {
            hBinding!!.expression.text = ""
            hBinding!!.result.text = ""
            exp = ""
        }

        hBinding!!.btnDelete.setOnClickListener {
            exp = exp.dropLast(1)
            hBinding!!.result.text = exp
        }

        hBinding!!.btnEqual.setOnClickListener {
            val res = eval(exp)
            addExp("=")
            hBinding!!.expression.text = exp
            hBinding!!.result.text = res.toString()
            exp = res.toString()
        }

        val btnDigs = arrayOf(hBinding!!.btn0, hBinding!!.btn1, hBinding!!.btn2, hBinding!!.btn3, hBinding!!.btn4, hBinding!!.btn5, hBinding!!.btn6, hBinding!!.btn7, hBinding!!.btn8, hBinding!!.btn9)
        btnDigs.forEach { button ->
            button.setOnClickListener {
                addExp(
                    when(it) {
                        hBinding!!.btn0 -> "0"
                        hBinding!!.btn1 -> "1"
                        hBinding!!.btn2 -> "2"
                        hBinding!!.btn3 -> "3"
                        hBinding!!.btn4 -> "4"
                        hBinding!!.btn5 -> "5"
                        hBinding!!.btn6 -> "6"
                        hBinding!!.btn7 -> "7"
                        hBinding!!.btn8 -> "8"
                        hBinding!!.btn9 -> "9"
                        else -> ""
                    }
                )
            }
        }

        hBinding!!.btnSin.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            exp = Math.sin(radians).toString()
            hBinding!!.result.text = Math.sin(radians).toString()
        }

        hBinding!!.btnCos.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            exp = Math.cos(radians).toString()
            hBinding!!.result.text = Math.cos(radians).toString()
        }

        hBinding!!.btnTan.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            exp = Math.tan(radians).toString()
            hBinding!!.result.text = Math.tan(radians).toString()
        }

        hBinding!!.btnCot.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            var cot = Math.cos(radians) / Math.sin(radians)
            exp = cot.toString()
            hBinding!!.result.text = cot.toString()
        }

        hBinding!!.btnSec.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            var sec = 1 / Math.cos(radians)
            exp = sec.toString()
            hBinding!!.result.text = sec.toString()
        }

        hBinding!!.btnCsc.setOnClickListener {
            var radians = Math.toRadians(exp.toDouble())
            var csc = 1 / Math.sin(radians)
            exp = csc.toString()
            hBinding!!.result.text = csc.toString()
        }

        hBinding!!.btnBin.setOnClickListener {

        }

        hBinding!!.btnOct.setOnClickListener {

        }

        hBinding!!.btnDec.setOnClickListener {

        }

        hBinding!!.btnHex.setOnClickListener {

        }

    }

    private fun addExp(element: String) {
        if (exp.length < 9) {
            exp = exp.plus(element)
            hBinding!!.result.text = exp
        }
    }

    @SuppressLint("SetTextI18n")
    private fun eval(expr: String): Float {
        var index = 0 // current index
        val skipWhile =
            { cond: (Char) -> Boolean -> while (index < expr.length && cond(expr[index])) index++ }
        val tryRead =
            { c: Char -> (index < expr.length && expr[index] == c).also { if (it) index++ } }
        val skipWhitespaces = { skipWhile { it.isWhitespace() } }
        val tryReadOp =
            { op: Char -> skipWhitespaces().run { tryRead(op) }.also { if (it) skipWhitespaces() } }
        var rootOp: () -> Float = { 0.0f }

        val num = {
            if (tryReadOp('(')) {
                rootOp().also {
                    tryReadOp(')').also {
                        if (!it) {
                            hBinding!!.result.text = "Missing at: $index"
                            exp = ""
                        }
                    }
                }
            } else {
                val start = index
                tryRead('-') or tryRead('+')
                skipWhile { it.isDigit() || it == '.' }
                try {
                    expr.substring(start, index).toFloat()
                } catch (e: NumberFormatException) {
                    hBinding!!.result.text = "Invalid number at:${start}"
                    exp = ""
                    "1.0".toFloat()
                }
            }
        }

        fun binary(left: () -> Float, op: Char): List<Float> = mutableListOf(left()).apply {
            while (tryReadOp(op)) addAll(binary(left, op))
        }

        val div = { binary(num, '/').reduce { a, b -> a / b } }
        val mul = { binary(div, '*').reduce { a, b -> a * b } }
        val sub = { binary(mul, '-').reduce { a, b -> a - b } }
        val add = { binary(sub, '+').reduce { a, b -> a + b } }

        rootOp = add
        return rootOp().also {
            if (index < expr.length) {
                hBinding!!.result.text = "Invalid expression at:${index}"
                exp = ""
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val returnExp = data?.getStringExtra("exp")
                val returnResult = data?.getStringExtra("result")
                val expression = findViewById<TextView>(R.id.expression)
                val resultView = findViewById<TextView>(R.id.result)
                expression.text = returnExp
                resultView.text = returnResult
                exp = returnExp!!
                print("success")
            }
        }
    }
}