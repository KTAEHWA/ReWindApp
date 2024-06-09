package com.android.re_wind.ui.home.page

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.ReplacementSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(private val dates: HashSet<CalendarDay>, private val text: String) :
    DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object : CustomTextSpan() {
            override fun drawText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
                paint.color = Color.RED
                paint.textSize = 20f
                canvas.drawText(text, x, y, paint)
            }
        })
    }
}

abstract class CustomTextSpan : ReplacementSpan() {
    abstract fun drawText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint)

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        drawText(canvas, text.toString().substring(start, end), x, y.toFloat(), paint)
    }
}
