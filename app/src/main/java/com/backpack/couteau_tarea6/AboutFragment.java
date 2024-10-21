package com.backpack.couteau_tarea6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout de este fragmento
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
