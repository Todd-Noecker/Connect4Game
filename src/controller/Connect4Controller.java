package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import javafx.application.Platform;
import model.Connect4Model;
import model.Connect4MoveMessage;

/**
 * This Class functions as the controller for the connect for game, it handles
 * the computer movements, server connections, and input/output streams
 * associated with this game.
 * 
 * @author Todd Noecker
 */
public class Connect4Controller {

	private Connect4Model model;
	private Socket connection;
	private boolean isConnected = false;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	private int myColor;
	private boolean isHuman;
	private boolean winRecord;
	// player 1(server) == red
	// player 2(client) == yellow.

	public Connect4Controller(Connect4Model passedModel) {
		this.model = passedModel;
	}

	/**
	 * This method will pass a human made turn to the model if the attempted move is
	 * valid.
	 * 
	 * @param y Column selected for move color the color passed.
	 */
	public void humanTurn(int y, int color) {
		if (model.getTurn() == true) {
			if (model.getRowPos(y) >= 0) {
				model.move(y, myColor);
				model.setMyTurn(false);
				this.sendMessage(new Connect4MoveMessage(y, model.getRowPos(y), myColor));
			} else {
				model.setMyTurn(true);
			}
		}
	}

	/**
	 * This method will generate a random integer to be used to select a row, it
	 * will reselect if it selects a full row and will then pass the move to the
	 * model to be executed.
	 */
	public void computerMove() {
		if (model.getTurn() == true) {

			boolean madeMove = false;

			while (madeMove == false && model.isBoardFull() == false) {
				Random rn = new Random();
				int rnMove = rn.nextInt(model.getXLEN() - 0 + 1) + 0;
				if (model.getRowPos(rnMove) >= 0) {
					model.move(rnMove, myColor);

					if (isConnected) {
						this.sendMessage(new Connect4MoveMessage(rnMove, model.getRowPos(rnMove), myColor));
					}
					madeMove = true;
				}
			}
		}
	}

	/**
	 * This method will generate a ServerSocket, Input and Output streams and
	 * establish all connections.
	 * 
	 * @param player passed mode true = set isHuman to true.
	 */
	public void startServer(boolean player) {
		try {
			
			ServerSocket server = new ServerSocket(4000);

			//This will block until a message is received.
			connection = server.accept();

			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());

			isConnected = true;
			model.setMyTurn(true);
			myColor = 1;
			isHuman = player;
			if (isHuman == false) {
				this.computerMove();
			}
		} catch (IOException e) {
			System.err.println("Something went wrong with the network! " + e.getMessage());
		}
	}

	/**
	 * This method will be called on a client instance of a gamestart. If selected
	 * the method will wait for a connection if found it will set the isHuman state,
	 * if the player is not a human
	 * @param player contains true for human player and false for computer
	 */
	public void startClient(boolean player) {
		try {
			//This will block until a message is received.
			connection = new Socket("localhost", 4000);
			
			isHuman = player;
			isConnected = true;
			model.setMyTurn(false);
			myColor = 2;
			
			//Object input and output streams.
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());

			if (isHuman == false) {
				Thread t = new Thread(() -> {
					try {
						Connect4MoveMessage otherMsg = (Connect4MoveMessage) ois.readObject();

						Platform.runLater(() -> {
							model.move(otherMsg.getRow(), otherMsg.getColor());
							model.setMyTurn(true);
							this.computerMove();
						});

					} catch (IOException | ClassNotFoundException e) {
						System.err.println("Something went wrong with serialization: " + e.getMessage());
					}
				});
				t.start();
			} else {
				Thread t = new Thread(() -> {
					try {
						Connect4MoveMessage otherMsg = (Connect4MoveMessage) ois.readObject();

						Platform.runLater(() -> {
							model.move(otherMsg.getRow(), otherMsg.getColor());
							model.setMyTurn(true);
						});

					} catch (IOException | ClassNotFoundException e) {
						System.err.println("Something went wrong with serialization: " + e.getMessage());
					}
				});
				t.start();
			}

		} catch (IOException e) {
			System.err.println("Something went wrong with the network! " + e.getMessage());
		}
	}

	/**
	 * This method will call the models check methods and see if a connect 4 has
	 * been made for the last color used. @return, this will return one of 4 values.
	 * 0 no win,1 player one win, 2 player two win, 3 board full no winner.
	 */
	public int didColorWin(int color) {

		return model.checkForWin(color);
	}

	/**
	 * Passes the turnstate to the model
	 * 
	 * @param turn the current turnstate.
	 */
	public void setMyTurn(boolean turn) {
		model.setMyTurn(turn);
	}

	/**
	 * Gets the turnstate to the model
	 * 
	 * @return the current turnstate of the model.
	 */
	public boolean isMyTurn() {
		return model.getTurn();
	}

	/**
	 * sets the controller as having seen a win.
	 * 
	 * @param winState the passed winstate.
	 */
	public void setWinRecord(boolean winState) {
		this.winRecord = winState;
	}

	/**
	 * returns the controller as having seen a win.
	 * 
	 * @return winState the returned winstate.
	 */
	public boolean getWinRecord() {
		return winRecord;
	}

	/*
	 * This method will send a message to the other connected game using a thread,
	 * this thread will then wait for a response from the other game and process
	 * that message. If the gamestate is set to computer (isHuman == false) then the
	 * game will play a move whenever a message is received.
	 */
	private void sendMessage(Connect4MoveMessage msg) {
		if (!isConnected) {
			return;
		}

		if (isHuman == true) {
			Thread t = new Thread(() -> {
				try {
					oos.writeObject(msg);
					oos.flush();

					Object otherMsg = ois.readObject();
					Connect4MoveMessage recieved = (Connect4MoveMessage) otherMsg;
					int col = recieved.getRow();
					int color = recieved.getColor();

					Platform.runLater(() -> {
						model.move(col, color);
						model.setMyTurn(true);
					});
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("Something went wrong with serialization: " + e.getMessage());
				}
			});
			t.start();
		} else {
			Thread t = new Thread(() -> {
				try {
					oos.writeObject(msg);
					oos.flush();

					Object otherMsg = ois.readObject();
					Connect4MoveMessage recieved = (Connect4MoveMessage) otherMsg;
					int col = recieved.getRow();
					int color = recieved.getColor();

					Platform.runLater(() -> {
						model.move(col, color);
						model.setMyTurn(true);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						this.computerMove();
					});

				} catch (IOException | ClassNotFoundException e) {
					System.err.println("Something went wrong with serialization: " + e.getMessage());
				}
			});
			t.start();
		}
	}
}