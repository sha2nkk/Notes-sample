package com.shashank.notes;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shashank on 10/06/16.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public ViewDataBinding binding;

    public RecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
