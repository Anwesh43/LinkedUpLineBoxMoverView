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
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val delay : Long = 20
val scGap : Float = 0.02f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
val parts : Int = 3
