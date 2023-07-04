package net.ruckman.snapweatherus;

public class AlertListViewItem {
    public final String event;        // the text for the ListView item title
    final String effective;
    final String expires;
    final String headline;
    final String description;
    final String instruction;

    AlertListViewItem(String event, String effective, String expires, String headline, String description, String instruction) {
        this.event = event;
        this.effective = effective;
        this.expires = expires;
        this.headline = headline;
        this.description = description;
        this.instruction = instruction;
    }
}
