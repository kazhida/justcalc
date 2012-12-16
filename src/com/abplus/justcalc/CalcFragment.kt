package com.abplus.justcalc

import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.view.View.OnClickListener
import java.util.ArrayList
import android.widget.TextView

/**
 * User: kazhida
 * Date: 2012/08/13
 * Time: 16:02
 */
class CalcFragment(): Fragment() {

    trait Callback {
        fun onCalculated(value: Double, display: String)
    }

    var callback: Callback? = null

    var accumulator: Double = 0.0
        get() {
            return $accumulator
        }
        set(value: Double) {
            if (value.compareTo(java.lang.Double.NaN) == 0 ||
                value == java.lang.Double.POSITIVE_INFINITY ||
                value == java.lang.Double.NEGATIVE_INFINITY) {
                $accumulator = value
                precision = 0.0
            } else {
                //  有効数字12桁にまとめる
                val minus = value < 0
                var v = Math.abs(value)
                var p = 0.0
                if (v < 1.0 / D12) {
                    $accumulator = 0.0
                    precision = 0.0
                } else {
                    while (v < D12) {
                        if (p == 0.0) p = 1.0
                        v *= 10
                        p *= 10
                    }
                    if (p > 0) {
                        //  小数部の余分な0を落とす
                        var v2: Long = Math.round(v)
                        //Note::  while (v2 % 10 == 0 && p > 1)だと ==のところでエラーになる
                        while (v2.mod(10).toDouble() == 0.0 && p > 1.0) {
                            v2 /= 10
                            p /= 10
                        }
                        if (p > 1.0) {
                            v = v2.toDouble() / p
                        } else {
                            v = v2.toDouble()
                            p = 0.0
                        }
                    }

                    $accumulator = if (minus) -v else v
                    precision = p
                }
            }
            val s = valueAsString
            callback?.onCalculated($accumulator, s)

            entered = true

            display(s)
        }

