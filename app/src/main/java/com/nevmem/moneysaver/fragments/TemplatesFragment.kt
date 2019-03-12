package com.nevmem.moneysaver.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Template
import com.nevmem.moneysaver.views.ConfirmationDialog
import com.nevmem.moneysaver.views.InfoDialog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.add_template_view.*
import kotlinx.android.synthetic.main.history_layout.*
import kotlinx.android.synthetic.main.history_layout.view.*
import kotlinx.android.synthetic.main.template.view.*
import kotlinx.android.synthetic.main.templates_page_fragment.*

class TemplatesFragment: Fragment() {
    lateinit var templatesFlow: Disposable
    lateinit var app: App

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.templates_page_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity!!.applicationContext as App
        app.loadTemplates()
    }

    private fun clearTemplates() {
        templatesAnchor.removeAllViews()
    }

    private fun createTemplate(template: Template, index: Int, inflater: LayoutInflater): View {
        val templateView = inflater.inflate(R.layout.template, templatesAnchor, false)
        with (templateView) {
            templateName.text = template.name
            templateValue.text = template.value.toString()
            templateWallet.text = template.value.toString()
            useIt.setOnClickListener {
                app.useTemplate(app.templates.getTemplate(index))
            }
            useIt.visibility = View.GONE
            templateSuccess.visibility = View.GONE
            templateSending.visibility = View.GONE
            templateError.visibility = View.GONE
            if (template.sending) {
                templateSending.visibility = View.VISIBLE
            } else if (template.success) {
                templateSuccess.visibility = View.VISIBLE
            } else if (template.error != null) {
                templateError.visibility = View.VISIBLE

                templateError.setOnClickListener {
                    val popupView = InfoDialog(activity!!, template.error.toString())
                    val popup = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    popup.showAtLocation(templatesHeader, Gravity.CENTER, 0, 0)
                    popupView.setOkListener {
                        popup.dismiss()
                    }
                }
            } else {
                useIt.visibility = View.VISIBLE
            }
        }
        return templateView
    }

    private fun showTemplates(templates: ArrayList<Template>) {
        clearTemplates()
        nothingToShow.visibility = View.GONE
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (index in 0 until(templates.size)) {
            templatesAnchor.addView(createTemplate(templates[index], index, inflater))
        }
        if (templates.size == 0)
            nothingToShow.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        templatesFlow = app.templatesFlow.subscribe { templates -> run {
            i("templates flow", "received ${templates.ready}")
            addNewTemplateView.visibility = View.GONE

            if (templates.ready) {
                templatesLoading.visibility = View.GONE
                showTemplates(templates.templates)
                addNewTemplateView.visibility = View.VISIBLE
            } else {
                clearTemplates()
                templatesLoading.visibility = View.VISIBLE
            }
        } }

        addNewTemplateView.setOnClickListener {
            Toast.makeText(activity, "This method doesn't implemented", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        templatesFlow.dispose()
    }
}