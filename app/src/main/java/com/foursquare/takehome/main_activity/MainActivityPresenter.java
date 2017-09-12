package com.foursquare.takehome.main_activity;

import android.os.AsyncTask;
import android.util.Log;

import com.foursquare.takehome.model.PeopleHereJsonResponse;
import com.foursquare.takehome.model.TimeRange;
import com.foursquare.takehome.model.Venue;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.foursquare.takehome.model.Person.NO_VISITOR;

/**
 * Created by paul on 9/11/17.
 */

class MainActivityPresenter implements MainActivityPresenterInterface.ActionInterface, Comparator<TimeRange> {

    private MainActivity mActivity;

    MainActivityPresenter(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Parsing a fake json response from assets/people.json
     */
    @Override
    public void parseVenueFromResponse() {

        new AsyncTask<Void, Void, Venue>() {
            @Override
            protected Venue doInBackground(Void... params) {
                try {
                    InputStream is = mActivity.getAssets().open("people.json");
                    InputStreamReader inputStreamReader = new InputStreamReader(is);

                    PeopleHereJsonResponse response = new Gson().fromJson(inputStreamReader, PeopleHereJsonResponse.class);
                    return response.getVenue();
                } catch (Exception e) {
                    Log.d(MainActivityPresenter.class.getSimpleName(), e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Venue venue) {
                mActivity.updateList(processVisitorsList(venue));
            }

        }.execute();
    }

    private List<TimeRange> processVisitorsList(Venue venue) {
        long openTime = venue.getOpenTime();
        long closeTime = venue.getCloseTime();

        // Add all visitors in a list to be sorted later
        List<TimeRange> visitorsList = new ArrayList<>();
        for (int i = 0; i < venue.getVisitors().size(); i++) {
            String name = venue.getVisitors().get(i).getName();
            long arriveTime = venue.getVisitors().get(i).getArriveTime();
            long leaveTime = venue.getVisitors().get(i).getLeaveTime();

            visitorsList.add(i, new TimeRange(name, arriveTime, leaveTime));
        }

        // Sort the visitor's list by arrival time
        visitorsList.sort(this);

        // Merge visitors who are at the venue during the same time, name doesn't matter here.
        // For instance, if Danny stayed between 9-10, and Ben stayed between 9:30-11
        // After merging, it becomes Visitors stayed between 9-11
        List<TimeRange> mergedVisitorsList = mergeVisitors(visitorsList);

        // Find the time between visitors in the merged list. There's no one in the venue in those times.
        List<TimeRange> noVisitorList = getNoVisitorsList(openTime, closeTime, mergedVisitorsList);

        // Sort the list again with No Visitors times included
        visitorsList.addAll(noVisitorList);
        visitorsList.sort(this);

        return visitorsList;
    }

    private List<TimeRange> mergeVisitors(List<TimeRange> list) {
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list;
        }

        List<TimeRange> newList = new ArrayList<>();
        List<TimeRange> mergeList = new ArrayList<>(list.size());
        for (TimeRange timeRange : list) {
            mergeList.add(new TimeRange(timeRange.getName(), timeRange.getArriveTime(), timeRange.getLeaveTime()));
        }

        int index = 0;
        for (int i = 1; i < mergeList.size(); i++) {
            TimeRange firstVisitor = mergeList.get(i - 1);
            TimeRange secondVisitor = mergeList.get(i);
            if (firstVisitor.getLeaveTime() < secondVisitor.getArriveTime()) {
                newList.add(index++, firstVisitor);
            } else {
                secondVisitor.setArriveTime(firstVisitor.getArriveTime());
                if (firstVisitor.getLeaveTime() > secondVisitor.getLeaveTime()) {
                    secondVisitor.setLeaveTime(firstVisitor.getLeaveTime());
                }
            }
            if (i == mergeList.size() - 1) {
                newList.add(index++, secondVisitor);
            }
        }

        return newList;
    }


    private List<TimeRange> getNoVisitorsList(long open, long close, List<TimeRange> timeRanges) {
        List<TimeRange> noVisitorList = new ArrayList<>();
        int index = 0;

        if (timeRanges.size() == 0) {
            return noVisitorList;
        }

        // Add No Visitors hours before the first visitor arrives
        if (open < timeRanges.get(0).getArriveTime()) {
            noVisitorList.add(index++, new TimeRange(NO_VISITOR, open, timeRanges.get(0).getArriveTime()));
        }
        // Add No Visitors hours between each time range
        if (timeRanges.size() > 1) {
            for (int i = 0; i < timeRanges.size() - 1; i++) {
                noVisitorList.add(index++, new TimeRange(NO_VISITOR, timeRanges.get(i).getLeaveTime(), timeRanges.get(i + 1).getArriveTime()));
            }
        }
        // Add No Visitors hours after the last visitor leaves
        if (close > timeRanges.get(timeRanges.size() - 1).getLeaveTime()) {
            noVisitorList.add(index++, new TimeRange(NO_VISITOR, timeRanges.get(timeRanges.size() - 1).getLeaveTime(), close));
        }
        return noVisitorList;
    }

    @Override
    public int compare(TimeRange o1, TimeRange o2) {
        if (o1.getArriveTime() == o2.getArriveTime()) {
            return (int) (o1.getLeaveTime() - o2.getLeaveTime());
        }
        return (int) (o1.getArriveTime() - o2.getArriveTime());
    }
}
