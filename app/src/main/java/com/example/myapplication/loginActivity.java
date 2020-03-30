package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Users;
import com.example.myapplication.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity
{
private EditText InputPhoneNumber , InputPassword;
private Button LoginButton;
private ProgressDialog loadingBar;
private TextView AdminLink , NotAdminLink;

private String parentDbname = "Users";
private CheckBox chkBoxRememberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton = (Button)findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText)findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText)findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        AdminLink = (TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView)findViewById(R.id.not_admin_panel_link);

        chkBoxRememberMe = (CheckBox)findViewById(R.id.remember_me_chkb);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) 
            {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbname = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbname = "Users";
            }
        });

    }

    private void LoginUser()
    {

        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
    {
        Toast.makeText(this, "Please type in your Phone Number...", Toast.LENGTH_SHORT).show();
    }

    else if(TextUtils.isEmpty(password))
    {
        Toast.makeText(this, "Please type in your Password...", Toast.LENGTH_SHORT).show();
    }

        else
        {
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait, While we are checking the credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phone , password);
        }


        }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbname).child(phone).exists())
                {
                    Users userData = dataSnapshot.child(parentDbname).child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                           if (parentDbname.equals("Admins"))
                           {
//                               Toast.makeText(loginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               Intent intent = new Intent(loginActivity.this,AdminCategoryActivity.class);

                               Prevalent.CurrentOnlineuser = userData; //Getting the ADMIN username for further purposes
                               startActivity(intent);
                           }
                           else if (parentDbname.equals("Users"))
                           {
                               Toast.makeText(loginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               Intent intent = new Intent(loginActivity.this,HomeActivity.class);

                              Prevalent.CurrentOnlineuser = userData;  //Getting the username for further purposes
                               startActivity(intent);
                           }
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            InputPassword.setText("");
                        }
                    }
                }

                else 
                {
                    Toast.makeText(loginActivity.this, "Account with this " + phone + " number does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    InputPassword.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
