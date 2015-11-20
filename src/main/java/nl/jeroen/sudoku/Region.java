package nl.jeroen.sudoku;

import java.util.ArrayList;
import java.util.List;

public class Region {
	private Field [] fields;
	private int size;
	
	public Region () {
		fields = new Field[Board.l];
	}
	
	public List<Integer> getPresentValues() {
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0;i<fields.length;i++) {
			if (fields[i].getValue() != Field.INIT) {
				result.add(new Integer(fields[i].getValue()));
			}
		}
		return result;
	}
	
	public List<Field> getFields () {
		ArrayList<Field> result = new ArrayList<Field>(Board.l);
		for (int i =0;i<Board.l;i++) {
			result.add(fields[i]);
		}
		return result;
	}
	
	public void addField(Field v) {
		fields[size++] = v;
	}
	
	public String toString() {
		String result = "";
		for (int i =0;i<fields.length;i++) {
			if (i>0) {
				result += ", ";
			}
			result += "(" +  fields[i].getIndex() + ", " + fields[i].getValue() + ")";
		}
		return result;
	}
	
	public boolean isValueAllowed(int nr) {
		boolean found = false;
		int i =0;
		while (!found && i < Board.l) {
			if (nr == fields[i++].getValue()) found = true;
		}
		return !found;
	}
}
