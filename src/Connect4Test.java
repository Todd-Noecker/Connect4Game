import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import controller.Connect4Controller;
import model.Connect4Model;
import model.Connect4MoveMessage;

public class Connect4Test {

	@Test
	void initBoard() {
		Connect4Model test = new Connect4Model();
		for (int xIndex = 0; xIndex < 6; xIndex++) {
			System.out.println();
			for (int yIndex = 0; yIndex < 7; yIndex++) {
				System.out.print(test.getSlot(xIndex, yIndex));
			}
			assertTrue(test.getSlot(4, 4) == 0);
			assertTrue(test.getSlot(5, 6) == 0);
			assertTrue(test.getSlot(0, 0) == 0);
			assertTrue(test.getSlot(0, 1) == 0);
			System.out.println();
		}
	}

	@Test
	void initPos() {
		System.out.println("\n");
		Connect4Model test = new Connect4Model();
		for (int xIndex = 0; xIndex < test.getYLEN(); xIndex++) {
			assertTrue(test.getRowPos(xIndex) == 5);
		}
	}

	@Test
	void testWinsHor() {
		System.out.println("\n");
		Connect4Model test = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test);
		test.move(0, 2);
		test.move(0, 2);
		test.move(1, 1);
		test.move(3, 2);
		test.move(1, 2);
		test.move(4, 2);
		test.move(1, 2);
		test.move(5, 1);
		test.move(2, 2);
		test.move(3, 2);
		test.move(2, 2);
		cont.humanTurn(6, 1);
		int retVal = test.checkForWin(2);
		assert (retVal == 2);
		assertTrue(cont.isMyTurn() == false);
		cont.setWinRecord(true);
		assertTrue(cont.getWinRecord());
	}

	@Test
	void testBoardFull() {
		System.out.println("\n");
		Connect4Model mode = new Connect4Model();
		Connect4Controller test = new Connect4Controller(mode);

		mode.move(0, 1);mode.move(1, 2);mode.move(2, 1);mode.move(3, 2);mode.move(4, 1);mode.move(5, 2);mode.move(6, 1);
		mode.move(0, 1);mode.move(1, 1);mode.move(2, 2);mode.move(3, 1);mode.move(4, 1);mode.move(5, 2);mode.move(6, 1);
		mode.move(0, 2);mode.move(1, 1);mode.move(2, 2);mode.move(3, 1);mode.move(4, 2);mode.move(5, 1);mode.move(6, 2);		
		mode.move(0, 2);mode.move(1, 2);mode.move(2, 1);mode.move(3, 2);mode.move(4, 2);mode.move(5, 1);mode.move(6, 2);

		assertTrue(mode.checkForWin(1) == 0);
		assertTrue(mode.checkForWin(2) == 0);
		
		mode.move(0, 1);mode.move(1, 2);mode.move(2, 1);mode.move(3, 2);mode.move(4, 1);mode.move(5, 2);mode.move(6, 1);
		mode.move(0, 1);mode.move(1, 1);mode.move(2, 2);mode.move(3, 1);mode.move(4, 1);mode.move(5, 2);mode.move(6, 1);

		int retVal = mode.checkForWin(1);
		assertTrue(retVal == 3);
	}

	@Test
	void testWinVert() {
		System.out.println("\n");
		Connect4Model test = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test);
		test.move(5, 1);
		test.move(5, 1);
		test.move(5, 1);
		test.move(5, 1);
		int retVal = test.checkForWin(1);
		assert (retVal == 1);
	}
	
	@Test
	void testWinDiag() {
		System.out.println("\n");
		Connect4Model test = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test);
		test.move(0, 1);
		test.move(1, 2);
		test.move(1, 1);
		test.move(2, 2);
		test.move(2, 2);
		test.move(2, 1);
		test.move(3, 2);
		test.move(3, 1);
		test.move(3, 2);
		test.move(3, 1);
		test.setMyTurn(true);
		cont.humanTurn(5, 1);
		cont.humanTurn(5, 1);
		int retVal = cont.didColorWin(1);
		assert (retVal == 1);
	}

	@Test
	void testServer() {
		System.out.println("\n");
		Connect4Model test = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test);

		System.out.println("\n");
		Connect4Model test1 = new Connect4Model();
		Connect4Controller cont1 = new Connect4Controller(test1);


	}

	@Test
	void testRN() {
		System.out.println("\n");
		Connect4Model test1 = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test1);
		while (test1.isBoardFull() == false) {
			cont.setMyTurn(true);
			cont.computerMove();
		}
	}
	@Test
	void moveMsg() {
		System.out.println("\n");
		Connect4Model test1 = new Connect4Model();
		Connect4Controller cont = new Connect4Controller(test1);
		Connect4MoveMessage mess = new Connect4MoveMessage(1,1,1);
		assertTrue(mess.getRow() == 1);
		assertTrue(mess.getColumn() == 1);
		assertTrue(mess.getColor() == 1);
		while (test1.isBoardFull() == false) {
			cont.setMyTurn(true);
			cont.computerMove();
		}
	}
}