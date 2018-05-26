package hr.fer.zemris.java.custom.collections;

/**
 * Javna klasa koja implementira kolekciju polje promjenjive velicine
 * 
 * @author Mihael
 *
 */
public class ArrayIndexedCollection extends Collection {
	/**
	 * Broj trenutno spremljenih elemenata
	 */
	private int size;

	/**
	 * Kapacitet kolekcije
	 */
	private int capacity;

	/**
	 * Objekti spremljeni u kolekciju
	 */
	private Object[] elements;

	// konstruktori

	/**
	 * Zadani konstruktor koji postavlja kapacitet na 16 i stvara polje jednake
	 * velicine
	 */
	public ArrayIndexedCollection() {
		// TODO Auto-generated constructor stub
		capacity = 16;
		elements = new Object[capacity];
	}

	/**
	 * Konstruktor koji postavlja kapacitet na vrijendost zadanog parametra i stvara
	 * polje jednake velicine
	 * 
	 * @param initialCapacity
	 *            - pocetni kapacitet
	 * 
	 * @throws IllegalArgumentException
	 *             - ako je pocetni kapacitet manji do 1
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException("Kapacitet mora biti veci od 1. Zadan je kapacitet " + initialCapacity);
		} else {
			capacity = initialCapacity;
			elements = new Object[capacity];
		}
	}

	/**
	 * Konstruktor koji prima kolekciju ciji je broj element manji od 16,kopira ju u
	 * elemente,te postavlja kapacitet na 16
	 * 
	 * @param collection
	 * @throws NullPointerException
	 *             - kolekcija ne smije biti null
	 */
	public ArrayIndexedCollection(Collection collection) {
		this();
		if (collection == null) {
			throw new NullPointerException("Kolekcija ne smije biti null!");
		} else {
			addAll(collection);
		}
	}

	/**
	 * Konstruktor koji kao argumente prima kolekciju i inicijalni kapacitet. U
	 * slucaju da je inicijalni kapacitet manji od velicine kolekcije polje se
	 * prealocira
	 * 
	 * @param collection
	 *            - referenca na kolekciju
	 * @param initialCapacity
	 *            - inicijalni kapacitet
	 * @throws NullPointerException
	 *             - ako je kolekcija null
	 */
	public ArrayIndexedCollection(Collection collection, int initialCapacity) {
		this(initialCapacity);
		if (collection != null) {
			addAll(collection);
		} else {
			throw new NullPointerException("Kolekcija ne smije biti null!");
		}
	}

	// ostale metode

	@Override
	public void add(Object value) {
		if (value == null) {
			throw new NullPointerException("Zadana vrijednost ne smije biti null!");
		} else if (size + 1 > capacity) {
			// moramo stvoriti vece polje
			Object[] pomArray = copyArray(elements.length);

			capacity *= 2;
			elements = new Object[capacity];

			int index = 0;
			for (int length = pomArray.length; index < length; index++) {
				elements[index] = pomArray[index];
			}

			elements[index] = value;
			size++;
		} else {
			elements[size++] = value;
		}
	}

	/**
	 * Vraca element na index elementu polja. Ako je index van raspona,javlja
	 * iznimku
	 * 
	 * @param index
	 *            - pozicija
	 * 
	 * @throws IndexOutOfBoundsException
	 *             - ako index nije u rasponu
	 * 
	 * @return element na poziciji index
	 */
	public Object get(int index) {
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException(
					"Index " + index + " nije u polju. Mora biti izmedu[0," + (size - 1) + "].");
		} else {
			return elements[index];
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}

		size = 0;
	}

	/**
	 * Dodaje element na odredenu poziciju u polju
	 * 
	 * @param value
	 *            - element
	 * @param position
	 *            - pozicija
	 * 
	 * @throws IndexOutOfBoundsException
	 *             - ako pozicija nije u rasponu
	 * @throws NullPointerException
	 *             - ako je objekt null
	 */
	public void insert(Object value, int position) {
		if (position < 0 || position > size) {
			throw new IndexOutOfBoundsException(
					"Predan je argument " + position + " no on mora biti izmedu [0," + size + "].");
		} else if (value == null) {
			throw new NullPointerException("Ne smije se umentnuti null!");
		} else {
			Object[] pomArray = copyArray(size);

			if (size + 1 > capacity) {
				capacity *= 2;
				elements = new Object[capacity];
			}

			int elementsIndex = 0;
			int arrayItem = 0;
			size++; // jer dodajemo novi clan
			while (elementsIndex < size) {
				if (elementsIndex == position) {
					elements[elementsIndex] = value;
				} else {
					elements[elementsIndex] = pomArray[arrayItem++];
				}
				elementsIndex++;
			}

		}
	}

	/**
	 * Metoda koja vraca poziciju prvog pojavljivanja elementa
	 * 
	 * @param value
	 *            - element kojeg trazimo
	 * @return -1 ako elementa nema,inace poziciju
	 */
	public int indexOf(Object value) {
		if (value != null) {
			for (int index = 0; index < size; index++) {
				if (elements[index].equals(value)) {
					return index;
				}
			}
		}

		return -1;
	}

	/**
	 * Metoda koja mice element na odredenoj poziciji
	 * 
	 * @param index
	 *            - pozicija s koje zelimo maknuti element
	 * 
	 * @throws IndexOutOfBoundsException
	 *             - ako index nije u rasponu
	 * 
	 */
	public void remove(int index) {
		if (index < 0 || index > size - 1) {
			throw new IndexOutOfBoundsException(
					"Predan je argument " + index + " no on mora biti izmedu [0," + (size - 1) + "].");
		}
		for (int i = index + 1; i < size; i++) {
			elements[i - 1] = elements[i];
		}

		elements[size - 1] = null;
		size--;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean contains(Object value) {
		for (Object object : elements) {
			if (value.equals(object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Object[] toArray() {
		Object[] forReturn = new Object[size];

		int index = 0;
		for (Object object : elements) {
			if (object != null) {
				forReturn[index] = object;
				index++;
			} else {
				break;
			}
		}

		return forReturn;
	}

	@Override
	public boolean remove(Object value) {
		// TODO Auto-generated method stub
		if (indexOf(value) != -1) {
			remove(indexOf(value));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void forEach(Processor processor) {
		for (Object object : elements) {
			if (object != null) {
				processor.process(object);

			}
		}
	}

	/**
	 * Metoda koja kopira elemente u pomocno polje do zadanog elementa
	 * 
	 * @param length
	 *            - duljina
	 * @return polje objekata
	 */
	private Object[] copyArray(int length) {
		Object[] array = new Object[length];

		for (int i = 0; i < length; i++) {
			array[i] = elements[i];
		}

		return array;
	}
}
