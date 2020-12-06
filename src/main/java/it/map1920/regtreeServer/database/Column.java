package it.map1920.regtreeServer.database;

/**
 * Modella una colonna della table di un database.
 *
 */
public class Column {
	private String name;
	private String type;

	/**
	 * @param name 
	 * @param type "number" or "string"
	 */
	Column(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getColumnName() {
		return name;
	}

	public boolean isNumber() {
		return type.equals("number");
	}

	public String toString() {
		return name + ":" + type;
	}
}