package com.nevmem.moneysaver.data

import java.util.ArrayList

class Template {
    var name: String
    var value: Double
    var tag: String
    var wallet: String
    var id: String

    var sending = false
    var success = false
    var error: String? = null

    constructor(fromName: String, fromValue: Double, fromTag: String, fromWallet: String, fromID: String) {
        name = fromName
        value = fromValue
        tag = fromTag
        wallet = fromWallet
        id = fromID
    }
}
