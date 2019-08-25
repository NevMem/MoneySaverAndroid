package com.nevmem.moneysaver.utils

abstract class TypeUtils {
    companion object {
        fun formatDouble(double: Double?): String {
            if (double != null)
                return String.format("%.2f", double)
            return "null"
        }
    }
}