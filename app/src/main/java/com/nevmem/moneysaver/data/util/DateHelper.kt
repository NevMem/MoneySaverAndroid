package com.nevmem.moneysaver.data.util

abstract class DateHelper {
    companion object {
        private fun fillTo2Length(current: String): String = current.padStart(2, '0')

        fun fillTo2Length(current: Int): String = fillTo2Length(current.toString())

        private fun isLeapYear(year: Int): Boolean {
            return (year % 4) == 0 && (year % 100 != 0 || year % 400 == 0)
        }

        private fun getAmountOfDaysInMonth(year: Int, month: String): Int {
            var leap = 0
            if (isLeapYear(year))
                leap = 1
            when (month) {
                "January", "March", "May", "July", "August", "October", "December" -> return 31
                "February" -> return 28 + leap
            }
            return 30
        }

        private fun getMonthName(index: Int): String {
            when (index - 1) {
                0 -> return "January"
                1 -> return "February"
                2 -> return "March"
                3 -> return "April"
                4 -> return "May"
                5 -> return "June"
                6 -> return "July"
                7 -> return "August"
                8 -> return "September"
                9 -> return "October"
                10 -> return "November"
                11 -> return "December"
            }
            return "Unknown"
        }

        fun getAmountOfDaysInMonth(year: Int, month: Int): Int {
            return getAmountOfDaysInMonth(year, getMonthName(month))
        }
    }
}