package org.moa.etlits.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.moa.etlits.R;
import org.moa.etlits.databinding.ActivityLoginBinding;
import org.moa.etlits.ui.validation.LoginFormState;
import org.moa.etlits.ui.viewmodels.login.LoginResult;
import org.moa.etlits.ui.viewmodels.login.LoginViewModel;
import org.moa.etlits.ui.viewmodels.login.LoginViewModelFactory;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;
import org.moa.etlits.utils.NetworkUtil;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private AlertDialog.Builder builder;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private TextInputLayout passwordLayout;

    private EncryptedPreferences encryptedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        usernameEditText = binding.username;
        passwordEditText = binding.password;
        loginButton = binding.login;
        loadingProgressBar = binding.loading;
        passwordLayout = binding.passwordLayout;
        builder = new AlertDialog.Builder(LoginActivity.this);
        encryptedPreferences = new EncryptedPreferences(LoginActivity.this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initViewModels();
        attachEventListeners();
    }

    private void initViewModels() {
        loginViewModel = new ViewModelProvider(LoginActivity.this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginViewModel.getLoginFormState().observe(LoginActivity.this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }

                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(LoginActivity.this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getLoginStatus().equals(LoginResult.LoginStatus.FAIL) && loginResult.getError() != null) {
                    showLoginFailed(getMessage(loginResult.getError()));
                }
                if (loginResult.getLoginStatus().equals(LoginResult.LoginStatus.SUCCESS)) {
                    onLoginSuccess(loginResult);
                }
            }
        });
    }

    private void attachEventListeners() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordEditText.getText().toString().trim().length() > 0) {
                    passwordLayout.setEndIconVisible(true);
                } else {
                    passwordLayout.setEndIconVisible(false);
                }
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        if (NetworkUtil.isInternetConnected(LoginActivity.this)) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString().trim(),
                    passwordEditText.getText().toString().trim());
        } else {
            showLoginFailed(getString(R.string.no_internet_connection));
        }
    }

    private void onLoginSuccess(LoginResult loginResult) {
        encryptedPreferences.write(Constants.USERNAME, loginResult.getUsername());
        encryptedPreferences.write(Constants.PASSWORD, loginResult.getPassword());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getMessage(Integer errorCode) {
        if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return getString(R.string.login_unauthorized);
        } else if (errorCode == HttpURLConnection.HTTP_NOT_FOUND) {

            return getString(R.string.login_server_unreachable);
        } else {
            return getString(R.string.login_generic_error);
        }
    }
    private void showLoginFailed(String message) {
        builder.setTitle(R.string.login_alert_title).setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}