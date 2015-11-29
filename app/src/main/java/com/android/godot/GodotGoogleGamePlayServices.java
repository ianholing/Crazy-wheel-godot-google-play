package com.android.godot;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.dualgames.crazywheel.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

public class GodotGoogleGamePlayServices extends Godot.SingletonBase {

    private static final int                RC_SAVED_GAMES = 9002;
    private static final int                RC_SIGN_IN = 9001;
    private static final int                REQUEST_ACHIEVEMENTS = 9002;
    private static final int                REQUEST_LEADERBOARD = 1337;
    private int                             mDevice_id;
    private Activity                        mActivity;
    private GoogleApiClient                 mGoogleApiClient;
    private Boolean                         mRequestSignIn = false;
    private Boolean                         mIntentInProgress = false;
    private Boolean                         mGooglePlayConnected = false;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    static public Godot.SingletonBase initialize(Activity p_activity) {
        Log.d("------- godot --------", "init GGPGS MODULE");
        return new GodotGoogleGamePlayServices(p_activity);
    }

    public GodotGoogleGamePlayServices(Activity pActivity) {
        mActivity = pActivity;
        Log.d("------- godot --------", "REGISTER GGPGS Singleton");
        registerClass("bbGGPS", new String[] {
                "init_GGPGS", "sign_in", "unlock_achy", "increment_achy", "show_achy_list",
                "is_logged_in", "sign_out", "show_leaderboard", "submit_score"
        });
    }
     
    protected void onMainDestroy() {
        disconnect();
    }


    public void init_GGPGS(int device_id) {
        Log.d("------- godot --------", "init_GGPGS MODULE");
        mDevice_id = device_id;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                        .addConnectionCallbacks(new ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle m_bundle) {
                                Log.d("------- godot --------", "connectioncallbacks on connected ");
                                mGooglePlayConnected = true;
                                Log.d("------- godot --------", "calling godot above ");
                            }

                            @Override
                            public void onConnectionSuspended(int m_cause) {
                                Log.w("------- godot --------", "connectioncallbacks onConnectionSuspended int cause " + String.valueOf(m_cause));
                                mGoogleApiClient.connect();
                            }
                        })
                        .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult mResult) {
                                Log.w("------- godot --------", "onConnectionFailed result code: " + String.valueOf(mResult));
                                if (mResolvingConnectionFailure) {
                                    // already resolving
                                    return;
                                }

                                // if the sign-in button was clicked or if auto sign-in is enabled,
                                // launch the sign-in flow
                                if (mSignInClicked || mAutoStartSignInflow) {
                                    mAutoStartSignInflow = false;
                                    mSignInClicked = false;
                                    mResolvingConnectionFailure = true;

                                    // Attempt to resolve the connection failure using BaseGameUtils.
                                    // The R.string.signin_other_error value should reference a generic
                                    // error string in your strings.xml file, such as "There was
                                    // an issue with sign-in, please try again later."
                                    if (!BaseGameUtils.resolveConnectionFailure(mActivity,
                                            mGoogleApiClient, mResult,
                                            RC_SIGN_IN, mActivity.getString(R.string.gamehelper_unknown_error))) {
                                        mResolvingConnectionFailure = false;
                                    }
                                }
                            }
                        })
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
//                        .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                        .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                        .build();

                sign_in();
            }
        });
    }

    public void sign_in() {
        Log.d("------- godot --------", "sign in into google game play services");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });
    }

    public void sign_out() {
        Log.d("------- godot --------", "sign out from google game play services");
        disconnect();
    }

    public void disconnect() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSignInClicked = false;
                if (mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGooglePlayConnected = false;
                }
                Log.d("------- godot --------", "disconnecting from google game play services");
            }
        });
    }

    public boolean is_logged_in() {
//        Log.w("------- godot --------", "Is user Logged in: " + (mGooglePlayConnected ? " true" : " false"));
//        GodotLib.calldeferred(mDevice_id, "is_user_logged_in", new Object[]{(boolean) mGooglePlayConnected});
        return mGooglePlayConnected;
    }

    public void increment_achy(final String achievement_id, final int increment_amount) {
        if(mGooglePlayConnected) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Games.Achievements.increment(mGoogleApiClient, achievement_id, increment_amount);
                }
            });
        } else {
            Log.w("------- godot --------", "trying to make Google Play Game Services calls before connected, try calling signIn first");
            return;
        }
    }

    public void unlock_achy(final String achievement_id) {
        if(mGooglePlayConnected) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Games.Achievements.unlock(mGoogleApiClient, achievement_id);
                }
            });
        } else {
            Log.w("------- godot --------", "trying to make Google Play Game Services calls before connected, try calling signIn first");
            return;
        }
    }

    public void show_achy_list() {
        if(mGooglePlayConnected) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                }
            });
        } else {
            Log.w("------- godot --------", "trying to make Google Play Game Services calls before connected, try calling signIn first");
            return;
        }
    }

    public void show_leaderboard(final String leaderboardID) {
        if(mGooglePlayConnected) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), REQUEST_LEADERBOARD);
                }
            });
        } else {
            Log.w("------- godot --------", "trying to make Google Play Game Services calls before connected, try calling signIn first");
            return;
        }
    }

    public void submit_score(final String leaderboardID, final int score) {
        Log.w("------- godot --------", "Submit Score: " + score);
        if(mGooglePlayConnected) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
                }
            });
        } else {
            Log.w("------- godot --------", "trying to make Google Play Game Services calls before connected, try calling signIn first");
            return;
        }
    }

    protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(mActivity,
                        requestCode, resultCode, R.string.gamehelper_unknown_error);
            }
        }
    }
}






