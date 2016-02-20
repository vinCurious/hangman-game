
/* 
 * HangMan.java 
 * 
 * Version: $Id: HangMan.java,v 1.1 2015/09/12 21:05:46 $
 * 
 * 
 */

import java.io.*;
import java.util.*;

/**
 * This program is Multiplayer HangMan game.
 *
 * @author Vinay Vasant More
 *
 */

public class HangMan {
	static String word = "";

	/**
	 * The main program.
	 *
	 * @param args
	 *            command line arguments
	 */

	public static void main(String args[]) throws IOException {
		String path = "";
		int j = 0;
		int noPlayers = 0;
		int missCount = 0;
		String randomWord;
		FileInputStream fs;
		Boolean play = true;
		String players[] = new String[15];
		int score[] = new int[15];

		Random rword = new Random();
		// Storing arguments
		if (args.length > 0) {
			path = args[0];
			for (j = 1; j < args.length; j++) {
				players[j] = args[j];
			}
			noPlayers = j - 1;
		}

		// Stream to read file
		fs = new FileInputStream(path);

		// storing all words from file to array list
		List<String> WordList = new ArrayList<String>();
		while (word != null) {
			word = new DataInputStream(fs).readLine();
			if (word != null) {
				WordList.add(word);
			}
		}
		// closing file
		fs.close();

		// run it for all players and continue next rounds based on user input
		while (play) {
			for (int i = 1; i <= noPlayers; i++) {
				System.out.println("\n\n\n\n********************************** " + players[i]
						+ " **********************************");
				System.out.println(
						"Welcome " + players[i] + " to the Hangman game. You have 8 chances to guess the correct word. "
								+ "\nYou'll get 10 points for correct guess and you'll lose 5 if you can't.");
				// selecting random word
				randomWord = WordList.get(rword.nextInt(WordList.size()));

				// calculating scores
				HangMan hang=new HangMan();
				missCount = hang.playGame(randomWord);
				if (missCount == 8)
					score[i] = score[i] - 5;
				else if (missCount < 8)
					score[i] = score[i] + 10;
			}

			// Printing scoreboard
			System.out.println("\n\n\n\n*************************** S C O R E B O A R D ***************************");
			for (int k = 1; k <= noPlayers; k++) {
				// System.out.println(players[k] + " : "+score[k]);
				System.out.printf("%-20s%s%n", players[k], score[k]);
			}
			System.out.println("***************************************************************************");

			// User input asking whether to continue or not
			System.out.println("Play again??(y/n)");
			Scanner pl = new Scanner(System.in);
			String input = pl.next();
			if (input.equals("y"))
				play = true;
			else {
				play = false;
			}
		}
		System.exit(0);
	}

	/**
	 * replaceLetterAt method evaluates new string replacing desired part
	 * 
	 * @param str
	 *            String expression to be evaluated
	 * @param pos
	 *            position of the string to be replaced
	 * @param letter
	 *            new string to be copied
	 *
	 * @return Method returns evaluated string back.
	 *
	 */
	public String replaceLetterAt(String str, int pos, String letter) {
		return str.substring(0, pos) + letter + str.substring(pos + 1);
	}

	/**
	 * playGame method is called for each player for hangman game with random
	 * word each time
	 * 
	 * @param word
	 *            random word selected for hangman game
	 *
	 * @return Method returns number of misses by player
	 *
	 */
	public int playGame(String word) {
		int result = 0;
		int i = 0;
		int miss = 0;
		String guessWord = "";

		for (i = 1; i <= word.length(); i++) {
			guessWord = guessWord + "_";
		}
		System.out.print(guessWord);
		Scanner sc = new Scanner(System.in);

		while (miss < 8) {
			if (guessWord.equals(word)) {
				System.out.print("\nCongratulations!! You get 10 points.");
				break;
			} else {
				System.out.print("\nGuess next letter: ");
				String letter = sc.next();

				// for right guessed word
				if (word.indexOf(letter) != -1) {
					System.out.println("Good Guess. " + (8 - miss) + " Chances left");
					for (i = 0; i < word.length(); i++) {
						if (i == word.indexOf(letter, i)) {
							guessWord = replaceLetterAt(guessWord, i, letter);
						}
					}
				} else if (word.indexOf(letter) == -1) // for wrong guessed word
				{
					miss = miss + 1;
					System.out.println("Sorry. Wrong Guess. " + (8 - miss) + " Chances left");
				}
				System.out.print(guessWord);
			}
		}
		if (miss == 8) {
			System.out.println("\nGame Over. 5 points will be deducted. The word is \'" + word + "\'.");
		}
		return miss;
	}
}
