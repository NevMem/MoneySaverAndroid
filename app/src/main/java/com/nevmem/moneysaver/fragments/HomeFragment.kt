package com.nevmem.moneysaver.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.HomePageActivityViewModel
import com.nevmem.moneysaver.R
import java.lang.NullPointerException

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_page_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val app = activity!!.applicationContext as App
//            activity?.let {
//                ViewModelProviders.of(it)
//            }
        } catch (_: KotlinNullPointerException) {
            System.out.println("Kotlin Null Pointer Exceptions")
        }
    }
}
