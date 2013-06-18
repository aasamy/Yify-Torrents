package com.yify.object;

import com.yify.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ReplyDialog extends DialogFragment {

	public interface ReplyListener {
		public void onReplyPressed(DialogFragment fragment, View view);
		public void onCancelPressed(DialogFragment fragment);
	}
	
	ReplyListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (ReplyListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " has to implement ReplyDialog.ReplyListener");
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.login_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		v.findViewById(R.id.password).setVisibility(View.GONE);
		v.findViewById(R.id.signinbutton).setVisibility(View.GONE);
		
		builder.setTitle("Leave Comment").setView(v)
		.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				listener.onReplyPressed(ReplyDialog.this, v);
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				listener.onCancelPressed(ReplyDialog.this);
				
			}
		});
		
		return builder.create();
	}
	
}
