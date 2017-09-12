package com.foursquare.takehome.main_activity;

import com.foursquare.takehome.model.TimeRange;

import java.util.List;

/**
 * Created by paul on 9/11/17.
 */

interface MainActivityPresenterInterface {

    interface ViewInterface {
        void updateList(List<TimeRange> timeRangeList);
    }

    interface ActionInterface {
        void parseVenueFromResponse();
    }
}
