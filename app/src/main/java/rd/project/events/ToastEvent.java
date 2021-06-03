package rd.project.events;

import android.content.Context;
import android.widget.Toast;

public class ToastEvent {

    private String text;

    public ToastEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
