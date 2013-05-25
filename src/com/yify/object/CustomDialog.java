package com.yify.object;

import com.yify.mobile.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class CustomDialog extends DialogFragment {
	
	public interface CustomDialogListener {
		public void onPositiveAction(DialogFragment dialog, View v);
		public void onNegativeAction(DialogFragment dialog);
	}
	
	CustomDialogListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (CustomDialogListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement CustomDialogListener");
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		final View v = inflater.inflate(R.layout.rating_picker, null);
		
		NumberPicker picker = (NumberPicker) v.findViewById(R.id.rating_pick);
		
		Bundle val = this.getArguments();
		
		picker.setMaxValue(9); picker.setMinValue(0); picker.setValue(val.getInt("current"));
		
		builder.setTitle("Set Rating").setView(v)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				listener.onPositiveAction(CustomDialog.this, v);
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				listener.onNegativeAction(CustomDialog.this);
				
			}
		});
		
		return builder.create();
		
	}

}
