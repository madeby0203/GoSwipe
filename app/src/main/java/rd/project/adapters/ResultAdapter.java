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
        String genre = "Unknown";
        switch (Integer.parseInt(movie.getGenre())){
            case 28: genre = "Action"; break;
            case 12: genre = "Adventure"; break;
            case 15: genre = "Animation"; break;
            case 35: genre = "Comedy";break;
            case 80: genre = "Crime";break;
            case 99: genre = "Documentary";break;
            case 10751: genre = "Family";break;
            case 18: genre = "Drama";break;
            case 14: genre = "Fantasy";break;
            case 36: genre = "History";break;
            case 27: genre = "Horror";break;
            case 10402: genre = "Music";break;
            case 9648: genre = "Mystery";break;
            case 10749: genre = "Romance";break;
            case 878: genre = "ScienceFiction";break;
            case 10770: genre = "TV_Movie";break;
            case 53: genre = "Thriller";break;
            case 10752: genre = "War";break;
            case 37: genre = "Western";break;
        }
        holder.genreText.setText(genre);
        holder.yearText.setText(movie.getYear());
        holder.scoreText.setText(movie.getVote().toString());

        String provider = "Unknown";
        switch (Integer.parseInt(movie.getPlatform())){
            case 8: provider = "Netflix"; break;
            case 9: provider = "Amazon video"; break;
            case 337: provider = "Disney plus"; break;
            case 72: provider = "Videoland"; break;
        }
        holder.platformText.setText(provider);

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
