package com.lisnx.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.lisnx.service.BaseActivity;

public class EditItemActivity extends BaseActivity implements OnClickListener, OnKeyListener {
	
	private View save;
	private View cancel;
	private EditText inputValue;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item);
		save = (View)findViewById(R.id.saveButton);
		save.setOnClickListener(this);
		cancel = (View)findViewById(R.id.cancelButton);
		
		cancel.setOnClickListener(this);
		inputValue = (EditText)findViewById(R.id.inputValue);
	}

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.cancelButton:
			finish();
			break;
		case R.id.saveButton:
			String value = inputValue.getText().toString();
			Intent data = new Intent();
			 data.putExtra("newValue", value);
			 setResult(RESULT_OK,data);
			finish();
			break;
		default:
			break;
		}
		
	}

}
