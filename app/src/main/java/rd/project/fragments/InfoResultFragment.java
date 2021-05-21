package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.w3c.dom.Text;
import rd.project.R;
import rd.project.api.Movie;

public class InfoResultFragment extends Fragment {

    public InfoResultFragment (){
        super(R.layout.fragment_resultinfo);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle movie = getArguments();
        TextView title = view.findViewById(R.id.r_titleText);
        TextView overview = view.findViewById(R.id.r_overviewText);
        title.setText(movie.getString("title"));
        overview.setText(movie.getString("overview"));

    }

}
