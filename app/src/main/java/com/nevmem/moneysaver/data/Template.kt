package com.nevmem.moneysaver.data

import java.util.ArrayList

class Template {
    var name: String
    var value: Double
    var tags: ArrayList<String>
    var wallet: String
    var id: String

    var sending = false
    var success = false
    var error = ""

    constructor(fromName: String, fromValue: Double, fromTags: ArrayList<String>, fromWallet: String, fromID: String) {
        name = fromName
        value = fromValue
        tags = fromTags
        wallet = fromWallet
        id = fromID
    }
}
