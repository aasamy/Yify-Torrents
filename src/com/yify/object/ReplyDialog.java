package com.yify.object;

import com.yify.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
		final View v = inflater.inflate(R.layout.reply_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		Button cancel = (Button) v.findViewById(R.id.cancel);
		final Button reply = (Button) v.findViewById(R.id.reply);
		final EditText message = (EditText) v.findViewById(R.id.editText1);
		
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				listener.onCancelPressed(ReplyDialog.this);
				
			}
			
		});
		
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				if(message.getText().toString().length() != 0) {
					
					reply.setEnabled(true);
					
				} else {
					reply.setEnabled(false);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
		};
		
		message.addTextChangedListener(watcher);
		
		reply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
				listener.onReplyPressed(ReplyDialog.this, v);
				
			}
			
		});
		
		builder.setTitle("Leave Comment").setView(v);
		
		return builder.create();
	}
	
}
