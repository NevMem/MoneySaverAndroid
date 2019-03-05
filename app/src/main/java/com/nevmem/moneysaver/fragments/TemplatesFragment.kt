package com.nevmem.moneysaver.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Template
import io.reactivex.disposables.Disposable
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
        var templateView = inflater.inflate(R.layout.template, templatesAnchor, false)
        with (templateView) {
            templateName.text = template.name
            templateValue.text = template.value.toString()
            templateWallet.text = template.value.toString()
            useIt.setOnClickListener {
                app.useTemplate(app.templates.getTemplate(index))
            }
            if (template.sending) {
                useIt.visibility = View.GONE
                templateSuccess.visibility = View.GONE
                templateSending.visibility = View.VISIBLE
            } else {
                templateSending.visibility = View.GONE
                if (!template.success) {
                    templateSuccess.visibility = View.GONE
                    useIt.visibility = View.VISIBLE
                } else {
                    useIt.visibility = View.GONE
                    templateSuccess.visibility = View.VISIBLE
                }
            }
        }
        return templateView
    }

    private fun showTemplates(templates: ArrayList<Template>) {
        clearTemplates()
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (index in 0 until(templates.size)) {
            templatesAnchor.addView(createTemplate(templates[index], index, inflater))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        templatesFlow = app.templatesFlow.subscribe { templates -> run {
            i("templates flow", "received ${templates.ready}")

            if (templates.ready) {
                templatesLoading.visibility = View.GONE
                showTemplates(templates.templates)
            } else {
                clearTemplates()
                templatesLoading.visibility = View.VISIBLE
            }
        } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        templatesFlow.dispose()
    }
}