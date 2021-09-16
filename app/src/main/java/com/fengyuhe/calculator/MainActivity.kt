package com.fengyuhe.calculator

import android.annotation.SuppressLint
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.fengyuhe.calculator.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private var exp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)

        mBinding!!.btnPlus.setOnClickListener { addExp("+") }
        mBinding!!.btnMinus.setOnClickListener { addExp("-") }
        mBinding!!.btnMultipy.setOnClickListener { addExp("*") }
        mBinding!!.btnDivide.setOnClickListener { addExp("/") }
        mBinding!!.btnBracketLeft.setOnClickListener { addExp("(") }
        mBinding!!.btnBracketRight.setOnClickListener { addExp(")") }
        mBinding!!.btnDot.setOnClickListener { addExp(".") }
        mBinding!!.btnNegative.setOnClickListener { addExp("-") }

        mBinding!!.btnAC.setOnClickListener {
            mBinding!!.expression.text = ""
            mBinding!!.result.text = ""
            exp = ""
        }

        mBinding!!.btnDelete.setOnClickListener {
            exp = exp.dropLast(1)
            mBinding!!.result.text = exp
        }

        mBinding!!.btnEqual.setOnClickListener {
            val res = eval(exp)
            addExp("=")
            mBinding!!.expression.text = exp
            mBinding!!.result.text = res.toString()
            exp = ""
        }

        val btnDigs = arrayOf(mBinding!!.btn0, mBinding!!.btn1, mBinding!!.btn2, mBinding!!.btn3, mBinding!!.btn4, mBinding!!.btn5, mBinding!!.btn6, mBinding!!.btn7, mBinding!!.btn8, mBinding!!.btn9)
        btnDigs.forEach { button ->
            button.setOnClickListener {
                addExp(
                    when(it) {
                        mBinding!!.btn0 -> "0"
                        mBinding!!.btn1 -> "1"
                        mBinding!!.btn2 -> "2"
                        mBinding!!.btn3 -> "3"
                        mBinding!!.btn4 -> "4"
                        mBinding!!.btn5 -> "5"
                        mBinding!!.btn6 -> "6"
                        mBinding!!.btn7 -> "7"
                        mBinding!!.btn8 -> "8"
                        mBinding!!.btn9 -> "9"
                        else -> ""
                    }
                )
            }
        }
    }

    private fun addExp(element: String) {
        exp = exp.plus(element)
        mBinding!!.result.text = exp
    }

    @SuppressLint("SetTextI18n")
    private fun eval(expr: String): Float {
        var index = 0 // current index
        val skipWhile = { cond: (Char) -> Boolean -> while (index < expr.length && cond(expr[index])) index++ }
        val tryRead = { c: Char -> (index < expr.length && expr[index] == c).also { if (it) index++ } }
        val skipWhitespaces = { skipWhile { it.isWhitespace() } }
        val tryReadOp = { op: Char -> skipWhitespaces().run { tryRead(op) }.also { if (it) skipWhitespaces() } }
        var rootOp: () -> Float = { 0.0f }

        val num = {
            if (tryReadOp('(')) {
                rootOp().also {
                    tryReadOp(')').also {
                        if (!it){
                            mBinding!!.result.text = "Missing at: $index"
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
                    mBinding!!.result.text = "Invalid number at:${start}"
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
                mBinding!!.result.text = "Invalid expression at:${index}"
                exp = ""
            }
        }
    }
}