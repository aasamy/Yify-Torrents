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

public class LoginDialog extends DialogFragment {
	
	public interface LoginDialogListener {
		public void onSignInPressed(DialogFragment fragment, View v, String userinput, String passinput);
	}
	
	LoginDialogListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (LoginDialogListener) activity;
		}catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement logindialoglistener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.login_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		final EditText user = (EditText) v.findViewById(R.id.username);
		final EditText pass = (EditText) v.findViewById(R.id.password);
		final Button signin = (Button) v.findViewById(R.id.signinbutton);
		
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				if(user.getText().toString().length() != 0) {
					
					if(pass.getText().toString().length() != 0) {
						
						signin.setEnabled(true);
						
					} else {
						signin.setEnabled(false);
					}
					
				} else {
					signin.setEnabled(false);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
		};
		
		pass.addTextChangedListener(watcher);
		user.addTextChangedListener(watcher);
		
		signin.setEnabled(false);
		
		signin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				listener.onSignInPressed(LoginDialog.this, v, user.getText().toString(), pass.getText().toString());
				
			}
			
		});
		
		builder.setTitle("Please sign in").setView(v);
		
		
		final Dialog dia =  builder.create();
		
		
		
		return dia;
		
	}

}
