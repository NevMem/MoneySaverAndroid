package com.nevmem.moneysaver.app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.MainPage
import com.nevmem.moneysaver.app.data.Template
import com.nevmem.moneysaver.app.data.repositories.TemplatesRepository
import com.nevmem.moneysaver.app.views.ConfirmationDialog
import com.nevmem.moneysaver.app.views.InfoDialog
import com.nevmem.moneysaver.app.views.NewTemplateDialog
import kotlinx.android.synthetic.main.add_template_view.*
import kotlinx.android.synthetic.main.template.view.*
import kotlinx.android.synthetic.main.templates_page_fragment.*
import javax.inject.Inject

class TemplatesFragment : Fragment() {
    @Inject
    lateinit var templatesRepo: TemplatesRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.templates_page_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app: App = (activity as MainPage).applicationContext as App
        app.appComponent.inject(this)
    }

    private fun clearTemplates() {
        templatesAnchor.removeAllViews()
    }

    private fun createTemplate(template: Template, index: Int, inflater: LayoutInflater): View {
        val templateView = inflater.inflate(R.layout.template, templatesAnchor, false)
        with(templateView) {
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
            when {
                template.sending -> templateSending.visibility = View.VISIBLE
                template.success -> templateSuccess.visibility = View.VISIBLE
                template.error != null -> {
                    templateError.visibility = View.VISIBLE

                    templateError.setOnClickListener {
                        showError(template.error.toString())
                    }
                }
                else -> useIt.visibility = View.VISIBLE
            }
            setOnLongClickListener {
                val dialog = ConfirmationDialog("Confirm", "Do you really want to delete this template?")
                dialog.setOkListener {
                    templatesRepo.removeTemplate(template.id)
                }
                dialog.show(activity!!.supportFragmentManager, "delete_confirmation")
                true
            }
        }
        return templateView
    }

    private fun showTemplates(templates: ArrayList<Template>) {
        clearTemplates()
        nothingToShow.visibility = View.GONE
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (index in 0 until (templates.size)) {
            templatesAnchor.addView(createTemplate(templates[index], index, inflater))
        }
        if (templates.size == 0)
            nothingToShow.visibility = View.VISIBLE
        else
            nothingToShow.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh.setColorSchemeColors(
            ContextCompat.getColor(context!!, R.color.themeColor),
            ContextCompat.getColor(context!!, R.color.specialColor),
            ContextCompat.getColor(context!!, R.color.errorColor)
        )
        refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context!!, R.color.backgroundColor))
        refresh.setOnRefreshListener {
            templatesRepo.tryUpdate()
        }
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
                refresh.isRefreshing = it
            }
        })
        addNewTemplateView.visibility = View.VISIBLE
        addNewTemplateView.setOnClickListener {
            newTemplate()
        }
    }

    private fun showError(message: String) {
        val dialog = InfoDialog("Error happened", message)
        dialog.show(activity!!.supportFragmentManager, "template_error")
    }

    private fun newTemplate() {
        val dialog = NewTemplateDialog()
        dialog.setOkListener {
            templatesRepo.createNewTemplate(it)
        }
        dialog.setErrorListener {
            showError(it)
        }
        dialog.show(activity!!.supportFragmentManager, "new_template_dialog")
    }
}