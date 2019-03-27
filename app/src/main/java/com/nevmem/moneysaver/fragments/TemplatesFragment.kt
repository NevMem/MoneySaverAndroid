package com.nevmem.moneysaver.fragments

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Template
import com.nevmem.moneysaver.data.TemplatesRepository
import com.nevmem.moneysaver.views.ConfirmationDialog
import com.nevmem.moneysaver.views.InfoDialog
import com.nevmem.moneysaver.views.NewTemplateDialog
import kotlinx.android.synthetic.main.add_template_view.*
import kotlinx.android.synthetic.main.template.view.*
import kotlinx.android.synthetic.main.templates_page_fragment.*
import javax.inject.Inject

class TemplatesFragment: Fragment() {
    @Inject
    lateinit var templatesRepo: TemplatesRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.templates_page_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainPage).appComponent.inject(this)
    }

    private fun clearTemplates() {
        templatesAnchor.removeAllViews()
    }

    private fun createTemplate(template: Template, index: Int, inflater: LayoutInflater): View {
        val templateView = inflater.inflate(R.layout.template, templatesAnchor, false)
        with (templateView) {
            templateName.text = template.name
            templateValue.text = template.value.toString()
            templateWallet.text = template.wallet
            useIt.setOnClickListener {
                templatesRepo.useTemplate(index)
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
            setOnLongClickListener {
                val popupView = ConfirmationDialog(activity!!, "Do you really want delete this template?")
                val popupWindow = PopupWindow(popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                    true)
                popupWindow.showAtLocation(templatesHeader, Gravity.CENTER, 0, 0)
                popupView.setDismissListener {
                    popupWindow.dismiss()
                }
                popupView.setOkListener {
//                    app.removeTemplate(template.id)
                    popupWindow.dismiss()
                }
                true
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
        else
            nothingToShow.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        templatesRepo.tryUpdate()
        templatesRepo.templates.observe(this, Observer {
            i("TR", "Received some new data")
            if (it != null) {
                clearTemplates()
                showTemplates(it)
            }
        })
        templatesRepo.loading.observe(this, Observer {
            if (it != null) {
                if (it)
                    templatesLoading.visibility = View.VISIBLE
                else
                    templatesLoading.visibility = View.GONE
            }
        })
        addNewTemplateView.visibility = View.VISIBLE
        addNewTemplateView.setOnClickListener {
            newTemplate()
        }
    }

    private fun newTemplate() {
        val popupView = NewTemplateDialog(activity!!)
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)

        popupView.setOkListener {
//            app.createNewTemplate(it)
            popup.dismiss()
        }

        popupView.setDismissListener {
            popup.dismiss()
        }

        popup.showAtLocation(templatesHeader, Gravity.CENTER, 0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}