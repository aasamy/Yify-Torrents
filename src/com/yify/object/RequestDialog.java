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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class RequestDialog extends DialogFragment {
	
	public interface RequestListener {
		public void onRequestPressed(DialogFragment fragment, View view);
		public void onCancelPressed(DialogFragment fragment);
	}
	
	RequestListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (RequestListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " has to implement ReplyDialog.ReplyListener");
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.request_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		Spinner type = (Spinner) v.findViewById(R.id.requestType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.entries_array, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		type.setAdapter(adapter);
		
		final EditText message = (EditText) v.findViewById(R.id.query);
		final Button request = (Button) v.findViewById(R.id.request);
		Button cancel = (Button) v.findViewById(R.id.request_cancel);
		
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				
				if(message.getText().toString().length() != 0) {
					
					request.setEnabled(true);
					
				} else {
					request.setEnabled(false);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
		};
		
		message.addTextChangedListener(watcher);
		
		request.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				listener.onRequestPressed(RequestDialog.this, v);
				
			}
			
		});
		
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				listener.onCancelPressed(RequestDialog.this);
				
			}
			
		});
		
		builder.setTitle("Request Movie").setView(v);
		
		return builder.create();
		
		
	}

}
