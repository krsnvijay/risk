package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author v_valla
 *
 */
public class Border {
	private int id;
	private ArrayList<Integer> neighbors;

	public Border(String borderLine) {
		String[] splitBorderLine = borderLine.split(" ");
		this.id = Integer.parseInt(splitBorderLine[0]);
		this.neighbors = Arrays.stream(splitBorderLine, 1, splitBorderLine.length).map(Integer::parseInt)
				.collect(Collectors.toCollection(ArrayList::new));

	}

	public int getId() {
		return id;
	}

	public ArrayList<Integer> getNeighbors() {
		return neighbors;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNeighbors(ArrayList<Integer> neighbors) {
		this.neighbors = neighbors;
	}

	@Override
	public String toString() {
		return String.format("%d %s", this.id,
				this.neighbors.stream().map(String::valueOf).collect(Collectors.joining(" ")));
	}
}
