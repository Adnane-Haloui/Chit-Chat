package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;


public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH> {

	public Cursor mCursor;
	private CursorDataSetObserver mCursorDataObserver;
	private boolean isDataValid;
	private String columnId;

	public CursorRecyclerViewAdapter(Cursor cursor, String idCol) {
		mCursor = cursor;
		isDataValid = mCursor != null;
		columnId = idCol;
		mCursorDataObserver = new CursorDataSetObserver();
		if (isDataValid) {
			mCursor.registerDataSetObserver(mCursorDataObserver);
		}
	}

	@Override
	public void setHasStableIds(boolean hasStableIds) {
		super.setHasStableIds(true);
	}

	@Override
	public long getItemId(int position) {
		return isDataValid && mCursor != null && mCursor.moveToPosition(position)
				? mCursor.getLong(mCursor.getColumnIndex(columnId)) : 0;
	}

	@Override
	public int getItemCount() {
		return isDataValid && mCursor != null ? mCursor.getCount() : 0;
	}

	public abstract void onBindViewHolder(VH holder, Cursor cursor);

	@Override
	public void onBindViewHolder(VH holder, int position) {
		if (isDataValid && mCursor != null && mCursor.moveToPosition(position)) {
			onBindViewHolder(holder, mCursor);
		}
	}

	public void swapCursor(Cursor cursor) {
		if (mCursor != cursor) {
			if (mCursor != null && mCursorDataObserver != null) {
				mCursor.unregisterDataSetObserver(mCursorDataObserver);
			}
			mCursor = cursor;
			if (mCursor != null) {
				if (mCursorDataObserver != null) {
					mCursor.registerDataSetObserver(mCursorDataObserver);
				}
				isDataValid = true;
				notifyDataSetChanged();
			} else {
				isDataValid = false;
				notifyDataSetChanged();
			}
		}
	}


	private class CursorDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			isDataValid = true;
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			isDataValid = false;
			notifyDataSetChanged();
		}
	}
}