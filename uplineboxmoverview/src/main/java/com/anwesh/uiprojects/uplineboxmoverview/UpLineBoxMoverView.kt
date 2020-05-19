package com.anwesh.uiprojects.uplineboxmoverview

/**
 * Created by anweshmishra on 20/05/20.
 */

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Canvas
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val parts : Int = 3
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val delay : Long = 20
val scGap : Float = 0.02f / parts
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawUpLineBoxMover(scale : Float, size : Float, w : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    save()
    translate(size / 2 + (w / 2 - size) * sf2, 0f)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    save()
    translate(0f, -size * 0.5f * sf2)
    drawLine(-size / 2, 0f, size / 2, 0f, paint)
    restore()
    restore()
}

fun Canvas.drawULBMNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(0f, gap * (i + 1))
    drawUpLineBoxMover(scale, size, w, paint)
    restore()
}

class UpLineBoxMoverView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * dir
                cb()
            }
        }
    }
}