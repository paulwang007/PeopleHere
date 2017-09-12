package com.foursquare.takehome.main_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.foursquare.takehome.R;
import com.foursquare.takehome.main_adapter.PersonAdapter;
import com.foursquare.takehome.model.TimeRange;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityPresenterInterface.ViewInterface{

    RecyclerView rvRecyclerView;
    PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityPresenter presenter = new MainActivityPresenter(this);

        rvRecyclerView = (RecyclerView) findViewById(R.id.rvRecyclerView);
        personAdapter = new PersonAdapter();

        rvRecyclerView.setAdapter(personAdapter);
        rvRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        presenter.parseVenueFromResponse();
    }

    @Override
    public void updateList(List<TimeRange> timeRangeList) {
        personAdapter.setPeopleList(timeRangeList);
    }
}
