package com.mikelle.qufoi.app.adapter

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.mikelle.qufoi.app.R
import com.mikelle.qufoi.app.helper.ApiLevelHelper
import com.mikelle.qufoi.app.model.Category
import com.mikelle.qufoi.app.model.Theme
import com.mikelle.qufoi.app.persistence.QufoiDatabaseHelper

class CategoryAdapter(private val mActivity: Activity) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val mResources: Resources
    private val mPackageName: String
    private val mLayoutInflater: LayoutInflater
    private var mCategories: List<Category>? = null

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    init {
        mResources = mActivity.resources
        mPackageName = mActivity.packageName
        mLayoutInflater = LayoutInflater.from(mActivity.applicationContext)
        updateCategories(mActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = mCategories!![position]
        val theme = category.theme
        setCategoryIcon(category, holder.icon)
        holder.itemView.setBackgroundColor(getColor(theme.windowBackgroundColor))
        holder.title.text = category.name
        holder.title.setTextColor(getColor(theme.textPrimaryColor))
        holder.title.setBackgroundColor(getColor(theme.primaryColor))
        holder.itemView.setOnClickListener { v -> mOnItemClickListener?.onClick(v, position) }
    }

    override fun getItemId(position: Int): Long {
        return mCategories!![position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mCategories!!.size
    }

    fun getItem(position: Int): Category {
        return mCategories!![position]
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter.notifyItemChanged
     * @param id Id of changed category.
     */
    fun notifyItemChanged(id: String?) {
        updateCategories(mActivity)
        notifyItemChanged(getItemPositionById(id))
    }

    private fun getItemPositionById(id: String?): Int {
        for (i in mCategories!!.indices) {
            if (mCategories!![i].id == id) {
                return i
            }

        }
        return -1
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    private fun setCategoryIcon(category: Category?, icon: ImageView) {
        val categoryImageResource = mResources.getIdentifier(
                ICON_CATEGORY + category?.id, DRAWABLE, mPackageName)
        val solved = category?.isSolved
        if (solved!!) {
            val solvedIcon = loadSolvedIcon(category, categoryImageResource)
            icon.setImageDrawable(solvedIcon)
        } else {
            icon.setImageResource(categoryImageResource)
        }
    }

    private fun updateCategories(activity: Activity) {
        mCategories = QufoiDatabaseHelper.getCategories(activity, true)
    }

    /**
     * Loads an icon that indicates that a category has already been solved.

     * @param category The solved category to display.
     * *
     * @param categoryImageResource The category's identifying image.
     * *
     * @return The icon indicating that the category has been solved.
     */
    private fun loadSolvedIcon(category: Category?, categoryImageResource: Int): Drawable {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            return loadSolvedIconLollipop(category, categoryImageResource)
        }
        return loadSolvedIconPreLollipop(category, categoryImageResource)
    }

    private fun loadSolvedIconLollipop(category: Category?, categoryImageResource: Int): LayerDrawable {
        val done = loadTintedDoneDrawable()
        val categoryIcon = loadTintedCategoryDrawable(category, categoryImageResource)
        val layers = arrayOf(categoryIcon, done) // ordering is back to front
        return LayerDrawable(layers)
    }

    private fun loadSolvedIconPreLollipop(category: Category?, categoryImageResource: Int): Drawable {
        return loadTintedCategoryDrawable(category, categoryImageResource)
    }

    /**
     * Loads and tints a drawable.

     * @param category The category providing the tint color
     * *
     * @param categoryImageResource The image resource to tint
     * *
     * @return The tinted resource
     */
    private fun loadTintedCategoryDrawable(category: Category?, categoryImageResource: Int): Drawable {
        val categoryIcon = ContextCompat.getDrawable(mActivity, categoryImageResource).mutate()
        return wrapAndTint(categoryIcon, category?.theme!!.primaryColor)
    }

    /**
     * Loads and tints a check mark.

     * @return The tinted check mark
     */
    private fun loadTintedDoneDrawable(): Drawable {
        val done = ContextCompat.getDrawable(mActivity, R.drawable.ic_tick)
        return wrapAndTint(done, android.R.color.white)
    }

    private fun wrapAndTint(done: Drawable, @ColorRes color: Int): Drawable {
        val compatDrawable = DrawableCompat.wrap(done)
        DrawableCompat.setTint(compatDrawable, getColor(color))
        return compatDrawable
    }

    /**
     * Convenience method for color loading.

     * @param colorRes The resource id of the color to load.
     * *
     * @return The loaded color.
     */
    private fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(mActivity, colorRes)
    }

    internal class ViewHolder(container: View) : RecyclerView.ViewHolder(container) {

        val icon: ImageView
        val title: TextView

        init {
            icon = container.findViewById(R.id.category_icon) as ImageView
            title = container.findViewById(R.id.category_title) as TextView
        }
    }

    companion object {

        public val DRAWABLE = "drawable"
        private val ICON_CATEGORY = "icon_category_"
    }
}
