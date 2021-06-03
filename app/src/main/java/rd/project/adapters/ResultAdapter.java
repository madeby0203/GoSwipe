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
import java.util.Map;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.resultHolder> { //source: https://github.com/Rohitohlyan66/InstagramSuggestion
    
    private final Context context;
    private final Map<Movie, Integer> results;
    
    public ResultAdapter(Map<Movie, Integer> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @NonNull
    @Override
    public resultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new resultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_result, parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull resultHolder holder, int position) {
        Movie movie = new ArrayList<>(results.keySet()).get(position); // Get movie at specified position
        Integer likes = results.getOrDefault(movie, 0);
        
        holder.positionText.setText(String.format(context.getString(R.string.movie_details_position), position + 1));
        holder.likesText.setText(likes + "");
        holder.titleText.setText(movie.getTitle());
        holder.genreText.setText(movie.getGenreString());
        holder.yearText.setText(movie.getYear());
        holder.scoreText.setText(movie.getVote().toString());
        holder.platformText.setText(movie.getPlatformString());

        new Thread(() -> {
            Bitmap bmp = movie.getPosterBM();
            ((Activity) context).runOnUiThread(() -> {
                holder.moviePoster.setImageBitmap(bmp);
            });

        }).start();
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
    
    public class resultHolder extends RecyclerView.ViewHolder{
        TextView positionText;
        TextView likesText;
        TextView titleText;
        ImageView moviePoster;
        TextView genreText;
        TextView yearText;
        TextView scoreText;
        TextView platformText;
        public resultHolder (@NonNull View itemView){
            super(itemView);
            positionText = itemView.findViewById(R.id.positionText);
            likesText = itemView.findViewById(R.id.likesText);
            titleText = itemView.findViewById(R.id.titleText);
            moviePoster = itemView.findViewById(R.id.imageSlider);
            genreText = itemView.findViewById(R.id.genreText);
            yearText = itemView.findViewById(R.id.yearText);
            scoreText = itemView.findViewById(R.id.scoreText);
            platformText = itemView.findViewById(R.id.platformText);
        }
    }
}
