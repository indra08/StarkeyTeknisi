package id.starkey.mitra.History.DalamProses;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.starkey.mitra.R;

/**
 * Created by Dani on 2/26/2018.
 */

public class DalamProsesFragment extends Fragment {
    public DalamProsesFragment(){}

    View vmenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //vmenu = inflater.inflate(R.layout.fragment_dalam_proses, container, false);
        vmenu = inflater.inflate(R.layout.fragment_dalam_proses, container, false);



        return vmenu;
    }
}
