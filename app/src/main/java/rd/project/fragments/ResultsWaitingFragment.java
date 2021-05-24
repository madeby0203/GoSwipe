package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.Application;
import rd.project.R;
import rd.project.events.MultiplayerEvent;

public class ResultsWaitingFragment extends Fragment {
    
    public ResultsWaitingFragment() {
        super(R.layout.fragment_results_waiting);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        updateProgressText();
        
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unregister events
        EventBus.getDefault().unregister(this);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiplayerPlayerLeave(MultiplayerEvent.ResultsCompletedCountUpdate event) {
        updateProgressText();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiplayerPlayerLeave(MultiplayerEvent.PlayerLeave event) {
        updateProgressText();
    }
    
    private void updateProgressText() {
        Application application = (Application) getContext().getApplicationContext();
    
    
        TextView progressText = getView().findViewById(R.id.resultsWaitingProgressText);
        progressText.setText(String.format(getString(R.string.results_waiting_progress),
                application.getMultiplayer().getResultsCompletedAmount(),
                application.getMultiplayer().getConnectedUsernames().size()));
    }
}
