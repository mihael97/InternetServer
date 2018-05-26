package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enum koji sadrze nazive tokena koji se mogu pojaviti u izrazu temeljem vrste
 * podatka
 * 
 * Moguci tipovi tokena:
 * TAGSTART,TEXT,TAGEND,STRING,DOUBLE,INTEGER,VARIABLE,OPERATOR,FUNCTION,END
 * 
 * 
 * @author Mihael
 *
 */
public enum TypeToken {

	/**
	 * Token koji predstavlja pocetak taga u izrazu
	 */
	TAGSTART,

	/**
	 * Token koji predstavlja tekst izvan tagova
	 */
	TEXT,

	/**
	 * Token koji predstavlja zavrstak taga
	 */
	TAGEND,

	/**
	 * Tag koji predstavlja tekst omeden navodnicima unutar tagova
	 */
	STRING,

	/**
	 * Token za double vrijednost
	 */
	DOUBLE,

	/**
	 * Token za cjelobrojnu vrijednost
	 */
	INTEGER,

	/**
	 * Token za matematicke operatore
	 */

	OPERATOR,
	/**
	 * Token za funkciju
	 */
	FUNCTION,

	/**
	 * Token za varijablu
	 */
	VARIABLE,

	/**
	 * Token za kraj nepraznog taga
	 */
	END
}
