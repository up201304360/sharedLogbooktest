package example;

import com.google.gson.JsonObject;
import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamFactory;
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
    private TextArea textArea;
    private DeepstreamClient client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        DeepstreamFactory deepstreamFactory = DeepstreamFactory.getInstance();
        try {
            client = deepstreamFactory.getClient("localhost:6020");
            client.login();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        root.getChildren().add(new Label("Shared Debriefing" + "\n" + "Date " + dateFormat.format(date)));


        textArea = new TextArea();
        root.getChildren().add(textArea);


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
    }

    private void checkUpdates(TextArea textArea) {
        String textWritten = textArea.getText();
        if (!textWritten.isEmpty()) {
            JsonObject data = new JsonObject();
            data.addProperty("textToSend", textWritten);
            record = client.record.getRecord("textToSend");
            record.set(data);
        }
        sendText(textWritten);
    }

    private void sendText(String textToSend) {


        client.event.emit("textToSend2", textToSend);


        client.event.subscribe("textToSend2", (eventName, data1) -> {


            if (data1 != null) {
                textArea.setText(data1.toString());

                //  changeString(data1);

            }

        });


    }

    //todo testar se ele entra no client event subscribe se escrever na outra cobnsola em vez de ser na propria

    private void updateText() {
        if (record != null) {
            System.out.println(record.get().toString() + " --------------");

            record.get(); // returns all record data as a JsonElement

            record.subscribe((recordName, data) -> {
                // some value in the record has changed

                sendText(data.toString());
/*
                record.subscribe((recordName, data) -> {
                    // some value in the record has changed
                    JsonElement elem = record.get("textToSend");
                    textArea.setText( elem.getAsString());


                });
*/


            });
        }
        }

    private void changeString(Object data) {


        String data2 = data.toString();
        String s3 = data2.replace("\\", "");
        int index = data2.indexOf(":");
        String s4 = s3.substring(index + 2);
        String fnal = s4.substring(0, s4.length() - 2);
        textArea.setText(fnal);

    }

    }