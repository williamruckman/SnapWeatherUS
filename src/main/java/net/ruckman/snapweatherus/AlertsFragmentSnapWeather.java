package net.ruckman.snapweatherus;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import java.util.ArrayList;
import java.util.List;

public class AlertsFragmentSnapWeather extends ListFragment {

    public List<AlertListViewItem> mItems;        // ListView items list
    private ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // initialize the items list
        mItems = new ArrayList<>();
        adapter = new AlertListViewAdapter(getActivity(), mItems);

    }

    public static AlertsFragmentSnapWeather newInstance() {
        return new AlertsFragmentSnapWeather();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);

        // initialize and set the list adapter
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        AlertListViewItem item = mItems.get(position);

        // do something

    }

    @Override
    public void onPause() {
        super.onPause();
        mItems.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        //looping through alert list for events
        for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
            mItems.add(new AlertListViewItem(ModelWeatherNOAA.CurrentAlerts.event[i], ModelWeatherNOAA.CurrentAlerts.effective[i], ModelWeatherNOAA.CurrentAlerts.expires[i], ModelWeatherNOAA.CurrentAlerts.headline[i], ModelWeatherNOAA.CurrentAlerts.description[i], ModelWeatherNOAA.CurrentAlerts.instruction[i]));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
