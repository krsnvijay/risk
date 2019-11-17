package utils;

public class MapAdaptor {
  MapParser parser;

  public MapAdaptor(ConquestMapParser conquestMapParser) {
    this.parser = conquestMapParser;
  }

  public MapAdaptor(DominationMapParser dominationMapParser) {
    this.parser = dominationMapParser;
  }

}
