package hr.fer.zemris.java.custom.collections;

/**
 * Javna klasa koja implementira stog
 * 
 * @author Mihael
 *
 */
public class ObjectStack {
	/**
	 * Referenca na kolekciju gdje se pohranjuju podaci i koja sadrzi prikladne
	 * metode
	 */
	private ArrayIndexedCollection collection;

	/**
	 * Javni konstruktor koji inicijalizira kolekciju za pohranu brojeva i operacije
	 */
	public ObjectStack() {
		// TODO Auto-generated constructor stub
		collection = new ArrayIndexedCollection(2);
	}

	/**
	 * Metoda provjerava da li je stog prazan
	 * 
	 * @return true ako je,inace false
	 */
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	/**
	 * Metoda koja vraca broj elemenata na stogu
	 * 
	 * @return 0 ako je prazan,inace jedan
	 */
	public int size() {
		return collection.size();
	}

	/**
	 * Metoda stavlja element u kolekciju
	 * 
	 * @param value
	 *            - vrijednost koju zelimo spremiti
	 */
	public void push(Object value) {
		collection.add(value);
	}

	/**
	 * Metoda koja vraca zadnji clan stavljen na stog
	 * 
	 * @return objekt koji predstavlja zadnji clan na stogu
	 * 
	 * @throws EmptyStackException
	 *             - ako je stog prazan
	 */
	public Object peek() {
		if (collection.size() == 0) {
			throw new EmptyStackException();
		}

		return collection.get(collection.size() - 1);
	}

	/**
	 * Metoda koja dohvaca zadnji clan i brise ga
	 * 
	 * @return zadnji clan stoga
	 */
	public Object pop() {
		Object forReturn = peek();
		collection.remove(collection.size() - 1);

		return forReturn;
	}

	/**
	 * Metoda koja brise sve sa stoga
	 */
	public void clear() {
		collection.clear();
	}
}
