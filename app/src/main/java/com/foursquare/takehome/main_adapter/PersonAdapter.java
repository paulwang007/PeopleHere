package com.foursquare.takehome.main_adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foursquare.takehome.R;
import com.foursquare.takehome.main_activity.MainViewHolder;
import com.foursquare.takehome.model.TimeRange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import static com.foursquare.takehome.model.Person.NO_VISITOR;


final public class PersonAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final float alpha = 0.4f;
    private List<TimeRange> people;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!validPeopleList()) {
            return;
        }

        if (holder instanceof MainViewHolder) {
            MainViewHolder mainViewHolder = (MainViewHolder) holder;
            TimeRange person = people.get(position);
            String name = person.getName();
            if (NO_VISITOR.equals(name)) {
                mainViewHolder.title.setAlpha(alpha);
                mainViewHolder.text.setAlpha(alpha);
            }
            mainViewHolder.title.setText(person.getName());
            mainViewHolder.text.setText(getTimeString(person.getArriveTime(), person.getLeaveTime()));
        }
    }

    @Override
    public int getItemCount() {
        if (!validPeopleList()) {
            return 0;
        }
        return people.size();
    }

    public void setPeopleList(List<TimeRange> peopleList) {
        people = peopleList;
        notifyDataSetChanged();
    }

    private boolean validPeopleList() {
        return people != null;
    }

    private String getTimeString(long startTime, long endTime) {
        return getHumanReadableTime(startTime) +
                " - " +
                getHumanReadableTime(endTime);
    }

    private String getHumanReadableTime(long time) {
        DateTimeFormatter builder = DateTimeFormat.forPattern("hh:mm a");
        DateTime dateTime = new DateTime(time * 1000, DateTimeZone.UTC);
        return builder.print(dateTime);
    }
}
