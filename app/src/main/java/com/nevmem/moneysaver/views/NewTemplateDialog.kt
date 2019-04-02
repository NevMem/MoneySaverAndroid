package com.nevmem.moneysaver.views

import android.content.Context
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.TemplateBase
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.data.repositories.WalletsRepository
import kotlinx.android.synthetic.main.new_template_dialog.view.*
import javax.inject.Inject

class NewTemplateDialog(ctx: Context) : ConstraintLayout(ctx) {
    lateinit var okCallback: (TemplateBase) -> Unit
    lateinit var dismissCallback: () -> Unit

    @Inject
    lateinit var walletsRepo: WalletsRepository
    @Inject
    lateinit var tagsRepo: TagsRepository

    init {
        inflate(ctx, R.layout.new_template_dialog, this)

        val app = ctx.applicationContext as App
        app.appComponent.inject(this)

        newTemplateTag.adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, tagsRepo.getTagsAsList())
        newTemplateWallet.adapter =
            ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, walletsRepo.getWalletsAsList())

        okButton.setOnClickListener {
            var value = 0.0
            try {
                value = newTemplateValue.text.toString().toDouble()
            } catch (e: NumberFormatException) {
            }
            okCallback(
                TemplateBase(
                    newTemplateName.text.toString(),
                    value,
                    newTemplateTag.selectedItem.toString(),
                    newTemplateWallet.selectedItem.toString()
                )
            )
        }

        dismissButton.setOnClickListener {
            dismissCallback()
        }
    }

    fun setOkListener(cb: (TemplateBase) -> Unit) {
        okCallback = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissCallback = cb
    }
}