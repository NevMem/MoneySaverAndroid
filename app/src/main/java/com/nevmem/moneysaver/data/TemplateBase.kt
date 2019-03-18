package com.nevmem.moneysaver.data

import android.util.Log.i
import java.util.ArrayList

class TemplateBase {
    var name: String
    var value: Double
    var tag: String
    var wallet: String

    constructor(fromName: String, fromValue: Double, fromTag: String, fromWallet: String) {
        name = fromName
        value = fromValue
        tag = fromTag
        wallet = fromWallet
    }
}