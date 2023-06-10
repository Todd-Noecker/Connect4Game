
package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import java.util.Observable;
import java.util.Observer;
import controller.Connect4Controller;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Connect4Model;
import model.Connect4MoveMessage;

/**
 *
 * This Class functions as the graphical view of the Connect4 program. It
 * functions through the event handler. This class is intended for Online use
 * and will first establish a client/server connection. The main display for
 * this method is updated through the update() method. This GUI view is called
 * from the Connect4 class containing main, and it's start method can be called
 * from the New Game button this GUI will present.
 * 
 * @author Todd Noecker
 */

@SuppressWarnings("deprecation")
public class Connect4View extends Application implements Observer {

	private BorderPane borderPane;
	private GridPane gameBoard;
	private Connect4Controller gameCntr;
	private Connect4Model gameModel;
	private boolean isComp = false;

	/**
	 * Creates the game board.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		// Generate the Objects for game instance and associates
		// observers.
		borderPane = new BorderPane();
		gameBoard = new GridPane();
		gameModel = new Connect4Model();
		gameCntr = new Connect4Controller(gameModel);
		gameModel.addObs(this);

		//Calls the method to add and generate the menu.
		addMenu(stage);
		
		// Set gameBoard and borderPane background to blue
		gameBoard.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		borderPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

		// Add circles to gridpane
		createCircles(gameBoard);

		// Add gameBoard to borderPane
		borderPane.setCenter(gameBoard);
		BorderPane.setMargin(gameBoard, new Insets(8, 8, 8, 8));

		// Display scene
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.setTitle("Connect 4");
		stage.show();
	}

	/**
	 * Creates the circles in the game board
	 * 
	 * @param pane GridPane from start
	 */
	private void createCircles(GridPane pane) {
		// Set gaps between circles
		pane.setHgap(8);
		pane.setVgap(8);

		// Fill with circles
		for (int index = 0; index <= gameModel.getYLEN() - 1; index++) {
			for (int innerIndex = 0; innerIndex <= gameModel.getXLEN() - 1; innerIndex++) {
				Circle circ = new Circle(20);
				circ.setId(String.valueOf(index));
				;
				circ.setFill(Color.WHITE);
				circ.setStroke(Color.WHITE);
				circ.setOnMouseClicked((event) -> {
					int myId = Integer.parseInt(circ.getId());
					// Need to determine the color of the current player and pass it. or
					// have that stored in the model or controller as a current
					// state(player1/player2)
					if (gameModel.getTurn() == true && isComp == false) {
						gameCntr.humanTurn(myId, 1);
					}

				});
				pane.add(circ, index, innerIndex);
			}
		}
	}


	/**
	 * Menu creator method. 
	 * Can start new game with two humans or a human and AI or two AI's
	 */
	private void addMenu(Stage stage) {
		MenuBar startMenu = new MenuBar();
		final Menu menu0 = new Menu("File");
		final Menu menu1 = new Menu("Network Setup");

		startMenu.setStyle("-fx-padding: 2 10 2 2;");
		startMenu.getMenus().addAll(menu0);
		menu0.getItems().addAll(menu1);
		borderPane.setTop(startMenu);

		// This will create and launch the networking window.
		menu1.setOnAction((event) -> {
			NetworkSetup network = new NetworkSetup();
			BorderPane networkPane = new BorderPane();
			GridPane networkGrid = new GridPane();

			// Labels
			Label labelCreate = new Label("Create:");
			Label labelPlay = new Label("Play as:");
			Label labelServer = new Label("Server");
			Label labelPort = new Label("Port");

			// Toggle groups
			ToggleGroup create = new ToggleGroup();
			ToggleGroup play = new ToggleGroup();

			// Radio buttons
			RadioButton server = new RadioButton("Server");
			RadioButton client = new RadioButton("Client");
			RadioButton human = new RadioButton("Human");
			RadioButton computer = new RadioButton("Computer");

			// Text boxes
			TextField serverField = new TextField("localhost");
			TextField portField = new TextField("4000");

			// Buttons
			Button proceed = new Button("OK");
			Button cancel = new Button("Cancel");

			// Creating a dialog
			Dialog<String> dialog = new Dialog<String>();

			// Setting the title
			dialog.setTitle("Network Setup");

			// Add RadioButtons to ToggleGroups and initialize selected
			server.setToggleGroup(create);
			server.setSelected(true);
			client.setToggleGroup(create);
			human.setToggleGroup(play);
			human.setSelected(true);
			computer.setToggleGroup(play);

			// Set gaps
			networkGrid.setHgap(8);
			networkGrid.setVgap(15);

			// Add everything to GridPane
			networkGrid.add(labelCreate, 0, 0);
			networkGrid.add(server, 1, 0);
			networkGrid.add(client, 2, 0);
			networkGrid.add(labelPlay, 0, 1);
			networkGrid.add(human, 1, 1);
			networkGrid.add(computer, 2, 1);
			networkGrid.add(labelServer, 0, 2);
			networkGrid.add(serverField, 1, 2);
			networkGrid.add(labelPort, 2, 2);
			networkGrid.add(portField, 3, 2);
			networkGrid.add(proceed, 0, 3);
			networkGrid.add(cancel, 1, 3);

			// Button event handlers
			proceed.setOnMouseClicked((eventOK) -> {
				network.close();
				if (server.isSelected()) {
					gameCntr.startServer(human.isSelected());
				} else {
					gameCntr.startClient(human.isSelected());
				}

				if (human.isSelected()) {
					isComp = false;
					// it will need to wait for an input and hold control.
				} else {
					isComp = true;
				}
			});

			cancel.setOnMouseClicked((eventCancel) -> {
				network.close();
			});

			// Add a boarder
			networkPane.setMargin(networkGrid, new Insets(10, 10, 10, 10));

			// Add GridPane to BorderPane
			networkPane.setCenter(networkGrid);
			Scene dialogScene = new Scene(networkPane);
			network.setTitle("Network Setup");
			network.setScene(dialogScene);
			network.setMaxHeight(400);
			network.setMaxWidth(600);
			network.showAndWait();
		});
	}

