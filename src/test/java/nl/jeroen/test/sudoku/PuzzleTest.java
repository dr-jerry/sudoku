package nl.jeroen.test.sudoku;

import junit.framework.Assert;
import nl.jeroen.sudoku.Puzzle;

import org.junit.Test;
import org.slf4j.LoggerFactory;
public class PuzzleTest {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PuzzleTest.class);
	
	@Test
	public void testHardest() {
		logger.warn("starting with the hardest");
		Puzzle p = new Puzzle("hardest.txt");
		p.solve();
		System.out.println(p.printBord(true));
		Assert.assertEquals(" 1 4 5 3 2 7 6 9 8\n 8 3 9 6 5 4 1 2 7\n 6 7 2 9 1 8 5 4 3\n 4 9 6 1 8 5 3 7 2\n 2 1 8 4 7 3 9 5 6\n 7 5 3 2 9 6 4 8 1\n 3 6 7 5 4 2 8 1 9\n 9 8 4 7 6 1 2 3 5\n 5 2 1 8 3 9 7 6 4\n", p.printBord(true));
	}
	@Test
	public void testEasy() {
		logger.warn("starting with easy");
		Puzzle p = new Puzzle("6.txt");
		p.solve();
		System.out.println(p.printBord(true));
		Assert.assertEquals(" 5 7 4 1 6 8 3 9 2\n 1 2 3 4 5 9 6 7 8\n 6 8 9 2 3 7 1 4 5\n 2 1 6 3 4 5 7 8 9\n 3 5 7 9 8 1 2 6 4\n 4 9 8 6 7 2 5 3 1\n 7 3 1 5 9 4 8 2 6\n 8 4 2 7 1 6 9 5 3\n 9 6 5 8 2 3 4 1 7\n", p.printBord(true));
	}
}
