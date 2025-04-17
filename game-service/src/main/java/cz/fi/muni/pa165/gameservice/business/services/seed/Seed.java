package cz.fi.muni.pa165.gameservice.business.services.seed;

import java.util.List;

public interface Seed<T> {

	/**
	 * Saves the seeded data into the database
	 */
	void runSeed(boolean logData);

	/**
	 * Returns seeded data. Returned values are values that were persisted in the database
	 * (incl. generated sequences)
	 * @return List of seeded data
	 */
	List<T> getData();

}
