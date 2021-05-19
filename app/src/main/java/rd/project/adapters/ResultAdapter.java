package rd.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import rd.project.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.resultHolder> {
    public ResultAdapter(int[] a) {
        this.a = a;
    }

    int a[];


    @NonNull
    @Override
    public resultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new resultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull resultHolder holder, int position) {
            holder.resultView.setImageResource(a[position]);
    }

    @Override
    public int getItemCount() {
        return a.length;
    }

    public class resultHolder extends RecyclerView.ViewHolder{
        CircleImageView resultView;
        public resultHolder (@NonNull View itemView){
            super(itemView);
            resultView = itemView.findViewById(R.id.imageSlider);
        }
    }
}