	/**
	 * This update method will update the GUI for the Connect4Game. 
	 * On each update it will determine if a connect 4 has been made. If so the game is halted and
	 * a win message is displayed to the users.
	 */
	@Override
	public void update(Observable o, Object arg) {

		if (gameCntr.getWinRecord() == false) {
			Connect4MoveMessage message = (Connect4MoveMessage) arg;
			int xPos = message.getRow();
			int yPos = message.getColumn();
			int intColor = message.getColor();
			String colorStr = null;
			Color color = null;
			if (intColor == 1) {
				color = Color.RED;
				colorStr = "Red";
			} else {
				color = Color.YELLOW;
				colorStr = "Yellow";
			}

			Circle targetCirc = getTargetNode(xPos, yPos);

			// This is currently just testing that the update will correctly update the
			// gameboard.
			// This is not a final imp.
			if (targetCirc != null) {
				targetCirc.setFill(color);
				targetCirc.setStroke(color);
			}
			// Update will then check the gameState.

			gameState(intColor, colorStr);

		} else {
		}
	}
	
	/**
	 * This method will be called by Update() to determine if the game is over.
	 * The game is over if a player got a connect 4 or id the board is full. It will display
	 * showAndWait() pop-ups on either event being true.
	 */
	private void gameState(int intColor, String colorStr) {

		// Fairly computation heavy method, best to run minimally.
		int didColorWinRet = gameCntr.didColorWin(intColor);

		// This runs every update to check if 4 have connected.
		if (didColorWinRet == intColor) {
			Alert prompt = new Alert(Alert.AlertType.INFORMATION);

			prompt.setTitle("Congrats " + colorStr);
			gameBoard.setDisable(true);
			prompt.setContentText(colorStr + " has Connected 4!");
			prompt.setHeaderText(colorStr + " Won!");
			gameCntr.setWinRecord(true);
			prompt.showAndWait();
		}
		// This runs every update to check if 4 have connected.
		if (didColorWinRet == 3) {
			Alert prompt = new Alert(Alert.AlertType.INFORMATION);

			prompt.setTitle("Board Full " + colorStr);
			gameBoard.setDisable(true);
			prompt.setContentText("The board is full. No winner This time");
			prompt.setHeaderText("Board Full");
			gameCntr.setWinRecord(true);
			prompt.showAndWait();

		}
	}

	/**
	 * Searchs through each node in the gridpane and match the exach node updated in the model. 
	 * The node will be passed back to update to be updated.
	 */
	private Circle getTargetNode(int x, int y) {
		for (Node node : gameBoard.getChildren()) {
			if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
				// Cast as circle(there are only circles in the game board object)
				return (Circle) node;
			}
		}
		return null;
	}

	/*
	 * This is the private class required and mentioned in the project
	 * specifications. I am not sure how this is basically just stubbed right now,
	 * and may not represent exactly how a Stage extension should be handled.
	 */

	private class NetworkSetup extends Stage {

		private Stage myStage;

		public NetworkSetup() {
			myStage = new Stage();
			myStage.initModality(Modality.APPLICATION_MODAL);
		}
	}
}
