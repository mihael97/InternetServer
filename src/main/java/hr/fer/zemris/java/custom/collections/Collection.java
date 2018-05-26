package hr.fer.zemris.java.custom.collections;

/**
 * Javna klasa koja implementira generalnu kolekciju objekata
 * 
 * @author Mihael
 *
 */
public class Collection {

	/**
	 * Konstruktor klase Collection koji je tipa protected
	 */
	protected Collection() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metoda koja provjerava je li kolekcija prazna
	 * 
	 * @return true ako je prazna,inace false
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Javna metoda koja vraca velicinu kolekcije
	 * 
	 * @return velcina tipa int
	 */
	public int size() {
		return 0;
	}

	/**
	 * Metoda koja stavlja objekt u kolekciju
	 * 
	 * @param value
	 *            - objekt kojeg zelimo staviti
	 * 
	 * @throws NullPointerException
	 *             - ako je objekt null
	 */
	public void add(Object value) {

	}

	/**
	 * Metoda koja provjerava nalazi li se objekt u kolekciji
	 * 
	 * @param value
	 *            - objekt ciju prisutnost zelimo provjeriti
	 * @return true ako se nalazi,inace false
	 */
	public boolean contains(Object value) {
		return false;
	}

	/**
	 * Metoda koja brise element iz kolekcije
	 * 
	 * @param value
	 *            - element kojeg zelimo izbrisati
	 * @return - true ako element postoji,inace false
	 */
	public boolean remove(Object value) {
		return false;
	}

	/**
	 * Metoda koja vraca kolekciju u obliku polja. Nikad ne vraca null
	 * 
	 * @return kolekcija u obliku polja objekata
	 */
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Metoda koja poziva proccesor.procces() za svaki element kolekcije
	 * 
	 * @param processor
	 *            - processor ciji proces zelimo koristiti
	 */
	public void forEach(Processor processor) {

	}

	/**
	 * Metoda koja stavlja sve elemente dane kolekcije u sadasnju kolekciju
	 * 
	 * @param other
	 *            - kolekcija elemenata koju zelimo dodati
	 */
	public void addAll(Collection other) {
		other.forEach(new Processor() {

			@Override
			public void process(Object value) {
				add(value);
			}

		});
	}

	/**
	 * Metoda koja brise sve elemente iz kolekcije
	 */
	public void clear() {

	}
}
