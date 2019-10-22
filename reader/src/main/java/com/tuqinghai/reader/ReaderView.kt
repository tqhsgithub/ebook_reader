package com.tuqinghai.reader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import kotlin.math.min


class ReaderView : View {

    private var defaultWidth: Int = 600//默认宽度
    private var defaultHeight: Int = 1000//默认高度


    private val pagePaint: TextPaint by lazy {
        val paint = TextPaint()
        paint.textSize = 30f
        paint.isAntiAlias = true
        paint
    }

    private val textPaint: TextPaint by lazy {
        val paint = TextPaint()
        paint.textSize = 60f
        paint.isAntiAlias = true
        paint
    }


    var textSize      //字体大小
        set(value) {
            textPaint.textSize = value
            invalidate()
        }
        get() = textPaint.textSize


    var text = ""               //阅读文本
        set(value) {
            field = value
            current = 1
            invalidate()
        }


    var pageEndPosition: List<Int> = ArrayList()

    var startIndex: Int
        set(value) {
            current = pageEndPosition.indexOfFirst { it >= value } + 1
        }
        get() = if (pageEndPosition.isEmpty()) 0 else pageEndPosition[current - 1]

    var current: Int = 1
        set(value) {
            field = when {
                value < 1 -> 1
                value > pageEndPosition.size -> pageEndPosition.size
                else -> value
            }
            super.invalidate()
        }

    var spacingmult = 1.0f       //基本间距倍数
        set(value) {
            field = value
            invalidate()
        }

    var spacingadd = 0f       //额外间距
        set(value) {
            field = value
            invalidate()
        }


    var showMenu = false

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {


    }

    var onPageChangeListener: OnPageChangeListener? = null

    var onMenuListener: OnMenuListener? = null

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = min(result, specSize)
        }
        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measureSize(defaultHeight, heightMeasureSpec)
        val width = measureSize(defaultWidth, widthMeasureSpec)
        setMeasuredDimension(width, height)
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (pageEndPosition.isEmpty()) {
            pageEndPosition = countPageEndPosition()
        }

        if (pageEndPosition.isEmpty()) {
            val index = "0/0"
            canvas?.drawText(
                "0/0",
                width.toFloat() - 15 * index.length - 25,
                height.toFloat() - 15,
                pagePaint
            )
            return
        }

        StaticLayout(
            text,
            if (current <= 1) 0 else pageEndPosition[current - 2],
            pageEndPosition[min(pageEndPosition.size - 1, current - 1)],
            textPaint,
            width,
            Layout.Alignment.ALIGN_NORMAL,
            spacingmult,
            spacingadd,
            true
        ).draw(canvas)

        if (pageEndPosition.isNotEmpty()) {
            val index = "${current}/${pageEndPosition.size}"
            canvas?.drawText(
                index,
                width.toFloat() - 15 * index.length - 25,
                height.toFloat() - 15,
                pagePaint
            )
        }
    }

    override fun invalidate() {
        pageEndPosition = countPageEndPosition()
        super.invalidate()
    }

    /**
     * 分页方法
     */
    private fun countPageEndPosition(): List<Int> {
        val pageEndPosition = ArrayList<Int>()
        if (width == 0) {
            return pageEndPosition
        }
        val h = (textPaint.fontMetrics.bottom - textPaint.fontMetrics.top) * spacingmult + spacingadd
        val layout = StaticLayout(
            text, 0, text.length, textPaint, width,
            Layout.Alignment.ALIGN_NORMAL, spacingmult, spacingadd, true
        )
        val countLine = layout.lineCount
        val pageLine = ((height) / h).toInt()
        var page = if (countLine % pageLine == 0) countLine / pageLine else countLine / pageLine + 1
        for (i in 1..page) {

            pageEndPosition.add(layout.getLineEnd(min(countLine - 1, pageLine * i - 1)))
        }
        return pageEndPosition

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (showMenu) {
                showMenu = false
                onMenuListener?.close()
                return true
            }
            when {
                event.x < width / 3 -> {
                    val c = current
                    current -= 1
                    onPageChangeListener?.change(c, current, pageEndPosition.size, true)
                    return true
                }
                event.x > width - width / 3 -> {
                    val c = current
                    current += 1
                    onPageChangeListener?.change(c, current, pageEndPosition.size)
                    return true
                }
                else -> {
                    showMenu = !showMenu
                    if (showMenu) {
                        onMenuListener?.open()
                    } else {
                        onMenuListener?.close()
                    }
                }
            }

        }


        return super.onTouchEvent(event)
    }


    interface OnPageChangeListener {
        fun change(before: Int, current: Int, count: Int, lastPage: Boolean = false)
    }

    interface OnMenuListener {
        fun open()
        fun close()
    }
}