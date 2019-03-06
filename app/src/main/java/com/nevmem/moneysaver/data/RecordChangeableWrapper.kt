package com.nevmem.moneysaver.data

class RecordChangeableWrapper(fromRecord: Record) {
    var record: Record
    var updating = false
    var error = ""
    var success = false
    var deleted = false

    init {
        record = fromRecord
    }
}