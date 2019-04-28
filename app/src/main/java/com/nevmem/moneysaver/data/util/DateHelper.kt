package com.nevmem.moneysaver.data.util

abstract class DateHelper {
    companion object {
        fun fillTo2Length(current: String): String = current.padStart(2, '0')

        fun isLeapYear(year: Int): Boolean {
            return (year % 4) == 0 && (year % 100 != 0 || year % 400 == 0)
        }

        private fun getMonthNumber(month: String): Int {
            when (month) {
                "January" -> return 1
                "February" -> return 2
                "March" -> return 3
                "April" -> return 4
                "May" -> return 5
                "June" -> return 6
                "July" -> return 7
                "August" -> return 8
                "September" -> return 9
                "October" -> return 10
                "November" -> return 11
                "December" -> return 12
            }
            return 13
        }

        fun getAmountOfDaysInMonth(year: Int, month: String): Int {
            var leap = 0
            if (isLeapYear(year))
                leap = 1
            when (month) {
                "January", "March", "May", "July", "August", "October", "December" -> return 31
                "February" -> return 28 + leap
            }
            return 30
        }

        fun getMonthName(index: Int): String {
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