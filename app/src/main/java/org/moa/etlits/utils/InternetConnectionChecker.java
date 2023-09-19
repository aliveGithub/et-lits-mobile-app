package org.moa.etlits.utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.LiveData;

public final class InternetConnectionChecker extends LiveData<Boolean> {
    private final ConnectivityManager.NetworkCallback networkCallback;
    private final ConnectivityManager connectivityManager;


    protected void onActive() {
        super.onActive();
        if (this.connectivityManager != null && this.networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(this.networkCallback);
            } catch (Exception e) {
                Log.w("InternetConnectionChecker", "NetworkCallback not registered or already unregistered");
            }
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            this.connectivityManager.registerNetworkCallback(builder.build(), this.networkCallback);
        }
    }

    protected void onInactive() {
        super.onInactive();
        if (this.connectivityManager != null && this.networkCallback != null) {
            this.connectivityManager.unregisterNetworkCallback(this.networkCallback);
        }
    }

    public InternetConnectionChecker(@NotNull ConnectivityManager connectivityManager) {
        super();
        this.connectivityManager = connectivityManager;
        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(@NotNull Network network) {
                super.onAvailable(network);
                Log.d("InternetConnectionChecker", "onAvailable: Network " + network + " is Available");
                InternetConnectionChecker.this.postValue(true);
            }

           public void onCapabilitiesChanged(@NotNull Network network, @NotNull NetworkCapabilities networkCapabilities) {
                boolean isInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                Log.d("InternetConnectionChecker", "networkCapabilities: " + network + ' ' + networkCapabilities);
                boolean isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                if (isValidated) {
                    Log.d("InternetConnectionChecker", "hasCapability: " + network + ' ' + networkCapabilities);
                } else {
                    Log.d("InternetConnectionChecker", "Network has No Connection Capability: " + network + ' ' + networkCapabilities);
                }

                InternetConnectionChecker.this.postValue(isInternet && isValidated);
            }

            public void onLost(@NotNull Network network) {
                super.onLost(network);
                Log.d("InternetConnectionChecker", "onLost: " + network + " Network Lost");
                InternetConnectionChecker.this.postValue(false);
            }
        };
    }
}
