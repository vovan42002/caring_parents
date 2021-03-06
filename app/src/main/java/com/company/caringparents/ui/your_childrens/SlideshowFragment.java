package com.company.caringparents.ui.your_childrens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.company.caringparents.Global;
import com.company.caringparents.R;
import com.company.caringparents.app.AppActivity;
import com.company.caringparents.databinding.FragmentSlideshowBinding;
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
import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment {
    private List<String> list;
    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;
    private Button buttonAddChild;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        buttonAddChild = root.findViewById(R.id.buttonAddChild);
        buttonAddChild.setOnClickListener(view -> showRegisterWindow());

        final String urlCheckParent = Global.ip + "/parent/checkParent?&email=" + encrypt(Global.email) +
                "&password=" + encrypt(Global.password);

        if (checkingExist(urlCheckParent) == true) {

            final String urlListChild = Global.ip + "/parent/listChilds?idParent=" + Global.id;
            list = listChild(urlListChild);

            ListView listView = root.findViewById(R.id.list_childs);

            if (listChild(urlListChild) == null) {
                System.out.println("List of child's is empty");
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_child, list);
                listView.setAdapter(adapter);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    System.out.println(((TextView)view).getText());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Global.child_name = ((TextView)view).getText().toString();
                            String urlGetChildId = Global.ip+"/child/getChildId?idParent="+Global.id+"&name="+Global.child_name;
                            Long idChild = getChildId(urlGetChildId);
                            if (idChild != null) {
                                Global.child_id = idChild;
                                Intent intent = new Intent(getContext(), AppActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).start();

                }
            });
        }
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<String> listChild(String url) {
        List<String> list = new ArrayList<>();
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
                sb.append(output).append("\n");
                list.add(output);
            }
            if (sb != null)
                return list;
            else return list;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
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
                System.out.println("?????????????? ????????????????????: " + sb.toString());
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

    public static String encrypt(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        System.out.println(sb);
        return sb.toString();
    }


    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("???????????????????? ??????????????");
        dialog.setMessage("?????????????? ???????????? ?????? ????????????????????");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View register_window = inflater.inflate(R.layout.register_window_child, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText nick = register_window.findViewById(R.id.nickField);
        final MaterialEditText password = register_window.findViewById(R.id.passwordField);


        dialog.setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        dialog.setPositiveButton("????????????????", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(email.getText().toString())) {
                Snackbar.make(binding.getRoot(), "?????????????? ??????????", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(nick.getText().toString())) {
                Snackbar.make(binding.getRoot(), "?????????????? ??????", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().length() == 0) {
                Snackbar.make(binding.getRoot(), "?????????????? ????????????", Snackbar.LENGTH_SHORT).show();
                return;
            } else if (password.getText().toString().length() < 6) {
                Snackbar.make(binding.getRoot(), "???????????? ?????????????? ????????????????", Snackbar.LENGTH_SHORT).show();
                return;
            } else if (password.getText().toString().length() > 50) {
                Snackbar.make(binding.getRoot(), "???????????? ?????????????? ????????????", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (isValidEmail(email.getText().toString()) == false) {
                System.out.println("Email valid: " + isValidEmail(email.getText().toString()));
                Snackbar.make(binding.getRoot(), "???????????????????????? ??????????", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // ???????????????????? ??????????????
            JSONObject js = new JSONObject();
            try {
                js.put("name", nick.getText().toString());
                js.put("latitude", "50.450001");
                js.put("longitude", "30.523333");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String url = Global.ip + "/child/add?parent_id=" + Global.id;
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
                    //send message to email
                    new Thread(() -> {
                        try {
                            GMailSender sender = new GMailSender("volodimirbogdan4@gmail.com",
                                    "4ftj2002");
                            sender.sendMail("Hello from Caring Parents", "???? ?????????????? ???????????????? ??????????????. " +
                                            "?????? ?????? ?????????????????????? ?? ?????????????? ????????????????????:" + nick.getText().toString(),
                                    "volodimirbogdan4@gmail.com", email.getText().toString());
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }).start();

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
        });
        dialog.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private Long getChildId(String url) {
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
                System.out.println("?????????????? ????????????????????: " + sb.toString());
            }
            if (sb != null) {
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
}