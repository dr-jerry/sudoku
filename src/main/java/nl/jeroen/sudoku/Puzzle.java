
package nl.jeroen.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.*;

import org.slf4j.LoggerFactory;

import nl.jeroen.sudoku.Board;


public class Puzzle {
	static org.slf4j.Logger logger = LoggerFactory.getLogger(Puzzle.class);
	private Stack<Stack<Integer>> iterations;
	private boolean recurse = true;
	private int unsolved = Board.l * Board.l;
	private Board theBoard;

	public Puzzle(String fileName) {
		
		theBoard = new Board();
		unsolved = Board.l * Board.l;
//		Region diag1 = new Region();
//		Region diag2 = new Region();
		for (int i=0;i<Board.l;i++) {
			Region hreg = new Region();
			Region vreg = new Region();
			for (int j=0;j<Board.l;j++) {
				int hindex = i*Board.l + j;
				int vindex = i + Board.l *j;

				theBoard.setField(hindex, Field.INIT);
				theBoard.setField(vindex, Field.INIT);
				hreg.addField(theBoard.getField(hindex));
				vreg.addField(theBoard.getField(vindex));
				theBoard.getField(hindex).addRegion(hreg);
				theBoard.getField(vindex).addRegion(vreg);
				if (j == Board.l-1) {

//				    diag1.addVeld(hetBord.getVeld(i*(Bord.l+1)));
//				    hetBord.getVeld(i*(Bord.l+1)).addRegion(diag1);
				    int mirroredHIndex = i*Board.l + Board.l - 1 -i;
//				    diag2.addVeld(hetBord.getVeld(mirroredHIndex));
//    				    hetBord.getVeld(mirroredHIndex).addRegion(diag2);
				}
			}
		}
		int [] leftUppers = {0, 3, 6, 27, 30, 33, 54, 57, 60};
		for (int i = 0;i<Board.l;i++) {
			Region squareRegion = new Region();
			for (int j=0;j<3;j++) {
				for (int k =0;k<3;k++) {
					int index = leftUppers[i] + j*9 + k;
					squareRegion.addField(theBoard.getField(index));
					theBoard.getField(index).addRegion(squareRegion);
				}
			}
		}

		Map<Integer, Integer> givens = initGivens(fileName);
		iterations = new Stack<Stack<Integer>>();
		iterations.push(new Stack<Integer>()); // thats right a stack of stacks.
	    //System.out.println("just pushed size is:" + iterations.size() );
		for (int index : givens.keySet()) {
			theBoard.setGiven(index, ((Integer) givens.get(new Integer(index))).intValue());
		}
		//System.out.println("het Bord .." + hetBord.toString());
	}

	public String printBord(boolean full){
		StringBuilder sb = new StringBuilder();
		for (int yy=0;yy < Board.l;yy++) {
			for (int xx=0;xx<Board.l;xx++) {
				int val = theBoard.getField(yy*Board.l + xx).getValue();
				if (!full) {
					if (!theBoard.getField(yy*Board.l + xx).isGiven()) {
						sb.append("grid").append(xx).append(" " ).append(yy)
							.append(" value=\"").append(val).append("\"");
					}
				} else {
					sb.append(" ").append(val);
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public int solve() {
		try {
			//System.out.println("unsolved " + unsolved);
			while (unsolved > 0) {
				logger.debug("unsolved {} board: {}", unsolved, theBoard);
				Map<Integer, List<Integer>> alternatives = new HashMap<Integer, List<Integer>>();
				int count = 0;
				for (int i=0; i < Board.l * Board.l;i++) {
					Field runner = theBoard.getField(i);
					if (!theBoard.getField(i).isFixed()) {
						int val = runner.getFixed(8, alternatives);
						if (val > 0) {
							theBoard.fix(i, val, iterations);
							//System.out.println("found a fixed value on " + i + " value is " + val);
							count++;
						} else if (val == -1) {
							//System.out.println("failed on " + i);
							//System.out.println(" " + hetBord.toString());
							return unsolved;
						}
					}
				}
				//System.out.println("unsolved " + unsolved + "count in this cycle was " + count);
				if (count == 0) {
					if (recurse) {
						Iterator<Integer> altIter = alternatives.keySet().iterator();
						// know for sure there is only one.
						Integer index = (Integer) altIter.next();
						List<Integer> possibilities = (List<Integer>) alternatives.get(index);
						System.out.println("going into trial on index (" + index + ") with [" + join(possibilities) +"]\n" + theBoard.toString());
						unsolved--;
						for (int trial=0;trial<possibilities.size();trial++){
							iterations.push(new Stack<Integer>());
							int possible = ((Integer)possibilities.get(trial)).intValue();
							//System.out.println("possible is " + possible);
							int oldunsolved = unsolved;
							theBoard.fix(index.intValue(), possible, iterations);
							int us = solve();
							if (us > 1) {
								theBoard.unfix(iterations);
								System.out.println("trial on " + index + " with " + possible + " was not usccuesful restored old board.\n" + theBoard.toString());
								unsolved = oldunsolved;
							}
							if (unsolved == 0) return 0;
						}
					} else {
						//System.out.println(" ended het bord is.." + hetBord);
						return unsolved;
					}
					return unsolved;
				}
				unsolved -= count;
				//System.out.println("found " + count + " unsolved is " + unsolved + " in cycle " + x++);
			}
		} catch (Exception e) {
			System.out.print(printBord(false));
			e.printStackTrace();
		}
		return unsolved;
	}

    Map<Integer, Integer> initGivens (String fileName) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Puzzle.class.getClassLoader().getResourceAsStream(fileName)));
			for (int yy = 0;yy<Board.l;yy++) {
				String line = br.readLine();
				StringTokenizer st = new StringTokenizer(line,",");
				int xx = 0;
				while (st.hasMoreTokens()) {
					String strVal = st.nextToken();
					if (!strVal.equals(" ")) {
						unsolved--;
						int val = Integer.parseInt(strVal);
						int index = yy*Board.l+xx;
						result.put(new Integer(index), new Integer(val));

					}
					xx++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("file not readable");
		} finally {
			try { br.close(); } catch (Exception e) {};
		}
		return result;
	}

	public String join (List<Integer> l) {
		StringBuilder result = new StringBuilder();
		boolean virgin = true;
		for (int val : l) {
			if (virgin) {
				virgin = !virgin;
			} else {
				result.append(",");
			}
			result.append(val);
		}
		return result.toString();
	}

	public static void main (String [] args) {
		Puzzle p = new Puzzle(args[0]);
		long start = System.currentTimeMillis();
		if (p.solve() <= 1){
			System.out.println("time is " + (System.currentTimeMillis() - start));
			System.out.print(p.printBord(true));
			//p.substituteValues(args[1]);
		}
		System.out.println("p.solve()>0");
	}


}
