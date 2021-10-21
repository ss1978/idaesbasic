package org.idaesbasic.controllers.todolist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TodolistController {

    public Path openedFile;

    @FXML
    private VBox todos_list;

    @FXML
    void add_todo_item(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/dialogs/AddTodo.fxml"));
        DialogPane addTodoDialog = loader.load();
        CreateNewTodoController controller = loader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(addTodoDialog);
        dialog.setTitle("Add new todo");
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.FINISH) {
            addTodo(controller.getTodo(), false);
        }
    }

    void addTodo(String todo, Boolean checked) throws IOException {
        // Load a new todo item
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/todo/todo_item.fxml"));
        Node todo_item = loader.load();
        //Set todo text
        TodoitemController todoController = loader.getController();
        todo_item.setUserData(todoController);
        todoController.setTodo(todo);
        todoController.setDone(checked);
        // Add todo to todolist
        todos_list.getChildren().add(todo_item);
    }

    public void loadFile(Path file) {
        openedFile = file;
        // Add each line from file to todolist
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach((line) -> {
                try {
                    addTodo(line.replace("[x] ", "").replace("[ ] ", ""), (line.startsWith("[x] ") ? true:false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileAs(Path file) {
        openedFile = file;
        try {
            saveFile(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveCurrentFile() throws IOException {
        saveFile(openedFile);
    }

    void saveFile(Path file) throws IOException {
        //Create a string with a todo per line
        String saveData = "";
        for(Node todoItem:todos_list.getChildren()) {
            TodoitemController controller = (TodoitemController) getController(todoItem);
            saveData += "[" + ((controller.isDone()) ? "x":" ") + "] " + controller.getTodo() + "\n";
        }
        //Save this string to the given file
        Files.writeString(file, saveData);
    }

    public static Object getController(Node node) {
        Object controller = null;
        do {
            controller = node.getUserData();
            node = node.getParent();
        } while (controller == null && node != null);
        return controller;
    }
}