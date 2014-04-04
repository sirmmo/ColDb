package it.mmo.coldb;

import it.mmo.coldb.datamodel.Index;
import it.mmo.coldb.datamodel.PrimaryKey;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun.Tuple2;

public class ColDB<T> implements Queryable<T>{

	private DB db;

	private BTreeMap<String, T> pk;

	private Class<T> k;

	private Field pk_field;
	private Map<String, Field> idx_fields = new HashMap<String, Field>();

	private String mapname;

	private Map<String, Boolean> indexes = new HashMap<String, Boolean>();

	private boolean is_ready = false;

	public ColDB(String file, String mapname, Class<T> k) {
		db = DBMaker.newFileDB(new File(file)).closeOnJvmShutdown().make();
		db.getTreeMap(mapname);
		this.mapname = mapname;

		this.k = k;

		for (Field f : k.getFields()) {
			this.addColumn(f.getName(), f.getAnnotation(Index.class) != null);
			if (f.getAnnotation(PrimaryKey.class) != null) {
				pk_field = f;
			}
		}
	}

	private ColDB<T> addColumn(String col_name, boolean indexed) {
		// BTreeMap<String, List<String>> tm =
		// db.getTreeMap(mapname+"__"+col_name);
		indexes.put(col_name, indexed);
		return this;
	}

	private ColDB<T> addColumn(String col_name) {
		return this.addColumn(col_name, false);
	}

	public ColDB<T> ready() {
		this.is_ready = true;
		return this;
	}

	public ColDB<T> save(T item) {
		Iterator<Entry<String, Boolean>> i = indexes.entrySet().iterator();

		try {

			String key = pk_field.get(item).toString();

			pk.put(key, item);

			while (i.hasNext()) {
				Entry<String, Boolean> idx = i.next();
				if (idx.getValue()) {

					BTreeMap<String, List<String>> tm = db.getTreeMap(mapname
							+ "__" + idx.getKey());
					List<String> s = tm.get(k.getField(idx.getKey()).get(item)
							.toString());
					s.add(key);
					tm.put(mapname + "__" + idx.getKey(), s);
				}
			}
			db.commit();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return this;
	}

	public QueryResult<T> filter(Tuple2<String, String> tup) throws Exception {
		if (!this.is_ready)
			throw new Exception();
		String field = tup.a;
		List<String> keys = new ArrayList<String>();
		keys.add(tup.b);
		if (!field.equals(pk_field.getName())) {
			BTreeMap<String, List<String>> tm = db.getTreeMap(mapname + "__"
					+ field);
			keys = tm.get(tup.b);
		}

		QueryResult<T> ret = new QueryResult<T>();
		for (String k : keys)
			ret.add(pk.get(k));

			return ret;
			}
}
