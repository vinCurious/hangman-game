
/* 
 * HangManClientController.java 
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * This program is Client part of Multiplayer HangMan game.
 *
 * @author Vinay Vasant More
 *
 */

public class HangManClientController {

	private BufferedReader ins;
	String randomWord;
	int playerno = 0;
	boolean play = true;
	int playerScore = 0;
	static int score[] = new int[10];
	HangManView view = new HangManView();
	byte[] rdata = new byte[1024];
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;

	public HangManClientController() {
	}

	/**
	 * connectServer method connects client to the server
	 * 
	 * @param None
	 *
	 * @return void method
	 *
	 */
	public void connectServer() throws IOException {

		// Make connection and initialize streams
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("kansas.cs.rit.edu");
		ins = new BufferedReader(new InputStreamReader(System.in));
		String sentence;

		// Player attempting to connect
		sentence = "connect me";
		sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, 1024);
		clientSocket.send(sendPacket);

		// Gets a dedicated port on server
		receivePacket = new DatagramPacket(rdata, rdata.length);
		clientSocket.receive(receivePacket);
		int port = Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));

		// receiving player info
		receivePacket = new DatagramPacket(rdata, rdata.length);
		clientSocket.receive(receivePacket);
		System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));

		receivePacket = new DatagramPacket(rdata, rdata.length);
		clientSocket.receive(receivePacket);
		playerno = Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));

		// Player acknowledging its connection
		sentence = "Player" + (playerno + 1) + ": Acknowledged. I am ready to play";
		sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
		clientSocket.send(sendPacket);

		// Receiving welcome message
		receivePacket = new DatagramPacket(rdata, rdata.length);
		clientSocket.receive(receivePacket);
		System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));

		while (play) {
			// Player receives random word
			receivePacket = new DatagramPacket(rdata, rdata.length);
			clientSocket.receive(receivePacket);
			randomWord = new String(receivePacket.getData(), 0, receivePacket.getLength());

			int missCount = playGame(randomWord);
			if (missCount == 8)
				playerScore = playerScore - 5;
			else if (missCount < 8)
				playerScore = playerScore + 10;

			sentence = playerScore + "";
			sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
			clientSocket.send(sendPacket);

			// Printing Score board
			for (int j = 0; j <= 3; j++) {
				receivePacket = new DatagramPacket(rdata, rdata.length);
				clientSocket.receive(receivePacket);
				score[j] = Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));
			}
			view.printScoreBoard();

			// User input asking whether to continue or not
			System.out.println("Play again??(y/n)");
			Scanner pl = new Scanner(System.in);
			String input = pl.next();
			if (input.equals("y"))
				play = true;
			else {
				play = false;
			}
			sentence = play + "";
			sendPacket = new DatagramPacket(sentence.getBytes(), sentence.getBytes().length, IPAddress, port);
			clientSocket.send(sendPacket);
		}
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

	/**
	 * The main program.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) throws Exception {
		HangManClientController client = new HangManClientController();
		client.connectServer();
	}
}
