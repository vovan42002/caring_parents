package com.company.caringparents;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.company.caringparents.email_package.GMailSender;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    Button buttonSignIn, buttonRegister;
    RelativeLayout root;
    final String ip = "http://192.168.0.109:8080/parent";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        buttonSignIn = findViewById(R.id.buttonSingIn);
        buttonRegister = findViewById(R.id.buttonRegister);

        root = findViewById(R.id.root_element);
        new Thread(() -> buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterWindow();
            }
        })).start();
        new Thread(() -> buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInWindow();
            }
        })).start();
    }

    private void showSignInWindow() {
        final MaterialEditText email_sign_in = findViewById(R.id.email_field_sign_in);
        final MaterialEditText password_sign_in = findViewById(R.id.password_field_sign_in);
        final String urlCheckParent = ip+"/checkParent?&email=" + encrypt(email_sign_in.getText().toString()) +
                "&password=" + encrypt(password_sign_in.getText().toString());
        if (checkingExist(urlCheckParent)) {
            Global.email = email_sign_in.getText().toString();
            Global.password = password_sign_in.getText().toString();
            final String urlGetParentId = ip+"/getParentId?email="+encrypt(email_sign_in.getText().toString())
                    +"&password="+ encrypt(password_sign_in.getText().toString());
            Global.id = getParentId(urlGetParentId);
            System.out.println("Global id="+Global.id);
            Intent intent = new Intent(this, Activity.class);
            startActivity(intent);
        } else
            Snackbar.make(root, "Неправильный логин или пароль!", Snackbar.LENGTH_SHORT).show();
    }


    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText nick = register_window.findViewById(R.id.nickField);
        final MaterialEditText password = register_window.findViewById(R.id.passwordField);



        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Введите почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nick.getText().toString())) {
                    Snackbar.make(root, "Введите никнейм", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() == 0) {
                    Snackbar.make(root, "Введите пароль", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (password.getText().toString().length() < 6) {
                    Snackbar.make(root, "Пароль слишком короткий", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (password.getText().toString().length() > 50) {
                    Snackbar.make(root, "Пароль слишком долгий", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(isValidEmail(email.getText().toString()) == false){
                    System.out.println("Email valid: "+isValidEmail(email.getText().toString()));
                    Snackbar.make(root, "Неправильная почта", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final String urlExistNick = ip+"/existNick?nickname=" + encrypt(nick.getText().toString());
                final String urlExistEmail = ip+"/existEmail?email="+encrypt(email.getText().toString());
                final String urlCheckParent = ip+"/checkParent?&email=" + encrypt(email.getText().toString()) +
                        "&password=" + encrypt(password.getText().toString());
                if (checkingExist(urlExistNick) == false) {
                    if(checkingExist(urlExistEmail)){
                        Snackbar.make(root, "Данная почта уже используется", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Регистрация пользователя
                    JSONObject js = new JSONObject();
                    try {
                        js.put("nickname", encrypt(nick.getText().toString()));
                        js.put("password", encrypt(password.getText().toString()));
                        js.put("email", encrypt(email.getText().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String url = ip+"/create";
                    System.out.println(url);
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) new URL(url).openConnection();

                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; utf-8");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);

                        connection.connect();

                        //send JSON
                        OutputStream os = connection.getOutputStream();
                        os.write(js.toString().getBytes("UTF-8"));
                        os.close();

                        StringBuilder sb = new StringBuilder();

                        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                            System.out.println("Checking existing parent");
                            if (checkingExist(urlCheckParent)) {
                                //send message to email
                                new Thread(() -> {
                                    try {
                                        GMailSender sender = new GMailSender("volodimirbogdan4@gmail.com",
                                                "4ftj2002");
                                        sender.sendMail("Hello from Caring Parents", "Спасибо за регистрацию в нашем " +
                                                        "приложении Caring Parents. Теперь контролировать ваших детей будет намного проще",
                                                "volodimirbogdan4@gmail.com", email.getText().toString());
                                    } catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }
                                }).start();

                                //initial global variables
                                Global.email = email.getText().toString(); //global email
                                Global.password = password.getText().toString();//global pass
                                //get parent id
                                final String urlGetParentId = ip+"/getParentId?email="+encrypt(email.getText().toString())
                                        +"&password="+ encrypt(password.getText().toString());
                                Global.id = getParentId(urlGetParentId); //global id
                                System.out.println("Global id="+Global.id);
                                //start Activity
                                Intent intent = new Intent(LoginActivity.this, Activity.class);
                                startActivity(intent);
                            }
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                                sb.append("\n");
                            }
                            System.out.println(sb.toString());
                        } else {
                            System.out.println("FAIL: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
                        }

                    } catch (Throwable cause) {
                        cause.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                } else if (checkingExist(urlExistNick) == true){
                    Snackbar.make(root, "Данный ник уже используется", Snackbar.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        dialog.show();

    }

    private boolean checkingExist(String url) {
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String output, one = "true";
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
                System.out.println("Никнейм существует: "+sb.toString());
                if (sb.toString().hashCode() == one.hashCode()) {
                    return true;
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private Long getParentId(String url) {
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
                System.out.println("Никнейм существует: "+sb.toString());
            }
            if (sb != null){
                return Long.valueOf(sb.toString());
            } else return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String encrypt(String str) {
        MessageDigest md5 = null;
        try { md5 = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        byte[] bytes = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes){
            sb.append(String.format("%02X", b));
        }
        System.out.println(sb);
        return sb.toString();
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}















