package com.nevmem.moneysaver.common

class Vars {
    companion object {
        private const val ServerBase = "https://zverevkazan.com"
        const val ServerApiLogin = "$ServerBase/api/login"
        const val ServerApiWallets = "$ServerBase/api/wallets"
        const val ServerApiTags = "$ServerBase/api/tags"
        const val ServerApiHistory = "$ServerBase/api/history"
        const val ServerApiInfo = "$ServerBase/api/info"
        const val ServerApiAdd = "$ServerBase/api/add"
        const val ServerApiEdit = "$ServerBase/api/edit"
        const val ServerApiTemplates = "$ServerBase/api/templates"
        const val ServerApiUseTemplate = "$ServerBase/api/useTemplate"
        const val ServerApiCreateTemplate = "$ServerBase/api/createTemplate"
        const val ServerApiRemoveTemplate = "$ServerBase/api/removeTemplate"
        const val ServerApiDeleteRecord = "$ServerBase/api/remove"
        const val ServerApiCheckLogin = "$ServerBase/api/checkLogin"
        const val ServerApiRegister = "$ServerBase/api/register"
        const val ServerApiAddTag = "$ServerBase/api/addTag"
        const val ServerApiAddWallet = "$ServerBase/api/addWallet"
        const val ServerApiRemoveTag = "$ServerBase/api/removeTag"
        const val ServerApiRemoveWallet = "$ServerBase/api/removeWallet"

        const val ServerPrivacy = "$ServerBase/privacy"

        /* Special constants */
        const val unknownFormat = "Server response has unknown format"
        const val unspecifiedData = "Server response has bad format(data array is missing)"
        const val corruptedRecord = "One or more record were corrupted"
    }
}
