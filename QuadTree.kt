package ve.com.patillasoft.game.Quadtree

import android.graphics.*

class QuadTree<T : ItemPosition>
//Constructors
(mDepth: Int = 0, mBounds: Rect) {
    constructor(pLevel: Int = 0, left: Int = 0, top: Int = 0, right: Int, bottom: Int) : this(pLevel, Rect(left, top, right, bottom))

    //    Vars
    private val mChildren: MutableList<QuadTree<T>>
    private val mItems: MutableList<T>
    private val mBounds: Rect
    private val mDepth: Int

    init {
        this.mBounds = mBounds
        this.mDepth = mDepth
        mChildren = mutableListOf<QuadTree<T>>()
        mItems = mutableListOf()
    }

    fun clear() {
        mChildren.clear()
        mItems.clear()
    }

    fun add(item: T) {
        val point = item.getPoint()
        if (mBounds.contains(point.x, point.y)) {
            insert(point.x, point.y, item)
        }
    }

    private fun insert(x: Int, y: Int, item: T) {
        when {
            mChildren.isNotEmpty() -> {
                if (y < mBounds.centerY()) {
                    if (x < mBounds.centerX()) { // top left
                        mChildren.get(1).insert(x, y, item)
                    } else { // top right
                        mChildren.get(0).insert(x, y, item)
                    }
                } else {
                    if (x < mBounds.centerX()) { // bottom left
                        mChildren.get(2).insert(x, y, item)
                    } else {
                        mChildren.get(3).insert(x, y, item)
                    }
                }
            }
            else -> {
                mItems.add(item)
                if (mItems.size > MAX_ELEMENTS && mDepth < MAX_DEPTH) {
                    split()
                }
            }
        }
    }

    private fun split() {

        val pDepth = mDepth + 1

        mChildren.add(QuadTree(pDepth, Rect(mBounds.centerX(), mBounds.top, mBounds.right, mBounds.centerY())))
        mChildren.add(QuadTree(pDepth, Rect(mBounds.left, mBounds.top, mBounds.centerX(), mBounds.centerY())))
        mChildren.add(QuadTree(pDepth, Rect(mBounds.left, mBounds.centerY(), mBounds.centerX(), mBounds.bottom)))
        mChildren.add(QuadTree(pDepth, Rect(mBounds.centerX(), mBounds.centerY(), mBounds.right, mBounds.bottom)))

        mItems.forEach {
            val point = it.getPoint()
            insert(point.x, point.y, it)
        }
        mItems.clear()
    }

    fun search(searchBounds: Rect): MutableList<T> {
        val results = mutableListOf<T>()
        search(searchBounds, results)
        return results
    }

    private fun search(searchBounds: Rect, results: MutableCollection<T>) {

        when {
            !Rect(searchBounds).intersect(mBounds) -> return
            mChildren.isNotEmpty() -> mChildren.forEach { it.search(searchBounds, results) }
            searchBounds.contains(mBounds) -> results.addAll(mItems)
            else -> results.addAll(mItems.filter { Rect(searchBounds).intersect(it.getBounds()) })
        }
    }

    fun draw(canvas: Canvas) {

        mChildren.forEach { it.draw(canvas) }

        with(myPaint) {
            color = Color.RED
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
        }

        when (mItems.size) {
            0 -> canvas.drawText("L${mDepth}", mBounds.centerX().toFloat(), mBounds.centerY().toFloat(), myPaint)
            else -> canvas.drawText("${mItems.size}", mBounds.centerX().toFloat(), mBounds.centerY().toFloat(), myPaint)
        }

        with(myPaint) {
            color = Color.GREEN
            alpha = 50
        }

//        mItems.forEach { canvas.drawRect(it.getBounds(), myPaint); }

        with(myPaint) {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.WHITE
            alpha = 100
        }

        canvas.drawRect(mBounds, myPaint)
    }

    companion object {
        val MAX_ELEMENTS = 10
        val MAX_DEPTH = 10
        val myPaint = Paint()
    }
}

fun Any?.isNull(): Boolean =
        when {
            this == null -> true
            else -> false
        }

fun Any?.isNotNull(): Boolean =
        when {
            this == null -> false
            else -> true
        }

interface ItemPosition {
    var x: Int
    var y: Int
    val width: Int
    val height: Int

    fun getPoint(): Point
    fun getBounds(): Rect
}





