package org.moa.etlits.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

public final class InternetConnectionChecker extends LiveData<Boolean> {
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onActive() {
        super.onActive();
        if (this.connectivityManager != null && this.networkCallback != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            this.connectivityManager.registerNetworkCallback(builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), (ConnectivityManager.NetworkCallback)this.networkCallback);
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onInactive() {
        super.onInactive();
        if (this.connectivityManager != null && this.networkCallback != null) {
            this.connectivityManager.unregisterNetworkCallback((ConnectivityManager.NetworkCallback)this.networkCallback);
        }
    }

    public InternetConnectionChecker(@NotNull ConnectivityManager connectivityManager) {
        super();
        this.connectivityManager = connectivityManager;
        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(@NotNull Network network) {
                super.onAvailable(network);
                Log.d("ContentValues", "onAvailable: Network " + network + " is Available");
                InternetConnectionChecker.this.postValue(true);
            }

            @RequiresApi(Build.VERSION_CODES.M)
            public void onCapabilitiesChanged(@NotNull Network network, @NotNull NetworkCapabilities networkCapabilities) {
                boolean isInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                Log.d("ContentValues", "networkCapabilities: " + network + ' ' + networkCapabilities);
                boolean isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                if (isValidated) {
                    Log.d("ContentValues", "hasCapability: " + network + ' ' + networkCapabilities);
                } else {
                    Log.d("ContentValues", "Network has No Connection Capability: " + network + ' ' + networkCapabilities);
                }

                InternetConnectionChecker.this.postValue(isInternet && isValidated);
            }

            public void onLost(@NotNull Network network) {
                super.onLost(network);
                Log.d("ContentValues", "onLost: " + network + " Network Lost");
                InternetConnectionChecker.this.postValue(false);
            }
        };
    }

    public InternetConnectionChecker(@NotNull Application appContext) {
      this.connectivityManager = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
