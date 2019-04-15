package com.nevmem.moneysaver.data.util

sealed class ParseResult
data class ParseError(var reason: String): ParseResult()
data class ParsedValue<ParsedType>(var parsed: ParsedType) : ParseResult()
