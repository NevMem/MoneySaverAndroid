package com.nevmem.moneysaver.utils

abstract class TypeUtils {
    companion object {
        fun formatDouble(double: Double?): String {
            if (double != null)
                return String.format("%.2f", double)
            return "null"
        }

        fun formatFloat(float: Float?): String {
            if (float != null)
                return String.format("%.2f", float)
            return "null"
        }
    }
}