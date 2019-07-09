package com.nevmem.moneysaver.data.util

abstract class DataHelper {
    companion object {
        /**
         * Checks if suspect is a sub sequence of array
         * returns null if NOT
         * returns sorted ArrayList of Ints which to remove to make array be equals to suspect
         */
        fun <T>isSubSequence(array: ArrayList<T>, suspect: ArrayList<T>): ArrayList<Int>? {
            var top = 0
            val indices = ArrayList<Int>()
            for (i in 0 until array.size) {
                if (top < suspect.size && array[i] == suspect[top]) {
                    top += 1
                } else {
                    indices.add(i)
                }
            }
            if (top != suspect.size) return null
            return indices
        }
    }
}