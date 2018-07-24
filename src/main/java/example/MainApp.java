package example;

import com.google.gson.JsonObject;
import io.deepstream.DeepstreamClient;
import io.deepstream.LoginResult;
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
    private String textWritten;
    Record record;
    DeepstreamClient client;
    TextArea textArea;
    @Override
    public void start(Stage primaryStage)  {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        root.getChildren().add(new Label("Shared Debriefing" + "\n" + "Date " + dateFormat.format(date)));


         textArea = new TextArea();
        root.getChildren().add(textArea);

        try {
            client = new DeepstreamClient( "localhost:6020");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Shared Logbook");

        primaryStage.setScene(scene);
        primaryStage.show();
        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        try {
                            checkUpdates(textArea);
                            updateText();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 500);
    }

    private void checkUpdates(TextArea textArea) throws URISyntaxException {
        textWritten=  textArea.getText();
        if(!textWritten.isEmpty())
            sendText(textWritten);
//vai vendo se tem texto escrito na textArea
    }

    private void sendText(String textToSend)  {
        client.login();
        System.out.println(client.getConnectionState());
        JsonObject data = new JsonObject();
        data.addProperty("textToSend", textToSend);
        record = client.record.getRecord("textToSend");
        record.set(data);
         System.out.println("record name" + record.name());


                System.out.println("record not null");


        System.out.println("sendText");

    }


    public void updateText()  {
        if(record!=null) {
            record.get(); // returns all record data as a JsonElement
            record.get("textToSend"); // returns the JsonElement 'reading'
            System.out.println("updateText111111111111");

            //subscribe to changes made by you or other clients using .subscribe()
            record.subscribe((recordName, data) -> {

                System.out.println("recordName: " + recordName + " data " + data );
                textArea.setText(recordName );
            });
        }

        System.out.println("updateText");


    }

    public static void main(String[] args) {
        launch(args);
    }
}
