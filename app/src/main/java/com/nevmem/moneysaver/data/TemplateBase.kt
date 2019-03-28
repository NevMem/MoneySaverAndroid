package com.nevmem.moneysaver.data

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