package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Border {
	int id;
	ArrayList<Integer> neighbors;

	public Border(String borderLine) {
		String[] splitBorderLine = borderLine.split(" ");
		this.id = Integer.parseInt(splitBorderLine[0]);
		this.neighbors = Arrays.stream(splitBorderLine, 1, splitBorderLine.length).map(Integer::parseInt)
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@Override
	public String toString() {
		return String.format("%d %s", this.id,
				this.neighbors.stream().map(String::valueOf).collect(Collectors.joining(" ")));
	}
}
