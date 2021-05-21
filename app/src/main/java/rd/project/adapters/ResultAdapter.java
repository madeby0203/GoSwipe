package rd.project.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import org.w3c.dom.Text;
import rd.project.R;
import rd.project.api.Movie;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.resultHolder> { //source: https://github.com/Rohitohlyan66/InstagramSuggestion
    public ResultAdapter(ArrayList<Movie> a) {
        this.a = a;
    }

    ArrayList<Movie> a;

    @NonNull
    @Override
    public resultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new resultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull resultHolder holder, int position) {
            holder.resultView.setImageResource(R.drawable.ic_launcher_foreground);
            holder.genreText.setText(a.get(position).getGenre());
            holder.titleText.setText(a.get(position).getTitle());
            holder.yearText.setText(a.get(position).getYear());
            holder.scoreText.setText(a.get(position).getVote().toString());
            holder.platformText.setText(a.get(position).getPlatform());
    }

    @Override
    public int getItemCount() {
        return a.size();
    }

    public class resultHolder extends RecyclerView.ViewHolder{
        CircleImageView resultView;
        TextView genreText;
        TextView titleText;
        TextView yearText;
        TextView scoreText;
        TextView platformText;
        public resultHolder (@NonNull View itemView){
            super(itemView);
            resultView = itemView.findViewById(R.id.imageSlider);
            genreText = itemView.findViewById(R.id.genreText);
            titleText = itemView.findViewById(R.id.titleText);
            yearText = itemView.findViewById(R.id.yearText);
            scoreText = itemView.findViewById(R.id.scoreText);
            platformText = itemView.findViewById(R.id.platformText);
        }
    }
}
