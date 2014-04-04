package it.mmo.coldb.samples.dogs;

import it.mmo.coldb.ColDB;
import it.mmo.coldb.datamodel.Index;
import it.mmo.coldb.datamodel.PrimaryKey;
import it.mmo.coldb.datamodel.Table;

public class Dogs extends ColDB<Dog> {

	public Dogs() {
		super("dogs", "dog_db", Dog.class);
	}

}

@Table
class Dog {

	@PrimaryKey
	public String id;

	@Index
	public String name;

	@Index
	public String owner;

	public int age;
}
