package a.second.kotlin.story.games

import a.second.kotlin.story.R
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.security.Key

class Hangman {

    class KeyboardRecyclerAdapter(context: Context, layout: Int, letter: Int, list: List<String>) : ArrayAdapter<String>(context, layout, letter, list) {

//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//            var letterView = View(context)
//
//            if (convertView == null) {
//                val inflater = LayoutInflater.from(parent.context)
//                letterView = inflater.inflate(R.layout.hangman_keyboard_adapter_views, parent,false)
//                val letterTextView : TextView = letterView.findViewById(R.id.hangman_letter)
//                letterTextView.text = "BOO"
//            }
//
//            return letterView
//        }
//
//        override fun getItem(position: Int): String {
//            return alphabetStringArray()[position]
//        }
//
//        override fun getCount(): Int {
//            return alphabetStringArray().size
//        }
//
//        fun alphabetStringArray(): List<String>{
//            val alphabet =
//                "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z"
//            return alphabet.split(", ")
//        }

    }

    fun alphabetStringArray(): List<String>{
        val alphabet =
            "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z"
        return alphabet.split(", ")
    }

    class GallowsCanvas(context: Context?, attrs: AttributeSet): View(context, attrs) {
        var mCanvas : Canvas = Canvas()
        var mPaint : Paint = Paint()
        var progress = 0

        fun setPaintColor() {
            mPaint.color = Color.BLACK
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = (pxToDp(6))
        }

        fun drawGallows() {
            setPaintColor()

            val xPosStart = 125
            val xPosEnd = 230
            val topY = 20
            val bottomY = 180

            mCanvas.drawLine(pxToDp(xPosStart), pxToDp(topY), pxToDp(xPosEnd), pxToDp(topY), mPaint)

            mCanvas.drawLine(pxToDp(xPosStart),
                pxToDp(topY),
                pxToDp(xPosStart),
                pxToDp(topY + 30),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd - 30),
                pxToDp(topY),
                pxToDp(xPosEnd),
                pxToDp(topY + 30),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd),
                pxToDp(topY),
                pxToDp(xPosEnd),
                pxToDp(bottomY),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd - 50),
                pxToDp(bottomY),
                pxToDp(xPosEnd + 50),
                pxToDp(bottomY),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd),
                pxToDp(bottomY - 30),
                pxToDp(xPosEnd - 30),
                pxToDp(bottomY),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd),
                pxToDp(bottomY - 30),
                pxToDp(xPosEnd + 30),
                pxToDp(bottomY),
                mPaint)
        }

        fun drawHangman() {
            var xPositionStart = 125
            var topYPosition = 165
            var bottomYPosition = 265

            if (progress > 0) {

            }
        }

        override fun onDraw(canvas: Canvas) {
            mCanvas = canvas
            drawGallows()
        }


        fun pxToDp(pixels : Int) : Float {
            return pixels * (Resources.getSystem().displayMetrics.density).toFloat()
        }
    }

    class AlphabetConversions{
        fun convertPositionToLetter(position: Int): String {
            var letter = ""
            if (position == 0) letter = "A"
            if (position == 1) letter = "B"
            if (position == 2) letter = "C"
            if (position == 3) letter = "D"
            if (position == 4) letter = "E"
            if (position == 5) letter = "F"
            if (position == 6) letter = "G"
            if (position == 7) letter = "H"
            if (position == 8) letter = "I"
            if (position == 9) letter = "J"
            if (position == 10) letter = "K"
            if (position == 11) letter = "L"
            if (position == 12) letter = "M"
            if (position == 13) letter = "N"
            if (position == 14) letter = "O"
            if (position == 15) letter = "P"
            if (position == 16) letter = "Q"
            if (position == 17) letter = "R"
            if (position == 18) letter = "S"
            if (position == 19) letter = "T"
            if (position == 20) letter = "U"
            if (position == 21) letter = "V"
            if (position == 22) letter = "W"
            if (position == 23) letter = "X"
            if (position == 24) letter = "Y"
            if (position == 25) letter = "Z"
            return letter
        }
    }

}