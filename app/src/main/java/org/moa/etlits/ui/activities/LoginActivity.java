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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.moa.etlits.R;
import org.moa.etlits.databinding.ActivityLoginBinding;
import org.moa.etlits.ui.viewmodels.login.LoginResult;
import org.moa.etlits.ui.viewmodels.login.LoginViewModel;
import org.moa.etlits.ui.viewmodels.login.LoginViewModelFactory;
import org.moa.etlits.utils.EncryptedPreferences;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private AlertDialog.Builder builder;

    private EncryptedPreferences encryptedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextInputLayout passwordLayout = binding.passwordLayout;
        builder = new AlertDialog.Builder(LoginActivity.this);
        encryptedPreferences = new EncryptedPreferences(LoginActivity.this);


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
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getLoginStatus().equals(LoginResult.LoginStatus.SUCCESS)) {

                    //TODO: save credentials for use in sync
                    encryptedPreferences.write("username", loginResult.getUsername());

                    updateUiWithUser(loginResult);
                    navigateToHome();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
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
                    loginViewModel.login(usernameEditText.getText().toString().trim(),
                            passwordEditText.getText().toString().trim());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });
    }


    private void updateUiWithUser(LoginResult loginResult) {
        String welcome = getString(R.string.welcome) + loginResult.getUsername();
        // TODO : initiate successful logged in experience
        if (getApplicationContext() != null) {
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorCode) {
        if (errorCode == 401) {
            builder.setTitle(R.string.login_alert_title).setMessage(R.string.login_unauthorized);
        } else if (errorCode == 404) {
            builder.setTitle(R.string.login_alert_title) .setMessage(R.string.login_server_unreachable);
        } else {
            builder.setTitle(R.string.login_alert_title) .setMessage(R.string.login_generic_error);
        }
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

     /**
     * Data validation state of the login form.
     */
    public static class LoginFormState {
        @Nullable
        private Integer usernameError;
        @Nullable
        private Integer passwordError;
        private boolean isDataValid;

        public LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
            this.usernameError = usernameError;
            this.passwordError = passwordError;
            this.isDataValid = false;
        }

        public LoginFormState(boolean isDataValid) {
            this.usernameError = null;
            this.passwordError = null;
            this.isDataValid = isDataValid;
        }

        @Nullable
        public Integer getUsernameError() {
            return usernameError;
        }

        @Nullable
        public Integer getPasswordError() {
            return passwordError;
        }

        public boolean isDataValid() {
            return isDataValid;
        }
    }
}