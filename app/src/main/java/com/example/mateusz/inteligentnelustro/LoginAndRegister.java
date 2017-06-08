package com.example.mateusz.inteligentnelustro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAndRegister extends AppCompatActivity {
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);

        mProgress = new ProgressDialog(LoginAndRegister.this);

        mProgress.setMessage("Logowanie...");
        mProgress.setCancelable(false);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setIndeterminate(true);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView tvRegister = (TextView) findViewById(R.id.Register);

        tvRegister.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginAndRegister.this,Register.class);
                LoginAndRegister.this.startActivity(registerIntent);

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success){

                                mProgress.dismiss();

                                Intent intent = new Intent(LoginAndRegister.this, MainActivity.class);

                                LoginAndRegister.this.startActivity(intent);
                            } else {
                                mProgress.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginAndRegister.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginAndRegister.this);
                queue.add(loginRequest);
            }
        });
    }





}
