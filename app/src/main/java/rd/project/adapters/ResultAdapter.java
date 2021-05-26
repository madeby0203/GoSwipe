package rd.project.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import rd.project.R;
import rd.project.api.Movie;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.resultHolder> { //source: https://github.com/Rohitohlyan66/InstagramSuggestion

    private final Context context;
    public ResultAdapter(ArrayList<Movie> a, Context context) {
        this.a = a;
        this.context = context;
    }

    ArrayList<Movie> a;

    @NonNull
    @Override
    public resultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new resultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_result_item, parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull resultHolder holder, int position) {
            holder.genreText.setText(a.get(position).getGenre());
            holder.titleText.setText(a.get(position).getTitle());
            holder.yearText.setText(a.get(position).getYear());
            holder.scoreText.setText(a.get(position).getVote().toString());
            holder.platformText.setText(a.get(position).getPlatform());
            holder.numberText.setText(Integer.toString(position+1)+":");

        new Thread(() -> {
            Bitmap bmp = a.get(position).getPosterBM();
            ((Activity) context).runOnUiThread(() -> {
                holder.resultView.setImageBitmap(bmp);
            });

        }).start();
    }

    @Override
    public int getItemCount() {
        return a.size();
    }

    public class resultHolder extends RecyclerView.ViewHolder{
        TextView numberText;
        ImageView resultView;
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
            numberText = itemView.findViewById(R.id.numberText);
        }
    }
}
