package rd.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rd.project.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    
    private final List<String> dataSet;
    
    public MessagesAdapter(List<String> dataSet) {
        this.dataSet = dataSet;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(dataSet.get(position));
    }
    
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.messageItemText);
        }
        
        public TextView getTextView() {
            return textView;
        }
    }
}
