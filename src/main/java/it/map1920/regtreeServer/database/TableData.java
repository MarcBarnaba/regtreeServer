package it.map1920.regtreeServer.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Permette di ottenere gli esempi contenuti in una tabella del database.
 * Fornisce un metodo per estrarre i valori distinti di una colonna di una table.
 */
public class TableData {

	private DbAccess db;

	public TableData(DbAccess db) {
		this.db = db;
	}

	/**
	 * Estrae le tuple dalla tabella table formulando una query sql.
	 * @param table
	 * @return Lista di oggetti Example che rappresenta il training set
	 * @throws SQLException
	 * @throws EmptySetException
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema = new TableSchema(db, table);

		String query = "select ";

		for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
			Column c = tSchema.getColumn(i);
			if (i > 0)
				query += ",";
			query += c.getColumnName();
		}
		if (tSchema.getNumberOfAttributes() == 0)
			throw new SQLException();
		query += (" FROM " + table);

		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			Example currentTuple = new Example();
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++)
				if (tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i + 1));
				else
					currentTuple.add(rs.getString(i + 1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if (empty)
			throw new EmptySetException();

		return transSet;

	}
	
	/**
	 * Formula ed esegue una interrogazione SQL per estrarre i valori distinti
	 * ordinati di column e popolare un insieme da restituire.
	 * @return Insieme di valori distinti ordinati in modalità ascendente che 
	 * l’attributo identificato da nome column assume nella tabella identificata dal nome table.
	 */
	public Set<Object> getDistinctColumnValues (String table, Column column) throws SQLException {
		
		Set<Object> colValues = new TreeSet<Object>();
		
		try{
			Statement s = db.getConnection().createStatement();

			// codice SQL: può generare l’eccezione SQLException
			String query = "SELECT " +
					"DISTINCT " +
					column.getColumnName() +
					" FROM " +
					table +
					" ORDER BY " +
					column.getColumnName() +
					";";

			ResultSet r = s.executeQuery(query);

			while(r.next()) {
				
				if (column.isNumber()) {
					colValues.add(r.getDouble(column.getColumnName()));
				} else {
					colValues.add(r.getString(column.getColumnName()));
				}
			}
			r.close();
			s.close();

		} catch (SQLException ex) {
			
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		return colValues;
	}
	
	public enum QUERY_TYPE { 
		MIN,
		MAX
	}

}
