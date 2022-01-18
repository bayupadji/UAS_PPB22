package android.example.loginuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUp extends AppCompatActivity {
    TextInputEditText txt_fullname, txt_username, txt_password, txt_email;
    Button btnSignUp;
    TextView tv_login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txt_fullname = findViewById(R.id.fullname);
        txt_username = findViewById(R.id.username);
        txt_password = findViewById(R.id.password);
        txt_email = findViewById(R.id.email);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tv_login = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname, username, email, password;
                fullname = String.valueOf(txt_fullname.getText());
                username = String.valueOf(txt_username.getText());
                email = String.valueOf(txt_email.getText());
                password = String.valueOf(txt_password.getText());

                if (!fullname.equals("") && !username.equals("") && !email.equals("") && !password.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String[] field = new String[4];
                            field[0] = "fullname";
                            field[1] = "username";
                            field[2] = "password";
                            field[3] = "email";
                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = fullname;
                            data[1] = username;
                            data[2] = password;
                            data[3] = email;

                            PutData putData = new PutData("http://192.168.100.4/LoginRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                        if (result.equals("Sign Up Success")){
                                            Intent intent = new Intent(getApplicationContext(),Login.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                        }
                                    Log.i("PutData", result);
                                }
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}