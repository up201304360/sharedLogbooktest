package example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.deepstream.DeepstreamClient;
import io.deepstream.Record;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainApp extends Application {
    private Record record;
    private DeepstreamClient client;
    private TextArea textArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        root.getChildren().add(new Label("Shared Debriefing" + "\n" + "Date " + dateFormat.format(date)));


        textArea = new TextArea();
        root.getChildren().add(textArea);

        try {
            //TODO change to IP
            client = new DeepstreamClient("127.0.0.1:6020");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Shared Logbook");

        primaryStage.setScene(scene);
        primaryStage.show();

        int delay = 5000;   // delay de 5 seg.
        int interval = 1000;  // intervalo de 1 seg.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkUpdates(textArea);
                updateText();
            }
        }, delay, interval);
        client.login();
    }

    private void checkUpdates(TextArea textArea) {
        String textWritten = textArea.getText();
        if (!textWritten.isEmpty())
            sendText(textWritten);
    }

    private void sendText(String textToSend) {

        JsonObject data = new JsonObject();
        data.addProperty("textToSend", textToSend);
        record = client.record.getRecord("textToSend");
        record.set(data);


    }

    private void updateText() {
        if (record != null) {

            record.get(); // returns all record data as a JsonElement
            System.out.println(record.get().getAsJsonObject());
//            JsonElement elem = record.get("textToSend");

            //  textArea.setText(elem.getAsString());


            //subscribe to changes made by you or other clients using .subscribe()
            record.subscribe((recordName, data) -> {
                System.out.println("mudou");
                // some value in the record has changed
                JsonElement elem = record.get("textToSend");

                textArea.setText(elem.getAsString());
                System.out.println("mudou");

            });

        }
    }
}
