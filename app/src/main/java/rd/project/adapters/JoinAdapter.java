package rd.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import rd.project.R;
import rd.project.events.JoinListClickEvent;

import java.util.List;

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.ViewHolder> {
    
    private final List<String> dataSet;
    
    public JoinAdapter(List<String> dataSet) {
        this.dataSet = dataSet;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);
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
    
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.serverItemAddress);
            view.setOnClickListener(this);
        }
        
        public TextView getTextView() {
            return textView;
        }
        
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new JoinListClickEvent(getTextView().getText().toString()));
        }
    }
}
