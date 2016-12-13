package com.example.android.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGridItemClickedListener} interface
 * to handle interaction events.
 */
public class MoviePostersFragment extends Fragment {

    //The reference to the adapter connected to the grid view
    ImageAdapter mImageAdapter = null;


    private OnGridItemClickedListener mListener;


    public MoviePostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageAdapter = new ImageAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentMoviePosters = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        //Accessing the grid view inside the this fragment
        GridView gridView = (GridView)fragmentMoviePosters.findViewById(R.id.main_grid_view);

        //Attach a adapter with this grid view
        gridView.setAdapter(mImageAdapter);

        //Add the click listener for grid view (anonymous class)
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //For debugging purpose
                Toast.makeText(getActivity(),"Item["+position+"] clicked!", Toast.LENGTH_SHORT).show();
                onButtonPressed(position);
            }
        });

        return fragmentMoviePosters;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int position) {
        if (mListener != null) {
            mListener.onMoviePosterClicked(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGridItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGridItemClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGridItemClickedListener {
        // TODO: Update argument type and name
        public void onMoviePosterClicked(int position);
    }

}
