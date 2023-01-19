package a.second.kotlin.story.gameFragments.gameData

import a.second.kotlin.story.R
import android.content.Context
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

class Hangman {
    class KeyboardGridViewAdapter(context: Context, layout: Int, letter: Int, list: List<String>) : ArrayAdapter<String>(context, layout, letter, list) {
    }

    class PuzzleRecyclerAdapter(alphabetList: ArrayList<String>) : RecyclerView.Adapter<PuzzleRecyclerAdapter.LetterHolder>() {
        val alphabetArray : ArrayList<String> = alphabetList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val letterItem = LayoutInflater.from(parent.context).inflate(R.layout.hangman_puzzle_adapter_view, parent, false)
            return LetterHolder(letterItem)
        }

        override fun onBindViewHolder(holder: LetterHolder, position: Int) {
            holder.letterView.text = alphabetArray[position]
        }

        override fun getItemCount(): Int {
            Log.i("testHang", "alphabetArray size return is ${alphabetArray.size}")
            return alphabetArray.size
        }

        class LetterHolder(itemView : View) : ViewHolder(itemView) {
            val letterView : TextView = itemView.findViewById(R.id.hangman_puzzle_letter)
        }
    }

    fun alphabetStringArray(): List<String>{
        val alphabet =
            "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z"
        return alphabet.split(", ")
    }

    class GallowsCanvas(context: Context?, attrs: AttributeSet?): View(context, attrs) {
        var mCanvas : Canvas = Canvas()
        var mPaint : Paint = Paint()

        val mPaintText : Paint = Paint()
        var progress = 0

        override fun onDraw(canvas: Canvas) {
            mCanvas = canvas
            drawHangMan()
            drawGallows()
        }

        fun reDrawCanvas() {
            invalidate()
        }

        fun setPaintColor() {
            mPaint.color = Color.BLACK
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = (pxToDp(6))
        }

        fun setPaintTextColor() {
            mPaintText.textSize = pxToDp(23)
            mPaintText.color = Color.BLACK
        }

        fun iterateProgress() { progress++ }
        fun resetProgress() { progress = 0 }

        fun drawGallows() {
            setPaintColor()

            val xPosStart = 125
            val xPosEnd = 230
            val topY = 10
            val bottomY = 140

            mCanvas.drawLine(pxToDp(xPosStart), pxToDp(topY), pxToDp(xPosEnd), pxToDp(topY), mPaint)

            //Top left -> right line and noose
            mCanvas.drawLine(pxToDp(xPosStart),
                pxToDp(topY),
                pxToDp(xPosStart),
                pxToDp(topY + 10),
                mPaint)
            mCanvas.drawLine(pxToDp(xPosEnd - 30),
                pxToDp(topY),
                pxToDp(xPosEnd),
                pxToDp(topY + 30),
                mPaint)
            //Top right -> bottom right line.
            mCanvas.drawLine(pxToDp(xPosEnd),
                pxToDp(topY),
                pxToDp(xPosEnd),
                pxToDp(bottomY),
                mPaint)
            //Bottom horizontal line
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

        fun drawHangMan() {
            val xPosStart = 125
            val topY = 45
            val bottomY = 120

            if (progress > 0) {
                mCanvas.drawCircle(pxToDp(xPosStart), pxToDp(topY), pxToDp(25), mPaint)
                if (progress > 6) {
                    mPaintText.setTextSize(pxToDp(15))
                    mCanvas.drawText("x", pxToDp(xPosStart - 10), pxToDp(topY - 10), mPaintText)
                    mCanvas.drawText("x", pxToDp(xPosStart + 3), pxToDp(topY - 10), mPaintText)

                    mPaint.style = Paint.Style.FILL
                    mCanvas.drawCircle(pxToDp(xPosStart), pxToDp(topY), pxToDp(3), mPaint)
                    mPaint.style = Paint.Style.STROKE
                    mCanvas.drawLine(pxToDp(xPosStart),
                        pxToDp(topY + 9),
                        pxToDp(xPosStart + 5),
                        pxToDp(topY + 18),
                        mPaint)
                    mCanvas.drawLine(pxToDp(xPosStart),
                        pxToDp(topY + 9),
                        pxToDp(xPosStart - 5),
                        pxToDp(topY + 18),
                        mPaint)
                }
            }
            if (progress > 1) {
                mCanvas.drawLine(pxToDp(xPosStart),
                    pxToDp(topY + 25),
                    pxToDp(xPosStart),
                    pxToDp(bottomY),
                    mPaint)
            }
            if (progress > 2) {
                mCanvas.drawLine(pxToDp(xPosStart),
                    pxToDp(topY + 60),
                    pxToDp(xPosStart - 30),
                    pxToDp(topY + 30),
                    mPaint)
            }
            if (progress > 3) {
                mCanvas.drawLine(pxToDp(xPosStart),
                    pxToDp(topY + 60),
                    pxToDp(xPosStart + 30),
                    pxToDp(topY + 30),
                    mPaint)
            }
            if (progress > 4) {
                mCanvas.drawLine(pxToDp(xPosStart),
                    pxToDp(bottomY),
                    pxToDp(xPosStart - 30),
                    pxToDp(bottomY + 30),
                    mPaint)
            }
            if (progress > 5) {
                mCanvas.drawLine(pxToDp(xPosStart),
                    pxToDp(bottomY),
                    pxToDp(xPosStart + 30),
                    pxToDp(bottomY + 30),
                    mPaint)
            }
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