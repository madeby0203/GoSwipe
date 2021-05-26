package rd.project.fragments;

import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.MainActivity;
import rd.project.R;

public class MenuFragment extends Fragment {


    public MenuFragment() {
        super(R.layout.fragment_menu);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        ImageView background1 = view.findViewById(R.id.poster_1); //source: https://stackoverflow.com/questions/36894384/android-move-background-continuously-with-animation
        ImageView background2 = view.findViewById(R.id.poster_2);
        ImageView background3 = view.findViewById(R.id.poster_3);
        ImageView background4 = view.findViewById(R.id.poster_4);
        ImageView background5 = view.findViewById(R.id.poster_5);
        ImageView background6 = view.findViewById(R.id.poster_6);
        ImageView background7 = view.findViewById(R.id.poster_7);
        ImageView background8 = view.findViewById(R.id.poster_8);
        ImageView background9 = view.findViewById(R.id.poster_9);
        ImageView background10 = view.findViewById(R.id.poster_10);
        ImageView background11 = view.findViewById(R.id.poster_11);
        ImageView background12 = view.findViewById(R.id.poster_12);
        ImageView background13 = view.findViewById(R.id.poster_13);


        ValueAnimator background = ValueAnimator.ofFloat(0.0f,1.0f);
        background.setRepeatCount(ValueAnimator.INFINITE);
        background.setInterpolator(new LinearInterpolator());
        background.setDuration(100000L);
        background.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator background) {
                float progress = (float) background.getAnimatedValue()*12;
                float width = background1.getWidth();
                float translationX = width * progress;
                background1.setTranslationX(translationX);
                background2.setTranslationX(translationX-width);
                background3.setTranslationX(translationX-width*2);
                background4.setTranslationX(translationX-width*3);
                background5.setTranslationX(translationX-width*4);
                background6.setTranslationX(translationX-width*5);
                background7.setTranslationX(translationX-width*6);
                background8.setTranslationX(translationX-width*7);
                background9.setTranslationX(translationX-width*8);
                background10.setTranslationX(translationX-width*9);
                background11.setTranslationX(translationX-width*10);
                background12.setTranslationX(translationX-width*11);
                background13.setTranslationX(translationX-width*12);
            }
        });
        background.start();


        Application application = (Application) getContext().getApplicationContext();
        
        // Close multiplayer stuff for both client and host (if it exists)
        if (application.getMultiplayer() != null) {
            application.getMultiplayer().close();
        }
        
        // Load username
        String username = application.getUsername();
        Button usernameButton = view.findViewById(R.id.usernameButton);
        usernameButton.setText(String.format(getString(R.string.welcome_message), username));
        
        // Initialize onClickListeners; each button should switch to the corresponding fragment
        usernameButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, NameFragment.class, null)
                .commit());
        
        Button hostButton = view.findViewById(R.id.hostButton);
        hostButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showProgressDialog("Starting server...");
            application.becomeHost();
            
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                    .replace(R.id.fragment_container_view, SetupFragment.class, null)
                    .commit();
        });
        
        Button joinButton = view.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, JoinFragment.class, null)
                .commit());

        Button tempButton = view.findViewById(R.id.toResults);
        tempButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                 // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, ResultsFragment.class, null)
                .commit());
    }
}
