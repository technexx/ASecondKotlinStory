package a.second.kotlin.story

open class Games {

    class Math : Games() {
        var answer = (0..100).random()

        var inputOne : Int = 0
        var inputTwo : Int = 0

        fun assignAdditionInputs() {
            inputOne = (0..answer).random()
            inputTwo = answer - inputOne
        }

        fun assignSubtractionInputs() {
            //inputOne - inputTwo = answer
            inputOne = (answer..100).random()
            inputTwo = inputOne - answer
        }

        fun assignMultiplicationInputs() {
            answer = (2..500).random()

            while (inputOne < answer) inputOne = (2..25).random()

            val maxSecondInput = answer/inputOne
            inputTwo = (2..maxSecondInput).random()
        }

        fun assignDivisionInputs() {
            //inputTwo / inputOne = answer
            answer = (2..500).random()

            while (inputOne < answer) inputOne = (2..25).random()
            val numberArray = ArrayList<Int>()

            for (i in 2..answer) {
                if (i % inputOne == 0) numberArray.add(i)
            }

            inputTwo = numberArray.random()
        }
    }

    class Hangman : Games() {

    }
}