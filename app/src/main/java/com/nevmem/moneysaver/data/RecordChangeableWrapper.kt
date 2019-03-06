package com.nevmem.moneysaver.data

import java.util.*

class RecordChangeableWrapper(fromRecord: Record) {
    var record: Record
    var loading = false
    var error = ""
    var success = ""

    var deleted = false

    init {
        record = fromRecord
    }
}