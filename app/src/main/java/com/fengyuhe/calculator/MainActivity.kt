package com.fengyuhe.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.fengyuhe.calculator.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private var exp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val orientation = resources.configuration.orientation
        setContentView(mBinding!!.root)

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val intent = Intent(this, HorizontalActivity::class.java)
//            setContentView(hBinding!!.root)
            intent.putExtra("exp", exp)
            intent.putExtra("result", mBinding!!.result.text)
            println("go landscape")
            startActivityForResult(intent, RESULT_OK)
        }

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
            exp = res.toString()
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

        setSupportActionBar(findViewById(R.id.main_actionBar))

    }

    private fun addExp(element: String) {
        if (exp.length < 9) {
            exp = exp.plus(element)
            mBinding!!.result.text = exp
        }
    }

    //计算机主要计算逻辑
    //0915: 已经包含括号匹配 自动忽略多余括号
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

    //Activity 跳转
    //TODO: Intent数据读取失败，回调不执行 未解决
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
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.exchange_rate -> {
            val intent = Intent(this, ExchangeActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.salary -> {
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}