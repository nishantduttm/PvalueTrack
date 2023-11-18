package com.example.votingapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.model.LogEntry;

import org.w3c.dom.Text;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {

    private LogEntry[] logs;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private TextView acText;
        private TextView acNameText;
        private TextView pName;
        private  TextView pCode;
        private TextView candidateName;
        private TextView round;
        public ViewHolder(View view) {
            super(view);

            timeTextView = (TextView) view.findViewById(R.id.timeStamp);
            acText = (TextView) view.findViewById(R.id.acCodeTextView);
            acNameText = (TextView) view.findViewById(R.id.acNameTextView);
            pCode = (TextView) view.findViewById(R.id.partyCodeTextView);
            pName = (TextView) view.findViewById(R.id.partyNameTextView);
            candidateName = (TextView) view.findViewById(R.id.candidateNameTextView);
            round = (TextView) view.findViewById(R.id.roundNumberTextView);

        }

        public void setText(LogEntry logEntry) {
            timeTextView.setText(logEntry.getTimeStamp());
            acText.setText( " "+logEntry.getAcCode());
            acNameText.setText(" "+logEntry.getAcName());
            pName.setText(" "+logEntry.getPartyName());
            pCode.setText(" "+logEntry.getPartyCode());
            candidateName.setText(" "+logEntry.getCandidateName());
            round.setText(" "+logEntry.getRoundNo());
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param logs LogEntry[] containing the data to populate views to be used
     * by RecyclerView
     */
    public LogsAdapter(LogEntry[] logs) {
        this.logs = logs;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.log_entry_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.setText(logs[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return logs.length;
    }
}
