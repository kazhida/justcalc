package com.abplus.justcalc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.FrameLayout
import com.google.ads.AdView
import com.google.ads.AdSize
import com.google.ads.AdRequest


/**
 * User: kazhida
 * Date: 2012/08/15
 * Time: 11:53
 */
class MainActivity(): FragmentActivity() {
    var calcFragment: CalcFragment? = null
    var adView: AdView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //  電卓フラグメントの初期化
        calcFragment = getSupportFragmentManager()?.findFragmentById(R.id.calc) as CalcFragment?
//        calcFragment?.hideMemoryRow()
//        calcFragment?.hideFunctionRow()
        adView = AdView(this, AdSize.BANNER, "a150d0490606375")

        val layout = findViewById(R.id.banner_area) as FrameLayout
        layout.addView(adView)

        adView?.loadAd(AdRequest());
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater()?.inflate(R.menu.options, menu)
        if (calcFragment != null) {
            menu?.findItem(R.id.calc_function_key_visible)?.setChecked(calcFragment!!.functionRowVisible())
            menu?.findItem(R.id.calc_memory_key_visible)?.setChecked(calcFragment!!.memoryRowVisible())
        }
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