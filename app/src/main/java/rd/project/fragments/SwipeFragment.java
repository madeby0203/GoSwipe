package rd.project.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;
import org.json.simple.JSONObject;
import rd.project.Application;
import rd.project.MainActivity;
import rd.project.R;
import rd.project.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.stream.Stream;

public class SwipeFragment extends Fragment {
    private final String TAG = "SwipeFragment";
    
    private List<Movie> movies;
    private List<Integer> liked = new ArrayList<>();
    private Movie currentMovie;
    private View view;

    public SwipeFragment (){
        super(R.layout.fragment_swipe);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.view = view;

        Button likeButton = view.findViewById(R.id.likeButton);
        Button dislikeButton = view.findViewById(R.id.dislikeButton);

        this.movies = ((Application) getContext().getApplicationContext()).getMultiplayer().getMovies();
        
        int[] index = {0};
        Stream<Movie> stream = movies.stream();
        Log.d(TAG, "Movies size: " + this.movies.size());

        try {
            updateView(index[0],false);
            index[0]++;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        index[0] = 1;

        nextMovieView(likeButton,index);
        nextMovieView(dislikeButton,index);

        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
    }

    private void nextMovieView(Button button, int[] index) {
        button.setOnClickListener(s -> {
            try {
                if(index[0] < movies.size()) {
                    if(button.getId() == R.id.likeButton) {
                        updateView(index[0],true);
                        liked.add(movies.get(index[0]-1).getId());
                        Log.v(TAG, "Movie liked: " + movies.get(index[0]-1).getTitle());
                    }
                    else {
                        updateView(index[0],false);
                        Log.v(TAG, "Movie disliked: " + movies.get(index[0]-1).getTitle());
                    }
                    index[0]++;
                } else { // No movies left, send results to host
                    if(button.getId() == R.id.likeButton){
                        liked.add(movies.get(index[0]-1).getId());
                        Log.v(TAG, "Movie liked: " + movies.get(index[0]-1));
                    }
    
                    getParentFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, ResultsWaitingFragment.class, null)
                            .commit();
                    
                    ((Application) getContext().getApplicationContext()).getMultiplayer().saveLikes(liked);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateView(int index, boolean liked) throws URISyntaxException, IOException {
        this.currentMovie = movies.get(index);
        new Thread(() -> {
            Bitmap bmp = movies.get(index).getPosterBM();
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(() -> {
                int animationDuration = 300;
                
                int l = 1;
                if (!liked) {
                    l = -1;
                }

                TextView title = view.findViewById(R.id.titleText);
                ImageView imageMovie = view.findViewById(R.id.movieView);
                ImageView imageMovieNext = view.findViewById(R.id.movieViewNext);
                
                // Get display width
                Point size = new Point();
                ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getSize(size);
                int displayWidth = size.x;

                imageMovieNext.setImageBitmap(bmp); // Set the next image to the next movie
                imageMovie.clearAnimation();        // Remove animations from queue
                imageMovie.animate()                // Reset position
                        .setDuration(0)
                        .translationX(0)
                        .start();
                imageMovie.animate()                // Move away the top image
                        .setDuration(animationDuration)
                        .translationX(l * displayWidth)
                        .start();
                Handler handler = new Handler();
                int finalL = l;
                handler.postDelayed(() -> {
                    Palette palette = Palette.from(bmp).generate();
                    int dominantColor = palette.getLightMutedColor(0);
                    int test = palette.getDarkMutedColor(20);
                    title.setTextColor(test);

                    int color = Color.TRANSPARENT;
                    Drawable background = view.getBackground();
                    if (background instanceof ColorDrawable)
                        color = ((ColorDrawable) background).getColor();

                    imageMovie.setImageBitmap(bmp);
                    ValueAnimator anim = new ValueAnimator();
                    anim.setIntValues(color, dominantColor);
                    anim.setEvaluator(new ArgbEvaluator());
                    anim.addUpdateListener(valueAnimator -> view
                            .setBackgroundColor((Integer)valueAnimator
                                    .getAnimatedValue()));
                    anim
                            .setDuration(300)
                            .start();

//                    imageMovie
//                            .animate()
//                            .x(0)
//                            .setDuration(0)
//                            .start();
                    title.setText(movies.get(index).getTitle());
                },animationDuration);
            });
        }).start();
    }
}
