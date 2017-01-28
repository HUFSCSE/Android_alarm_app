package com.example.gudrbscse.khkalarm4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gudrbscse on 2017-01-23.
 */

public class KhkDateListAdapter extends RecyclerView.Adapter<KhkDateListAdapter.KhkDateViewHolder> {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;

    public KhkDateListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }
    @Override
    public KhkDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
        return new KhkDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KhkDateViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        //String name = mCursor.getString(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME));
        //int partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE));
        int _month, _day, _hour, _minute;
        String _note=null;

        _month = mCursor.getInt(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MONTH));
        _day = mCursor.getInt(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_DAY));
        _hour = mCursor.getInt(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_HOUR));
        _minute = mCursor.getInt(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MINUTE));
        _note =  mCursor.getString(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_NOTE));

        String alarm_str = String.valueOf(_month+1)+" / "
                +String.valueOf(_day)+" / "
                +String.valueOf(_hour)+" / "
                +String.valueOf(_minute);

        holder.mtv_alarm_time.setText(alarm_str);
        holder.met_alarm_note.setText(_note);

        // TODO (6) Retrieve the id from the cursor and
        long id = mCursor.getLong(mCursor.getColumnIndex(KhkDateContract.KhkDateEntry._ID));
        // TODO (7) Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(id);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
    class KhkDateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mtv_alarm_time;
        ImageButton mib_alarm_edit;
        EditText met_alarm_note;

        public KhkDateViewHolder(View itemView) {
            super(itemView);
            met_alarm_note = (EditText) itemView.findViewById(R.id.et_alarm_note);
            mtv_alarm_time = (TextView) itemView.findViewById(R.id.tv_alarm_time);
            mib_alarm_edit = (ImageButton) itemView.findViewById(R.id.ib_alarm_edit);
            mib_alarm_edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "Clicked button at position: "
                            + getAdapterPosition() + " "
                            +this.itemView.getTag()
                    ,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, ChildActivity.class);
            intent.putExtra("_id", Integer.parseInt(this.itemView.getTag().toString()));
            intent.putExtra("date",mtv_alarm_time.getText().toString());
            intent.putExtra("note", met_alarm_note.getText().toString());
            mContext.startActivity(intent);
        }
    }
}
