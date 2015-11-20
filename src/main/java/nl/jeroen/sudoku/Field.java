package nl.jeroen.sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Field {
	private int value;
	private int index;
	private List<Region> regions;
	private boolean fixed;
	private boolean given;

	private static final List<Integer> ALLOWED_VALUES = new ArrayList<Integer>();
	public static final int INIT = 0;
	static {
		for (int i=0;i<Board.l;i++) {
			ALLOWED_VALUES.add(new Integer(i+1));
		}
	}
	public Object clone() {
		Field result = new Field();
		result.fixed = this.fixed;
		result.index = this.index;
		result.regions = this.regions;
		result.given = this.given;
		result.value = this.value;
		return result;
	}
	
	public boolean isGiven() {
		return given;
	}
	public void setGiven(boolean given) {
		this.given = given;
	}
	public Field () {
		this(INIT, 0);
	}
	public Field (int val, int i) {
		value = val;
		index = i;
	}

	public boolean isFixed() {
		return fixed;
	}
	public void setFixed(boolean given) {
		this.fixed = given;
	}
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public List<Region> getRegions() {
		return regions;
	}
	
	public List<Integer> getValuesLeft() {
		List<Integer> result = (ArrayList) ((ArrayList) ALLOWED_VALUES).clone();
		for (int i =0;i<regions.size();i++) {
			Region reg = (Region) regions.get(i);
//			System.out.println(reg.toString());
			result.removeAll(reg.getPresentValues());
		}
		return result;
	}
	
	public int getFixed(int threshold, Map<Integer, List<Integer>> subResults) {
		List<Integer> left = getValuesLeft();
		if (left.size() == 0) {
			return -1;
			//throw new RuntimeException("0 posibilities on " + index);
		}
		if (left.size() == 1) {
			return ((Integer) left.get(0)).intValue();
		}
		if (left.size() == 2) {
//			System.out.println(" put in alternatives ");
			subResults.clear();
			subResults.put(new Integer(index), left);
		}
//		System.out.println("index is " + index + " size is " + left.size());
		if (left.size() <= threshold) {
			for (int possibleValue : left) {
//				System.out.println("possible value " + possibleValue);
				for (Region reg : getRegions()) {
					int found = 0;
					for (Field v : reg.getFields()) {
//						System.out.println("analyzing field " + v.getIndex() + ", " + v.getValue() );
						if (!v.isFixed() && v != this && !v.isAllowed(possibleValue)) {
							++found;
						}
						if (found > 1) {
							break;
						}
					}
//					System.out.println("found index " + index + " value " + possibleValue );
					if (found == 0) 
						return possibleValue;
				}
			}
			// System.out.println("ended analyzing tile " + index + " size " + left.size() + " values " + valueStr);
		}
		return 0;
	}
	
	boolean isAllowed (int possibleValue) {
		for (Region reg : regions) {
			if(!reg.isValueAllowed(possibleValue)) {
				return true;
			}
		}
		return false;
	}
	
	public void addRegion(Region reg) {
		if (regions == null) {
			regions = new ArrayList<Region>();
		}
		regions.add(reg);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
