package com.nevmem.moneysaver.app.data.util

sealed class ParseResult
data class ParseError(var reason: String) : ParseResult()
data class ParsedValue<ParsedType>(var parsed: ParsedType) : ParseResult()
