package com.pertamina.spbucare.ui.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val listener: RecyclerItemTouchHelperListener) :
        ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = (viewHolder as ConfirmAdapter.ViewHolder).binding.viewForeground
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(
            c: Canvas, recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
            actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = (viewHolder as ConfirmAdapter.ViewHolder).binding.viewForeground
        viewHolder.binding.tvReject.visibility = View.VISIBLE
        viewHolder.binding.deleteIcon.visibility = View.VISIBLE
        viewHolder.binding.tvAgree.visibility = View.VISIBLE
        viewHolder.binding.agreeIcon.visibility = View.VISIBLE
        if (dX > 0) {
            viewHolder.binding.viewBackground.setBackgroundColor(Color.parseColor("#DA251C"))
            viewHolder.binding.tvAgree.visibility = View.GONE
            viewHolder.binding.agreeIcon.visibility = View.GONE
        } else {
            viewHolder.binding.viewBackground.setBackgroundColor(Color.parseColor("#85C226"))
            viewHolder.binding.tvReject.visibility = View.GONE
            viewHolder.binding.deleteIcon.visibility = View.GONE
        }
        getDefaultUIUtil().onDrawOver(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
        )
        listener.onRefreshDisable(dX, dY)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = (viewHolder as ConfirmAdapter.ViewHolder).binding.viewForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
            actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView = (viewHolder as ConfirmAdapter.ViewHolder).binding.viewForeground
        getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
        fun onRefreshDisable(dX: Float, dY: Float)
    }
}