package com.nevmem.moneysaver

public class Vars {
    companion object {
        private const val ServerBase = "http://104.236.71.129"
        const val ServerApiLogin = "$ServerBase/api/login"
        const val ServerApiWallets = "$ServerBase/api/wallets"
        const val ServerApiTags= "$ServerBase/api/tags"
        const val ServerApiData= "$ServerBase/api/data"
        const val ServerApiInfo= "$ServerBase/api/info"
        const val ServerApiAdd = "$ServerBase/api/add"
        const val ServerApiEdit = "$ServerBase/api/edit"
        const val ServerApiTemplates = "$ServerBase/api/templates"
        const val ServerApiUseTemplate = "$ServerBase/api/useTemplate"
        const val ServerApiCreateTemplate = "$ServerBase/api/createTemplate"
        const val ServerApiRemoveTemplate = "$ServerBase/api/removeTemplate"
        const val ServerApiDeleteRecord = "$ServerBase/api/remove"
    }
}
