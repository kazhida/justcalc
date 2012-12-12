package com.abplus.justcalc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.app.Activity
import android.app.FragmentManager;

/**
 * User: kazhida
 * Date: 2012/08/15
 * Time: 11:53
 */
class MainActivity(): Activity() {
    var calcFragment: CalcFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //  電卓フラグメントの初期化
        calcFragment = getFragmentManager()?.findFragmentById(R.id.calc) as CalcFragment?
//        calcFragment?.hideMemoryRow()
//        calcFragment?.hideFunctionRow()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater()?.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.calc_function_key_visible -> {
                if (item != null) {
                    if (item.isChecked()) {
                        item.setChecked(false)
                        calcFragment?.hideFunctionRow()
                    } else {
                        item.setChecked(true)
                        calcFragment?.showFunctionRow()
                    }
                }
                return true
            }
            R.id.calc_memory_key_visible -> {
                if (item != null) {
                    if (item.isChecked()) {
                        item.setChecked(false)
                        calcFragment?.hideMemoryRow()
                    } else {
                        item.setChecked(true)
                        calcFragment?.showMemoryRow()
                    }
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}