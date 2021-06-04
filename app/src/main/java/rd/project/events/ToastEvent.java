package rd.project.events;

public class ToastEvent {
    
    private final String text;
    
    public ToastEvent(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
}
