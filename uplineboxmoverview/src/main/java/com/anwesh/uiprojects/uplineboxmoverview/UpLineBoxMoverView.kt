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
    val lSize : Float = size - paint.strokeWidth / 2
    save()
    translate(size / 2 + (w - size) * sf2, 0f)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    save()
    translate(0f, -size * 0.5f -size * 0.5f * sf1)
    drawLine(-lSize / 2, 0f, lSize / 2, 0f, paint)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class ULBMNode(var i : Int, val state : State = State()) {

        private var next : ULBMNode? = null
        private var prev : ULBMNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = ULBMNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawULBMNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : ULBMNode {
            var curr : ULBMNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class UpLineBoxMover(var i : Int) {

        private val root : ULBMNode = ULBMNode(0)
        private var curr : ULBMNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : UpLineBoxMoverView) {

        private val animator : Animator = Animator(view)
        private val ulbm : UpLineBoxMover = UpLineBoxMover(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            ulbm.draw(canvas, paint)
            animator.animate {
                ulbm.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ulbm.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : UpLineBoxMoverView {
            val view : UpLineBoxMoverView = UpLineBoxMoverView(activity)
            activity.setContentView(view)
            return view
        }
    }
}