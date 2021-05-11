package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Controller {
    Socket socket;
    DataOutputStream out;
    Thread thread;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    TextArea textAreaUserList;

    Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onSubmit(){
        String text = textField.getText();
        textArea.appendText(text+"\n");
        textField.clear();
        try {
            out.writeUTF(text);
        } catch (IOException exception) {
            textArea.appendText("Произошла ошибка");
            exception.printStackTrace();
        }
    }

    @FXML
    private void buttonOn() throws Exception{

        stage.setTitle("Клиент сетевого чата");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root, 500, 300));

        try {
            socket = new Socket("192.168.1.5",8188);
            DataInputStream in =new DataInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream()); // Инициализация out
            String response = in.readUTF(); // Ждём сообщение от сервера
            textArea.appendText(response+"\n"); // Введите имя
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            String response = in.readUTF(); // ждём сообщение от сервера
                            if(response.indexOf("**userlist**")==0){
                                textAreaUserList.clear();
                                String[] userList = response.split("//");
                                for (String userName:userList) {
                                    textAreaUserList.appendText(userName+"\n");
                                }
                            }else{
                                textArea.appendText(response+"\n");
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        //Platform.
    }

}
