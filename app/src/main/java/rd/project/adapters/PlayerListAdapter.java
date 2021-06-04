package rd.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rd.project.R;

import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private final List<String> dataSet;
    private final Context context;
    
    public PlayerListAdapter(List<String> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }
    
    @NonNull
    @Override
    public PlayerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerListAdapter.ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(PlayerListAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(dataSet.get(position));
        
        // Add a star before the host's name
        if (position == 0) { // Host, set icon to star
            viewHolder.getTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.ic_baseline_star_24),
                    null,
                    null,
                    null);
        } else { // Not host, set icon to empty
            viewHolder.getTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.empty_24),
                    null,
                    null,
                    null);
        }
    }
    
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.playerItemName);
        }
        
        public TextView getTextView() {
            return textView;
        }
    }
}
