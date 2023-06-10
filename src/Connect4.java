import javafx.application.Application;
import view.Connect4View;

/**
 *
 * This Class functions as the graphical view of the Cryptogram program. It
 * functions through the event handler. The main display for this method is
 * updated through the update() method. This GUI view class is started with a
 * command line arg -window in the main Cryptogram file.
 * 
 * @author Todd Noecker
 */

public class Connect4 {

	public static void main(String[] args) {
		Application.launch(Connect4View.class, args);
		System.exit(0);
	}
}
