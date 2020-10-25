package com.example.paint_application.a.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View





class Custom_Paint_View(context: Context?, attrs: AttributeSet?) : View(context!!, attrs)
{


     lateinit var btmBackground : Bitmap
     lateinit var btmView : Bitmap
     var paint : Paint
     var mPath : Path
     var colorbackground :Int?= 0
     var sizeBrush : Int?=0
     var sizeErawser : Int?=0
     var aX : Float?=0.0f
     var ay : Float?= 0.0f
     lateinit var canvas : Canvas
     var SPACE = 4
     var bitmapList = ArrayList<Bitmap>()

         init {
             paint = Paint()
             mPath = Path()
             sizeErawser= 12
             sizeBrush =12
             colorbackground= Color.WHITE

             paint.setColor(Color.BLACK)
             paint.isAntiAlias=true
             paint.isDither= true
             paint.style= Paint.Style.STROKE
             paint.strokeCap= Paint.Cap.ROUND
             paint.strokeJoin= Paint.Join.ROUND
             paint.strokeWidth=toPx(sizeBrush!!)
         }

    private fun toPx(sizeBrush: Int): Float {
        return  sizeBrush*(resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        btmBackground= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        btmView= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas= Canvas(btmView)
        super.onSizeChanged(w, h, oldw, oldh)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawColor(colorbackground!!)
        canvas.drawBitmap(btmBackground,0f,0f,null)
        canvas.drawBitmap(btmView,0f,0f,null)
    }


    fun setColorBackground(color : Int)
    {
        colorbackground=color
        invalidate()
    }

    fun setSizeBrush(size : Int)
    {
       sizeBrush= size
        paint.strokeWidth= (toPx(sizeBrush!!)).toString().toFloat()
    }

    fun setBrushColor(color : Int)
    {
        paint.setColor(color)
    }

    fun setSizeEraser(size : Int)
    {
        sizeErawser=size
        paint.strokeWidth= (toPx(sizeErawser!!)).toString().toFloat()
    }

    fun enableEraser()
    {
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))

    }
    fun disableEraser()
    {
        paint.setXfermode(null)
        paint.setShader(null)
        paint.setMaskFilter(null)

    }

    fun addLastAction(bitmap : Bitmap)
    {
    bitmapList.add(bitmap)
    }

    fun returnLastAction()
    {
        if(bitmapList.size>0)
        {
            bitmapList.removeAt(bitmapList.size - 1)
            if(bitmapList.size >0)
            {
                 btmView= bitmapList.get(bitmapList.size-1)
            }

        }
        else
        {
            btmView= Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        }
        canvas= Canvas(btmView)
        invalidate()

    }




    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val x = event?.getX()
        val y= event?.getY()


        when(event?.action)
        {
            MotionEvent.ACTION_DOWN -> touchStart(x!!, y!!)
            MotionEvent.ACTION_MOVE -> touchMove(x!!,y!!)
            MotionEvent.ACTION_UP ->
            {
                touchUp()
                addLastAction(getBitmap())
            }
        }

        return true

    }

    private fun touchUp() {
        mPath.reset()
    }

    private fun touchMove(x: Float, y: Float) {
        var dx = Math.abs(x- aX!!)
        var dy = Math.abs(y- ay!!)

        if(dx >= SPACE ||  dy>= SPACE)
        {
            mPath.quadTo(x,y,(x+ aX!!)/2,(y+ ay!!)/2)
            ay=y
            aX=x

            canvas.drawPath(mPath, paint)
            invalidate()
        }

    }

    private fun touchStart(x: Float, y: Float) {

        mPath.moveTo(x,y)
        aX= x
        ay= y


    }

    fun  getBitmap() : Bitmap{
        this.isDrawingCacheEnabled= true
        this.buildDrawingCache()
        var bitmap = Bitmap.createBitmap(this.getDrawingCache())
        this.isDrawingCacheEnabled= false

        return  bitmap
    }
}



