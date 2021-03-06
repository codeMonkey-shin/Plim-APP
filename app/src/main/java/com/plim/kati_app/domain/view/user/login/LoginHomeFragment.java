package com.plim.kati_app.domain.view.user.login;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.plim.kati_app.R;
import com.plim.kati_app.domain.asset.KatiDialog;
import com.plim.kati_app.domain.model.KatiViewModel;
import com.plim.kati_app.domain.model.room.KatiData;
import com.plim.kati_app.domain.model.room.KatiDatabase;
import com.plim.kati_app.domain.view.MainActivity;
import com.plim.kati_app.domain.view.user.findPW.FindPasswordActivity;
import com.plim.kati_app.domain.view.user.register.RegisterActivity;

import java.util.List;

import kotlin.Pair;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

public class LoginHomeFragment extends Fragment {

    // Associate
    // View
    private EditText idText, pwText;
    private Button loginButton, idFindButton, pwFindButton, accountCreateButton;

    /**
     * System Callback
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login_home_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.idText = view.findViewById(R.id.loginActivity_idText);
        this.pwText = view.findViewById(R.id.loginActivity_pwText);
        this.loginButton = view.findViewById(R.id.loginActivity_loginButton);
        this.idFindButton = view.findViewById(R.id.loginActivity_idFindButton);
        this.pwFindButton = view.findViewById(R.id.loginActivity_pwFindButton);
        this.accountCreateButton = view.findViewById(R.id.loginActivity_accountCreateButton);

        //????????? ??? ?????? ???????????? ?????? ??????????????? ??????. ?????? ????????? ???.
//        this.idText.setText("remember@rem.com");
//        this.pwText.setText("1234");

        this.pwFindButton.setOnClickListener(v->this.startActivity(new Intent(this.getContext(), FindPasswordActivity.class)));

        new Thread(() -> {
            KatiDatabase database = KatiDatabase.getAppDatabase(this.getContext());
            if (database.katiDataDao().getValue(KatiDatabase.AUTHORIZATION) != null) {
                getActivity().runOnUiThread(() ->
                    KatiDialog.simpleAlertDialog(getContext(),"?????? ????????? ?????? ????????????.","????????? ?????? ????????????.",(dialog, which) -> startMainActivity(),
                            this.getResources().getColor(R.color.kati_coral, getActivity().getTheme())).showDialog()
                );
            }
        }).start();

        /*
         * ???????????? ?????? ??????
         */
        this.accountCreateButton.setOnClickListener(v -> {
            this.startActivity(new Intent(getContext(), RegisterActivity.class));
        });

        /*
         * ????????? ?????? ??????
         * 401 -> ?????? ??????
         * 500 -> ?????? ??????
         * 200 -> ?????? ??????
         * */
        this.loginButton.setOnClickListener(v -> {
            String email = idText.getText().toString();
            String password = pwText.getText().toString();

            LoginRequest login = new LoginRequest(email, password);
            RetrofitClient retrofitClient = new RetrofitClient();
            Call<LoginRequest> call = retrofitClient.apiService.postRetrofitData(login);
            call.enqueue(new Callback<LoginRequest>() {
                @Override
                public void onResponse(Call<LoginRequest> call, Response<LoginRequest> response) {
                    if (response.isSuccessful()) {
                        Log.d("?????? ????????? : ", "code : " + response.code());

                        KatiDialog.simpleAlertDialog(
                                getContext(),
                                "??????????????? ????????????????????????.",
                                "??????????????? ????????????????????????.",
                                (dialog, which) -> {
                                    //??????????????? ???????????? ????????? ??????.
                                    KatiDatabase database = KatiDatabase.getAppDatabase(getContext());
                                    new Thread(() -> database.katiDataDao().insert(new KatiData(KatiDatabase.AUTHORIZATION, response.headers().get(KatiDatabase.AUTHORIZATION)))).start();
                                    startMainActivity();
                                }, getResources().getColor(R.color.kati_coral, getContext().getTheme())).showDialog();

                    } else {
                        Log.e("?????? ???????????? : ", "error code : " + response.code());
                        KatiDialog.simpleAlertDialog(getContext(),
                                "???????????? ????????? ????????????.",
                                "?????? ?????????????????? ???????????? ????????? ?????? ??? ????????????.",
                                (dialog, which) -> {
                                    idText.setText("");
                                    pwText.setText("");
                                }
                                , getResources().getColor(R.color.kati_coral, getContext().getTheme())).showDialog();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<LoginRequest> call, Throwable t) {
                    Log.e("????????????", t.getMessage());
                }
            });

        });
    }

    /**
     * ?????? ??????????????? ????????????.
     */
    public void startMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

}