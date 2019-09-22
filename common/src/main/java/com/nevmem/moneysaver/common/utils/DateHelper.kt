package com.nevmem.moneysaver.common.utils

import com.nevmem.moneysaver.common.data.RecordDate

abstract class DateHelper {
    companion object {
        private fun fillTo2Length(current: String): String = current.padStart(2, '0')

        fun fillTo2Length(current: Int): String =
            fillTo2Length(current.toString())

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
            return getAmountOfDaysInMonth(
                year,
                getMonthName(month)
            )
        }

        /**
         *  Finds a day before date, in such way as between result and date inclusively exactly amount days
         */
        fun dayBefore(date: RecordDate, amount: Int): RecordDate {
            val result = RecordDate(date)
            result.hour = 0
            result.minute = 0
            for (i in 0 until(amount - 1)) {
                if (result.day == 1) {
                    if (result.month == 1) {
                        result.month = 12
                        result.year -= 1
                    } else {
                        result.month -= 1
                    }
                    result.day =
                        Companion.getAmountOfDaysInMonth(
                            result.year,
                            result.month
                        )
                } else {
                    result.day -= 1
                }
            }
            return result
        }

        private fun sameDay(first: RecordDate, second: RecordDate): Boolean {
            return first.year == second.year && first.month == second.month
                && first.day == second.day
        }

        fun amountOfDaysBetween(first: RecordDate?, second: RecordDate?): Int {
            if (first == null || second == null)
                return 0
            if (first > second)
                return amountOfDaysBetween(
                    second,
                    first
                )
            val runner = RecordDate(first)
            var amount = 1
            while (!sameDay(runner, second)) {
                runner.nextDay(true)
                amount += 1
            }
            return amount
        }
    }
}