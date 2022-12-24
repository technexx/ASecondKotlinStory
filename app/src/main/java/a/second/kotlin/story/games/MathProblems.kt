package a.second.kotlin.story.games

import android.util.Log

class MathProblems {
    var inputOne : Int = 0
    var inputTwo : Int = 0
    var answer = (0..100).random()

    var TYPE_OF_PROBLEM : Int = 0
    var ADD_PROBLEM : Int = 0
    var SUB_PROBLEM : Int = 1
    var MULT_PROBLEM : Int = 2
    var DIV_PROBLEM : Int = 3

    fun selectTypeOfProblem() : Int{
        return (0..3).random()
    }

    fun assignAdditionInputs() {
        inputOne = (0..answer).random()
        inputTwo = answer - inputOne

        Log.i("testAdd", "answer for addition is $answer")
        Log.i("testAdd", "first input for addition is $inputOne")
        Log.i("testAdd", "second input for addition is $inputTwo")
    }

    fun assignSubtractionInputs() {
        //inputOne - inputTwo = answer
        inputOne = (answer..100).random()
        inputTwo = inputOne - answer

        Log.i("testSub", "answer for subtraction is $answer")
        Log.i("testSub", "first input for subtraction is $inputOne")
        Log.i("testSub", "second input for subtraction is $inputTwo")
    }

    fun assignMultiplicationInputs() {
        inputOne = (2..25).random()
        inputTwo = (2..25).random()
        answer = inputOne * inputTwo

        Log.i("testMult", "answer for multiplication is $answer")
        Log.i("testMult", "first input for multiplication is $inputOne")
        Log.i("testMult", "second input for multiplication is $inputTwo")
    }

    fun assignDivisionInputs() {
        val tempInputOne = (2..25).random()

        inputTwo = (2..25).random()
        answer = tempInputOne * inputTwo
        inputTwo - (answer % tempInputOne)

        inputOne = tempInputOne

        Log.i("testDiv", "answer for division is $answer")
        Log.i("testDiv", "first input for division is $inputOne")
        Log.i("testDiv", "second input for division is $inputTwo")
    }
}