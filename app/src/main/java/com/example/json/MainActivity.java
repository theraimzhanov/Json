package com.example.json;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button button0, button1, button2, button3;
    private ImageView imageViewStar;
    private String url = "https://cinewest.ru/amerikanskie-aktery-top-50-gollivudskih-muzhchin/";
    private ArrayList<String> urls;
    private ArrayList<String> names;
    private int numberOfQuestion,numberOfRightAnswer;
    private ArrayList<Button> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageViewStar = findViewById(R.id.imageViewStar);
        urls = new ArrayList<>();
        names = new ArrayList<>();
        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        getContent();
        platGame();
    }
    private void getContent() {
        DownloadContentTask task = new DownloadContentTask();
        try {
            String content = task.execute(url).get();
            String start = "<p>Представляем список американских актеров мужчин: голливудских мачо, комедийных актеров, заслуженных ветеранов экрана и их более молодых коллег.</p>";
            String end = "<div class='code-block code-block-2' style='margin: 8px 0; clear: both;'>";
            Pattern pattern = Pattern.compile(start + "(.*?)" + end);
            Matcher matcher = pattern.matcher(content);
            String splContent = "";
            while (matcher.find()) {
                splContent = matcher.group(1);
            }
            Pattern patternImage = Pattern.compile("href=\"(.*?)\"");
            Pattern patternName = Pattern.compile("style=\"text-align: center;\">(.*?)</");
            Matcher matcherImage = patternImage.matcher(splContent);
            Matcher matcherName = patternName.matcher(splContent);
            while (matcherImage.find()) {
                urls.add(matcherImage.group(1));
            }
            while (matcherName.find()) {
                names.add(matcherName.group(1));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void platGame() {
        generateQuestion();
        Bitmap bitmap = null;
        DownloadImageTask task = new DownloadImageTask();
        try {
          bitmap  = task.execute(urls.get(numberOfQuestion)).get();
            if (bitmap != null) {
                imageViewStar.setImageBitmap(bitmap);
                for (int i = 0; i < buttons.size(); i++) {
                    if (i == numberOfRightAnswer) {
                        buttons.get(i).setText(names.get(numberOfQuestion));
                    } else {
                        int wrongAnswer = generateWrongAnswer();
                        buttons.get(i).setText(names.get(wrongAnswer));
                    }
                    imageViewStar.setImageBitmap(bitmap);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generateQuestion() {
        numberOfQuestion = (int) (Math.random() * names.size());
        numberOfRightAnswer = (int) (Math.random() * buttons.size());
    }

    private int generateWrongAnswer() {
        return (int) (Math.random() * names.size());
    }

    public void onClickAnswer(View view) {
        platGame();
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) == numberOfRightAnswer) {
            Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неверно, правильный ответ: " + names.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
        }
    }

    private static class DownloadContentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection connection = null;
            StringBuilder builder = new StringBuilder();
            try {
                url = new URL(strings[0]);
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = bufferedReader.readLine();
                    }
                    return builder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection connection = null;
            StringBuilder builder = new StringBuilder();
            try {
                url = new URL(strings[0]);
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL uri = null;
            HttpURLConnection connection = null;
            try {
                uri = new URL(strings[0]);
                connection = (HttpURLConnection) uri.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result.toString();
        }
    }

    private static class DownLoadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
    }
}