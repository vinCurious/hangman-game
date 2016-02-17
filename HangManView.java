/* 
 * HangManView.java 
 *  
 */

/**
 * This program is view part of Multiplayer HangMan game.
 *
 * @author Vinay Vasant More
 *
 */

public class HangManView {

	HangManView() {
	}

	/**
	 * printScoreBoard prints updated score board
	 * 
	 * @param None
	 *
	 * @return void method
	 *
	 */
	void printScoreBoard() {
		// Printing Scoreboard
		System.out.println("\n\n*************************** S C O R E B O A R D ***************************");
		for (int k = 0; k <= 3; k++) {
			System.out.printf("%-20s%s%n", "Player" + (k + 1), HangManClientController.score[k]);
		}
		System.out.println("***************************************************************************");
	}
}
