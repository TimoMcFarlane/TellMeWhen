package fi.timomcfarlane.tellmewhen.form;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.timomcfarlane.tellmewhen.R;

/**
 * Class represents a Fragment for displaying a list of Alarms inside FormActivity
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class AlarmListFragment extends Fragment {
    private RecyclerView recycledList;
    private AlarmRecyclerAdapter adapter;
    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.form_alarm_fragment, container, false);
        return view;
    }

    /**
     * Inside onViewCreated recycler view is setup and listitem onClick is added
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AlarmRecyclerAdapter(
                getContext(),
                ((FormActivity)getActivity()).getAlarms(),
                (v, position) -> ((FormActivity)getActivity()).removeAlarm(position)
        );
        recycledList = (RecyclerView) view.findViewById(R.id.form_alarm_list);
        recycledList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycledList.setAdapter(adapter);
    }

    public RecyclerView getList() {
        return this.recycledList;
    }
}
