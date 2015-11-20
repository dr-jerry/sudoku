package nl.jeroen.sudoku;

import java.util.Stack;

public class Board {
    public final static int l = 9;
    private Field [] velden = new Field [9*9];
    
    public Field getField(int index) {
    	return velden [index];
    }
    
    public void setField(int index, Field v) {
    	velden[index] = v;
    }
    
    public void setField(int index, int value) {
    	if (velden[index] == null) {
    		velden[index] = new Field(value, index);
    	} else {
    		velden[index].setValue(value);
    	}
    }
    
    public void fix (int index, int value, Stack<Stack<Integer>> moves) {
    	setField(index, value);
    	getField(index).setFixed(true);
		if (moves.peek() == null) 
			moves.push(new Stack<Integer>());
		((Stack<Integer>) moves.peek()).push(new Integer (index));
    }
    
    /**
     * removes the top element from moves paramater and resets all indices on the bord to zero.
     * @param moves
     */
    public void unfix (Stack<Stack<Integer>> iterations) {
		Stack<Integer> moves = (Stack<Integer>) iterations.pop();
		Integer move;		
		while (!moves.isEmpty()) {
			move = (Integer) moves.pop();
			//System.out.println("unfix " + move.intValue());
			getField(move.intValue()).setValue(Field.INIT);
			getField(move.intValue()).setFixed(false);
		}

    }
    
    public void setGiven(int index, int value) {
    	setField(index, value);
    	getField(index).setFixed(true);
    	getField(index).setGiven(true);
    }
    
    public Object clone() {
    	Board result = new Board();
    	for (int i=0;i<velden.length;i++) {
    		result.setField(i,(Field) this.getField(i).clone());
    	}
    	return result;
    }
    
    public String toString () {
    	String result = "";
    	for (int i =0;i<Board.l;i++) {
    		String line = "";
    		for (int j =0;j<Board.l;j++) {
    			line += velden[i*Board.l+j].getValue() + " ";
    		}
    		result += line +"\n";
    	}
    	return result;
    }
}