    private val D12 = 1000.0 * 1000 * 1000 * 1000
    private var precision: Double = 0.0
    private var memoryRegister: Double? = null
    private var entered: Boolean = true

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.calc_panel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initListeners()
        display()
    }

    public fun hideMemoryRow() {
        val row: View? = getActivity()?.findViewById(R.id.calc_memory_row)
        row?.setVisibility(View.GONE)
    }

    public fun showMemoryRow() {
        val row: View? = getActivity()?.findViewById(R.id.calc_memory_row)
        row?.setVisibility(View.VISIBLE)
    }

    public fun hideFunctionRow() {
        val row1: View? = getActivity()?.findViewById(R.id.calc_func_row1)
        row1?.setVisibility(View.GONE)
        val row2: View? = getActivity()?.findViewById(R.id.calc_func_row2)
        row2?.setVisibility(View.GONE)
    }

    public fun showFunctionRow() {
        val row1: View? = getActivity()?.findViewById(R.id.calc_func_row1)
        row1?.setVisibility(View.VISIBLE)
        val row2: View? = getActivity()?.findViewById(R.id.calc_func_row2)
        row2?.setVisibility(View.VISIBLE)
    }

    private fun delDigit() {
        //  絶対値にしておく
        val minus = $accumulator < 0
        if (minus) $accumulator = -$accumulator

        if (precision > 1.0) {
            precision /= 10
            $accumulator = Math.floor($accumulator * precision) / precision
        } else if (precision > 0.0) {
            precision = 0.0
        } else {
            $accumulator = Math.floor($accumulator / 10)
        }
        //  元の符号に戻す
        if (minus) $accumulator = -$accumulator

        display()
    }

    private fun addDigit(digit: Int) {
        if (entered) {
            //  演算子を押した後
            $accumulator = digit.toDouble()
            precision = 0.0
            entered = false
        } else if (precision > 0) {
            //  小数部の入力
            precision *= 10.0
            $accumulator += digit.toDouble() / precision
        } else {
            //  整数の入力
            $accumulator *= 10.0
            $accumulator += digit.toDouble()
        }
        display()
    }

    val valueAsString: String get() {
        if (accumulator.compareTo(java.lang.Double.NaN) == 0) {
            return "ERROR"
        } else {
            return when (accumulator) {
                java.lang.Double.POSITIVE_INFINITY -> "+∞"
                java.lang.Double.NEGATIVE_INFINITY -> "-∞"
                else -> {
                    val minus = accumulator < 0
                    val v = if (precision > 0) {
                        Math.abs(accumulator) * precision
                    } else {
                        Math.abs(accumulator)
                    }
                    var s = if (minus) {
                        "-" + v.toLong()
                    } else {
                        "" + v.toLong()
                    }
                    var t = ""
                    var p = precision
                    while (p > 1) {
                        val last = s.length - 1
                        t = s.substring(last, last + 1) + t
                        s = s.substring(0, last)
                        if (s.length == 0) s = "0"
                        p /= 10
                    }
                    /*Note::
                        Kotlinには普通の(Cみたいな)for文がないので、white()で代用
                    ::Note*/

                    if (t.length > 0) t = "." + t
                    var cnt = 0
                    while (s.length > 0) {
                        val last = s.length - 1
                        if (cnt == 3) {
                            t = "," + t
                            cnt = 0
                        }
                        t = s.substring(last, last + 1) + t
                        s = s.substring(0, last)
                        cnt++
                    }
                    t
                }
            }
        }
    }

    private fun display(s: String? = null) {
        //  表示する
        val view = getActivity()?.findViewById(R.id.calc_value) as TextView?
        view?.setText(if (s != null) s else valueAsString)
    }

    private fun initListeners() {
        val activity = getActivity()

        activity?.findViewById(R.id.calc_button_0)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(0)
            }
        })
        activity?.findViewById(R.id.calc_button_1)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(1)
            }
        })
        activity?.findViewById(R.id.calc_button_2)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(2)
            }
        })
        activity?.findViewById(R.id.calc_button_3)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(3)
            }
        })
        activity?.findViewById(R.id.calc_button_4)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(4)
            }
        })
        activity?.findViewById(R.id.calc_button_5)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(5)
            }
        })
        activity?.findViewById(R.id.calc_button_6)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(6)
            }
        })
        activity?.findViewById(R.id.calc_button_7)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(7)
            }
        })
        activity?.findViewById(R.id.calc_button_8)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(8)
            }
        })
        activity?.findViewById(R.id.calc_button_9)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                addDigit(9)
            }
        })
        activity?.findViewById(R.id.calc_button_bs)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                delDigit()
            }
        })
        activity?.findViewById(R.id.calc_button_dot)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                if (precision == 0.0) {
                    precision = 1.0
                    display()
                }
            }
        })

        activity?.findViewById(R.id.calc_button_c)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                accumulator = 0.0
            }
        })
        activity?.findViewById(R.id.calc_button_ca)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                accumulator = 0.0
                stack.clear()
            }
        })

        activity?.findViewById(R.id.calc_button_m)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                memoryRegister = accumulator
            }
        })
        activity?.findViewById(R.id.calc_button_m_add)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                if (memoryRegister != null) {
                    memoryRegister = memoryRegister!! + accumulator
                } else {
                    memoryRegister = accumulator
                }
            }
        })
        activity?.findViewById(R.id.calc_button_m_sub)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                if (memoryRegister != null) {
                    memoryRegister = memoryRegister!! - accumulator
                } else {
                    memoryRegister = -accumulator
                }
            }
        })
        activity?.findViewById(R.id.calc_button_mc)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                memoryRegister = null
            }
        })
        activity?.findViewById(R.id.calc_button_mr)?.setOnClickListener(object : OnClickListener{
            public override fun onClick(v: View?) {
                if (memoryRegister != null) {
                    accumulator = memoryRegister!!
                }
            }
        })

        activity?.findViewById(R.id.calc_button_sin)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.sin(a / 180.0 * Math.PI)
        })
        activity?.findViewById(R.id.calc_button_cos)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.cos(a / 180.0 * Math.PI)
        })
        activity?.findViewById(R.id.calc_button_tan)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.tan(a / 180.0 * Math.PI)
        })
        activity?.findViewById(R.id.calc_button_arc_sin)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.asin(a) * 180.0 / Math.PI
        })
        activity?.findViewById(R.id.calc_button_arc_cos)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.acos(a) * 180.0 / Math.PI
        })
        activity?.findViewById(R.id.calc_button_arc_tan)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.atan(a) * 180.0 / Math.PI
        })
        activity?.findViewById(R.id.calc_button_square)?.setOnClickListener(MoListener {
            (a: Double): Double -> a * a
        })
        activity?.findViewById(R.id.calc_button_sqrt)?.setOnClickListener(MoListener {
            (a: Double): Double -> Math.sqrt(a)
        })
        activity?.findViewById(R.id.calc_button_reverse)?.setOnClickListener(MoListener {
            (a: Double): Double -> -a
        })
        activity?.findViewById(R.id.calc_button_recip)?.setOnClickListener(MoListener {
            (a: Double): Double -> 1 / a
        })

        activity?.findViewById(R.id.calc_button_add)?.setOnClickListener(DoListener(Operator.ADD))
        activity?.findViewById(R.id.calc_button_sub)?.setOnClickListener(DoListener(Operator.SUB))
        activity?.findViewById(R.id.calc_button_mul)?.setOnClickListener(DoListener(Operator.MUL))
        activity?.findViewById(R.id.calc_button_div)?.setOnClickListener(DoListener(Operator.DIV))
        activity?.findViewById(R.id.calc_button_ent)?.setOnClickListener(DoListener(Operator.ENT))
    }

    private class MoListener(val f: (Double) -> Double): OnClickListener {
        public override fun onClick(v: View?) {
            accumulator = f(accumulator)
        }
    }

    private class DoListener(val operator: Operator): OnClickListener {
        public override fun onClick(v: View?) {
            stack.push(accumulator, operator)

            if (stack.eval()) {
                accumulator = stack.top().value
            } else {
                entered = true
            }
        }
    }

    enum class Operator {
        ADD
        SUB
        MUL
        DIV
        ENT
    }

    private class StackItem(val value: Double, val operator: Operator)

    private class Stack() {
        val buf = ArrayList<StackItem>()

        fun clear() {
            buf.clear()
        }

        fun push(value: Double, operator: Operator): Boolean {
            return buf.add(StackItem(value, operator))
        }

        fun top(): StackItem {
            val top = buf.last
            if (top != null) {
                return top
            }  else {
                return StackItem(0.0, Operator.ENT)
            }
        }

        fun pop(): StackItem {
            val top = top()
            buf.remove(top)
            return top
        }

        fun eval(): Boolean {
            val v2 = pop()
            val v1 = pop()
            if (v2.operator == Operator.MUL || v2.operator == Operator.DIV) {
                when (v1.operator) {
                    Operator.MUL -> {
                        buf.add(StackItem(v1.value * v2.value, v2.operator))
                        return true
                    }
                    Operator.DIV -> {
                        buf.add(StackItem(v1.value / v2.value, v2.operator))
                        return true
                    }
                    else -> {
                        buf.add(v1)
                        buf.add(v2)
                        return false
                    }
                }
            } else {
                when (v1.operator) {
                    Operator.MUL -> {
                        buf.add(StackItem(v1.value * v2.value, v2.operator))
                        eval()
                        return true
                    }
                    Operator.DIV -> {
                        buf.add(StackItem(v1.value / v2.value, v2.operator))
                        eval()
                        return true
                    }
                    Operator.ADD -> {
                        buf.add(StackItem(v1.value + v2.value, v2.operator))
                        eval()
                        return true
                    }
                    Operator.SUB -> {
                        buf.add(StackItem(v1.value - v2.value, v2.operator))
                        eval()
                        return true
                    }
                    else -> {
                        buf.add(v2)
                        return false
                    }
                }
            }
        }
    }
    private val stack = Stack()
}
