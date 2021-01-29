package com.goatsandtigers.crypcross;

import java.net.URL;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class CrypticCrosswordActivity extends Activity implements CrosswordController {

    private static final String PUZZLE_SOLVED_KEY = "PUZZLE_SOLVED_KEY";
    public static final String KEY_SCOREBOARD_DATA = "KEY_SCOREBOARD_DATA";
    public static final String KEY_PUZZLE_FILENAME = "KEY_PUZZLE_FILENAME";
    private Crossword crossword;
    private AdView adView;
    private CrosswordView crosswordView;
    private CrosswordCluesLayout crosswordCluesLayout;
    private ScrollView cluesScroll;
    private LinearLayout rootLayout;
    private View keyboardView;
    private boolean keyboardVisible;
    private ClueToRevealAnswerFor clueToRevealAnswerFor;
    private long startTime;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crossword = new Crossword(this, getPuzzleFilename());
        createCrosswordView();
        createCluesView();
        createKeyboardView();
        rootLayout = new LinearLayout(this) {
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                int crosswordWidth = w - (2 * getCrosswordhorizontalPadding());
                crosswordView.getLayoutParams().width = crosswordWidth;
                crosswordView.getLayoutParams().height = crosswordWidth;
            }
        };
        rootLayout.addView(buildAdView());
        rootLayout.addView(crosswordView);
        rootLayout.addView(cluesScroll);
        rootLayout.addView(keyboardView);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        // setContentView(R.layout.activity_cryptic_crossword);
        setContentView(rootLayout);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private AdView buildAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        //adView.setAdUnitId("ca-app-pub-7942129400584060/");
        adView.setAdUnitId("ca-app-pub-7942129400584060/9134341538");
        // Line below is test ID. Do not use.
        //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(params);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        return adView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTimeTaken();
    }

    private void saveTimeTaken() {
        long previousTime = getTimeTakenForPuzzle();
        long additionalTime = System.currentTimeMillis() - startTime;
        saveTimeTakenForPuzzle(previousTime + additionalTime);
    }

    private void saveTimeTakenForPuzzle(long timeTaken) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(buildTimeTakenKey(), timeTaken).commit();
    }

    private long getTimeTakenForPuzzle() {
        return PreferenceManager.getDefaultSharedPreferences(this).getLong(buildTimeTakenKey(), 0);
    }

    private String buildTimeTakenKey() {
        return "TIME_TAKEN" + getPuzzleFilename();
    }

    private int getCrosswordhorizontalPadding() {
        return isTablet(this) ? 35 : 100;
    }

    private void createCluesView() {
        crosswordCluesLayout = new CrosswordCluesLayout(this, crossword, this);
        cluesScroll = new ScrollView(this);
        cluesScroll.addView(crosswordCluesLayout);
        cluesScroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        crosswordCluesLayout.updateFontSize();
    }

    private void createCrosswordView() {
        crosswordView = new CrosswordView(this, crossword, this);
        crosswordView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private int getKeyboardHeight() {
        return isTablet(this) ? 140 : 340;
    }

    public static boolean isTablet(Context context) {
        try {
            // Compute screen size
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float screenWidth = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));

            // Tablet devices should have a screen size greater than 6 inches
            return size >= 6;
        } catch (Throwable t) {
            return false;
        }
    }

    private String getPuzzleFilename() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_PUZZLE_FILENAME, "1_1.txt");
    }

    private String getPuzzleIdForScoreboard() {
        return getPuzzleFilename().split("\\.")[0];
    }

    private void createKeyboardView() {
        keyboardView = new KeyboardView(this, this);
        keyboardView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getKeyboardHeight()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cryptic_crossword, menu);
        this.menu = menu;
        updateMenuFontSelection(FontSize.getSelectedFontSize(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_reveal_letter) {
            revealLetter();
            return true;
        } else if (id == R.id.action_reveal_word) {
            revealWord();
            return true;
        } else if (id == R.id.action_view_scoreboard) {
            showScoreboard();
        } else if (id == R.id.action_select_puzzle) {
            showMainMenu();
        } else if (id == R.id.action_medium_sized_fonts) {
            setFontSize(FontSize.MEDIUM);
            updateMenuFontSelection(FontSize.MEDIUM);
        } else if (id == R.id.action_large_fonts) {
            setFontSize(FontSize.LARGE);
            updateMenuFontSelection(FontSize.LARGE);
        } else if (id == R.id.action_rate_this_app) {
            rateApp();
        }
        return super.onOptionsItemSelected(item);
    }

    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details?id=com.goatsandtigers.crypticcrosswords");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=com.goatsandtigers.crypticcrosswords");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void updateMenuFontSelection(FontSize fontSize) {
        menu.findItem(R.id.action_medium_sized_fonts).setChecked(fontSize == FontSize.MEDIUM);
        menu.findItem(R.id.action_large_fonts).setChecked(fontSize == FontSize.LARGE);
    }

    private void setFontSize(FontSize fontSize) {
        FontSize.setSelectedFontSize(this, fontSize);
        crosswordCluesLayout.updateFontSize();
    }

    private void showMainMenu() {
        Intent intent = new Intent(getBaseContext(), MainMenuActivity.class);
        startActivity(intent);
    }

    private void showScoreboard() {
        /*String result = "Bill,123,45,GB,android,JFE,22,19,US,android,Dan,678,19,DK,android,Colin,311,4,SK,android,Laura,156,7,GB,android,Anita,310,2,GB,android,Fawna,213,0,GB,android";
        CrypticCrosswordActivity.this.startScoreBoardActivity(result);*/
        if (!isPuzzleSolved()) {
            String msg = "The scoreboard for this puzzle will be viewable at any time once it has been solved.";
            new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(msg).setPositiveButton(android.R.string.yes, null)
                    .setIcon(R.drawable.ic_launcher).show();
        } else {
            LoadWebPageASYNC task = new LoadWebPageASYNC();
            String url = "http://goatsandtigers.com/crypcross2/return_best_times.php?puzzle=" + getPuzzleIdForScoreboard();
            task.execute(new String[] { url });
        }
    }

    private void revealWord() {
        crosswordView.revealSelectedWord();
        String msg = getAnswerAndExplanationForSelectedWord();
        new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(msg).setPositiveButton(android.R.string.yes, null)
                .setIcon(R.drawable.ic_launcher).show();
        checkForVictory();
    }

    private String getAnswerAndExplanationForSelectedWord() {
        if (clueToRevealAnswerFor == null) {
            return "No cell selected. Please select a crossword cell before choosing \"Reveal Word and Explain Answer\".";
        } else {
            String matchingCluePrefix = clueToRevealAnswerFor.clueNumber + ".";
            List<ClueAndExplanation> clueList =
                    clueToRevealAnswerFor.direction == Direction.RIGHT ? crossword.getAcrossClues() : crossword.getDownClues();
            for (ClueAndExplanation clueAndExplanation : clueList) {
                if (clueAndExplanation.clue.startsWith(matchingCluePrefix)) {
                    String clueNumberPrefix = clueAndExplanation.clue.split("\\.")[0] + ". ";
                    String clueWithAnswerLengthRemoved = clueAndExplanation.clue.split("\\.\\s\\(")[0] + ".";
                    String clueWithNumberRemoved = clueWithAnswerLengthRemoved.replace(clueNumberPrefix, "");
                    return clueWithNumberRemoved + "\n\n" + clueAndExplanation.explanation.replace(". ", ".\n");
                }
            }
        }
        return "";
    }

    private void revealLetter() {
        crosswordView.revealSelectedLetter();
        checkForVictory();
    }

    private class ClueToRevealAnswerFor {
        int clueNumber;
        Direction direction;

        public ClueToRevealAnswerFor(int clueNumber, Direction direction) {
            this.clueNumber = clueNumber;
            this.direction = direction;
        }
    }

    @Override
    public void onClueSelected(int clueNumber, Direction direction) {
        clueToRevealAnswerFor = new ClueToRevealAnswerFor(clueNumber, direction);
        crosswordView.highlightClue(clueNumber, direction);
        showKeyboard();
    }

    private void showKeyboard() {
        if (!keyboardVisible) {
            keyboardView.setVisibility(View.VISIBLE);
            cluesScroll.getLayoutParams().height = rootLayout.getHeight() - adView.getHeight() - crosswordView.getHeight() - getKeyboardHeight();
            keyboardView.getLayoutParams().height = getKeyboardHeight();
            keyboardVisible = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (keyboardVisible) {
            crosswordView.unhighlightAllSquares();
            hideKeyboard();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void hideKeyboard() {
        if (keyboardVisible) {
            keyboardView.getLayoutParams().height = 0;
            cluesScroll.getLayoutParams().height = rootLayout.getHeight() - crosswordView.getHeight();
            keyboardVisible = false;
        }
    }

    @Override
    public void scrollToClue(int clueNumber, Direction direction) {
        crosswordCluesLayout.scrollToClue(clueNumber, direction);
    }

    @Override
    public void onLetterPressed(String letter) {
        crosswordView.onLetterPressed(letter);
        checkForVictory();
    }

    private void checkForVictory() {
        if (!isPuzzleSolved() && crosswordView.isSolved()) {
            saveTimeTaken();
            markPuzzleSolved();
            askUserForNicknameForScoreboard();
        }
    }

    private void markPuzzleSolved() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(buildPuzzleSolvedKey(), true).commit();
    }

    private boolean isPuzzleSolved() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(buildPuzzleSolvedKey(), false);
    }

    private String buildPuzzleSolvedKey() {
        return PUZZLE_SOLVED_KEY + getPuzzleFilename();
    }

    public static boolean isPuzzleWithFilenameSolved(Context context, String filename) {
        String key = PUZZLE_SOLVED_KEY + filename;
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    private void askUserForNicknameForScoreboard() {
        final EnterNicknameDialog enterNicknameDialog = new EnterNicknameDialog(this);

        new AlertDialog.Builder(this).setTitle("Congratulations!").setMessage("Please enter your nickname to see the best times for this puzzle.")
                .setView(enterNicknameDialog).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                LoadWebPageASYNC task = new LoadWebPageASYNC();
                String nickname = enterNicknameDialog.getNickName();
                saveDefaultNickname(nickname);
                nickname = webifyNickname(nickname);
                int timeTakenInSeconds = (int) (getTimeTakenForPuzzle() / 1000);
                int numRevealedLetters = crosswordView.getNumRevealedLetters();
                String country = enterNicknameDialog.getCountryCode();
                String url =
                        "http://goatsandtigers.com/crypcross2/best_times.php?name=" + nickname + "&puzzle=" + getPuzzleIdForScoreboard()
                                + "&time=" + timeTakenInSeconds + "&revealedletters=" + numRevealedLetters + "&country=" + country
                                + "&platform=android";
                task.execute(new String[] { url });
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }

    private class LoadWebPageASYNC extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                response = new Scanner(new URL(urls[0]).openStream(), "UTF-8").useDelimiter("\\A").next();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO remove this hack
            //result =
            //        "Bill,123,45,GB,android,JFE,22,19,US,android,Dan,678,19,DK,android,Colin,311,4,SK,android,Laura,156,7,GB,android,Anita,310,2,GB,android,Fawna,213,0,GB,android";
            CrypticCrosswordActivity.this.startScoreBoardActivity(result);
        }
    }

    private void saveDefaultNickname(String nickname) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(EnterNicknameDialog.KEY_DEFAULT_NICKNAME, nickname).commit();
    }

    public void startScoreBoardActivity(String data) {
        if (data == null || data.length() == 0) {
            String msg = "Unable to retrieve score board data. Please check your internet connection.";
            new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(msg).setPositiveButton(android.R.string.yes, null)
                    .setIcon(R.drawable.ic_launcher).show();
        } else {
            Intent intent = new Intent(getBaseContext(), ScoreBoardActivity.class);
            intent.putExtra(KEY_SCOREBOARD_DATA, data);
            startActivity(intent);
        }
    }

    public static String webifyNickname(String nickname) {
        return nickname.replace(" ", "%20").replace("=", "").replace("&", "").replace("\n", "").replace("\r", "");
    }
}
