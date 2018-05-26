package hr.fer.zemris.java.custom.scripting.exec;

import java.util.Objects;
import java.util.TreeMap;

import hr.fer.zemris.java.custom.scripting.exec.exceptions.ObjectMultistackException;


/**
 * Implementacija stoga. Možemo pohraniti više stogova gdje svaki ima svoj
 * ključ(String)
 * 
 * @author Mihael
 *
 */
public class ObjectMultistack {

	/**
	 * Mapa koja sadrži String kao ključ i povezanu listu {@link MultistackEntry}
	 * kao vrijednost
	 */
	private TreeMap<String, MultistackEntry> keyMap;

	/**
	 * Konstruktor za inicijalizaciju
	 */
	public ObjectMultistack() {
		keyMap = new TreeMap<>();
	}

	/**
	 * Metoda stavlja novi element stoga za vrijednošću {@link ValueWrapper} na vrh
	 * stoga
	 * 
	 * @param name
	 *            - ključ
	 * @param valueWrapper
	 *            - vrijednost koju želimo pohraniti
	 * @throws NullPointerException
	 *             - ako je predana vrijednost ili naziv null
	 * 
	 */
	public void push(String name, ValueWrapper valueWrapper) {

		Objects.requireNonNull(name);
		Objects.requireNonNull(valueWrapper);

		if (keyMap.containsKey(name)) {
			keyMap.replace(name, new MultistackEntry(valueWrapper, keyMap.get(name)));
		} else {
			keyMap.put(name, new MultistackEntry(valueWrapper, null));
		}
	}

	/**
	 * Metoda vraća prvi element sa stoga i briše ga
	 * 
	 * @param name
	 *            - ključ
	 * @return {@link ValueWrapper} kao vrijednost prvog elementa stoga
	 */
	public ValueWrapper pop(String name) {
		MultistackEntry entry = checkIfExists(name);

		keyMap.replace(name, entry.next);
		return entry.getValue();
	}

	/**
	 * Metod vraća prvi element sa vrha stoga
	 * 
	 * @param name
	 *            - ključ
	 * @return {@link ValueWrapper} vrijednost
	 */
	public ValueWrapper peek(String name) {
		return checkIfExists(name).getValue();
	}

	/**
	 * Metoda provjerava postoji li stog konkretnog ključa
	 * 
	 * @param name
	 *            -ključ
	 * @return {@link MultistackEntry}
	 * 
	 * @throws ObjectMultistackException
	 *             - ako je stog prazan ili ne postoji
	 */
	private MultistackEntry checkIfExists(String name) {
		
		if(!keyMap.containsKey(name)) 
			throw new ObjectMultistackException("Key doesn't exist!");
		MultistackEntry entry = keyMap.get(name);

		if (entry == null) {
			throw new ObjectMultistackException("Stack is empty!");
		}

		return entry;
	}

	/**
	 * Metoda provjerava je li stog danog ključa prazan
	 * 
	 * @param name
	 *            - ključ
	 * @return true ako je stog prazan,inače false
	 */
	public boolean isEmpty(String name) {
		return keyMap.get(name) == null;
	}

	/**
	 * Privatna struktura koja predstavlja element stoga
	 * 
	 * @author Mihael
	 *
	 */
	private static class MultistackEntry {
		/**
		 * Vrijednost pohranjena u elementu
		 */
		ValueWrapper value;
		/**
		 * Referenca na sljedeći čvor
		 */
		MultistackEntry next;

		/**
		 * Konstruktor za inicijalizaciju novog elementa stoga
		 * 
		 * @param value
		 *            - vrijednost za pohranu
		 * @param next
		 *            - sljedeći čvor
		 */
		public MultistackEntry(ValueWrapper value, MultistackEntry next) {
			this.value = value;
			this.next = next;
		}

		/**
		 * Metoda vraća vrijednost čvora
		 * 
		 * @return vrijednost {@link ValueWrapper}
		 */
		public ValueWrapper getValue() {
			return value;
		}

		/**
		 * Metoda vraća sljedeći element
		 * 
		 * @return sljedeći element
		 */
		@SuppressWarnings("unused")
		public MultistackEntry getNext() {
			return next;
		}

	}
}
