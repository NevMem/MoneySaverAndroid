package com.nevmem.moneysaver.app.data

class TemplateBase(fromName: String, fromValue: Double, fromTag: String, fromWallet: String) {
    var name: String = fromName
    var value: Double = fromValue
    var tag: String = fromTag
    var wallet: String = fromWallet
}