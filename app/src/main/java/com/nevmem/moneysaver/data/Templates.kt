package com.nevmem.moneysaver.data

class Templates {
    var ready: Boolean = false
    var templates: ArrayList<Template> = ArrayList()

    fun clear() {
        templates.clear()
    }

    fun add(template: Template) {
        templates.add(template)
    }

    fun getTemplate(index: Int): Template {
        return templates[index]
    }
}